import {Injectable} from '@angular/core';
import {HttpClient, HttpErrorResponse} from '@angular/common/http';
import {BehaviorSubject, Observable, of, throwError, timer} from 'rxjs';
import {catchError, shareReplay, switchMap, takeUntil, tap} from 'rxjs/operators';
import {environment} from '../../../environments/environment';
import {AuthService} from '../../auth/services/auth.service';

export interface DashboardStatistics {
  activeTickets: number;
  closedTickets: number;
  pendingTickets: number;
  totalTickets: number;
}

export interface RecentTicket {
  id: number;
  title: string;
  status?: string;
  statusId: number;
  category?: string;
  categoryId: number;
  priority?: string;
  priorityId?: number;
  insertedDate: string;
  lastActivityDate?: string;
  studentId: number;
  operatorId?: number;
}

@Injectable({
  providedIn: 'root'
})
export class DashboardService {
  private apiUrl = `${environment.apiUrl}/dashboard`;

  private loadingSubject = new BehaviorSubject<boolean>(false);
  loading$ = this.loadingSubject.asObservable();

  private errorSubject = new BehaviorSubject<string | null>(null);
  error$ = this.errorSubject.asObservable();

  private refreshTrigger = new BehaviorSubject<boolean>(true);
  private stopAutoRefresh$ = new BehaviorSubject<boolean>(false);

  private statisticsCache$?: Observable<DashboardStatistics>;
  private recentTicketsCache$?: Observable<RecentTicket[]>;
  private cacheDuration = 60000; // 1 minuta
  private lastStatisticsFetch = 0;
  private lastTicketsFetch = 0;

  private lastUserId?: number;

  constructor(
    private http: HttpClient,
    private authService: AuthService
  ) {
    this.authService.currentUser.subscribe(user => {
      const userId = user?.userId;

      if (userId !== this.lastUserId) {
        this.lastUserId = userId;
        this.refreshAllData();
      }
    });
  }

  getStatistics(forceRefresh = false): Observable<DashboardStatistics> {
    const now = Date.now();
    const cacheExpired = now - this.lastStatisticsFetch > this.cacheDuration;
    const currentUser = this.authService.currentUserValue;
    const userChanged = currentUser?.userId !== this.lastUserId;

    if (forceRefresh || !this.statisticsCache$ || cacheExpired || userChanged) {
      this.loadingSubject.next(true);
      this.errorSubject.next(null);
      this.lastStatisticsFetch = now;
      this.lastUserId = currentUser?.userId;

      this.statisticsCache$ = this.http.get<DashboardStatistics>(`${this.apiUrl}/statistics`).pipe(
        tap(() => this.loadingSubject.next(false)),
        catchError(error => this.handleApiError(error, 'Nie udało się pobrać statystyk')),
        shareReplay(1)
      );
    }

    return this.statisticsCache$;
  }

  getRecentTickets(limit = 5, forceRefresh = false): Observable<RecentTicket[]> {
    const now = Date.now();
    const cacheExpired = now - this.lastTicketsFetch > this.cacheDuration;
    const currentUser = this.authService.currentUserValue;
    const userChanged = currentUser?.userId !== this.lastUserId;

    if (forceRefresh || !this.recentTicketsCache$ || cacheExpired || userChanged) {
      this.loadingSubject.next(true);
      this.errorSubject.next(null);
      this.lastTicketsFetch = now;
      this.lastUserId = currentUser?.userId;

      this.recentTicketsCache$ = this.http.get<RecentTicket[]>(
        `${this.apiUrl}/tickets/recent?limit=${limit}`
      ).pipe(
        tap(() => this.loadingSubject.next(false)),
        catchError(error => this.handleApiError(error, 'Nie udało się pobrać zgłoszeń')),
        shareReplay(1)
      );
    }

    return this.recentTicketsCache$;
  }

  refreshAllData(): void {
    this.statisticsCache$ = undefined;
    this.recentTicketsCache$ = undefined;
    this.lastStatisticsFetch = 0;
    this.lastTicketsFetch = 0;
    this.refreshTrigger.next(true);
  }

  startAutoRefresh(intervalMs = 30000): void {
    this.stopAutoRefresh$.next(false);

    timer(intervalMs, intervalMs).pipe(
      takeUntil(this.stopAutoRefresh$),
      switchMap(() => {
        this.refreshAllData();
        return of(true);
      })
    ).subscribe();
  }

  stopAutoRefresh(): void {
    this.stopAutoRefresh$.next(true);
  }

  stopAutoRefreshTask(): void {
    this.stopAutoRefresh$.next(true);
  }

  getStatusClass(statusId: number): string {
    switch (statusId) {
      case 1:
        return 'bg-primary';    // NEW
      case 2:
        return 'bg-warning';    // IN_PROGRESS
      case 3:
        return 'bg-success';    // CLOSED
      default:
        return 'bg-secondary'; // Unknown
    }
  }

  getStatusName(statusId: number): string {
    switch (statusId) {
      case 1:
        return 'Nowe';
      case 2:
        return 'W trakcie';
      case 3:
        return 'Zamknięte';
      default:
        return 'Nieznany';
    }
  }

  formatDate(dateString: string): string {
    if (!dateString) return '';

    const date = new Date(dateString);
    return date.toLocaleDateString('pl-PL', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit'
    });
  }

  sortTickets(tickets: RecentTicket[], field: keyof RecentTicket, direction: 'asc' | 'desc'): RecentTicket[] {
    return [...tickets].sort((a, b) => {
      let aValue: any = a[field];
      let bValue: any = b[field];

      if (field === 'insertedDate' || field === 'lastActivityDate') {
        aValue = aValue ? new Date(aValue).getTime() : 0;
        bValue = bValue ? new Date(bValue).getTime() : 0;
      }

      if (aValue === null || aValue === undefined) return direction === 'asc' ? -1 : 1;
      if (bValue === null || bValue === undefined) return direction === 'asc' ? 1 : -1;

      if (typeof aValue === 'string' && typeof bValue === 'string') {
        return direction === 'asc'
          ? aValue.localeCompare(bValue, 'pl', {sensitivity: 'base'})
          : bValue.localeCompare(aValue, 'pl', {sensitivity: 'base'});
      }

      return direction === 'asc'
        ? (aValue < bValue ? -1 : aValue > bValue ? 1 : 0)
        : (aValue > bValue ? -1 : aValue < bValue ? 1 : 0);
    });
  }

  filterTickets(tickets: RecentTicket[], searchTerm: string): RecentTicket[] {
    if (!searchTerm || searchTerm.trim() === '') return tickets;

    const term = searchTerm.toLowerCase().trim();
    return tickets.filter(ticket =>
      ticket.title?.toLowerCase().includes(term) ||
      ticket.status?.toLowerCase().includes(term) ||
      ticket.category?.toLowerCase().includes(term) ||
      ticket.priority?.toLowerCase().includes(term) ||
      ticket.id.toString().includes(term)
    );
  }

  setCacheLifetime(milliseconds: number): void {
    this.cacheDuration = milliseconds;
  }

  clearError(): void {
    this.errorSubject.next(null);
  }

  private handleApiError(error: HttpErrorResponse, defaultMessage: string): Observable<never> {
    this.loadingSubject.next(false);
    let errorMessage = defaultMessage;

    if (error.error instanceof ErrorEvent) {
      errorMessage = `Błąd: ${error.error.message}`;
    } else {
      switch (error.status) {
        case 0:
          errorMessage = 'Brak połączenia z serwerem';
          break;
        case 401:
          errorMessage = 'Sesja wygasła. Zaloguj się ponownie';
          break;
        case 403:
          errorMessage = 'Brak uprawnień do tej funkcji';
          break;
        case 404:
          errorMessage = 'Nie znaleziono zasobu';
          break;
        case 500:
          errorMessage = 'Błąd serwera';
          break;
        default:
          errorMessage = `${defaultMessage} (Kod: ${error.status})`;
      }
    }

    console.error('Dashboard API error:', error);
    this.errorSubject.next(errorMessage);
    return throwError(() => new Error(errorMessage));
  }

  getEmptyStatistics(): Observable<DashboardStatistics> {
    return of({
      activeTickets: 0,
      closedTickets: 0,
      pendingTickets: 0,
      totalTickets: 0
    });
  }

  getEmptyTicketsList(): Observable<RecentTicket[]> {
    return of([]);
  }
}
