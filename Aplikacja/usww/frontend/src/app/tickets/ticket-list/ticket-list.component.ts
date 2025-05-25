import { Component, OnDestroy, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { NgbDropdownModule, NgbModule, NgbPaginationModule } from '@ng-bootstrap/ng-bootstrap';
import { Subject } from 'rxjs';

import { Ticket, TicketService } from '../services/ticket.service';
import { Dictionary, DictionaryService } from '../../shared/services/dictionary.service';
import { AuthService } from '../../auth/services/auth.service';
import { ToastService } from '../../shared/services/toast.service';
import { User, UserService } from '../../admin/services/user.service';

@Component({
  selector: 'app-ticket-list',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    FormsModule,
    NgbModule,
    NgbPaginationModule,
    NgbDropdownModule
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
  operators: User[] = [];
  operatorNames: Record<number, string> = {};

  loading = true;
  processingTicketId: number | null = null;
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

  defaultOperatorLogin = 'unknown_operator';
  defaultOperator?: User;
  Math = Math;

  private destroy$ = new Subject<void>();
  private isUnassignedView = false;

  constructor(
    private ticketService: TicketService,
    private dictionaryService: DictionaryService,
    private route: ActivatedRoute,
    public authService: AuthService,
    private toastService: ToastService,
    private userService: UserService
  ) {
    this.operatorNames = {};
  }

  ngOnInit(): void {
    this.loadDictionaries();

    this.route.url.subscribe(segments => {
      const wasUnassignedView = this.isUnassignedView;
      this.isUnassignedView = segments.length > 1 && segments[1].path === 'unassigned';

      if (this.isUnassignedView) {
        this.filterAssignment = 'unassigned';
      } else if (wasUnassignedView) {
        this.filterAssignment = '';
      }

      this.loadTickets();
    });
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
      error: (error: unknown) => {
        console.error('Error loading categories:', error);
      }
    });

    this.dictionaryService.getTicketStatuses().subscribe({
      next: (statuses) => {
        this.statuses = statuses;
      },
      error: (error: unknown) => {
        console.error('Error loading statuses:', error);
      }
    });

    this.dictionaryService.getTicketPriorities().subscribe({
      next: (priorities) => {
        this.priorities = priorities;
      },
      error: (error: unknown) => {
        console.error('Error loading priorities:', error);
      }
    });
  }

  loadOperators(): void {
    if (!this.canAssignTicket()) {
      return;
    }

    this.ticketService.getOperators().subscribe({
      next: (operators: User[]) => {
        this.operators = operators;
        this.defaultOperator = operators.find(op => op.login === this.defaultOperatorLogin);
        operators.forEach(operator => {
          this.operatorNames[operator.id] = `${operator.forename} ${operator.surname}`;
        });

        if (!this.defaultOperator) {
          this.loadUnknownOperator();
        }
      },
      error: (error: unknown) => {
        console.error('Error loading operators:', error);
      }
    });
  }

  private loadUnknownOperator(): void {
    this.userService.getUserByLogin(this.defaultOperatorLogin).subscribe({
      next: (unknownOperator) => {
        this.defaultOperator = unknownOperator;
        this.operatorNames[unknownOperator.id] = `${unknownOperator.forename} ${unknownOperator.surname}`;
      },
      error: (error) => {
        console.error('Error loading unknown operator:', error);
      }
    });
  }

  isTicketUnassigned(ticket: Ticket): boolean {
    if (!ticket.operatorId) {
      return true;
    }

    if (this.defaultOperator) {
      return this.defaultOperator.id === ticket.operatorId;
    }

    const operator = this.operators.find(op => op.id === ticket.operatorId);
    return operator?.login === this.defaultOperatorLogin;
  }

  loadTickets(): void {
    this.loading = true;

    if (this.filterAssignment === 'unassigned') {
      this.ticketService.getUnassignedTickets().subscribe({
        next: (tickets) => {
          this.tickets = tickets;
          this.loadOperatorNamesForTickets();
          this.applyFilters();
          this.loading = false;
        },
        error: (error: unknown) => {
          this.error = 'Wystąpił błąd podczas ładowania zgłoszeń.';
          console.error('Error loading tickets:', error);
          this.loading = false;
        }
      });
    } else {
      this.ticketService.getAllTickets().subscribe({
        next: (tickets) => {
          this.tickets = tickets;
          this.loadOperatorNamesForTickets();
          this.applyFilters();
          this.loading = false;
        },
        error: (error: unknown) => {
          this.error = 'Wystąpił błąd podczas ładowania zgłoszeń.';
          console.error('Error loading tickets:', error);
          this.loading = false;
        }
      });
    }
  }

  private loadOperatorNamesForTickets(): void {
    if (this.canAssignTicket()) {
      this.loadOperators();
    } else {
      this.tickets.forEach(ticket => {
        if (ticket.operatorId && !this.operatorNames[ticket.operatorId]) {
          this.operatorNames[ticket.operatorId] = 'Operator';
        }
      });
    }
  }

  applyFilters(): void {
    if (this.isUnassignedView && this.filterAssignment === 'unassigned') {
      this.filteredTickets = [...this.tickets];
      this.sortFilteredTickets();
      this.collectionSize = this.filteredTickets.length;
      return;
    }

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
          assignmentMatch = !this.isTicketUnassigned(ticket);
        } else if (this.filterAssignment === 'unassigned') {
          assignmentMatch = this.isTicketUnassigned(ticket);
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
        case 'operatorId':
          return direction * ((a.operatorId || 0) - (b.operatorId || 0));
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

    if (!this.isUnassignedView) {
      this.filterAssignment = '';
    }

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

  getOperatorName(operatorId?: number): string {
    if (!operatorId) return 'Nieprzypisany';
    return this.operatorNames[operatorId] || `Operator #${operatorId}`;
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
    return date.toLocaleDateString('pl-PL') + ' ' + date.toLocaleTimeString('pl-PL', {
      hour: '2-digit',
      minute: '2-digit'
    });
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

  assignToMe(ticketId: number | undefined): void {
    if (!ticketId || !this.canAssignTicket()) {
      return;
    }

    this.processingTicketId = ticketId;
    const operatorId = this.authService.currentUserValue?.userId;

    if (!operatorId) {
      this.toastService.showError('Brak ID operatora. Zaloguj się ponownie.');
      this.processingTicketId = null;
      return;
    }

    this.ticketService.assignTicketToOperator(ticketId, operatorId).subscribe({
      next: (updatedTicket: Ticket) => {
        // Aktualizacja zgłoszenia w lokalnej tablicy
        const index = this.tickets.findIndex(t => t.id === ticketId);
        if (index !== -1) {
          this.tickets[index] = updatedTicket;
        }

        this.applyFilters();
        this.toastService.showSuccess('Zgłoszenie zostało przypisane do Ciebie');
        this.processingTicketId = null;
      },
      error: (error: unknown) => {
        console.error('Error assigning ticket:', error);
        this.toastService.showError('Nie udało się przypisać zgłoszenia');
        this.processingTicketId = null;
      }
    });
  }

  assignToOperator(ticketId: number | undefined, operatorId: number): void {
    if (!ticketId || !this.authService.isAdmin()) {
      return;
    }

    this.processingTicketId = ticketId;
    this.ticketService.assignTicketToOperator(ticketId, operatorId).subscribe({
      next: (updatedTicket: Ticket) => {
        const index = this.tickets.findIndex(t => t.id === ticketId);
        if (index !== -1) {
          this.tickets[index] = updatedTicket;
        }

        this.applyFilters();
        this.toastService.showSuccess('Zgłoszenie zostało przypisane do operatora');
        this.processingTicketId = null;
      },
      error: (error: unknown) => {
        console.error('Error assigning ticket:', error);
        this.toastService.showError('Nie udało się przypisać zgłoszenia');
        this.processingTicketId = null;
      }
    });
  }

  changeTicketStatus(ticketId: number, statusId: number): void {
    if (!this.authService.canModifyResource(ticketId, 'Ticket')) {
      return;
    }

    this.processingTicketId = ticketId;
    this.ticketService.updateTicketStatus(ticketId, statusId).subscribe({
      next: (updatedTicket: Ticket) => {
        const index = this.tickets.findIndex(t => t.id === ticketId);
        if (index !== -1) {
          this.tickets[index] = updatedTicket;
        }

        this.applyFilters();
        this.toastService.showSuccess('Status zgłoszenia został zmieniony');
        this.processingTicketId = null;
      },
      error: (error: unknown) => {
        console.error('Error updating ticket status:', error);
        this.toastService.showError('Nie udało się zmienić statusu zgłoszenia');
        this.processingTicketId = null;
      }
    });
  }

  archiveTicket(ticketId: number): void {
    if (!ticketId || !this.authService.isAdmin()) {
      return;
    }

    this.processingTicketId = ticketId;
    this.ticketService.archiveTicket(ticketId).subscribe({
      next: (updatedTicket: Ticket) => {
        const index = this.tickets.findIndex(t => t.id === ticketId);
        if (index !== -1) {
          this.tickets[index] = updatedTicket;
        }

        this.applyFilters();
        this.toastService.showSuccess('Zgłoszenie zostało zarchiwizowane');
        this.processingTicketId = null;
      },
      error: (error: unknown) => {
        console.error('Error archiving ticket:', error);
        this.toastService.showError('Nie udało się zarchiwizować zgłoszenia');
        this.processingTicketId = null;
      }
    });
  }

  refreshList(): void {
    this.loadTickets();
  }

  shouldShowAssignmentFilter(): boolean {
    return this.canFilterByAssignment() && !this.isUnassignedView;
  }

  protected isOnUnassignedView(): boolean {
    return this.isUnassignedView;
  }
}
