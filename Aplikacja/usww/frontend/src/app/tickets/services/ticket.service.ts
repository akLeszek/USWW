import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {environment} from '../../../environments/environment';
import {User} from '../../admin/services/user.service';

export interface Ticket {
  id?: number;
  title: string;
  insertedDate?: string;
  changeDate?: string;
  archive?: boolean;
  operatorId?: number;
  studentId?: number;
  statusId: number;
  categoryId: number;
  priorityId?: number;
}

export interface TicketMessage {
  id?: number;
  messageText: string;
  insertDate?: string;
  ticketId: number;
  senderId: number;
}

export interface MessageAttachment {
  id?: number;
  messageId: number;
  fileName?: string;
  attachment?: any;
}

export interface UploadConfig {
  maxFileSize: number;
  maxRequestSize: number;
  allowedContentTypes: string[];
}

@Injectable({
  providedIn: 'root'
})
export class TicketService {
  private apiUrl = `${environment.apiUrl}/tickets`;
  private messageApiUrl = `${environment.apiUrl}/messages`;
  private configApiUrl = `${environment.apiUrl}/config`;

  private uploadConfig: UploadConfig | null = null;

  constructor(private http: HttpClient) {
    this.loadUploadConfig();
  }

  private loadUploadConfig(): void {
    this.http.get<UploadConfig>(`${this.configApiUrl}/upload`).subscribe({
      next: (config) => {
        this.uploadConfig = config;
        console.log('Upload configuration loaded:', config);
      },
      error: (error) => {
        console.error('Failed to load upload configuration:', error);
        this.uploadConfig = {
          maxFileSize: 5 * 1024 * 1024,
          maxRequestSize: 10 * 1024 * 1024,
          allowedContentTypes: ['application/pdf', 'image/jpeg', 'image/png']
        };
      }
    });
  }

  getUploadConfig(): UploadConfig {
    if (!this.uploadConfig) {
      return {
        maxFileSize: 5 * 1024 * 1024,
        maxRequestSize: 10 * 1024 * 1024,
        allowedContentTypes: ['application/pdf', 'image/jpeg', 'image/png']
      };
    }
    return this.uploadConfig;
  }

  getReadableAllowedFileTypes(): string {
    return 'PDF, JPG, PNG';
  }


  getReadableMaxFileSize(): string {
    const config = this.getUploadConfig();
    const mbSize = config.maxFileSize / (1024 * 1024);
    return `${mbSize} MB`;
  }

  createTicket(ticket: Ticket): Observable<Ticket> {
    return this.http.post<Ticket>(this.apiUrl, ticket);
  }

  getTicket(id: number): Observable<Ticket> {
    return this.http.get<Ticket>(`${this.apiUrl}/${id}`);
  }

  getAllTickets(): Observable<Ticket[]> {
    return this.http.get<Ticket[]>(this.apiUrl);
  }

  getActiveTickets(): Observable<Ticket[]> {
    return this.http.get<Ticket[]>(`${this.apiUrl}/active`);
  }

  getArchivedTickets(): Observable<Ticket[]> {
    return this.http.get<Ticket[]>(`${this.apiUrl}/archive`);
  }

  updateTicket(id: number, ticket: Ticket): Observable<Ticket> {
    return this.http.put<Ticket>(`${this.apiUrl}/${id}`, ticket);
  }

  archiveTicket(ticketId: number): Observable<Ticket> {
    return this.http.post<Ticket>(`${this.apiUrl}/${ticketId}/archive`, {});
  }

  restoreTicket(id: number): Observable<Ticket> {
    return this.http.post<Ticket>(`${this.apiUrl}/${id}/restore`, {});
  }

  getTicketMessages(ticketId: number): Observable<TicketMessage[]> {
    return this.http.get<TicketMessage[]>(`${this.messageApiUrl}/ticket/${ticketId}`);
  }

  createTicketMessage(message: TicketMessage): Observable<TicketMessage> {
    return this.http.post<TicketMessage>(this.messageApiUrl, message);
  }

  getMessageAttachments(messageId: number): Observable<MessageAttachment[]> {
    return this.http.get<MessageAttachment[]>(`${this.messageApiUrl}/${messageId}/attachments`);
  }

  getAttachment(attachmentId: number): Observable<Blob> {
    return this.http.get(`${this.messageApiUrl}/attachments/${attachmentId}`, {
      responseType: 'blob'
    });
  }

  addAttachment(messageId: number, file: File): Observable<MessageAttachment> {
    const formData = new FormData();
    formData.append('file', file);

    return this.http.post<MessageAttachment>(`${this.messageApiUrl}/${messageId}/attachments`, formData);
  }

  deleteAttachment(attachmentId: number): Observable<void> {
    return this.http.delete<void>(`${this.messageApiUrl}/attachments/${attachmentId}`);
  }

  isAllowedFileType(file: File): boolean {
    const config = this.getUploadConfig();
    return config.allowedContentTypes.includes(file.type);
  }

  isAllowedFileSize(file: File): boolean {
    const config = this.getUploadConfig();
    return file.size <= config.maxFileSize;
  }

  validateFile(file: File): { valid: boolean; errorMessage?: string } {
    if (!this.isAllowedFileType(file)) {
      return {
        valid: false,
        errorMessage: `Niedozwolony typ pliku. Dozwolone formaty: ${this.getReadableAllowedFileTypes()}`
      };
    }

    if (!this.isAllowedFileSize(file)) {
      return {
        valid: false,
        errorMessage: `Plik jest zbyt duży. Maksymalny rozmiar: ${this.getReadableMaxFileSize()}`
      };
    }

    return {valid: true};
  }

  getOperators(): Observable<User[]> {
    return this.http.get<User[]>(`${environment.apiUrl}/users/operators`);
  }

  assignTicketToOperator(ticketId: number, operatorId: number): Observable<Ticket> {
    return this.http.put<Ticket>(`${this.apiUrl}/${ticketId}/assign/${operatorId}`, {});
  }

  updateTicketStatus(ticketId: number, statusId: number): Observable<Ticket> {
    return this.http.put<Ticket>(`${this.apiUrl}/${ticketId}/status/${statusId}`, {});
  }
}
