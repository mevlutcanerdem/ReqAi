import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { clearStoredToken, getStoredToken, isTokenExpired } from '../utils/token.util';

export const authGuard: CanActivateFn = () => {
  const router = inject(Router);
  const token = getStoredToken();

  if (!token) {
    router.navigate(['/login']);
    return false;
  }

  if (isTokenExpired(token)) {
    clearStoredToken();
    router.navigate(['/login'], {
      queryParams: { reason: 'session-expired' }
    });
    return false;
  }

  return true;
};
