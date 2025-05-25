import {Component, OnDestroy, OnInit} from '@angular/core';
import {RouterModule} from '@angular/router';
import {AsyncPipe, NgClass, NgFor, NgIf} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {NgbModule} from '@ng-bootstrap/ng-bootstrap';
import {BehaviorSubject, combineLatest, Observable, Subject} from 'rxjs';
import {catchError, debounceTime, distinctUntilChanged, finalize, map, startWith, takeUntil} from 'rxjs/operators';
import {AuthService} from '../auth/services/auth.service';
import {DashboardService, DashboardStatistics, RecentTicket} from './services/dashboard.service';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [RouterModule, NgClass, NgFor, NgIf, NgbModule, AsyncPipe, FormsModule],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent implements OnInit, OnDestroy {

  statistics$!: Observable<DashboardStatistics>;
  recentTickets$!: Observable<RecentTicket[]>;
  filteredSortedTickets$!: Observable<RecentTicket[]>;

  searchTerm = new BehaviorSubject<string>('');
  sortField = new BehaviorSubject<keyof RecentTicket>('insertedDate');
  sortDirection = new BehaviorSubject<'asc' | 'desc'>('desc');

  loading = false;
  error = '';
  refreshing = false;

  private destroy$ = new Subject<void>();

  constructor(
    private dashboardService: DashboardService,
    private authService: AuthService
  ) {
  }

  ngOnInit(): void {
    this.initDataStreams();

    this.setupFiltering();

    this.loadDashboardData(true);

    this.dashboardService.error$
      .pipe(takeUntil(this.destroy$))
      .subscribe(errorMsg => {
        if (errorMsg) {
          this.error = errorMsg;
        }
      });

    this.dashboardService.loading$
      .pipe(takeUntil(this.destroy$))
      .subscribe(isLoading => {
        this.loading = isLoading;
      });
  }

  private initDataStreams(): void {
    this.statistics$ = this.dashboardService.getStatistics().pipe(
      takeUntil(this.destroy$),
      catchError(err => {
        console.error('Error loading statistics:', err);
        return this.getEmptyStatistics();
      })
    );

    this.recentTickets$ = this.dashboardService.getRecentTickets().pipe(
      takeUntil(this.destroy$),
      catchError(err => {
        console.error('Error loading tickets:', err);
        return [];
      })
    );
  }

  private setupFiltering(): void {
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
        let result = this.filterTickets(tickets, search);
        result = this.sortTickets(result, field, direction);
        return result;
      })
    );
  }

  private filterTickets(tickets: RecentTicket[], searchTerm: string): RecentTicket[] {
    if (!searchTerm) return tickets;

    const term = searchTerm.toLowerCase();
    return tickets.filter(ticket =>
      ticket.title.toLowerCase().includes(term) ||
      (ticket.status?.toLowerCase().includes(term) || '') ||
      (ticket.category?.toLowerCase().includes(term) || '')
    );
  }

  private sortTickets(tickets: RecentTicket[], field: keyof RecentTicket, direction: 'asc' | 'desc'): RecentTicket[] {
    return this.dashboardService.sortTickets(tickets, field, direction);
  }

  toggleSort(field: keyof RecentTicket): void {
    const currentField = this.sortField.value;

    if (currentField === field) {
      const newDirection = this.sortDirection.value === 'asc' ? 'desc' : 'asc';
      this.sortDirection.next(newDirection);
    } else {
      this.sortField.next(field);
      this.sortDirection.next('desc');
    }
  }

  loadDashboardData(forceRefresh: boolean = false): void {
    this.error = '';
    this.refreshing = true;

    this.statistics$ = this.dashboardService.getStatistics(forceRefresh).pipe(
      takeUntil(this.destroy$),
      finalize(() => this.refreshing = false),
      catchError(err => {
        console.error('Error refreshing statistics:', err);
        return this.getEmptyStatistics();
      })
    );

    this.recentTickets$ = this.dashboardService.getRecentTickets(5, forceRefresh).pipe(
      takeUntil(this.destroy$),
      catchError(err => {
        console.error('Error refreshing tickets:', err);
        return [];
      })
    );
  }

  refreshData(): void {
    this.loadDashboardData(true);
  }

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

  formatDate(dateString: string): string {
    return this.dashboardService.formatDate(dateString);
  }

  getStatusClass(statusId: number): string {
    return this.dashboardService.getStatusClass(statusId);
  }

  get quickActions(): { label: string; route: string; icon: string }[] {
    const actions = [];

    if (this.authService.isStudent()) {
      actions.push({
        label: 'Nowe zgłoszenie',
        route: '/tickets/new',
        icon: 'bi-plus-circle'
      });
    }

    if (this.authService.isAdmin()) {
      actions.push({
        label: 'Zarządzaj użytkownikami',
        route: '/admin/users',
        icon: 'bi-people'
      });
    }

    if (this.authService.isAdmin() || this.authService.isOperator()) {
      actions.push({
        label: 'Wszystkie zgłoszenia',
        route: '/tickets',
        icon: 'bi-list-check'
      });
    }

    return actions;
  }

  ngOnDestroy(): void {
    this.dashboardService.stopAutoRefresh();

    this.destroy$.next();
    this.destroy$.complete();
  }
}
