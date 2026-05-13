import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { User, Role } from '../models';
import { environment } from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class UserService {
  private http = inject(HttpClient);
  private base = `${environment.apiUrl}/admin/users`;

  list(): Observable<User[]> {
    return this.http.get<User[]>(this.base);
  }

  ban(id: number, banned: boolean): Observable<User> {
    return this.http.patch<User>(`${this.base}/${id}/ban`, { banned });
  }

  setRole(id: number, role: Role): Observable<User> {
    return this.http.patch<User>(`${this.base}/${id}/role`, { role });
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/${id}`);
  }
}