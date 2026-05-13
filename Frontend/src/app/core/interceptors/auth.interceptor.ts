import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';
import { AuthService } from '../services/auth.service';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
    const auth   = inject(AuthService);
    const router = inject(Router);
    const token  = auth.token();

    const hasManualAuth = req.headers.has('Authorization');

    const authReq = token && !hasManualAuth
        ? req.clone({ setHeaders: { Authorization: `Bearer ${token}` } })
        : req;

    return next(authReq).pipe(
        catchError((err) => {
            if (err.status === 401 && !hasManualAuth) {
                auth.logout();
                router.navigate(['/login']);
            }
            return throwError(() => err);
        })
    );
};
