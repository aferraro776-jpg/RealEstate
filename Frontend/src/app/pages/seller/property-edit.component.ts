import { CommonModule } from '@angular/common';
import { Component, OnInit, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { PropertyService } from '../../core/services/property.service';
import { CATEGORY_LABELS, Property, PropertyCategory } from '../../core/models';

@Component({
  selector: 'app-property-edit',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './property-edit.component.html',
  styleUrls: ['./property-edit.component.css'],
})
export class PropertyEditComponent implements OnInit {
  private svc   = inject(PropertyService);
  private route = inject(ActivatedRoute);
  private router = inject(Router);

  protected isNew  = true;
  protected saving = signal(false);
  protected error  = signal<string | null>(null);
  protected photosText = '';

  protected categories: { value: PropertyCategory; label: string }[] = (
      Object.keys(CATEGORY_LABELS) as PropertyCategory[]
  ).map((v) => ({ value: v, label: CATEGORY_LABELS[v] }));

  protected model: Partial<Property> = {
    title: '', description: '', listingType: 'SALE', category: 'APARTMENT',
    price: 0, squareMeters: 0, city: '', address: '',
    latitude: 41.9, longitude: 12.5, photos: [],
  };

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.isNew = false;
      this.svc.get(Number(id)).subscribe({
        next: (p) => { this.model = { ...p }; this.photosText = (p.photos ?? []).join('\n'); },
      });
    }
  }

  onPhotosChange(value: string): void {
    this.photosText = value;
    this.model.photos = value.split('\n').map((s) => s.trim()).filter(Boolean);
  }

  save(): void {
    this.saving.set(true);
    this.error.set(null);
    const op$ = this.isNew
        ? this.svc.create(this.model)
        : this.svc.update(this.model.id!, this.model);
    op$.subscribe({
      next:  () => this.router.navigate(['/seller']),
      error: (err) => { this.error.set(err?.error?.message ?? 'Salvataggio non riuscito'); this.saving.set(false); },
    });
  }
}