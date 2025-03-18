import { Routes } from '@angular/router';
import { LoginComponent } from './auth/login/login.component';
import { ChangePasswordComponent } from './auth/change-password/change-password.component';
import { DashboardComponent } from './dashboard/dashboard.component';
import { LayoutComponent } from './shared/layout/layout.component';
import { authGuard } from './auth/guards/auth.guard';
import { CreateTicketComponent } from './tickets/create-ticket/create-ticket.component';
import { TicketListComponent } from './tickets/ticket-list/ticket-list.component';
import { TicketDetailComponent } from './tickets/ticket-detail/ticket-detail.component';
import { UserListComponent } from './admin/user-management/user-list/user-list.component';
import { UserFormComponent } from './admin/user-management/user-form/user-form.component';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  {
    path: 'auth/change-password',
    component: ChangePasswordComponent
  },
  {
    path: '',
    component: LayoutComponent,
    canActivate: [authGuard],
    children: [
      { path: 'dashboard', component: DashboardComponent },
      { path: 'tickets', component: TicketListComponent },
      { path: 'tickets/new', component: CreateTicketComponent },
      { path: 'tickets/:id', component: TicketDetailComponent },
      { path: 'admin/users', component: UserListComponent },
      { path: 'admin/users/new', component: UserFormComponent },
      { path: 'admin/users/:id/edit', component: UserFormComponent },
      { path: '', redirectTo: '/dashboard', pathMatch: 'full' }
    ]
  },
  { path: '**', redirectTo: '/dashboard' }
];
