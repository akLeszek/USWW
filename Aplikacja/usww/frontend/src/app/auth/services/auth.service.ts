import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { environment } from '../../../environments/environment';

interface LoginResponse {
  token: string;
  requirePasswordChange?: boolean;
  userId?: number;
}

interface PasswordChangeRequest {
  currentPassword: string;
  newPassword: string;
}

interface PasswordChangeResponse {
  message: string;
  token: string;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private currentUserSubject: BehaviorSubject<any>;
  public currentUser: Observable<any>;
  private apiUrl = `${environment.apiUrl}/auth`;

  constructor(private http: HttpClient) {
    const storedUser = localStorage.getItem('currentUser');
    this.currentUserSubject = new BehaviorSubject<any>(storedUser ? JSON.parse(storedUser) : null);
    this.currentUser = this.currentUserSubject.asObservable();
  }

  public get currentUserValue(): any {
    return this.currentUserSubject.value;
  }

  login(username: string, password: string): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.apiUrl}/login`, {username, password})
      .pipe(
        tap(response => {
          if (!response.requirePasswordChange) {
            localStorage.setItem('currentUser', JSON.stringify({
              username,
              token: response.token
            }));
            this.currentUserSubject.next({username, token: response.token});
          }
        })
      );
  }

  changePassword(userId: number, currentPassword: string, newPassword: string): Observable<PasswordChangeResponse> {
    return this.http.post<PasswordChangeResponse>(
      `${this.apiUrl}/change-password?userId=${userId}`,
      {currentPassword, newPassword}
    ).pipe(
      tap(response => {
        const username = this.currentUserValue?.username;
        localStorage.setItem('currentUser', JSON.stringify({
          username,
          token: response.token
        }));
        this.currentUserSubject.next({username, token: response.token});
      })
    );
  }

  logout(): void {
    localStorage.removeItem('currentUser');
    this.currentUserSubject.next(null);
  }

  isLoggedIn(): boolean {
    return !!this.currentUserValue;
  }

  getToken(): string | null {
    return this.currentUserValue ? this.currentUserValue.token : null;
  }
}
