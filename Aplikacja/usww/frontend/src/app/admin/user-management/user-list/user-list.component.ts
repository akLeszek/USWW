import {Component, OnInit} from '@angular/core';
import {CommonModule} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {RouterModule} from '@angular/router';
import {NgbModule} from '@ng-bootstrap/ng-bootstrap';

import {UserService} from '../../services/user.service';
import {AuthService} from '../../../auth/services/auth.service';
import {Dictionary, DictionaryService} from '../../../shared/services/dictionary.service';
import {HasRoleDirective} from '../../../shared/directives/permission.directive';

interface User {
  id: number;
  login: string;
  forename: string;
  surname: string;
  groupId: number;
  organizationUnitId: number;
  loginBan: boolean;
  archive: boolean;
  lastLogin?: string;
  generatedPassword?: string;
}

@Component({
  selector: 'app-user-list',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    RouterModule,
    NgbModule,
    HasRoleDirective
  ],
  templateUrl: './user-list.component.html',
  styleUrls: ['./user-list.component.scss']
})
export class UserListComponent implements OnInit {
  users: User[] = [];
  filteredUsers: User[] = [];
  userGroups: Dictionary[] = [];
  organizationUnits: Dictionary[] = [];

  loading = true;
  processingUserId: number | null = null;
  error = '';
  success = '';

  filterLogin = '';
  filterGroup = '';
  filterOrganizationUnit = '';

  page = 1;
  pageSize = 10;
  collectionSize = 0;

  Math = Math;

  constructor(
    private userService: UserService,
    private dictionaryService: DictionaryService,
    public authService: AuthService
  ) {
  }

  ngOnInit(): void {
    this.loadUsers();
    this.loadUserGroups();
    this.loadOrganizationUnits();
  }

  loadUsers(): void {
    this.loading = true;
    this.userService.getAllUsers().subscribe({
      next: (users) => {
        this.users = users;
        this.applyFilters();
        this.loading = false;
      },
      error: (error) => {
        this.error = 'Nie udało się załadować użytkowników';
        this.loading = false;
      }
    });
  }

  loadUserGroups(): void {
    this.dictionaryService.getUserGroups().subscribe({
      next: (groups) => {
        this.userGroups = groups;
      },
      error: (error) => {
        console.error('Błąd ładowania grup użytkowników', error);
      }
    });
  }

  loadOrganizationUnits(): void {
    this.dictionaryService.getOrganizationUnits().subscribe({
      next: (units) => {
        this.organizationUnits = units;
      },
      error: (error) => {
        console.error('Błąd ładowania jednostek organizacyjnych', error);
      }
    });
  }

  applyFilters(): void {
    this.filteredUsers = this.users.filter(user => {
      const loginMatch = !this.filterLogin ||
        user.login.toLowerCase().includes(this.filterLogin.toLowerCase());

      const groupMatch = !this.filterGroup ||
        user.groupId === parseInt(this.filterGroup);

      const organizationUnitMatch = !this.filterOrganizationUnit ||
        user.organizationUnitId === parseInt(this.filterOrganizationUnit);

      return loginMatch && groupMatch && organizationUnitMatch;
    });

    this.collectionSize = this.filteredUsers.length;
  }

  resetFilters(): void {
    this.filterLogin = '';
    this.filterGroup = '';
    this.filterOrganizationUnit = '';
    this.applyFilters();
  }

  getUserGroupName(groupId: number): string {
    const group = this.userGroups.find(g => g.id === groupId);
    return group ? group.name : 'Nieznana grupa';
  }

  getOrganizationUnitName(unitId: number): string {
    const unit = this.organizationUnits.find(u => u.id === unitId);
    return unit ? unit.name : 'Nieznana jednostka';
  }

  blockUser(userId: number): void {
    this.error = '';
    this.success = '';
    this.processingUserId = userId;

    this.userService.blockUser(userId).subscribe({
      next: () => {
        const user = this.users.find(u => u.id === userId);
        if (user) user.loginBan = true;
        this.success = 'Użytkownik został zablokowany pomyślnie';
        this.processingUserId = null;
      },
      error: (error) => {
        this.error = 'Nie udało się zablokować użytkownika';
        this.processingUserId = null;
      }
    });
  }

  unblockUser(userId: number): void {
    this.error = '';
    this.success = '';
    this.processingUserId = userId;

    this.userService.unblockUser(userId).subscribe({
      next: () => {
        const user = this.users.find(u => u.id === userId);
        if (user) user.loginBan = false;
        this.success = 'Użytkownik został odblokowany pomyślnie';
        this.processingUserId = null;
      },
      error: (error) => {
        this.error = 'Nie udało się odblokować użytkownika';
        this.processingUserId = null;
      }
    });
  }

  canCreateUser(): boolean {
    return this.authService.isAdmin();
  }

  canEditUser(user: User): boolean {
    return this.authService.isAdmin() ||
      (this.authService.currentUserValue?.userId === user.id);
  }

  canBlockUser(user: User): boolean {
    if (!this.authService.isAdmin()) return false;
    return user.id !== this.authService.currentUserValue?.userId;
  }

  canUnblockUser(user: User): boolean {
    return this.authService.isAdmin();
  }

  canResetPassword(user: User): boolean {
    return this.authService.isAdmin();
  }

  canArchiveUser(user: User): boolean {
    return this.authService.isAdmin() && user.id !== this.authService.currentUserValue?.userId;
  }

  resetPassword(userId: number): void {
    this.error = '';
    this.success = '';
    this.processingUserId = userId;

    this.userService.resetPassword(userId).subscribe({
      next: (response: User) => {
        this.success = `Hasło zostało zresetowane. Nowe hasło: ${response.generatedPassword}`;
        this.processingUserId = null;
      },
      error: (error: unknown) => {
        this.error = 'Nie udało się zresetować hasła użytkownika';
        this.processingUserId = null;
      }
    });
  }

  archiveUser(userId: number): void {
    this.error = '';
    this.success = '';
    this.processingUserId = userId;

    this.userService.archiveUser(userId).subscribe({
      next: () => {
        const user = this.users.find(u => u.id === userId);
        if (user) {
          user.archive = true;
          user.loginBan = true;
        }
        this.success = 'Użytkownik został zarchiwizowany pomyślnie';
        this.processingUserId = null;
      },
      error: (error) => {
        this.error = 'Nie udało się zarchiwizować użytkownika';
        this.processingUserId = null;
      }
    });
  }

  get currentPageUsers() {
    const startIndex = (this.page - 1) * this.pageSize;
    return this.filteredUsers.slice(startIndex, startIndex + this.pageSize);
  }

  restoreUser(userId: number): void {
    this.error = '';
    this.success = '';
    this.processingUserId = userId;

    this.userService.restoreUser(userId).subscribe({
      next: (response: User) => {
        const user = this.users.find(u => u.id === userId);
        if (user) {
          user.archive = false;
        }
        this.success = 'Użytkownik został przywrócony pomyślnie';
        this.processingUserId = null;
      },
      error: (error: unknown) => {
        this.error = 'Nie udało się przywrócić użytkownika';
        this.processingUserId = null;
      }
    });
  }
}
