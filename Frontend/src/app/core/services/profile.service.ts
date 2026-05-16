import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { environment } from '../../../environments/environment';
import { AuthService } from './auth.service';

export interface ProfileDto {
    id:           number;
    name:         string;
    surname:      string;
    email:        string;
    birthDate:    string | null;
    authProvider: string | null;
    role:         string;
}

export interface ProfileUpdateRequest {
    name?:      string;
    surname?:   string;
    email?:     string;
    birthDate?: string | null;
}

@Injectable({ providedIn: 'root' })
export class ProfileService {
    private http    = inject(HttpClient);
    private authSvc = inject(AuthService);

    private get base(): string {
        return this.authSvc.hasRole('SELLER')
            ? `${environment.apiUrl}/seller`
            : `${environment.apiUrl}/user`;
    }

    get(): Observable<ProfileDto> {
        return this.http.get<any>(`${this.base}/me`).pipe(
            map(data => ({
                ...data,
                authProvider: data.authProvider ?? 'LOCAL',
                birthDate: data.birthDate
                    ? new Date(data.birthDate).toISOString().split('T')[0]
                    : null,
            } as ProfileDto))
        );
    }

    update(req: ProfileUpdateRequest): Observable<string> {
        return this.http.put<string>(`${this.base}/me`, req, { responseType: 'text' as 'json' });
    }

    changePassword(oldPassword: string, newPassword: string): Observable<string> {
        return this.http.put<string>(
            `${this.base}/me/password`,
            { oldPassword, newPassword },
            { responseType: 'text' as 'json' }
        );
    }

    deleteAccount(): Observable<string> {
        return this.http.delete<string>(`${this.base}/me`, { responseType: 'text' as 'json' });
    }
}