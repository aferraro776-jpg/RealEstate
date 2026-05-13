import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Property, PropertyFilters } from '../models';
import { environment } from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class PropertyService {
  private http = inject(HttpClient);
  private base = `${environment.apiUrl}/posts`;

  list(filters: PropertyFilters = {}): Observable<Property[]> {
    let params = new HttpParams();
    if (filters.q)           params = params.set('q', filters.q);
    if (filters.category)    params = params.set('category', filters.category);
    if (filters.listingType) params = params.set('listingType', filters.listingType);
    if (filters.minPrice)    params = params.set('minPrice', filters.minPrice);
    if (filters.maxPrice)    params = params.set('maxPrice', filters.maxPrice);
    if (filters.sort)        params = params.set('sort', filters.sort);
    if (filters.city)        params = params.set('city', filters.city);
    if (filters.minSquareMeters) params = params.set('minSquareMeters', filters.minSquareMeters);
    return this.http.get<Property[]>(this.base, { params });
  }

  get(id: number): Observable<Property> {
    return this.http.get<Property>(`${this.base}/${id}`);
  }

  create(p: Partial<Property>): Observable<Property> {
    return this.http.post<Property>(this.base, p);
  }

  update(id: number, p: Partial<Property>): Observable<Property> {
    return this.http.put<Property>(`${this.base}/${id}`, p);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/${id}`);
  }

  mine(): Observable<Property[]> {
    return this.http.get<Property[]>(`${this.base}/mine`);
  }

  lowerPrice(id: number, newPrice: number): Observable<Property> {
    return this.http.patch<Property>(`${this.base}/${id}/reduce-price`, { price: newPrice });
  }

  promoteOnFacebook(id: number): Observable<{ postUrl: string }> {
    return this.http.post<{ postUrl: string }>(`${this.base}/${id}/promote`, {});
  }
}