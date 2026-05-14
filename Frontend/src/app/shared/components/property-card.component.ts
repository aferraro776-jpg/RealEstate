import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';
import { RouterLink } from '@angular/router';
import { CATEGORY_LABELS, LISTING_TYPE_LABELS, Property } from '../../core/models';

@Component({
  selector: 'app-property-card',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './property-card.component.html',
  styleUrls: ['./property-card.component.css'],
})
export class PropertyCardComponent {
  @Input({ required: true }) property!: Property;
  protected placeholder = 'https://placehold.co/600x450/eee/999?text=hw26';

  protected get categoryLabel(): string {
    return CATEGORY_LABELS[this.property.category] ?? this.property.category;
  }

  protected get listingLabel(): string {
    return LISTING_TYPE_LABELS[this.property.listingType] ?? this.property.listingType;
  }

  ngOnInit() {
    console.log('photos:', this.property.photos);
  }
}