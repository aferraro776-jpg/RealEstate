import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Auction, Bid } from '../models';

@Injectable({ providedIn: 'root' })
export class AuctionService {
  private http = inject(HttpClient);
  private base = environment.apiUrl;

  byProperty(propertyId: number): Observable<Auction | null> {
    return this.http.get<Auction | null>(`${this.base}/properties/${propertyId}/auction`);
  }

  create(propertyId: number, payload: { startPrice: number; endsAt: string }): Observable<Auction> {
    return this.http.post<Auction>(`${this.base}/properties/${propertyId}/auction`, payload);
  }

  placeBid(auctionId: number, amount: number): Observable<Bid> {
    return this.http.post<Bid>(`${this.base}/auctions/${auctionId}/bids`, { amount });
  }

  bids(auctionId: number): Observable<Bid[]> {
    return this.http.get<Bid[]>(`${this.base}/auctions/${auctionId}/bids`);
  }

  close(auctionId: number): Observable<Auction> {
    return this.http.post<Auction>(`${this.base}/auctions/${auctionId}/close`, {});
  }
}