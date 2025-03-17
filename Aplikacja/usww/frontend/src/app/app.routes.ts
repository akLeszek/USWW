import {Routes} from '@angular/router';
import {LoginComponent} from './auth/login/login.component';
import {ChangePasswordComponent} from './auth/change-password/change-password.component';
import {DashboardComponent} from './dashboard/dashboard.component';
import {LayoutComponent} from './shared/layout/layout.component';
import {authGuard} from './auth/guards/auth.guard';
import {CreateTicketComponent} from './tickets/create-ticket/create-ticket.component';

export const routes: Routes = [
  {path: 'login', component: LoginComponent},
  {
    path: 'auth/change-password',
    component: ChangePasswordComponent
  },
  {
    path: '',
    component: LayoutComponent,
    canActivate: [authGuard],
    children: [
      {path: 'dashboard', component: DashboardComponent},
      {path: 'tickets/new', component: CreateTicketComponent},
      {path: '', redirectTo: '/dashboard', pathMatch: 'full'}
    ]
  },
  {path: '**', redirectTo: '/dashboard'}
];
