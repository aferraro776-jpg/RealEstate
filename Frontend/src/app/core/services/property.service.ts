import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, switchMap, forkJoin, of } from 'rxjs';
import { map } from 'rxjs/operators';
import {
  Post, PostCreateDto,
  Property, RealEstateDto, PropertyFilters, RealEstateRequest,
} from '../models';
import { AuthService } from './auth.service';
import { environment } from '../../../environments/environment';

function toProperty(raw: any, re?: any): Property {
  let city = '';
  let address = re?.address ?? '';

  if (re?.address) {
    const parts = re.address.split(',');
    if (parts.length >= 2) {
      const secondPart = parts[1].trim();
      const cityMatch = secondPart.match(/^\d+\s+(.+?)\s*\(.*\)$/);
      city = cityMatch ? cityMatch[1].trim() : secondPart;
    }
  }

  return {
    id:           raw.id,
    code:         String(raw.id).padStart(5, '0'),
    title:        raw.title        ?? '',
    description:  raw.description  ?? '',
    category:     re?.type         ?? 'APARTMENT',
    listingType:  raw.listingType  ?? 'SALE',
    price:        raw.currentPrice ?? 0,
    oldPrice:     raw.previousPrice > 0 ? raw.previousPrice : null,
    squareMeters: re?.squareMetres  ?? 0,
    address:      address,
    city:         city,
    latitude:     re?.latit         ?? 0,
    longitude:    re?.longit        ?? 0,
    photos:       (raw.photoUrls ?? []).map((p: any) =>
        typeof p === 'string' ? p : p.url),
    sellerId:     raw.sellerId      ?? 0,
    realEstateId: raw.realEstateId  ?? null,
    sellerName:   raw.sellerName    ?? '',
    createdAt:    raw.createdAt     ?? '',
  };
}

@Injectable({ providedIn: 'root' })
export class PropertyService {
  private http   = inject(HttpClient);
  private auth   = inject(AuthService);
  private base   = `${environment.apiUrl}/posts`;
  private reBase = `${environment.apiUrl}/realestate`;

  private enrichPosts(posts: any[]): Observable<Property[]> {
    if (!posts.length) return of([]);
    return forkJoin(
        posts.map(post =>
            this.http.get<any>(`${this.reBase}/${post.realEstateId}`).pipe(
                map(re => toProperty(post, re))
            )
        )
    );
  }

  list(filters: PropertyFilters = {}): Observable<Property[]> {
    let params = new HttpParams();
    if (filters.sortBy)                  params = params.set('sortBy', filters.sortBy);
    if (filters.direction)               params = params.set('direction', filters.direction);
    if (filters.q)                       params = params.set('q', filters.q);
    if (filters.city)                    params = params.set('city', filters.city);
    if (filters.category)                params = params.set('category', filters.category);
    if (filters.listingType)             params = params.set('listingType', filters.listingType);
    if (filters.minPrice != null)        params = params.set('minPrice', filters.minPrice);
    if (filters.maxPrice != null)        params = params.set('maxPrice', filters.maxPrice);
    if (filters.minSquareMeters != null) params = params.set('minSquareMeters', filters.minSquareMeters);
    return this.http.get<any[]>(this.base, { params }).pipe(
        switchMap(posts => this.enrichPosts(posts))
    );
  }

  get(id: number): Observable<Property> {
    return this.http.get<any>(`${this.base}/${id}`).pipe(
        switchMap(post =>
            this.http.get<any>(`${this.reBase}/${post.realEstateId}`).pipe(
                map(re => toProperty(post, re))
            )
        )
    );
  }

  mine(): Observable<Property[]> {
    const user = this.auth.user();
    if (!user) return of([]);
    return this.http.get<any[]>(`${this.base}/seller/${user.id}`).pipe(
        switchMap(posts => this.enrichPosts(posts))
    );
  }

  getBySeller(sellerId: number): Observable<Property[]> {
    return this.http.get<any[]>(`${this.base}/seller/${sellerId}`).pipe(
        switchMap(posts => this.enrichPosts(posts))
    );
  }

  getRealEstate(id: number): Observable<RealEstateDto> {
    return this.http.get<RealEstateDto>(`${this.reBase}/${id}`);
  }

  updateRealEstate(id: number, data: RealEstateRequest): Observable<RealEstateDto> {
    return this.http.patch<RealEstateDto>(`${this.reBase}/${id}`, data);
  }

  createWithRealEstate(dto: PostCreateDto): Observable<Post> {
    return this.http.post<Post>(`${this.base}/with-realestate`, dto);
  }

  update(id: number, data: any): Observable<Property> {
    return this.http.put<any>(`${this.base}/${id}`, data).pipe(
        switchMap(post =>
            this.http.get<any>(`${this.reBase}/${post.realEstateId}`).pipe(
                map(re => toProperty(post, re))
            )
        )
    );
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/${id}`);
  }

  lowerPrice(id: number, newPrice: number): Observable<Property> {
    return this.http.patch<any>(`${this.base}/${id}/reduce-price`, { newPrice }).pipe(
        switchMap(post =>
            this.http.get<any>(`${this.reBase}/${post.realEstateId}`).pipe(
                map(re => toProperty(post, re))
            )
        )
    );
  }

  promoteOnFacebook(id: number): Observable<{ postUrl: string }> {
    return this.http.post<{ postUrl: string }>(`${this.base}/${id}/promote/facebook`, {});
  }
}