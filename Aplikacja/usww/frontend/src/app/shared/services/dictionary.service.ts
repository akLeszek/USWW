import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {environment} from '../../../environments/environment';

export interface Dictionary {
  id?: number;
  idn: string;
  name: string;
  requiresOrganizationUnit?: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class DictionaryService {
  private apiUrl = `${environment.apiUrl}/dictionaries`;
  private adminApiUrl = `${environment.apiUrl}/admin/dictionaries`;

  constructor(private http: HttpClient) {}

  getTicketCategories(): Observable<Dictionary[]> {
    return this.http.get<Dictionary[]>(`${this.apiUrl}/categories`);
  }

  getTicketStatuses(): Observable<Dictionary[]> {
    return this.http.get<Dictionary[]>(`${this.apiUrl}/statuses`);
  }

  getTicketPriorities(): Observable<Dictionary[]> {
    return this.http.get<Dictionary[]>(`${this.apiUrl}/priorities`);
  }

  getUserGroups(): Observable<Dictionary[]> {
    return this.http.get<Dictionary[]>(`${this.apiUrl}/user-groups`);
  }

  getOrganizationUnits(): Observable<Dictionary[]> {
    return this.http.get<Dictionary[]>(`${this.apiUrl}/organization-units`);
  }

  // Admin CRUD operations
  createTicketCategory(category: Dictionary): Observable<Dictionary> {
    return this.http.post<Dictionary>(`${this.adminApiUrl}/categories`, category);
  }

  updateTicketCategory(id: number, category: Dictionary): Observable<Dictionary> {
    return this.http.put<Dictionary>(`${this.adminApiUrl}/categories/${id}`, category);
  }

  deleteTicketCategory(id: number): Observable<void> {
    return this.http.delete<void>(`${this.adminApiUrl}/categories/${id}`);
  }

  createTicketStatus(status: Dictionary): Observable<Dictionary> {
    return this.http.post<Dictionary>(`${this.adminApiUrl}/statuses`, status);
  }

  updateTicketStatus(id: number, status: Dictionary): Observable<Dictionary> {
    return this.http.put<Dictionary>(`${this.adminApiUrl}/statuses/${id}`, status);
  }

  deleteTicketStatus(id: number): Observable<void> {
    return this.http.delete<void>(`${this.adminApiUrl}/statuses/${id}`);
  }

  createTicketPriority(priority: Dictionary): Observable<Dictionary> {
    return this.http.post<Dictionary>(`${this.adminApiUrl}/priorities`, priority);
  }

  updateTicketPriority(id: number, priority: Dictionary): Observable<Dictionary> {
    return this.http.put<Dictionary>(`${this.adminApiUrl}/priorities/${id}`, priority);
  }

  deleteTicketPriority(id: number): Observable<void> {
    return this.http.delete<void>(`${this.adminApiUrl}/priorities/${id}`);
  }
}
