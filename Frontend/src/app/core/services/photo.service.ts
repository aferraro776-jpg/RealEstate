import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface PhotoResponse {
    url: string;
}

@Injectable({ providedIn: 'root' })
export class PhotoService {
    private http = inject(HttpClient);
    private base = `${environment.apiUrl}/api/photos`;

    upload(file: File): Observable<PhotoResponse> {
        const fd = new FormData();
        fd.append('file', file);
        return this.http.post<PhotoResponse>(`${this.base}/upload`, fd);
    }
}