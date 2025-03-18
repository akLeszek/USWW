import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { Ticket, TicketMessage, TicketService } from '../services/ticket.service';
import { Dictionary, DictionaryService } from '../../shared/services/dictionary.service';
import { AuthService } from '../../auth/services/auth.service';

@Component({
  selector: 'app-ticket-detail',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    ReactiveFormsModule,
    FormsModule
  ],
  templateUrl: './ticket-detail.component.html',
  styleUrls: ['./ticket-detail.component.scss']
})
export class TicketDetailComponent implements OnInit {
  ticketId!: number;
  ticket: Ticket | null = null;
  messages: TicketMessage[] = [];
  categories: Dictionary[] = [];
  statuses: Dictionary[] = [];
  priorities: Dictionary[] = [];

  // Zmienne dla selektorów - zmieniono typy aby obsługiwały undefined
  selectedStatusId: number | undefined | null = null;
  selectedPriorityId: number | undefined | null = null;

  messageForm: FormGroup;

  loading = true;
  messageSending = false;
  updating = false;
  success = '';
  error = '';

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private formBuilder: FormBuilder,
    private ticketService: TicketService,
    private dictionaryService: DictionaryService,
    private authService: AuthService
  ) {
    this.messageForm = this.formBuilder.group({
      messageText: ['', [Validators.required]],
      attachment: [null]
    });
  }

  ngOnInit(): void {
    console.log('TicketDetailComponent ngOnInit');

    this.route.paramMap.subscribe(params => {
      console.log('Route params:', params);
      const id = params.get('id');

      if (id) {
        console.log('Got ticket ID:', id);
        this.ticketId = +id;
        this.loadData();
      } else {
        console.error('No ticket ID found in route');
        this.router.navigate(['/tickets']);
      }
    });
  }

  loadData(): void {
    console.log('Loading data for ticket ID:', this.ticketId);
    this.loading = true;

    // Ładowanie słowników
    this.loadDictionaries();

    // Ładowanie szczegółów zgłoszenia
    this.ticketService.getTicket(this.ticketId).subscribe({
      next: (ticket) => {
        console.log('Loaded ticket details:', ticket);
        this.ticket = ticket;

        // Inicjalizacja wartości selektorów
        // Używamy sprawdzania typu, aby uniknąć błędów typów
        this.selectedStatusId = ticket.statusId !== undefined ? ticket.statusId : null;
        this.selectedPriorityId = ticket.priorityId !== undefined ? ticket.priorityId : null;

        // Ładowanie wiadomości dla zgłoszenia
        this.loadMessages();
      },
      error: (error) => {
        console.error('Error loading ticket details:', error);
        this.error = 'Nie udało się załadować szczegółów zgłoszenia.';
        this.loading = false;
      }
    });
  }

  loadDictionaries(): void {
    // Ładowanie kategorii
    this.dictionaryService.getTicketCategories().subscribe({
      next: (categories) => {
        console.log('Loaded categories:', categories);
        this.categories = categories;
      },
      error: (error) => {
        console.error('Error loading categories:', error);
      }
    });

    // Ładowanie statusów
    this.dictionaryService.getTicketStatuses().subscribe({
      next: (statuses) => {
        console.log('Loaded statuses:', statuses);
        this.statuses = statuses;
      },
      error: (error) => {
        console.error('Error loading statuses:', error);
      }
    });

    // Ładowanie priorytetów
    this.dictionaryService.getTicketPriorities().subscribe({
      next: (priorities) => {
        console.log('Loaded priorities:', priorities);
        this.priorities = priorities;
      },
      error: (error) => {
        console.error('Error loading priorities:', error);
      }
    });
  }

  loadMessages(): void {
    this.ticketService.getTicketMessages(this.ticketId).subscribe({
      next: (messages) => {
        console.log('Loaded messages:', messages);
        this.messages = messages.sort((a, b) =>
          new Date(a.insertDate || '').getTime() - new Date(b.insertDate || '').getTime()
        );
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading ticket messages:', error);
        this.error = 'Nie udało się załadować wiadomości dla zgłoszenia.';
        this.loading = false;
      }
    });
  }

  onFileChange(event: any): void {
    if (event.target.files.length > 0) {
      const file = event.target.files[0];
      this.messageForm.patchValue({
        attachment: file
      });
    }
  }

  sendMessage(): void {
    if (this.messageForm.invalid || !this.ticket) {
      return;
    }

    this.messageSending = true;
    this.success = '';
    this.error = '';

    const currentUser = this.authService.currentUserValue;
    if (!currentUser) {
      this.error = 'Nie jesteś zalogowany. Zaloguj się ponownie.';
      this.messageSending = false;
      return;
    }

    const messageData: TicketMessage = {
      messageText: this.messageForm.value.messageText,
      ticketId: this.ticketId,
      senderId: currentUser.userId
    };

    console.log('Sending message:', messageData);

    this.ticketService.createTicketMessage(messageData).subscribe({
      next: (message) => {
        console.log('Message sent successfully:', message);
        // Sukces - dodaj wiadomość do listy i wyczyść formularz
        this.success = 'Wiadomość została wysłana.';
        this.messages.push(message);
        this.messageForm.reset({
          messageText: '',
          attachment: null
        });

        // Odśwież listę wiadomości
        this.loadMessages();

        this.messageSending = false;
      },
      error: (error) => {
        console.error('Error sending message:', error);
        this.error = 'Nie udało się wysłać wiadomości. Spróbuj ponownie.';
        this.messageSending = false;
      }
    });
  }

  updateTicketStatus(statusId: number | undefined | null): void {
    if (!this.ticket || statusId === null || statusId === undefined) return;

    this.updating = true;

    const updatedTicket: Ticket = {
      ...this.ticket,
      statusId: statusId
    };

    console.log('Updating ticket status:', updatedTicket);

    this.ticketService.updateTicket(this.ticketId, updatedTicket).subscribe({
      next: (ticket) => {
        console.log('Status updated successfully:', ticket);
        this.ticket = ticket;
        this.selectedStatusId = ticket.statusId; // Aktualizacja wybranej wartości
        this.success = 'Status zgłoszenia został zaktualizowany.';
        this.updating = false;
      },
      error: (error) => {
        console.error('Error updating ticket status:', error);
        this.error = 'Nie udało się zaktualizować statusu zgłoszenia.';
        this.updating = false;
      }
    });
  }

  updateTicketPriority(priorityId: number | undefined | null): void {
    if (!this.ticket || priorityId === null || priorityId === undefined) return;

    this.updating = true;

    const updatedTicket: Ticket = {
      ...this.ticket,
      priorityId: priorityId
    };

    console.log('Updating ticket priority:', updatedTicket);

    this.ticketService.updateTicket(this.ticketId, updatedTicket).subscribe({
      next: (ticket) => {
        console.log('Priority updated successfully:', ticket);
        this.ticket = ticket;
        this.selectedPriorityId = ticket.priorityId; // Aktualizacja wybranej wartości
        this.success = 'Priorytet zgłoszenia został zaktualizowany.';
        this.updating = false;
      },
      error: (error) => {
        console.error('Error updating ticket priority:', error);
        this.error = 'Nie udało się zaktualizować priorytetu zgłoszenia.';
        this.updating = false;
      }
    });
  }

  archiveTicket(): void {
    if (!this.ticket || this.updating) return;

    this.updating = true;

    console.log('Archiving ticket:', this.ticketId);

    this.ticketService.archiveTicket(this.ticketId).subscribe({
      next: (ticket) => {
        console.log('Ticket archived successfully:', ticket);
        this.ticket = ticket;
        this.success = 'Zgłoszenie zostało zarchiwizowane.';
        this.updating = false;
      },
      error: (error) => {
        console.error('Error archiving ticket:', error);
        this.error = 'Nie udało się zarchiwizować zgłoszenia.';
        this.updating = false;
      }
    });
  }

  restoreTicket(): void {
    if (!this.ticket || this.updating) return;

    this.updating = true;

    console.log('Restoring ticket:', this.ticketId);

    this.ticketService.restoreTicket(this.ticketId).subscribe({
      next: (ticket) => {
        console.log('Ticket restored successfully:', ticket);
        this.ticket = ticket;
        this.success = 'Zgłoszenie zostało przywrócone.';
        this.updating = false;
      },
      error: (error) => {
        console.error('Error restoring ticket:', error);
        this.error = 'Nie udało się przywrócić zgłoszenia.';
        this.updating = false;
      }
    });
  }

  isClosedStatus(statusId?: number): boolean {
    if (!statusId) return false;
    const status = this.statuses.find(s => s.id === statusId);
    return status?.idn === 'CLOSED';
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
