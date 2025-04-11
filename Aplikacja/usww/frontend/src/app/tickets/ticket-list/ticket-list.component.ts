import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { NgbModule, NgbPaginationModule, NgbDropdownModule } from '@ng-bootstrap/ng-bootstrap';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

import { Ticket, TicketService } from '../services/ticket.service';
import { Dictionary, DictionaryService } from '../../shared/services/dictionary.service';
import { AuthService } from '../../auth/services/auth.service';
import { HasPermissionDirective, HasRoleDirective } from '../../shared/directives/permission.directive';
import { ToastService } from '../../shared/services/toast.service';

@Component({
  selector: 'app-ticket-list',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    FormsModule,
    NgbModule,
    NgbPaginationModule,
    NgbDropdownModule,
    HasPermissionDirective,
    HasRoleDirective
  ],
  templateUrl: './ticket-list.component.html',
  styleUrls: ['./ticket-list.component.scss']
})
export class TicketListComponent implements OnInit, OnDestroy {
  tickets: Ticket[] = [];
  filteredTickets: Ticket[] = [];
  categories: Dictionary[] = [];
  statuses: Dictionary[] = [];
  priorities: Dictionary[] = [];
  operators: any[] = [];

  loading = true;
  processing = false;
  error = '';
  success = '';

  filterTitle = '';
  filterCategory = '';
  filterStatus = '';
  filterPriority = '';
  filterAssignment = '';

  page = 1;
  pageSize = 10;
  collectionSize = 0;

  sortColumn = 'insertedDate';
  sortDirection = 'desc';

  private destroy$ = new Subject<void>();

  constructor(
    private ticketService: TicketService,
    private dictionaryService: DictionaryService,
    public authService: AuthService,
    private toastService: ToastService
  ) {}

  ngOnInit(): void {
    this.loadDictionaries();
    this.loadTickets();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  loadDictionaries(): void {
    this.dictionaryService.getTicketCategories().subscribe({
      next: (categories) => {
        this.categories = categories;
      },
      error: (error) => {
        console.error('Error loading categories:', error);
      }
    });

    this.dictionaryService.getTicketStatuses().subscribe({
      next: (statuses) => {
        this.statuses = statuses;
      },
      error: (error) => {
        console.error('Error loading statuses:', error);
      }
    });

    this.dictionaryService.getTicketPriorities().subscribe({
      next: (priorities) => {
        this.priorities = priorities;
      },
      error: (error) => {
        console.error('Error loading priorities:', error);
      }
    });

    if (this.canAssignTicket()) {
      this.loadOperators();
    }
  }

  loadOperators(): void {
    this.ticketService.getOperators().subscribe({
      next: (operators) => {
        this.operators = operators;
      },
      error: (error) => {
        console.error('Error loading operators:', error);
      }
    });
  }

  loadTickets(): void {
    this.loading = true;
    this.ticketService.getAllTickets().subscribe({
      next: (tickets) => {
        this.tickets = tickets;
        this.applyFilters();
        this.loading = false;
      },
      error: (error) => {
        this.error = 'Wystąpił błąd podczas ładowania zgłoszeń.';
        console.error('Error loading tickets:', error);
        this.loading = false;
      }
    });
  }

  applyFilters(): void {
    this.filteredTickets = this.tickets.filter(ticket => {
      const titleMatch = !this.filterTitle ||
        (ticket.title && ticket.title.toLowerCase().includes(this.filterTitle.toLowerCase()));

      const categoryMatch = !this.filterCategory ||
        ticket.categoryId === parseInt(this.filterCategory);

      const statusMatch = !this.filterStatus ||
        ticket.statusId === parseInt(this.filterStatus);

      const priorityMatch = !this.filterPriority ||
        (ticket.priorityId && ticket.priorityId === parseInt(this.filterPriority));

      let assignmentMatch = true;
      if (this.canFilterByAssignment() && this.filterAssignment) {
        const currentUserId = this.authService.currentUserValue?.userId;

        if (this.filterAssignment === 'assigned') {
          assignmentMatch = !!ticket.operatorId && ticket.operatorId !== 0;
        } else if (this.filterAssignment === 'unassigned') {
          assignmentMatch = !ticket.operatorId || ticket.operatorId === 0;
        } else if (this.filterAssignment === 'mine') {
          assignmentMatch = ticket.operatorId === currentUserId;
        }
      }

      return titleMatch && categoryMatch && statusMatch && priorityMatch && assignmentMatch;
    });

    this.sortFilteredTickets();

    this.collectionSize = this.filteredTickets.length;
  }

  sortFilteredTickets(): void {
    this.filteredTickets.sort((a, b) => {
      const direction = this.sortDirection === 'asc' ? 1 : -1;

      switch (this.sortColumn) {
        case 'title':
          return direction * ((a.title || '').localeCompare(b.title || ''));
        case 'categoryId':
          return direction * ((a.categoryId || 0) - (b.categoryId || 0));
        case 'statusId':
          return direction * ((a.statusId || 0) - (b.statusId || 0));
        case 'priorityId':
          return direction * ((a.priorityId || 0) - (b.priorityId || 0));
        case 'insertedDate':
        default:
          return direction * ((new Date(a.insertedDate || '').getTime()) - (new Date(b.insertedDate || '').getTime()));
      }
    });
  }

  resetFilters(): void {
    this.filterTitle = '';
    this.filterCategory = '';
    this.filterStatus = '';
    this.filterPriority = '';
    this.filterAssignment = '';
    this.applyFilters();
  }

  sort(column: string): void {
    if (this.sortColumn === column) {
      this.sortDirection = this.sortDirection === 'asc' ? 'desc' : 'asc';
    } else {
      this.sortColumn = column;
      this.sortDirection = 'asc';
    }
    this.sortFilteredTickets();
  }

  get currentPageTickets(): Ticket[] {
    return this.filteredTickets
      .slice((this.page - 1) * this.pageSize, (this.page) * this.pageSize);
  }

  // Metody pomocnicze do wyświetlania danych
  getCategoryName(categoryId?: number): string {
    if (!categoryId) return '';
    const category = this.categories.find(c => c.id === categoryId);
    return category ? category.name : '';
  }

  getStatusName(statusId?: number): string {
    if (!statusId) return '';
    const status = this.statuses.find(s => s.id === statusId);
    return status ? status.name : '';
  }

  getPriorityName(priorityId?: number): string {
    if (!priorityId) return '';
    const priority = this.priorities.find(p => p.id === priorityId);
    return priority ? priority.name : '';
  }

  getStatusClass(statusId?: number): string {
    if (!statusId) return 'bg-secondary';

    const status = this.statuses.find(s => s.id === statusId);
    if (!status) return 'bg-secondary';

    switch (status.idn) {
      case 'NEW':
        return 'bg-primary';
      case 'IN_PROGRESS':
        return 'bg-warning';
      case 'CLOSED':
        return 'bg-success';
      default:
        return 'bg-secondary';
    }
  }

  getPriorityClass(priorityId?: number): string {
    if (!priorityId) return 'bg-secondary';

    const priority = this.priorities.find(p => p.id === priorityId);
    if (!priority) return 'bg-secondary';

    switch (priority.idn) {
      case 'LOW':
        return 'bg-info';
      case 'MEDIUM':
        return 'bg-warning';
      case 'HIGH':
        return 'bg-danger';
      case 'CRITICAL':
        return 'bg-danger text-white';
      default:
        return 'bg-secondary';
    }
  }

  formatDate(dateString?: string): string {
    if (!dateString) return '';
    const date = new Date(dateString);
    return date.toLocaleDateString('pl-PL') + ' ' + date.toLocaleTimeString('pl-PL', {hour: '2-digit', minute:'2-digit'});
  }

  canCreateTicket(): boolean {
    return this.authService.hasPermission('Ticket', 'CREATE');
  }

  canAssignTicket(): boolean {
    return this.authService.isAdmin() || this.authService.isOperator();
  }

  canFilterByAssignment(): boolean {
    return this.authService.isAdmin() || this.authService.isOperator();
  }

  getAvailableStatuses(): Dictionary[] {
    if (this.authService.isAdmin()) {
      return this.statuses;
    }

    if (this.authService.isOperator()) {
      return this.statuses.filter(s => s.idn !== 'CLOSED');
    }

    return this.statuses;
  }

  assignToMe(ticketId: number): void {
    if (!this.canAssignTicket()) {
      return;
    }

    this.processing = true;
    const operatorId = this.authService.currentUserValue?.userId;

    this.ticketService.assignTicketToOperator(ticketId, operatorId).subscribe({
      next: (updatedTicket) => {
        // Aktualizacja zgłoszenia w lokalnej tablicy
        const index = this.tickets.findIndex(t => t.id === ticketId);
        if (index !== -1) {
          this.tickets[index] = updatedTicket;
        }

        this.applyFilters();
        this.toastService.showSuccess('Zgłoszenie zostało przypisane do Ciebie');
        this.processing = false;
      },
      error: (error) => {
        console.error('Error assigning ticket:', error);
        this.toastService.showError('Nie udało się przypisać zgłoszenia');
        this.processing = false;
      }
    });
  }

  assignToOperator(ticketId: number, operatorId: number): void {
    if (!this.canAssignTicket()) {
      return;
    }

    this.processing = true;
    this.ticketService.assignTicketToOperator(ticketId, operatorId).subscribe({
      next: (updatedTicket) => {
        // Aktualizacja zgłoszenia w lokalnej tablicy
        const index = this.tickets.findIndex(t => t.id === ticketId);
        if (index !== -1) {
          this.tickets[index] = updatedTicket;
        }

        this.applyFilters();
        this.toastService.showSuccess('Zgłoszenie zostało przypisane do operatora');
        this.processing = false;
      },
      error: (error) => {
        console.error('Error assigning ticket:', error);
        this.toastService.showError('Nie udało się przypisać zgłoszenia');
        this.processing = false;
      }
    });
  }

  changeTicketStatus(ticketId: number, statusId: number): void {
    if (!this.authService.canModifyResource(ticketId, 'Ticket')) {
      return;
    }

    this.processing = true;
    this.ticketService.updateTicketStatus(ticketId, statusId).subscribe({
      next: (updatedTicket) => {
        // Aktualizacja zgłoszenia w lokalnej tablicy
        const index = this.tickets.findIndex(t => t.id === ticketId);
        if (index !== -1) {
          this.tickets[index] = updatedTicket;
        }

        this.applyFilters();
        this.toastService.showSuccess('Status zgłoszenia został zmieniony');
        this.processing = false;
      },
      error: (error) => {
        console.error('Error updating ticket status:', error);
        this.toastService.showError('Nie udało się zmienić statusu zgłoszenia');
        this.processing = false;
      }
    });
  }

  refreshList(): void {
    this.loadTickets();
  }
}
