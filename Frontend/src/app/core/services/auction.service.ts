import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface AuctionDto {
  id: number;
  postId: number;
  startingPrice: number;
  currentBest: number;
  endDate: string;
  closed: boolean;
  winnerId: number | null;
}

export interface AuctionCreateRequest {
  postId: number;
  startingPrice: number;
  durationDays: number;
}

@Injectable({ providedIn: 'root' })
export class AuctionService {
  private http = inject(HttpClient);
  private base = 'http://localhost:8080/api/auctions';

  create(req: AuctionCreateRequest): Observable<AuctionDto> {
    return this.http.post<AuctionDto>(this.base, req);
  }

  getByPostId(postId: number): Observable<AuctionDto> {
    return this.http.get<AuctionDto>(`${this.base}/post/${postId}`);
  }

  delete(auctionId: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/${auctionId}`);
  }
}