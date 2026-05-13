import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface ContactRequest {
    senderName:    string;
    senderSurname: string;
    message:       string;
    postId:        number;
    postTitle:     string;
}

@Injectable({ providedIn: 'root' })
export class ContactService {
    private http = inject(HttpClient);
    private base = `${environment.apiUrl}/contact`;

    send(req: ContactRequest): Observable<string> {
        return this.http.post<string>(this.base, req, { responseType: 'text' as 'json' });
    }
}