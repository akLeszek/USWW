import {Routes} from '@angular/router';
import {LoginComponent} from './auth/login/login.component';
import {ChangePasswordComponent} from './auth/change-password/change-password.component';
import {authGuard} from './auth/guards/auth.guard';

export const routes: Routes = [
  {path: 'login', component: LoginComponent},
  {
    path: 'auth/change-password',
    component: ChangePasswordComponent,
    canActivate: [authGuard]
  },
  {path: '', redirectTo: '/login', pathMatch: 'full'},
];
