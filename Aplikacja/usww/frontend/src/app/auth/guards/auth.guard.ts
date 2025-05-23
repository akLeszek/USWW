import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { map, catchError } from 'rxjs/operators';
import { of } from 'rxjs';

export const authGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  if (!authService.isLoggedIn()) {
    router.navigate(['/login']);
    return false;
  }

  return authService.loadUserProfile().pipe(
    map(() => true),
    catchError(error => {
      console.error('Auth guard - profile load failed:', error);
      authService.logout().subscribe({
        complete: () => router.navigate(['/login'])
      });
      return of(false);
    })
  );
};
