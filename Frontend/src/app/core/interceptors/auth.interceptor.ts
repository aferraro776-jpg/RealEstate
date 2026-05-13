import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { AuthService } from '../services/auth.service';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
    const auth  = inject(AuthService);
    const token = auth.token(); // signal → si legge con ()

    if (token) {
        return next(req.clone({
            setHeaders: { Authorization: `Bearer ${token}` }
        }));
    }

    return next(req);
};