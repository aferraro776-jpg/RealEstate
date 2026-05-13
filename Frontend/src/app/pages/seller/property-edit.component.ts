import { CommonModule } from '@angular/common';
import { Component, OnInit, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { PropertyService } from '../../core/services/property.service';
import { REAL_ESTATE_TYPE_LABELS, RealEstateType, RealEstateRequest, PostCreateDto } from '../../core/models';

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

  protected isNew      = true;
  protected saving     = signal(false);
  protected error      = signal<string | null>(null);
  protected photosText = '';

  protected categories: { value: RealEstateType; label: string }[] = (
      Object.keys(REAL_ESTATE_TYPE_LABELS) as RealEstateType[]
  ).map(v => ({ value: v, label: REAL_ESTATE_TYPE_LABELS[v] }));

  protected model = {
    title: '', description: '', listingType: 'SALE' as 'SALE' | 'RENT',
    price: 0, photos: [] as string[],
  };

  protected re = {
    type:          'APARTMENT' as RealEstateType,
    numberOfRooms: 0,
    squareMetres:  0,
    street:        '',
    civicNumber:   '',
    city:          '',
    cap:           '',
    province:      '',
  };

  protected apartment   = { floor: 0,    hasElevator: false };
  protected villa       = { hasGarden: false, hasPool: false, numberOfFloors: 1 };
  protected garage      = { width: 0,    height: 0, isElectric: false };
  protected buildingLot = { cubature: 0, plannedUse: '' };
  protected nonBuilding = { cropType: '' };

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (!id) return;

    this.isNew = false;
    this.svc.get(Number(id)).subscribe({
      next: (p) => {
        this.model = {
          title:       p.title,
          description: p.description,
          listingType: p.listingType,
          price:       p.price,
          photos:      p.photos,
        };
        this.photosText = p.photos.join('\n');
        this.re = {
          type:          p.category as RealEstateType,
          numberOfRooms: (p as any).numberOfRooms ?? 0,
          squareMetres:  p.squareMeters ?? 0,
          street:        p.address ?? '',
          civicNumber:   '',
          city:          p.city ?? '',
          cap:           '',
          province:      '',
        };
        const x = p as any;
        if (x.floor          != null) this.apartment.floor        = x.floor;
        if (x.hasElevator    != null) this.apartment.hasElevator  = x.hasElevator;
        if (x.hasGarden      != null) this.villa.hasGarden        = x.hasGarden;
        if (x.hasPool        != null) this.villa.hasPool          = x.hasPool;
        if (x.numberOfFloors != null) this.villa.numberOfFloors   = x.numberOfFloors;
        if (x.width          != null) this.garage.width           = x.width;
        if (x.height         != null) this.garage.height          = x.height;
        if (x.isElectric     != null) this.garage.isElectric      = x.isElectric;
        if (x.cubature       != null) this.buildingLot.cubature   = x.cubature;
        if (x.plannedUse     != null) this.buildingLot.plannedUse = x.plannedUse;
        if (x.cropType       != null) this.nonBuilding.cropType   = x.cropType;
      },
    });
  }

  protected onPhotosChange(v: string): void {
    this.photosText   = v;
    this.model.photos = v.split('\n').map(s => s.trim()).filter(Boolean);
  }

  private extraFields(): Partial<RealEstateRequest> {
    switch (this.re.type) {
      case 'APARTMENT':        return { ...this.apartment };
      case 'VILLA':            return { ...this.villa };
      case 'GARAGE':           return { ...this.garage };
      case 'BUILDING_LOT':     return { ...this.buildingLot };
      case 'NON_BUILDING_LOT': return { ...this.nonBuilding };
      default:                 return {};
    }
  }

  private buildRealEstateDto(): RealEstateRequest {
    return {
      type:          this.re.type,
      title:         this.model.title,
      numberOfRooms: this.re.numberOfRooms,
      description:   this.model.description,
      squareMetres:  this.re.squareMetres,
      street:        this.re.street,
      civicNumber:   this.re.civicNumber,
      city:          this.re.city,
      cap:           this.re.cap,
      province:      this.re.province,
      ...this.extraFields(),
    };
  }

  protected save(): void {
    this.saving.set(true);
    this.error.set(null);

    const dto: PostCreateDto = {
      title:        this.model.title,
      description:  this.model.description,
      currentPrice: this.model.price,
      photoUrls:    this.model.photos.map(url => ({ url })),
      realEstate:   this.buildRealEstateDto(),
    };

    const onSuccess = () => this.router.navigate(['/seller']);
    const onError   = (e: any) => {
      this.error.set(e?.error ?? 'Errore durante il salvataggio.');
      this.saving.set(false);
    };

    if (this.isNew) {
      this.svc.createWithRealEstate(dto).subscribe({ next: onSuccess, error: onError });
    } else {
      this.svc.update(
          Number(this.route.snapshot.paramMap.get('id')),
          { ...dto, listingType: this.model.listingType }
      ).subscribe({ next: onSuccess, error: onError });
    }
  }
}