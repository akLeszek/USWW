import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { AuthService } from '../../auth/services/auth.service';
import { DictionaryService, Dictionary } from '../services/dictionary.service';

interface UserProfile {
  id: number;
  login: string;
  forename: string;
  surname: string;
  groupId: number;
  organizationUnitId?: number;
  lastLogin?: string;
  firstLogin: boolean;
  loginBan: boolean;
  archive: boolean;
}

@Component({
  selector: 'app-user-profile',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule
  ],
  templateUrl: './user-profile.component.html',
  styleUrls: ['./user-profile.component.scss']
})
export class UserProfileComponent implements OnInit {
  @Output() closeProfile = new EventEmitter<void>();

  userProfile: UserProfile | null = null;
  userGroups: Dictionary[] = [];
  organizationUnits: Dictionary[] = [];

  passwordForm: FormGroup;
  showPasswordForm = false;

  loading = true;
  passwordChanging = false;
  error = '';
  success = '';
  passwordError = '';
  passwordSuccess = '';

  constructor(
    private authService: AuthService,
    private dictionaryService: DictionaryService,
    private formBuilder: FormBuilder
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
    this.loadUserProfile();
    this.loadDictionaries();
  }

  passwordMatchValidator(form: FormGroup) {
    const newPassword = form.get('newPassword')?.value;
    const confirmPassword = form.get('confirmPassword')?.value;
    return newPassword === confirmPassword ? null : { passwordMismatch: true };
  }

  loadUserProfile(): void {
    this.loading = true;
    this.authService.loadUserProfile().subscribe({
      next: () => {
        this.userProfile = this.authService.currentUserValue;
        this.loading = false;
      },
      error: (error) => {
        this.error = 'Nie udało się załadować profilu użytkownika';
        this.loading = false;
      }
    });
  }

  loadDictionaries(): void {
    this.dictionaryService.getUserGroups().subscribe({
      next: (groups) => {
        this.userGroups = groups;
      },
      error: (error) => {
        console.error('Error loading user groups:', error);
      }
    });

    this.dictionaryService.getOrganizationUnits().subscribe({
      next: (units) => {
        this.organizationUnits = units;
      },
      error: (error) => {
        console.error('Error loading organization units:', error);
      }
    });
  }

  togglePasswordForm(): void {
    this.showPasswordForm = !this.showPasswordForm;
    if (!this.showPasswordForm) {
      this.passwordForm.reset();
      this.passwordError = '';
      this.passwordSuccess = '';
    }
  }

  onPasswordSubmit(): void {
    if (this.passwordForm.invalid || !this.userProfile) {
      return;
    }

    this.passwordChanging = true;
    this.passwordError = '';
    this.passwordSuccess = '';

    const { currentPassword, newPassword } = this.passwordForm.value;

    this.authService.changePassword(this.userProfile.id, currentPassword, newPassword).subscribe({
      next: () => {
        this.passwordSuccess = 'Hasło zostało zmienione pomyślnie';
        this.passwordForm.reset();
        this.passwordChanging = false;
        setTimeout(() => {
          this.togglePasswordForm();
        }, 2000);
      },
      error: (error) => {
        this.passwordError = error.error?.message || 'Wystąpił błąd podczas zmiany hasła';
        this.passwordChanging = false;
      }
    });
  }

  getUserGroupName(): string {
    if (!this.userProfile?.groupId) return 'Nieznana grupa';
    const group = this.userGroups.find(g => g.id === this.userProfile!.groupId);
    return group ? group.name : 'Nieznana grupa';
  }

  getOrganizationUnitName(): string {
    if (!this.userProfile?.organizationUnitId) return 'Brak';
    const unit = this.organizationUnits.find(u => u.id === this.userProfile!.organizationUnitId);
    return unit ? unit.name : 'Nieznana jednostka';
  }

  formatDate(dateString?: string): string {
    if (!dateString) return 'Nigdy';
    const date = new Date(dateString);
    return date.toLocaleDateString('pl-PL') + ' ' + date.toLocaleTimeString('pl-PL', {
      hour: '2-digit',
      minute: '2-digit'
    });
  }

  closeProfilePanel(): void {
    this.closeProfile.emit();
  }
}
