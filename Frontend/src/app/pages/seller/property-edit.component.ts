import { CommonModule } from '@angular/common';
import { Component, OnInit, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { PropertyService } from '../../core/services/property.service';
import { PhotoService, PhotoResponse } from '../../core/services/photo.service';
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
  private photoSvc = inject(PhotoService);
  private route  = inject(ActivatedRoute);
  private router = inject(Router);

  protected isNew        = true;
  protected saving       = signal(false);
  protected uploading    = signal(false);
  protected error        = signal<string | null>(null);
  protected uploadedUrls = signal<string[]>([]);

  protected categories: { value: RealEstateType; label: string }[] = (
      Object.keys(REAL_ESTATE_TYPE_LABELS) as RealEstateType[]
  ).map(v => ({ value: v, label: REAL_ESTATE_TYPE_LABELS[v] }));

  protected model = {
    title: '', description: '', listingType: 'SALE' as 'SALE' | 'RENT',
    price: null as number | null,
  };

  protected re = {
    type:          'APARTMENT' as RealEstateType,
    numberOfRooms: null as number | null,
    squareMetres:  null as number | null,
    street:        '',
    civicNumber:   '',
    city:          '',
    cap:           '',
    province:      '',
  };

  protected apartment   = { floor: null as number | null, hasElevator: false };
  protected villa       = { hasGarden: false, hasPool: false, numberOfFloors: null as number | null };
  protected garage      = { width: null as number | null, height: null as number | null, isElectric: false };
  protected buildingLot = { cubature: null as number | null, plannedUse: '' };
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
        };
        this.uploadedUrls.set(p.photos ?? []);
        this.re = {
          type:          p.category as RealEstateType,
          numberOfRooms: (p as any).numberOfRooms ?? null,
          squareMetres:  p.squareMeters ?? null,
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

  protected onFilesSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (!input.files?.length) return;

    this.uploading.set(true);
    const files = Array.from(input.files);
    let completed = 0;

    files.forEach(file => {
      this.photoSvc.upload(file).subscribe({
        next: (photo: PhotoResponse) => {
          this.uploadedUrls.update(urls => [...urls, photo.url]);
          completed++;
          if (completed === files.length) this.uploading.set(false);
        },
        error: () => {
          this.error.set('Errore durante il caricamento di una foto.');
          completed++;
          if (completed === files.length) this.uploading.set(false);
        },
      });
    });

    input.value = '';
  }

  protected removePhoto(url: string): void {
    this.uploadedUrls.update(urls => urls.filter(u => u !== url));
  }

  private extraFields(): Partial<RealEstateRequest> {
    switch (this.re.type) {
      case 'APARTMENT': return {
        floor:        this.apartment.floor        ?? undefined,
        hasElevator:  this.apartment.hasElevator,
      };
      case 'VILLA': return {
        hasGarden:      this.villa.hasGarden,
        hasPool:        this.villa.hasPool,
        numberOfFloors: this.villa.numberOfFloors ?? undefined,
      };
      case 'GARAGE': return {
        width:      this.garage.width      ?? undefined,
        height:     this.garage.height     ?? undefined,
        isElectric: this.garage.isElectric,
      };
      case 'BUILDING_LOT': return {
        cubature:   this.buildingLot.cubature   ?? undefined,
        plannedUse: this.buildingLot.plannedUse,
      };
      case 'NON_BUILDING_LOT': return { cropType: this.nonBuilding.cropType };
      default: return {};
    }
  }

  private buildRealEstateDto(): RealEstateRequest {
    return {
      type:          this.re.type,
      title:         this.model.title,
      numberOfRooms: this.re.numberOfRooms ?? 0,
      description:   this.model.description,
      squareMetres:  this.re.squareMetres  ?? 0,
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
      currentPrice: this.model.price ?? 0,
      photoUrls:    this.uploadedUrls().map(url => ({ url })),
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