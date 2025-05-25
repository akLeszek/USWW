import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import {ActivatedRoute, Router, RouterLink} from '@angular/router';
import { AuthService } from '../services/auth.service';
import { NgClass, NgIf } from '@angular/common';

@Component({
  selector: 'app-change-password',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    NgClass,
    NgIf,
    RouterLink
  ],
  templateUrl: './change-password.component.html',
  styleUrls: ['./change-password.component.scss']
})
export class ChangePasswordComponent implements OnInit {
  passwordForm: FormGroup;
  userId: number | null = null;
  errorMessage: string = '';
  successMessage: string = '';
  loading: boolean = false;
  isFirstLogin: boolean = false;
  isProfileContext: boolean = false;

  constructor(
    private formBuilder: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private authService: AuthService
  ) {
    this.passwordForm = this.formBuilder.group({
      currentPassword: ['', [Validators.required]],
      newPassword: ['', [Validators.required, Validators.minLength(6)]],
      confirmPassword: ['', [Validators.required]]
    }, {
      validators: this.passwordMatchValidator
    });
  }

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      this.userId = params['userId'] ? Number(params['userId']) : null;
      this.isFirstLogin = !!this.userId;
      this.isProfileContext = !this.isFirstLogin;

      if (this.isProfileContext) {
        const currentUser = this.authService.currentUserValue;
        if (currentUser) {
          this.userId = currentUser.userId;
        } else {
          this.router.navigate(['/login']);
        }
      } else if (!this.userId) {
        this.router.navigate(['/login']);
      }
    });
  }

  passwordMatchValidator(form: FormGroup) {
    const newPassword = form.get('newPassword')?.value;
    const confirmPassword = form.get('confirmPassword')?.value;

    return newPassword === confirmPassword ? null : { passwordMismatch: true };
  }

  onSubmit(): void {
    if (this.passwordForm.invalid || !this.userId) {
      return;
    }

    this.loading = true;
    this.errorMessage = '';
    this.successMessage = '';

    const { currentPassword, newPassword } = this.passwordForm.value;

    this.authService.changePassword(this.userId, currentPassword, newPassword).subscribe({
      next: (response) => {
        this.successMessage = 'Hasło zostało zmienione pomyślnie. Za chwilę zostaniesz przekierowany.';

        setTimeout(() => {
          if (this.isFirstLogin) {
            this.router.navigate(['/dashboard']);
          } else {
            this.router.navigate(['/profile']);
          }
        }, 2000);
      },
      error: (error) => {
        this.errorMessage = error.error?.message || 'Wystąpił błąd podczas zmiany hasła.';
        this.loading = false;
      },
      complete: () => {
        this.loading = false;
      }
    });
  }

  getTitle(): string {
    return this.isFirstLogin ? 'Zmiana hasła' : 'Zmiana hasła';
  }

  getInfoMessage(): string {
    return this.isFirstLogin
      ? 'Wymagana jest zmiana hasła przy pierwszym logowaniu. Nowe hasło musi mieć co najmniej 6 znaków.'
      : 'Wprowadź aktualne hasło oraz nowe hasło. Nowe hasło musi mieć co najmniej 6 znaków.';
  }

  getBackRoute(): string {
    return this.isFirstLogin ? '/login' : '/profile';
  }
}
