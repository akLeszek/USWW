import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';

import { UserService } from '../../services/user.service';
import { DictionaryService, Dictionary } from '../../../shared/services/dictionary.service';

@Component({
  selector: 'app-user-form',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterModule
  ],
  templateUrl: './user-form.component.html',
  styleUrls: ['./user-form.component.scss']
})
export class UserFormComponent implements OnInit {
  userForm!: FormGroup;
  userGroups: Dictionary[] = [];
  organizationUnits: Dictionary[] = [];
  isEditMode = false;
  userId: number | null = null;
  loading = false;
  submitting = false;
  error = '';
  success = '';

  constructor(
    private fb: FormBuilder,
    private userService: UserService,
    private dictionaryService: DictionaryService,
    private route: ActivatedRoute,
    private router: Router
  ) {
    this.initForm();
  }

  initForm(): void {
    this.userForm = this.fb.group({
      login: ['', [
        Validators.required,
        Validators.minLength(3),
        Validators.maxLength(32),
        Validators.pattern(/^[a-zA-Z0-9_]+$/)
      ]],
      forename: ['', [
        Validators.required,
        Validators.maxLength(32),
        Validators.pattern(/^[a-zA-ZąćęłńóśźżĄĆĘŁŃÓŚŹŻ-]+$/)
      ]],
      surname: ['', [
        Validators.required,
        Validators.maxLength(64),
        Validators.pattern(/^[a-zA-ZąćęłńóśźżĄĆĘŁŃÓŚŹŻ-]+$/)
      ]],
      groupId: ['', Validators.required],
      organizationUnitId: [''],
      loginBan: [false]
    });

    // Nasłuchuj zmian w polu groupId
    this.userForm.get('groupId')?.valueChanges.subscribe(groupId => {
      this.updateOrganizationUnitValidation(groupId);
    });
  }

  updateOrganizationUnitValidation(groupId: number | string): void {
    if (!groupId) return;

    const group = this.userGroups.find(g => g.id === Number(groupId));
    if (!group) return;

    const organizationUnitField = this.userForm.get('organizationUnitId');

    if (group.requiresOrganizationUnit) {
      organizationUnitField?.setValidators([Validators.required]);
    } else {
      organizationUnitField?.clearValidators();
    }

    organizationUnitField?.updateValueAndValidity();
  }

  // Pomocnicza metoda do sprawdzania wymagalności jednostki
  isOrganizationUnitRequired(groupId: number | string): boolean {
    if (!groupId) return false;
    const group = this.userGroups.find(g => g.id === Number(groupId));
    return group?.requiresOrganizationUnit || false;
  }

  ngOnInit(): void {
    this.loadDictionaries();
  }

  loadDictionaries(): void {
    // Jednoczesne ładowanie słowników
    Promise.all([
      this.loadUserGroups(),
      this.loadOrganizationUnits()
    ]).then(() => {
      // Po załadowaniu słowników sprawdź, czy jesteśmy w trybie edycji
      this.route.paramMap.subscribe(params => {
        const id = params.get('id');
        if (id) {
          this.isEditMode = true;
          this.userId = +id;
          this.loadUserDetails();
        }
      });
    });
  }

  loadUserGroups(): Promise<void> {
    return new Promise((resolve, reject) => {
      this.dictionaryService.getUserGroups().subscribe({
        next: (groups) => {
          this.userGroups = groups;
          resolve();
        },
        error: (error) => {
          console.error('Błąd ładowania grup użytkowników', error);
          this.error = 'Nie udało się załadować grup użytkowników';
          reject(error);
        }
      });
    });
  }

  loadOrganizationUnits(): Promise<void> {
    return new Promise((resolve, reject) => {
      this.dictionaryService.getOrganizationUnits().subscribe({
        next: (units) => {
          this.organizationUnits = units;
          resolve();
        },
        error: (error) => {
          console.error('Błąd ładowania jednostek organizacyjnych', error);
          this.error = 'Nie udało się załadować jednostek organizacyjnych';
          reject(error);
        }
      });
    });
  }

  loadUserDetails(): void {
    if (!this.userId) return;

    this.loading = true;
    this.userService.getUserById(this.userId).subscribe({
      next: (user) => {
        this.userForm.patchValue({
          login: user.login,
          forename: user.forename,
          surname: user.surname,
          groupId: user.groupId,
          organizationUnitId: user.organizationUnitId,
          loginBan: user.loginBan
        });

        // Wyłącz edycję loginu w trybie edycji
        this.userForm.get('login')?.disable();

        // Odśwież walidację jednostki organizacyjnej
        this.updateOrganizationUnitValidation(user.groupId);

        this.loading = false;
      },
      error: (error) => {
        this.error = 'Nie udało się załadować danych użytkownika';
        this.loading = false;
      }
    });
  }

  onSubmit(): void {
    if (this.userForm.invalid) {
      this.markFormGroupTouched(this.userForm);
      return;
    }

    this.submitting = true;
    this.error = '';
    this.success = '';

    // Pobierz wartości formularza, uwzględniając stan disabled
    const userData = {
      ...this.userForm.getRawValue(),
      login: this.isEditMode ?
        this.userForm.get('login')?.value :
        this.userForm.getRawValue().login
    };

    if (this.isEditMode && this.userId) {
      this.userService.updateUser(this.userId, userData).subscribe({
        next: () => {
          this.success = 'Dane użytkownika zostały zaktualizowane';
          this.submitting = false;
          setTimeout(() => {
            this.router.navigate(['/admin/users']);
          }, 2000);
        },
        error: (error) => {
          this.error = error.error?.message || 'Nie udało się zaktualizować użytkownika';
          this.submitting = false;
        }
      });
    } else {
      this.userService.createUser(userData).subscribe({
        next: (response) => {
          this.success = `Użytkownik został utworzony. Login: ${response.login}, Domyślne hasło: ${response.login}123`;
          this.submitting = false;
          setTimeout(() => {
            this.router.navigate(['/admin/users']);
          }, 3000);
        },
        error: (error) => {
          this.error = error.error?.message || 'Nie udało się utworzyć użytkownika';
          this.submitting = false;
        }
      });
    }
  }

  cancelForm(): void {
    this.router.navigate(['/admin/users']);
  }

  // Funkcja do oznaczenia wszystkich pól formularza jako dotknięte
  markFormGroupTouched(formGroup: FormGroup) {
    Object.values(formGroup.controls).forEach(control => {
      control.markAsTouched();

      if (control instanceof FormGroup) {
        this.markFormGroupTouched(control);
      }
    });
  }

  // Metody do wyświetlania komunikatów o błędach
  getLoginErrorMessage(): string {
    const loginControl = this.userForm.get('login');
    if (loginControl?.errors) {
      if (loginControl.errors['required']) return 'Login jest wymagany';
      if (loginControl.errors['minlength']) return 'Login musi mieć minimum 3 znaki';
      if (loginControl.errors['maxlength']) return 'Login może mieć maksymalnie 32 znaki';
      if (loginControl.errors['pattern']) return 'Login może zawierać tylko litery, cyfry i podkreślenia';
    }
    return '';
  }

  getForenameErrorMessage(): string {
    const forenameControl = this.userForm.get('forename');
    if (forenameControl?.errors) {
      if (forenameControl.errors['required']) return 'Imię jest wymagane';
      if (forenameControl.errors['maxlength']) return 'Imię może mieć maksymalnie 32 znaki';
      if (forenameControl.errors['pattern']) return 'Imię może zawierać tylko litery i myślniki';
    }
    return '';
  }

  getSurnameErrorMessage(): string {
    const surnameControl = this.userForm.get('surname');
    if (surnameControl?.errors) {
      if (surnameControl.errors['required']) return 'Nazwisko jest wymagane';
      if (surnameControl.errors['maxlength']) return 'Nazwisko może mieć maksymalnie 64 znaki';
      if (surnameControl.errors['pattern']) return 'Nazwisko może zawierać tylko litery i myślniki';
    }
    return '';
  }
}
