import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

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

@Injectable({
  providedIn: 'root'
})
export class TicketService {
  private apiUrl = `${environment.apiUrl}/tickets`;
  private messageApiUrl = `${environment.apiUrl}/messages`;

  constructor(private http: HttpClient) { }

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

  archiveTicket(id: number): Observable<Ticket> {
    return this.http.post<Ticket>(`${this.apiUrl}/${id}/archive`, {});
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
}
