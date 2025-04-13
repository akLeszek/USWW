import { Component, OnInit, OnDestroy } from '@angular/core';
import { RouterModule } from '@angular/router';
import { CommonModule, AsyncPipe, NgClass, NgFor, NgIf } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { Observable, Subject, combineLatest, BehaviorSubject } from 'rxjs';
import { catchError, map, takeUntil, tap, startWith, finalize, debounceTime, distinctUntilChanged } from 'rxjs/operators';
import { AuthService } from '../auth/services/auth.service';
import { DashboardService, DashboardStatistics, RecentTicket } from './services/dashboard.service';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [RouterModule, NgClass, NgFor, NgIf, NgbModule, AsyncPipe, FormsModule],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent implements OnInit, OnDestroy {
  // Strumienie danych
  statistics$!: Observable<DashboardStatistics>;
  recentTickets$!: Observable<RecentTicket[]>;
  filteredSortedTickets$!: Observable<RecentTicket[]>;

  // Stan wyszukiwania i sortowania
  searchTerm = new BehaviorSubject<string>('');
  sortField = new BehaviorSubject<keyof RecentTicket>('insertedDate');
  sortDirection = new BehaviorSubject<'asc' | 'desc'>('desc');

  // Stany interfejsu
  loading = false;
  error = '';
  refreshing = false;

  // Anulowanie subskrypcji przy zniszczeniu komponentu
  private destroy$ = new Subject<void>();

  constructor(
    private dashboardService: DashboardService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    // Inicjalizacja głównych strumieni danych
    this.initDataStreams();

    // Konfiguracja filtrowania i sortowania
    this.setupFiltering();

    // Wstępne pobranie danych
    this.loadDashboardData();

    // Nasłuchiwanie błędów z serwisu
    this.dashboardService.error$
      .pipe(takeUntil(this.destroy$))
      .subscribe(errorMsg => {
        if (errorMsg) {
          this.error = errorMsg;
        }
      });

    // Nasłuchiwanie stanu ładowania
    this.dashboardService.loading$
      .pipe(takeUntil(this.destroy$))
      .subscribe(isLoading => {
        this.loading = isLoading;
      });
  }

  /**
   * Inicjalizacja głównych strumieni danych
   */
  private initDataStreams(): void {
    // Strumień statystyk
    this.statistics$ = this.dashboardService.getStatistics().pipe(
      takeUntil(this.destroy$),
      catchError(err => {
        console.error('Error loading statistics:', err);
        return this.getEmptyStatistics();
      })
    );

    // Strumień zgłoszeń
    this.recentTickets$ = this.dashboardService.getRecentTickets().pipe(
      takeUntil(this.destroy$),
      catchError(err => {
        console.error('Error loading tickets:', err);
        return [];
      })
    );
  }

  /**
   * Konfiguracja filtrowania i sortowania zgłoszeń
   */
  private setupFiltering(): void {
    // Filtrowanie i sortowanie zgłoszeń w oparciu o kryteria
    this.filteredSortedTickets$ = combineLatest([
      this.recentTickets$,
      this.searchTerm.pipe(
        debounceTime(300),
        distinctUntilChanged(),
        startWith('')
      ),
      this.sortField,
      this.sortDirection
    ]).pipe(
      map(([tickets, search, field, direction]) => {
        // Najpierw filtrowanie
        let result = this.filterTickets(tickets, search);

        // Potem sortowanie
        result = this.sortTickets(result, field, direction);

        return result;
      })
    );
  }

  /**
   * Filtrowanie zgłoszeń wg frazy
   */
  private filterTickets(tickets: RecentTicket[], searchTerm: string): RecentTicket[] {
    if (!searchTerm) return tickets;

    const term = searchTerm.toLowerCase();
    return tickets.filter(ticket =>
      ticket.title.toLowerCase().includes(term) ||
      (ticket.status?.toLowerCase().includes(term) || '') ||
      (ticket.category?.toLowerCase().includes(term) || '')
    );
  }

  /**
   * Sortowanie zgłoszeń
   */
  private sortTickets(tickets: RecentTicket[], field: keyof RecentTicket, direction: 'asc' | 'desc'): RecentTicket[] {
    return this.dashboardService.sortTickets(tickets, field, direction);
  }

  /**
   * Ustawia pole i kierunek sortowania
   */
  toggleSort(field: keyof RecentTicket): void {
    const currentField = this.sortField.value;

    if (currentField === field) {
      // Jeśli to samo pole, odwróć kierunek
      const newDirection = this.sortDirection.value === 'asc' ? 'desc' : 'asc';
      this.sortDirection.next(newDirection);
    } else {
      // Jeśli inne pole, ustaw je i domyślny kierunek (desc)
      this.sortField.next(field);
      this.sortDirection.next('desc');
    }
  }

  /**
   * Pobiera dane dashboardu
   */
  loadDashboardData(forceRefresh: boolean = false): void {
    this.error = '';
    this.refreshing = true;

    // Odśwież statystyki
    this.statistics$ = this.dashboardService.getStatistics(forceRefresh).pipe(
      takeUntil(this.destroy$),
      finalize(() => this.refreshing = false),
      catchError(err => {
        console.error('Error refreshing statistics:', err);
        return this.getEmptyStatistics();
      })
    );

    // Odśwież zgłoszenia
    this.recentTickets$ = this.dashboardService.getRecentTickets(5, forceRefresh).pipe(
      takeUntil(this.destroy$),
      catchError(err => {
        console.error('Error refreshing tickets:', err);
        return [];
      })
    );
  }

  /**
   * Wymusza odświeżenie wszystkich danych
   */
  refreshData(): void {
    this.loadDashboardData(true);
  }

  /**
   * Pusta struktura statystyk w przypadku błędu
   */
  private getEmptyStatistics(): Observable<DashboardStatistics> {
    return new Observable<DashboardStatistics>(observer => {
      observer.next({
        activeTickets: 0,
        closedTickets: 0,
        pendingTickets: 0,
        totalTickets: 0
      });
      observer.complete();
    });
  }

  /**
   * Format daty
   */
  formatDate(dateString: string): string {
    return this.dashboardService.formatDate(dateString);
  }

  /**
   * Status CSS
   */
  getStatusClass(statusId: number): string {
    return this.dashboardService.getStatusClass(statusId);
  }

  /**
   * Dostosowane opcje szybkiego dostępu na podstawie roli
   */
  get quickActions(): {label: string; route: string; icon: string}[] {
    const actions = [
      {label: 'Nowe zgłoszenie', route: '/tickets/new', icon: 'bi-plus-circle'}
    ];

    if (this.authService.hasRole('ADMIN')) {
      actions.push(
        {label: 'Zarządzaj użytkownikami', route: '/admin/users', icon: 'bi-people'}
      );
    }

    if (this.authService.hasRole('ADMIN') || this.authService.hasRole('OPERATOR')) {
      actions.push(
        {label: 'Wszystkie zgłoszenia', route: '/tickets', icon: 'bi-list-check'}
      );
    }

    return actions;
  }

  /**
   * Czyszczenie zasobów
   */
  ngOnDestroy(): void {
    // Anulowanie auto-odświeżania
    this.dashboardService.stopAutoRefresh();

    // Anulowanie subskrypcji
    this.destroy$.next();
    this.destroy$.complete();
  }
}
