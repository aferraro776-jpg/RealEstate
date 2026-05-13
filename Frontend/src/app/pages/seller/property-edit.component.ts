import { CommonModule } from '@angular/common';
import { Component, OnInit, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { Observable } from 'rxjs';
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
  private svc    = inject(PropertyService);
  private route  = inject(ActivatedRoute);
  private router = inject(Router);

  protected isNew  = true;
  protected saving = signal(false);
  protected error  = signal<string | null>(null);
  protected photosText = '';

  protected categories: { value: PropertyCategory; label: string }[] = (
      Object.keys(CATEGORY_LABELS) as PropertyCategory[]
  ).map(v => ({ value: v, label: CATEGORY_LABELS[v] }));

  // Campi del post
  protected model: Partial<Property> = {
    title: '', description: '', listingType: 'SALE',
    price: 0, photos: [],
  };

  // Campi del RealEstate separati
  protected re = {
    type:          'APARTMENT' as PropertyCategory,
    numberOfRooms: 0,
    squareMetres:  0,
    street:        '',
    civicNumber:   '',
    city:          '',
    cap:           '',
    province:      '',
  };

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.isNew = false;
      this.svc.get(Number(id)).subscribe({
        next: (p) => {
          this.model = {
            id:          p.id,
            title:       p.title,
            description: p.description,
            listingType: p.listingType,
            price:       p.price,
            photos:      p.photos ?? [],
          };
          this.photosText = (p.photos ?? []).join('\n');

          // Scompone "Via Roma 7, 87040 Mendicino (CS)" nei campi separati
          const addr = p.address ?? '';
          const parts = addr.split(',');
          if (parts.length >= 2) {
            const streetPart = parts[0].trim();
            const lastSpace  = streetPart.lastIndexOf(' ');
            this.re.street      = lastSpace > 0 ? streetPart.substring(0, lastSpace) : streetPart;
            this.re.civicNumber = lastSpace > 0 ? streetPart.substring(lastSpace + 1) : '';

            const rest     = parts[1].trim();
            const capMatch = rest.match(/^(\d{5})\s+(.+?)\s*\((\w+)\)$/);
            if (capMatch) {
              this.re.cap      = capMatch[1];
              this.re.city     = capMatch[2];
              this.re.province = capMatch[3];
            }
          }

          this.re.type         = p.category as PropertyCategory;
          this.re.squareMetres = p.squareMeters ?? 0;
        },
      });
    }
  }

  onPhotosChange(value: string): void {
    this.photosText = value;
    this.model.photos = value.split('\n').map(s => s.trim()).filter(Boolean);
  }

  save(): void {
    this.saving.set(true);
    this.error.set(null);

    const dto = {
      title:        this.model.title,
      description:  this.model.description,
      listingType:  this.model.listingType,
      currentPrice: this.model.price,
      photoUrls:    this.model.photos,
      realEstate: {
        type:          this.re.type,
        title:         this.model.title,
        description:   this.model.description,
        numberOfRooms: this.re.numberOfRooms,
        squareMetres:  this.re.squareMetres,
        street:        this.re.street,
        civicNumber:   this.re.civicNumber,
        city:          this.re.city,
        cap:           this.re.cap,
        province:      this.re.province,
      },
    };

    const op$: Observable<any> = this.isNew
        ? this.svc.createWithRealEstate(dto as any)
        : this.svc.update(this.model.id!, dto as any);

    op$.subscribe({
      next:  () => this.router.navigate(['/seller']),
      error: (err) => {
        this.error.set(err?.error?.message ?? 'Salvataggio non riuscito');
        this.saving.set(false);
      },
    });
  }
}