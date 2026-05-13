import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Review } from '../models';
import { environment } from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class ReviewService {
  private http = inject(HttpClient);
  private base = `${environment.apiUrl}/reviews`;

  list(propertyId: number): Observable<Review[]> {
    return this.http.get<Review[]>(`${this.base}/property/${propertyId}`);
  }

  create(r: Omit<Review, 'id' | 'createdAt'>): Observable<Review> {
    return this.http.post<Review>(this.base, r);
  }
}