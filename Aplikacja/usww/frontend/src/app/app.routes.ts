import {Routes} from '@angular/router';
import {LoginComponent} from './auth/login/login.component';
import {ChangePasswordComponent} from './auth/change-password/change-password.component';
import {DashboardComponent} from './dashboard/dashboard.component';
import {LayoutComponent} from './shared/layout/layout.component';
import {authGuard} from './auth/guards/auth.guard';
import {CreateTicketComponent} from './tickets/create-ticket/create-ticket.component';
import {TicketListComponent} from './tickets/ticket-list/ticket-list.component';
import {TicketDetailComponent} from './tickets/ticket-detail/ticket-detail.component';
import {UserListComponent} from './admin/user-management/user-list/user-list.component';
import {UserFormComponent} from './admin/user-management/user-form/user-form.component';
import {DictionaryListComponent} from './admin/dictionaries/dictionary-list/dictionary-list.component';
import {DictionaryFormComponent} from './admin/dictionaries/dictionary-form/dictionary-form.component';
import {UserProfileComponent} from './shared/user-profile/user-profile.component';

export const routes: Routes = [
  {path: 'login', component: LoginComponent},
  {path: 'auth/change-password', component: ChangePasswordComponent},
  {
    path: '',
    component: LayoutComponent,
    canActivate: [authGuard],
    children: [
      {path: 'dashboard', component: DashboardComponent},
      {path: 'profile', component: UserProfileComponent},
      {path: 'profile/change-password', component: ChangePasswordComponent},
      {path: 'tickets', component: TicketListComponent},
      {path: 'tickets/new', component: CreateTicketComponent},
      {path: 'tickets/unassigned', component: TicketListComponent},
      {path: 'tickets/:id', component: TicketDetailComponent},
      {path: 'admin/users', component: UserListComponent},
      {path: 'admin/users', component: UserListComponent},
      {path: 'admin/users/new', component: UserFormComponent},
      {path: 'admin/users/:id/edit', component: UserFormComponent},
      {path: 'admin/dictionaries/:type', component: DictionaryListComponent},
      {path: 'admin/dictionaries/:type/new', component: DictionaryFormComponent},
      {path: 'admin/dictionaries/:type/:id/edit', component: DictionaryFormComponent},
      {path: '', redirectTo: '/dashboard', pathMatch: 'full'}
    ]
  },
  {path: '**', redirectTo: '/dashboard'}
];
