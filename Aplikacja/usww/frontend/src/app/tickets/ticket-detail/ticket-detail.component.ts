import {Component, OnInit} from '@angular/core';
import {CommonModule} from '@angular/common';
import {ActivatedRoute, Router, RouterModule} from '@angular/router';
import {FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators} from '@angular/forms';
import {MessageAttachment, Ticket, TicketMessage, TicketService} from '../services/ticket.service';
import {Dictionary, DictionaryService} from '../../shared/services/dictionary.service';
import {AuthService} from '../../auth/services/auth.service';
import {HasPermissionDirective, HasRoleDirective} from '../../shared/directives/permission.directive';

@Component({
  selector: 'app-ticket-detail',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    ReactiveFormsModule,
    FormsModule,
    HasPermissionDirective,
    HasRoleDirective
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
  messageAttachments: { [key: number]: MessageAttachment[] } = {};

  selectedStatusId: number | undefined | null = null;
  selectedPriorityId: number | undefined | null = null;
  messageForm: FormGroup;

  loading = true;
  messageSending = false;
  updating = false;
  success = '';
  error = '';
  fileError = '';

  allowedFileTypes = '';
  maxFileSize = '';

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

    this.allowedFileTypes = this.ticketService.getReadableAllowedFileTypes();
    this.maxFileSize = this.ticketService.getReadableMaxFileSize();
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

    this.loadDictionaries();

    this.ticketService.getTicket(this.ticketId).subscribe({
      next: (ticket) => {
        this.ticket = ticket;

        this.selectedStatusId = ticket.statusId !== undefined ? ticket.statusId : null;
        this.selectedPriorityId = ticket.priorityId !== undefined ? ticket.priorityId : null;

        this.loadMessages();
      },
      error: (error) => {
        this.error = 'Nie udało się załadować szczegółów zgłoszenia.';
        this.loading = false;
      }
    });
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
  }

  loadMessages(): void {
    this.ticketService.getTicketMessages(this.ticketId).subscribe({
      next: (messages) => {
        this.messages = messages.sort((a, b) =>
          new Date(a.insertDate || '').getTime() - new Date(b.insertDate || '').getTime()
        );

        this.messages.forEach(message => {
          if (message.id) {
            this.loadMessageAttachments(message.id);
          }
        });

        this.loading = false;
      },
      error: (error) => {
        this.error = 'Nie udało się załadować wiadomości dla zgłoszenia.';
        this.loading = false;
      }
    });
  }

  loadMessageAttachments(messageId: number): void {
    this.ticketService.getMessageAttachments(messageId).subscribe({
      next: (attachments: MessageAttachment[]) => {
        this.messageAttachments[messageId] = attachments;
      },
      error: (error) => {
        console.error(`Error loading attachments for message ${messageId}:`, error);
      }
    });
  }

  onFileChange(event: any): void {
    this.fileError = '';

    if (event.target.files.length > 0) {
      const file = event.target.files[0];
      const validation = this.ticketService.validateFile(file);
      if (!validation.valid) {
        this.fileError = validation.errorMessage || 'Nieprawidłowy plik';
        event.target.value = '';
        return;
      }

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
    this.fileError = '';

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

    this.ticketService.createTicketMessage(messageData).subscribe({
      next: (message) => {
        const attachment = this.messageForm.value.attachment;
        if (message.id && attachment) {
          this.ticketService.addAttachment(message.id, attachment).subscribe({
            next: (attachmentData) => {
              if (!this.messageAttachments[message.id!]) {
                this.messageAttachments[message.id!] = [];
              }

              this.messageAttachments[message.id!].push(attachmentData);
            },
            error: (error) => {
              let errorMessage = 'Wiadomość została wysłana, ale nie udało się dodać załącznika.';
              if (error.error && typeof error.error === 'string') {
                errorMessage += ' ' + error.error;
              }
              this.error = errorMessage;
            }
          });
        }

        this.success = 'Wiadomość została wysłana.';
        this.messages.push(message);
        this.messageForm.reset({
          messageText: '',
          attachment: null
        });

        const fileInput = document.querySelector('input[type="file"]') as HTMLInputElement;
        if (fileInput) {
          fileInput.value = '';
        }

        this.loadMessages();
        this.messageSending = false;
      },
      error: (error) => {
        this.error = 'Nie udało się wysłać wiadomości. Spróbuj ponownie.';
        this.messageSending = false;
      }
    });
  }

  downloadAttachment(attachmentId?: number): void {
    if (attachmentId === undefined) return;

    const attachment = this.findAttachment(attachmentId);

    this.ticketService.getAttachment(attachmentId).subscribe({
      next: (blob) => {
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;

        const fileName = attachment && attachment.fileName ?
          attachment.fileName : `attachment-${attachmentId}`;
        a.download = fileName;

        document.body.appendChild(a);
        a.click();

        window.URL.revokeObjectURL(url);
        document.body.removeChild(a);
      },
      error: (error) => {
        this.error = 'Nie udało się pobrać załącznika.';
      }
    });
  }

  findAttachment(attachmentId: number): any {
    for (const messageId in this.messageAttachments) {
      const found = this.messageAttachments[messageId].find(a => a.id === attachmentId);
      if (found) return found;
    }
    return null;
  }

  updateTicketStatus(statusId: number | undefined | null): void {
    if (!this.ticket || statusId === null || statusId === undefined || !this.canChangeStatus()) return;

    this.updating = true;

    const updatedTicket: Ticket = {
      ...this.ticket,
      statusId: statusId
    };

    this.ticketService.updateTicket(this.ticketId, updatedTicket).subscribe({
      next: (ticket) => {
        this.ticket = ticket;
        this.selectedStatusId = ticket.statusId;
        this.success = 'Status zgłoszenia został zaktualizowany.';
        this.updating = false;
      },
      error: (error) => {
        this.error = 'Nie udało się zaktualizować statusu zgłoszenia.';
        this.updating = false;
      }
    });
  }

  updateTicketPriority(priorityId: number | undefined | null): void {
    if (!this.ticket || priorityId === null || priorityId === undefined || !this.canChangePriority()) return;

    this.updating = true;

    const updatedTicket: Ticket = {
      ...this.ticket,
      priorityId: priorityId
    };

    this.ticketService.updateTicket(this.ticketId, updatedTicket).subscribe({
      next: (ticket) => {
        this.ticket = ticket;
        this.selectedPriorityId = ticket.priorityId;
        this.success = 'Priorytet zgłoszenia został zaktualizowany.';
        this.updating = false;
      },
      error: (error) => {
        this.error = 'Nie udało się zaktualizować priorytetu zgłoszenia.';
        this.updating = false;
      }
    });
  }

  archiveTicket(): void {
    if (!this.ticket || this.updating || !this.canArchiveTicket()) return;

    this.updating = true;

    this.ticketService.archiveTicket(this.ticketId).subscribe({
      next: (ticket) => {
        this.ticket = ticket;
        this.success = 'Zgłoszenie zostało zarchiwizowane.';
        this.updating = false;
      },
      error: (error) => {
        this.error = 'Nie udało się zarchiwizować zgłoszenia.';
        this.updating = false;
      }
    });
  }

  restoreTicket(): void {
    if (!this.ticket || this.updating || !this.canRestoreTicket()) return;

    this.updating = true;

    this.ticketService.restoreTicket(this.ticketId).subscribe({
      next: (ticket) => {
        this.ticket = ticket;
        this.success = 'Zgłoszenie zostało przywrócone.';
        this.updating = false;
      },
      error: (error) => {
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

  canEditTicket(): boolean {
    if (!this.ticket) return false;

    return this.authService.canModifyResource(this.ticket.id!, 'Ticket') &&
      !this.ticket.archive &&
      !this.isClosedStatus(this.ticket.statusId);
  }

  canChangeStatus(): boolean {
    return this.authService.isAdmin() || this.authService.isOperator();
  }

  canChangePriority(): boolean {
    return this.authService.isAdmin() || this.authService.isOperator();
  }

  canArchiveTicket(): boolean {
    if (!this.ticket) return false;

    return (this.authService.isAdmin() || this.authService.isOperator()) &&
      !this.ticket.archive;
  }

  canRestoreTicket(): boolean {
    if (!this.ticket) return false;

    return this.authService.isAdmin() && this.ticket.archive;
  }

  canAddMessage(): boolean {
    if (!this.ticket) return false;

    return !this.ticket.archive && !this.isClosedStatus(this.ticket.statusId);
  }

  // Metody pomocnicze dla widoku
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
    return date.toLocaleDateString('pl-PL') + ' ' + date.toLocaleTimeString('pl-PL', {
      hour: '2-digit',
      minute: '2-digit'
    });
  }

  hasAttachments(messageId?: number): boolean {
    return messageId !== undefined &&
      this.messageAttachments[messageId] !== undefined &&
      this.messageAttachments[messageId].length > 0;
  }

  getMessageAttachments(messageId?: number): MessageAttachment[] {
    return messageId !== undefined ? (this.messageAttachments[messageId] || []) : [];
  }
}
