import { CommonModule } from '@angular/common';
import { Component, Input, OnInit, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { CATEGORY_LABELS, LISTING_TYPE_LABELS, Property } from '../../core/models';
import { AuctionService, AuctionDto } from '../../core/services/auction.service';

@Component({
  selector: 'app-property-card',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './property-card.component.html',
  styleUrls: ['./property-card.component.css'],
})
export class PropertyCardComponent implements OnInit {
  @Input({ required: true }) property!: Property;
  private auc = inject(AuctionService);

  protected placeholder = 'https://placehold.co/600x450/eee/999?text=hw26';
  protected auction = signal<AuctionDto | null>(null);

  protected get categoryLabel(): string {
    return CATEGORY_LABELS[this.property.category] ?? this.property.category;
  }

  protected get listingLabel(): string {
    return LISTING_TYPE_LABELS[this.property.listingType] ?? this.property.listingType;
  }

  ngOnInit(): void {
    this.auc.getByPostId(this.property.id).subscribe({
      next: (a) => { if (!a.closed) this.auction.set(a); },
      error: () => {},
    });
  }

  formatPrice(n: number): string {
    return new Intl.NumberFormat('it-IT', {
      style: 'currency', currency: 'EUR', maximumFractionDigits: 0,
    }).format(n);
  }
}