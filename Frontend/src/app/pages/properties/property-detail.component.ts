import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute } from '@angular/router';
import { PropertyService } from '../../core/services/property.service';
import { ReviewService } from '../../core/services/review.service';
import { Property, Review, CATEGORY_LABELS, LISTING_TYPE_LABELS } from '../../core/models';

@Component({
  selector: 'app-property-detail',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './property-detail.component.html',
  styleUrls: ['./property-detail.component.css'],
})
export class PropertyDetailComponent implements OnInit {
  private svc    = inject(PropertyService);
  private revSvc = inject(ReviewService);
  private route  = inject(ActivatedRoute);

  property  = signal<Property | null>(null);
  reviews   = signal<Review[]>([]);
  loading   = signal(true);
  placeholder = 'https://placehold.co/900x420/eee/999?text=hw26';

  catLabel     = () => { const p = this.property(); return p ? CATEGORY_LABELS[p.category] : ''; };
  listingLabel = () => { const p = this.property(); return p ? LISTING_TYPE_LABELS[p.listingType] : ''; };

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.svc.get(id).subscribe({
      next: (p) => { this.property.set(p); this.loading.set(false); },
      error: ()  => { this.property.set(null); this.loading.set(false); },
    });
    this.revSvc.list(id).subscribe({
      next: (r) => this.reviews.set(r),
    });
  }
}