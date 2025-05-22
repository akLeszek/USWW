import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { Ticket, TicketService } from '../services/ticket.service';
import { Dictionary, DictionaryService } from '../../shared/services/dictionary.service';
import { AuthService } from '../../auth/services/auth.service';

@Component({
  selector: 'app-create-ticket',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterModule
  ],
  templateUrl: './create-ticket.component.html',
  styleUrls: ['./create-ticket.component.scss']
})
export class CreateTicketComponent implements OnInit {
  ticketForm: FormGroup;
  categories: Dictionary[] = [];
  statuses: Dictionary[] = [];
  loading = false;
  success = false;
  error = '';
  allowedFileTypes = '';
  maxFileSize = '';

  constructor(
    private formBuilder: FormBuilder,
    protected ticketService: TicketService,
    private dictionaryService: DictionaryService,
    private authService: AuthService,
    private router: Router
  ) {
    this.ticketForm = this.formBuilder.group({
      title: ['', [Validators.required, Validators.maxLength(30)]],
      categoryId: ['', Validators.required],
      message: ['', Validators.required],
      attachment: [null]
    });

    this.allowedFileTypes = this.ticketService.getReadableAllowedFileTypes();
    this.maxFileSize = this.ticketService.getReadableMaxFileSize();
  }

  ngOnInit(): void {
    this.loadCategories();
    this.loadStatuses();
  }

  loadCategories(): void {
    this.dictionaryService.getTicketCategories().subscribe({
      next: (categories) => {
        this.categories = categories;
      },
      error: (error) => {
        this.error = 'Nie udało się załadować kategorii zgłoszeń.';
        console.error('Error loading categories:', error);
      }
    });
  }

  loadStatuses(): void {
    this.dictionaryService.getTicketStatuses().subscribe({
      next: (statuses) => {
        this.statuses = statuses;
      },
      error: (error) => {
        console.error('Error loading statuses:', error);
      }
    });
  }

  onFileChange(event: any): void {
    if (event.target.files.length > 0) {
      const file = event.target.files[0];

      const validation = this.ticketService.validateFile(file);
      if (!validation.valid) {
        this.error = validation.errorMessage || 'Nieprawidłowy plik';
        event.target.value = '';
        return;
      }

      this.ticketForm.patchValue({
        attachment: file
      });

      this.error = '';
    }
  }

  onSubmit(): void {
    if (this.ticketForm.invalid) {
      return;
    }

    this.loading = true;
    this.error = '';

    const newStatus = this.statuses.find(status => status.idn === 'NEW');
    if (!newStatus) {
      this.error = 'Nie znaleziono statusu "NOWE". Skontaktuj się z administratorem.';
      this.loading = false;
      return;
    }

    const currentUser = this.authService.currentUserValue;
    if (!currentUser) {
      this.error = 'Nie jesteś zalogowany. Zaloguj się ponownie.';
      this.loading = false;
      return;
    }

    const ticketData: Ticket = {
      title: this.ticketForm.value.title,
      categoryId: parseInt(this.ticketForm.value.categoryId),
      statusId: newStatus.id!,
      studentId: currentUser.userId
    };

    this.ticketService.createTicket(ticketData).subscribe({
      next: (ticket) => {
        if (this.ticketForm.value.message && ticket.id) {
          const messageData = {
            messageText: this.ticketForm.value.message,
            ticketId: ticket.id,
            senderId: currentUser.userId
          };

          this.ticketService.createTicketMessage(messageData).subscribe({
            next: (message) => {
              const attachment = this.ticketForm.value.attachment;
              if (message.id && attachment) {
                this.ticketService.addAttachment(message.id, attachment).subscribe({
                  next: (attachmentData) => {
                    this.handleSuccess();
                  },
                  error: (error) => {
                    console.error('Error adding attachment:', error);
                    this.error = 'Zgłoszenie zostało utworzone, ale nie udało się dodać załącznika.';
                    this.loading = false;
                  }
                });
              } else {
                this.handleSuccess();
              }
            },
            error: (error) => {
              console.error('Error creating message:', error);
              this.handleSuccess();
            }
          });
        } else {
          this.handleSuccess();
        }
      },
      error: (error) => {
        this.error = 'Nie udało się utworzyć zgłoszenia. Spróbuj ponownie.';
        console.error('Error creating ticket:', error);
        this.loading = false;
      }
    });
  }

  private handleSuccess(): void {
    this.success = true;
    this.loading = false;
    setTimeout(() => {
      this.router.navigate(['/dashboard']);
    }, 2000);
  }
}
