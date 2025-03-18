import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private apiUrl = `${environment.apiUrl}/admin/users`;
  private dictionaryApiUrl = `${environment.apiUrl}/dictionaries`;

  constructor(private http: HttpClient) {}

  getAllUsers(): Observable<any[]> {
    return this.http.get<any[]>(this.apiUrl);
  }

  getUserGroups(): Observable<any[]> {
    return this.http.get<any[]>(`${this.dictionaryApiUrl}/user-groups`);
  }

  getOrganizationUnits(): Observable<any[]> {
    return this.http.get<any[]>(`${this.dictionaryApiUrl}/organization-units`);
  }

  blockUser(userId: number): Observable<any> {
    return this.http.post(`${this.apiUrl}/${userId}/block`, {});
  }

  unblockUser(userId: number): Observable<any> {
    return this.http.post(`${this.apiUrl}/${userId}/unblock`, {});
  }

  archiveUser(userId: number): Observable<any> {
    return this.http.post(`${this.apiUrl}/${userId}/archive`, {});
  }

  restoreUser(userId: number): Observable<any> {
    return this.http.post(`${this.apiUrl}/${userId}/restore`, {});
  }

  createUser(userData: any): Observable<any> {
    return this.http.post(this.apiUrl, userData);
  }

  getUserById(userId: number): Observable<any> {
    return this.http.get(`${this.apiUrl}/${userId}`);
  }

  updateUser(userId: number, userData: any): Observable<any> {
    return this.http.put(`${this.apiUrl}/${userId}`, userData);
  }
}
