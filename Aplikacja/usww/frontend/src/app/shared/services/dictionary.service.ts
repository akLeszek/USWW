import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {environment} from '../../../environments/environment';

export interface Dictionary {
  id: number;
  idn: string;
  name: string;
}

@Injectable({
  providedIn: 'root'
})
export class DictionaryService {
  private apiUrl = `${environment.apiUrl}/dictionaries`;

  constructor(private http: HttpClient) {
  }

  getTicketCategories(): Observable<Dictionary[]> {
    return this.http.get<Dictionary[]>(`${this.apiUrl}/categories`);
  }

  getTicketStatuses(): Observable<Dictionary[]> {
    return this.http.get<Dictionary[]>(`${this.apiUrl}/statuses`);
  }

  getTicketPriorities(): Observable<Dictionary[]> {
    return this.http.get<Dictionary[]>(`${this.apiUrl}/priorities`);
  }
}
