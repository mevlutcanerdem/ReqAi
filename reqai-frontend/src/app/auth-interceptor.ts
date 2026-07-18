import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';
import { clearStoredToken, getStoredToken } from './utils/token.util';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const router = inject(Router);
  const token = getStoredToken();
  const isAuthRequest = req.url.includes('/api/v1/auth/');

  const authReq = token
    ? req.clone({
        setHeaders: {
          Authorization: `Bearer ${token}`
        }
      })
    : req;

  return next(authReq).pipe(
    catchError((error: HttpErrorResponse) => {
      if (!isAuthRequest && (error.status === 401 || error.status === 403)) {
        clearStoredToken();
        router.navigate(['/login'], {
          queryParams: { reason: 'session-expired' }
        });
      }
      return throwError(() => error);
    })
  );
};
