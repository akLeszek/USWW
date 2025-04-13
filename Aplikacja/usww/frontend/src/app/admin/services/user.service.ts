import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {environment} from '../../../environments/environment';
import {AuthService} from '../../auth/services/auth.service';

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
  generatedPassword?: string;
}

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private apiUrl = `${environment.apiUrl}/admin/users`;

  constructor(private http: HttpClient, private authService: AuthService) {
  }

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

  createUser(userData: Partial<User>): Observable<User> {
    return this.http.post<User>(this.apiUrl, userData);
  }

  getUserById(userId: number): Observable<User> {
    if (this.authService.isAdmin()) {
      return this.http.get<User>(`${this.apiUrl}/${userId}`);
    } else {
      return this.http.get<User>(`${environment.apiUrl}/users/${userId}`);
    }
  }

  getPublicUserById(userId: number): Observable<User> {
    return this.http.get<User>(`${environment.apiUrl}/users/${userId}`);
  }

  getUserByLogin(login: string): Observable<User> {
    return this.http.get<User>(`${environment.apiUrl}/users/by-login/${login}`);
  }

  updateUser(userId: number, userData: Partial<User>): Observable<User> {
    return this.http.put<User>(`${this.apiUrl}/${userId}`, userData);
  }

  resetPassword(userId: number): Observable<User> {
    return this.http.post<User>(`${this.apiUrl}/${userId}/reset-password`, {});
  }

  restoreUser(userId: number): Observable<User> {
    return this.http.post<User>(`${this.apiUrl}/${userId}/restore`, {});
  }
}
