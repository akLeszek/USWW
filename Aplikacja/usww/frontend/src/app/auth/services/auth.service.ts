import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {BehaviorSubject, Observable, tap} from 'rxjs';
import {environment} from '../../../environments/environment';

interface LoginResponse {
  token: string;
  userId: number;
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
            localStorage.setItem('currentUser', JSON.stringify({
              username,
              userId: response.userId,
              token: response.token
            }));
            this.currentUserSubject.next({
              username,
              userId: response.userId,
              token: response.token
            });
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
        if (username && response.token) {
          localStorage.setItem('currentUser', JSON.stringify({
            username,
            userId: userId,
            token: response.token
          }));
          this.currentUserSubject.next({username, userId, token: response.token});
        }
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

  /**
   * Sprawdza czy zalogowany użytkownik ma podaną rolę
   * @param roleName Nazwa roli do sprawdzenia (ADMIN, OPERATOR, STUDENT)
   * @returns true jeśli użytkownik ma daną rolę, false w przeciwnym przypadku
   */
  hasRole(roleName: string): boolean {
    const currentUser = this.currentUserValue;

    if (!currentUser) {
      return false;
    }

    // Jeśli bezpośrednio odczytujemy role z tokena JWT
    if (currentUser.role) {
      return currentUser.role === roleName;
    }

    // Jeśli mamy tablicę ról w userDetails
    if (currentUser.roles && Array.isArray(currentUser.roles)) {
      return currentUser.roles.includes(roleName);
    }

    // Jeśli role są mapowane z groupId (zgodnie ze strukturą bazy)
    if (currentUser.groupId) {
      const roleMapping: Record<number, string> = {
        1: 'ADMIN',    // Administrator
        2: 'OPERATOR', // Operator (dziekanat)
        3: 'STUDENT'   // Student
      };

      return roleMapping[currentUser.groupId] === roleName;
    }

    // Jeśli nie mamy żadnej informacji o rolach, domyślnie zwracamy false
    return false;
  }

  /**
   * Aktualnie zalogowany użytkownik jest administratorem
   */
  isAdmin(): boolean {
    return this.hasRole('ADMIN');
  }

  /**
   * Aktualnie zalogowany użytkownik jest operatorem (pracownikiem dziekanatu)
   */
  isOperator(): boolean {
    return this.hasRole('OPERATOR');
  }

  /**
   * Aktualnie zalogowany użytkownik jest studentem
   */
  isStudent(): boolean {
    return this.hasRole('STUDENT');
  }

  /**
   * Ładuje profil użytkownika zawierający pełne dane z API
   */
  loadUserProfile(): Observable<any> {
    return this.http.get<any>(`${environment.apiUrl}/users/profile`).pipe(
      tap(profile => {
        // Aktualizacja danych użytkownika o pełny profil
        const currentUser = this.currentUserValue;
        if (currentUser) {
          const updatedUser = {
            ...currentUser,
            ...profile
          };
          localStorage.setItem('currentUser', JSON.stringify(updatedUser));
          this.currentUserSubject.next(updatedUser);
        }
      })
    );
  }
}
