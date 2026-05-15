import { CommonModule } from '@angular/common';
import { Component, OnInit, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { PropertyService } from '../../core/services/property.service';
import { AuctionService, AuctionDto } from '../../core/services/auction.service';
import { CATEGORY_LABELS, LISTING_TYPE_LABELS, Property } from '../../core/models';
import { AuctionModalComponent } from '../../shared/auction-modal/auction-modal.component';

@Component({
  selector: 'app-seller-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink, AuctionModalComponent],
  templateUrl: './seller-dashboard.component.html',
  styleUrls: ['./seller-dashboard.component.css'],
})
export class SellerDashboardComponent implements OnInit {
  private svc = inject(PropertyService);
  private auc = inject(AuctionService);

  protected items     = signal<Property[]>([]);
  protected loading   = signal(true);
  protected msg       = signal<string | null>(null);
  protected modalPost = signal<Property | null>(null);
  protected auctions  = signal<Map<number, AuctionDto>>(new Map());

  ngOnInit(): void { this.load(); }

  private load(): void {
    this.svc.mine().subscribe({
      next: (r) => {
        this.items.set(r);
        this.loading.set(false);
        r.forEach(p => this.loadAuction(p.id));
      },
      error: () => this.loading.set(false),
    });
  }

  private loadAuction(postId: number): void {
    this.auc.getByPostId(postId).subscribe({
      next: (a) => {
        this.auctions.update(m => {
          const next = new Map(m);
          next.set(postId, a);
          return next;
        });
      },
      error: () => {},
    });
  }

  getAuction(postId: number): AuctionDto | undefined {
    return this.auctions().get(postId);
  }

  catLabel(p: Property): string     { return CATEGORY_LABELS[p.category] ?? p.category; }
  listingLabel(p: Property): string { return LISTING_TYPE_LABELS[p.listingType] ?? p.listingType; }

  openAuctionModal(p: Property): void { this.modalPost.set(p); }
  closeAuctionModal(): void           { this.modalPost.set(null); }

  onAuctionCreated(a: AuctionDto): void {
    this.auctions.update(m => {
      const next = new Map(m);
      next.set(a.postId, a);
      return next;
    });
    this.msg.set('Asta creata con successo.');
    setTimeout(() => this.msg.set(null), 3000);
  }

  onAuctionDeleted(postId: number): void {
    this.auctions.update(m => {
      const next = new Map(m);
      next.delete(postId);
      return next;
    });
    this.msg.set('Asta eliminata.');
    setTimeout(() => this.msg.set(null), 3000);
  }

  remove(p: Property): void {
    if (!confirm(`Eliminare "${p.title}"?`)) return;
    this.svc.delete(p.id).subscribe({
      next: () => {
        this.items.update(list => list.filter(item => item.id !== p.id));
        this.msg.set('Annuncio eliminato.');
        setTimeout(() => this.msg.set(null), 3000);
      },
      error: () => this.msg.set('Errore durante l\'eliminazione.'),
    });
  }

  lower(p: Property): void {
    const v = prompt(`Nuovo prezzo per "${p.title}" (attuale: ${p.price} €)`, String(p.price - 1000));
    if (!v) return;
    const n = Number(v);
    if (!n || n >= p.price) { this.msg.set('Il nuovo prezzo deve essere inferiore al precedente.'); return; }
    this.svc.lowerPrice(p.id, n).subscribe({ next: () => this.load() });
  }

  formatPrice(n: number): string {
    return new Intl.NumberFormat('it-IT', { style: 'currency', currency: 'EUR', maximumFractionDigits: 0 }).format(n);
  }
}