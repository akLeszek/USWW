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
  private adminApiUrl = `${environment.apiUrl}/admin/users`;
  private publicApiUrl = `${environment.apiUrl}/users`;

  constructor(private http: HttpClient, private authService: AuthService) {
  }

  getAllUsers(): Observable<User[]> {
    return this.http.get<User[]>(this.adminApiUrl);
  }

  blockUser(userId: number): Observable<User> {
    return this.http.post<User>(`${this.adminApiUrl}/${userId}/block`, {});
  }

  unblockUser(userId: number): Observable<User> {
    return this.http.post<User>(`${this.adminApiUrl}/${userId}/unblock`, {});
  }

  archiveUser(userId: number): Observable<User> {
    return this.http.post<User>(`${this.adminApiUrl}/${userId}/archive`, {});
  }

  createUser(userData: Partial<User>): Observable<User> {
    return this.http.post<User>(this.adminApiUrl, userData);
  }

  getUserById(userId: number): Observable<User> {
    if (this.authService.isAdmin()) {
      return this.http.get<User>(`${this.adminApiUrl}/${userId}`);
    } else {
      return this.http.get<User>(`${this.publicApiUrl}/${userId}`);
    }
  }

  getPublicUserById(userId: number): Observable<User> {
    return this.http.get<User>(`${this.publicApiUrl}/${userId}`);
  }

  getUserByLogin(login: string): Observable<User> {
    return this.http.get<User>(`${this.publicApiUrl}/by-login/${login}`);
  }

  updateUser(userId: number, userData: Partial<User>): Observable<User> {
    return this.http.put<User>(`${this.adminApiUrl}/${userId}`, userData);
  }

  resetPassword(userId: number): Observable<User> {
    return this.http.post<User>(`${this.adminApiUrl}/${userId}/reset-password`, {});
  }

  restoreUser(userId: number): Observable<User> {
    return this.http.post<User>(`${this.adminApiUrl}/${userId}/restore`, {});
  }
}
