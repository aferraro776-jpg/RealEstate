import { CommonModule } from '@angular/common';
import { Component, OnInit, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { PropertyService } from '../../core/services/property.service';
import {
  CATEGORY_LABELS, Property, PropertyCategory,
  PropertyFilters, ListingType,
} from '../../core/models';
import { PropertyCardComponent } from '../../shared/components/property-card.component';

@Component({
  selector: 'app-property-list',
  standalone: true,
  imports: [CommonModule, FormsModule, PropertyCardComponent],
  templateUrl: './property-list.component.html',
  styleUrls: ['./property-list.component.css'],
})
export class PropertyListComponent implements OnInit {
  private svc   = inject(PropertyService);
  private route  = inject(ActivatedRoute);
  private router = inject(Router);

  protected items   = signal<Property[]>([]);
  protected loading = signal(true);
  protected error   = signal<string | null>(null);
  protected filters: PropertyFilters = { sort: 'newest' };

  protected categories: { value: PropertyCategory; label: string }[] = (
      Object.keys(CATEGORY_LABELS) as PropertyCategory[]
  ).map((v) => ({ value: v, label: CATEGORY_LABELS[v] }));

  ngOnInit(): void {
    const qp = this.route.snapshot.queryParamMap;
    this.filters = {
      q:              qp.get('q') ?? undefined,
      city:           qp.get('city') ?? undefined,
      category:       (qp.get('category') as PropertyCategory) ?? undefined,
      listingType:    (qp.get('listingType') as ListingType) ?? undefined,
      minPrice:       qp.get('minPrice') ? Number(qp.get('minPrice')) : undefined,
      maxPrice:       qp.get('maxPrice') ? Number(qp.get('maxPrice')) : undefined,
      minSquareMeters: qp.get('minSquareMeters') ? Number(qp.get('minSquareMeters')) : undefined,
      sort:           (qp.get('sort') as PropertyFilters['sort']) ?? 'newest',
    };
    this.reload();
  }

  reload(): void {
    this.loading.set(true);
    this.error.set(null);
    this.router.navigate([], {
      queryParams: this.filters as Record<string, unknown>,
      replaceUrl: true,
    });
    this.svc.list(this.filters).subscribe({
      next: (res) => { this.items.set(res); this.loading.set(false); },
      error: (err) => {
        this.error.set('Impossibile caricare gli annunci (' + (err?.message ?? 'errore') + ')');
        this.loading.set(false);
      },
    });
  }

  clear(): void {
    this.filters = { sort: 'newest' };
    this.reload();
  }
}