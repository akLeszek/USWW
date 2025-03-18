import {Component, OnInit} from '@angular/core';
import {CommonModule} from '@angular/common';
import {ActivatedRoute, Router, RouterModule} from '@angular/router';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {Ticket, TicketMessage, TicketService} from '../services/ticket.service';
import {Dictionary, DictionaryService} from '../../shared/services/dictionary.service';
import {AuthService} from '../../auth/services/auth.service';

@Component({
  selector: 'app-ticket-detail',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    ReactiveFormsModule
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
    this.route.paramMap.subscribe(params => {
      const id = params.get('id');
      if (id) {
        this.ticketId = +id;
        this.loadData();
      } else {
        this.router.navigate(['/tickets']);
      }
    });
  }

  loadData(): void {
    this.loading = true;

    // Ładowanie słowników
    this.loadDictionaries();

    // Ładowanie szczegółów zgłoszenia
    this.ticketService.getTicket(this.ticketId).subscribe({
      next: (ticket) => {
        this.ticket = ticket;

        // Ładowanie wiadomości dla zgłoszenia
        this.loadMessages();
      },
      error: (error) => {
        this.error = 'Nie udało się załadować szczegółów zgłoszenia.';
        console.error('Error loading ticket details:', error);
        this.loading = false;
      }
    });
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

  loadMessages(): void {
    this.ticketService.getTicketMessages(this.ticketId).subscribe({
      next: (messages) => {
        this.messages = messages.sort((a, b) =>
          new Date(a.insertDate || '').getTime() - new Date(b.insertDate || '').getTime()
        );
        this.loading = false;
      },
      error: (error) => {
        this.error = 'Nie udało się załadować wiadomości dla zgłoszenia.';
        console.error('Error loading ticket messages:', error);
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
      senderId: currentUser.id
    };

    this.ticketService.createTicketMessage(messageData).subscribe({
      next: (message) => {
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
        this.error = 'Nie udało się wysłać wiadomości. Spróbuj ponownie.';
        console.error('Error sending message:', error);
        this.messageSending = false;
      }
    });
  }

  updateTicketStatus(statusId: number): void {
    if (!this.ticket) return;

    this.updating = true;

    const updatedTicket: Ticket = {
      ...this.ticket,
      statusId: statusId
    };

    this.ticketService.updateTicket(this.ticketId, updatedTicket).subscribe({
      next: (ticket) => {
        this.ticket = ticket;
        this.success = 'Status zgłoszenia został zaktualizowany.';
        this.updating = false;
      },
      error: (error) => {
        this.error = 'Nie udało się zaktualizować statusu zgłoszenia.';
        console.error('Error updating ticket status:', error);
        this.updating = false;
      }
    });
  }

  updateTicketPriority(priorityId: number): void {
    if (!this.ticket) return;

    this.updating = true;

    const updatedTicket: Ticket = {
      ...this.ticket,
      priorityId: priorityId
    };

    this.ticketService.updateTicket(this.ticketId, updatedTicket).subscribe({
      next: (ticket) => {
        this.ticket = ticket;
        this.success = 'Priorytet zgłoszenia został zaktualizowany.';
        this.updating = false;
      },
      error: (error) => {
        this.error = 'Nie udało się zaktualizować priorytetu zgłoszenia.';
        console.error('Error updating ticket priority:', error);
        this.updating = false;
      }
    });
  }

  archiveTicket(): void {
    if (!this.ticket || this.updating) return;

    this.updating = true;

    this.ticketService.archiveTicket(this.ticketId).subscribe({
      next: (ticket) => {
        this.ticket = ticket;
        this.success = 'Zgłoszenie zostało zarchiwizowane.';
        this.updating = false;
      },
      error: (error) => {
        this.error = 'Nie udało się zarchiwizować zgłoszenia.';
        console.error('Error archiving ticket:', error);
        this.updating = false;
      }
    });
  }

  restoreTicket(): void {
    if (!this.ticket || this.updating) return;

    this.updating = true;

    this.ticketService.restoreTicket(this.ticketId).subscribe({
      next: (ticket) => {
        this.ticket = ticket;
        this.success = 'Zgłoszenie zostało przywrócone.';
        this.updating = false;
      },
      error: (error) => {
        this.error = 'Nie udało się przywrócić zgłoszenia.';
        console.error('Error restoring ticket:', error);
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
    return date.toLocaleDateString('pl-PL') + ' ' + date.toLocaleTimeString('pl-PL', {
      hour: '2-digit',
      minute: '2-digit'
    });
  }
}
