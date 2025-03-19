import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Dictionary } from '../../shared/services/dictionary.service';

export interface User {
  id: number;
  login: string;
  forename: string;
  surname: string;
  groupId: number;
  organizationUnitId: number;
  loginBan: boolean;
  archive: boolean;
  lastLogin?: string;
  firstLogin?: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private apiUrl = `${environment.apiUrl}/admin/users`;

  constructor(private http: HttpClient) {}

  getAllUsers(): Observable<User[]> {
    return this.http.get<User[]>(this.apiUrl);
  }

  blockUser(userId: number): Observable<User> {
    return this.http.post<User>(`${this.apiUrl}/${userId}/block`, {});
  }

  unblockUser(userId: number): Observable<User> {
    return this.http.post<User>(`${this.apiUrl}/${userId}/unblock`, {});
  }

  archiveUser(userId: number): Observable<User> {
    return this.http.post<User>(`${this.apiUrl}/${userId}/archive`, {});
  }

  restoreUser(userId: number): Observable<User> {
    return this.http.post<User>(`${this.apiUrl}/${userId}/restore`, {});
  }

  createUser(userData: Partial<User>): Observable<User> {
    return this.http.post<User>(this.apiUrl, userData);
  }

  getUserById(userId: number): Observable<User> {
    return this.http.get<User>(`${this.apiUrl}/${userId}`);
  }

  updateUser(userId: number, userData: Partial<User>): Observable<User> {
    return this.http.put<User>(`${this.apiUrl}/${userId}`, userData);
  }
}
