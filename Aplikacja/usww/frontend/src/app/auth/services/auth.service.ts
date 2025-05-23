import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {BehaviorSubject, catchError, Observable, of, tap, throwError} from 'rxjs';
import {environment} from '../../../environments/environment';

interface LoginResponse {
  token: string;
  userId: number;
  username?: string;
  requirePasswordChange?: boolean;
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
            const loginUsername = response.username || username;
            const initialUserData = {
              username: loginUsername,
              userId: response.userId,
              token: response.token
            };
            localStorage.setItem('currentUser', JSON.stringify(initialUserData));
            this.currentUserSubject.next(initialUserData);

            this.loadUserProfile().pipe(
              catchError(profileError => {
                console.error("Error loading user profile after login:", profileError);
                return of(null);
              })
            ).subscribe();
          }
        }),
        catchError(loginError => {
          console.error("Login HTTP request failed:", loginError);
          return throwError(() => loginError);
        })
      );
  }

  changePassword(userId: number, currentPassword: string, newPassword: string): Observable<PasswordChangeResponse> {
    return this.http.post<PasswordChangeResponse>(
      `${this.apiUrl}/change-password?userId=${userId}`,
      {currentPassword, newPassword}
    ).pipe(
      tap(response => {
        if (response.token) {
          const updatedUserData = {
            username: null,
            userId: userId,
            token: response.token
          };
          localStorage.setItem('currentUser', JSON.stringify(updatedUserData));
          this.currentUserSubject.next(updatedUserData);

          this.loadUserProfile().pipe(
            catchError(profileError => {
              console.error("Error loading user profile after password change:", profileError);
              return of(null);
            })
          ).subscribe();
        } else {
          console.warn("Password change response did not include a token.");
        }
      }),
      catchError(error => {
        return throwError(() => error);
      })
    );
  }

  logout(): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/logout`, {}).pipe(
      tap(() => {
        localStorage.removeItem('currentUser');
        this.currentUserSubject.next(null);
      }),
      catchError(error => {
        localStorage.removeItem('currentUser');
        this.currentUserSubject.next(null);
        return of(void 0);
      })
    );
  }

  isLoggedIn(): boolean {
    const user = this.currentUserValue;
    if (!user || !user.token) {
      return false;
    }

    try {
      const payload = JSON.parse(atob(user.token.split('.')[1]));
      const currentTime = Math.floor(Date.now() / 1000);
      return payload.exp > currentTime;
    } catch (error) {
      localStorage.removeItem('currentUser');
      this.currentUserSubject.next(null);
      return false;
    }
  }

  getToken(): string | null {
    return this.currentUserValue ? this.currentUserValue.token : null;
  }

  hasRole(roleName: string): boolean {
    const currentUser = this.currentUserValue;
    if (!currentUser) {
      return false;
    }

    if (currentUser.roles && Array.isArray(currentUser.roles)) {
      return currentUser.roles.includes(roleName);
    }

    if (currentUser.groupId !== undefined && currentUser.groupId !== null) {
      const roleMapping: Record<number, string> = {
        1: 'ADMIN',
        2: 'OPERATOR',
        3: 'STUDENT'
      };
      return roleMapping[currentUser.groupId] === roleName;
    }

    return false;
  }

  isAdmin(): boolean {
    return this.hasRole('ADMIN');
  }

  isOperator(): boolean {
    return this.hasRole('OPERATOR');
  }

  isStudent(): boolean {
    return this.hasRole('STUDENT');
  }

  private handleApiError(error: any): void {
    if (error.status === 401) {
      localStorage.removeItem('currentUser');
      this.currentUserSubject.next(null);
    }
  }

  loadUserProfile(): Observable<any> {
    return this.http.get<any>(`${environment.apiUrl}/users/profile`).pipe(
      tap(profile => {
        const correctUsername = profile.login || profile.username;
        const currentUser = this.currentUserValue;
        if (currentUser) {
          const updatedUser = {...currentUser, ...profile, username: correctUsername || currentUser.username};
          localStorage.setItem('currentUser', JSON.stringify(updatedUser));
          this.currentUserSubject.next(updatedUser);
        }
      }),
      catchError(error => {
        this.handleApiError(error);
        return throwError(() => new Error('Failed to load user profile'));
      })
    );
  }

  public hasPermission(resource: string, permission: string): boolean {
    const currentUser = this.currentUserValue;
    if (!currentUser) return false;

    if (this.isAdmin()) return true;

    if (resource === 'Ticket') {
      if (this.isOperator()) {
        return ['READ', 'WRITE', 'ASSIGN'].includes(permission);
      }
      if (this.isStudent()) {
        return ['READ', 'CREATE'].includes(permission);
      }
    }

    return false;
  }

  public canAccessResource(resourceId: number, resourceType: string): boolean {
    return this.isAdmin() ? true : this.hasPermission(resourceType, 'READ');
  }

  public canModifyResource(resourceId: number, resourceType: string): boolean {
    return this.isAdmin() ? true : this.hasPermission(resourceType, 'WRITE');
  }
}
