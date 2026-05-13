import { CommonModule } from '@angular/common';
import { Component, OnInit, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { PropertyService } from '../../core/services/property.service';
import { AuctionService } from '../../core/services/auction.service';
import { CATEGORY_LABELS, LISTING_TYPE_LABELS, Property } from '../../core/models';

@Component({
  selector: 'app-seller-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './seller-dashboard.component.html',
  styleUrls: ['./seller-dashboard.component.css'],
})
export class SellerDashboardComponent implements OnInit {
  private svc = inject(PropertyService);
  private auc = inject(AuctionService);

  protected items   = signal<Property[]>([]);
  protected loading = signal(true);
  protected msg     = signal<string | null>(null);

  ngOnInit(): void { this.load(); }

  private load(): void {
    this.svc.mine().subscribe({
      next: (r) => { this.items.set(r); this.loading.set(false); },
      error: ()  => this.loading.set(false),
    });
  }

  catLabel(p: Property): string     { return CATEGORY_LABELS[p.category] ?? p.category; }
  listingLabel(p: Property): string { return LISTING_TYPE_LABELS[p.listingType] ?? p.listingType; }

  remove(p: Property): void {
    if (!confirm(`Eliminare "${p.title}"?`)) return;
    this.svc.delete(p.id).subscribe({ next: () => this.load() });
  }

  lower(p: Property): void {
    const v = prompt(`Nuovo prezzo per "${p.title}" (attuale: ${p.price} €)`, String(p.price - 1000));
    if (!v) return;
    const n = Number(v);
    if (!n || n >= p.price) { this.msg.set('Il nuovo prezzo deve essere inferiore al precedente.'); return; }
    this.svc.lowerPrice(p.id, n).subscribe({ next: () => this.load() });
  }

  createAuction(p: Property): void {
    const start = prompt('Prezzo di partenza (€)', String(p.price));
    if (!start) return;
    const days = prompt('Durata in giorni', '7');
    if (!days) return;
    const endsAt = new Date(Date.now() + Number(days) * 86400000).toISOString();
    this.auc.create(p.id, { startPrice: Number(start), endsAt }).subscribe({
      next:  () => this.msg.set('Asta creata.'),
      error: () => this.msg.set('Errore creazione asta.'),
    });
  }

  promote(p: Property): void {
    this.svc.promoteOnFacebook(p.id).subscribe({
      next:  (r) => this.msg.set('Pubblicato su Facebook: ' + r.postUrl),
      error: ()  => this.msg.set('Promozione non riuscita. Configura le credenziali Facebook lato backend.'),
    });
  }
}