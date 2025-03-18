import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { NgbModule, NgbPaginationModule } from '@ng-bootstrap/ng-bootstrap';
import { Ticket, TicketService } from '../services/ticket.service';
import { Dictionary, DictionaryService } from '../../shared/services/dictionary.service';

@Component({
  selector: 'app-ticket-list',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    FormsModule,
    NgbModule,
    NgbPaginationModule
  ],
  templateUrl: './ticket-list.component.html',
  styleUrls: ['./ticket-list.component.scss']
})
export class TicketListComponent implements OnInit {
  tickets: Ticket[] = [];
  filteredTickets: Ticket[] = [];
  categories: Dictionary[] = [];
  statuses: Dictionary[] = [];
  priorities: Dictionary[] = [];

  loading = true;
  error = '';

  // Parametry filtrowania
  filterTitle = '';
  filterCategory = '';
  filterStatus = '';
  filterPriority = '';

  // Parametry paginacji
  page = 1;
  pageSize = 10;
  collectionSize = 0;

  // Parametry sortowania
  sortColumn = 'insertedDate';
  sortDirection = 'desc';

  constructor(
    private ticketService: TicketService,
    private dictionaryService: DictionaryService
  ) { }

  ngOnInit(): void {
    this.loadDictionaries();
    this.loadTickets();
  }

  loadDictionaries(): void {
    // Ładowanie kategorii
    this.dictionaryService.getTicketCategories().subscribe({
      next: (categories) => {
        this.categories = categories;
      },
      error: (error) => {
        console.error('Error loading categories:', error);
      }
    });

    // Ładowanie statusów
    this.dictionaryService.getTicketStatuses().subscribe({
      next: (statuses) => {
        this.statuses = statuses;
      },
      error: (error) => {
        console.error('Error loading statuses:', error);
      }
    });

    // Ładowanie priorytetów
    this.dictionaryService.getTicketPriorities().subscribe({
      next: (priorities) => {
        this.priorities = priorities;
      },
      error: (error) => {
        console.error('Error loading priorities:', error);
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
      return (
        (this.filterTitle === '' || (ticket.title && ticket.title.toLowerCase().includes(this.filterTitle.toLowerCase()))) &&
        (this.filterCategory === '' || ticket.categoryId === parseInt(this.filterCategory)) &&
        (this.filterStatus === '' || ticket.statusId === parseInt(this.filterStatus)) &&
        (this.filterPriority === '' || (ticket.priorityId && ticket.priorityId === parseInt(this.filterPriority)))
      );
    });

    // Sortowanie
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

    this.collectionSize = this.filteredTickets.length;
  }

  resetFilters(): void {
    this.filterTitle = '';
    this.filterCategory = '';
    this.filterStatus = '';
    this.filterPriority = '';
    this.applyFilters();
  }

  sort(column: string): void {
    if (this.sortColumn === column) {
      this.sortDirection = this.sortDirection === 'asc' ? 'desc' : 'asc';
    } else {
      this.sortColumn = column;
      this.sortDirection = 'asc';
    }
    this.applyFilters();
  }

  get currentPageTickets(): Ticket[] {
    return this.filteredTickets
      .slice((this.page - 1) * this.pageSize, (this.page) * this.pageSize);
  }

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
}
