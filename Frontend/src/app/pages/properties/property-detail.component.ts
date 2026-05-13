import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { PropertyService } from '../../core/services/property.service';
import { ReviewService } from '../../core/services/review.service';
import { ContactService } from '../../core/services/contact.service';
import { AuthService } from '../../core/services/auth.service';
import { Property, Review, CATEGORY_LABELS, LISTING_TYPE_LABELS } from '../../core/models';
import { environment } from '../../../environments/environment';

interface SellerInfo {
  name:    string;
  surname: string;
  email:   string;
}

@Component({
  selector: 'app-property-detail',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './property-detail.component.html',
  styleUrls: ['./property-detail.component.css'],
})
export class PropertyDetailComponent implements OnInit {
  private svc        = inject(PropertyService);
  private revSvc     = inject(ReviewService);
  private contactSvc = inject(ContactService);
  protected authSvc    = inject(AuthService);
  private http       = inject(HttpClient);
  private route      = inject(ActivatedRoute);

  property    = signal<Property | null>(null);
  reviews     = signal<Review[]>([]);
  loading     = signal(true);
  seller      = signal<SellerInfo | null>(null);
  placeholder = 'https://placehold.co/900x420/eee/999?text=hw26';

  showModal    = signal(false);
  contactMsg   = '';
  contactSent  = signal(false);
  contactError = signal<string | null>(null);
  sending      = signal(false);

  catLabel     = () => { const p = this.property(); return p ? CATEGORY_LABELS[p.category] : ''; };
  listingLabel = () => { const p = this.property(); return p ? LISTING_TYPE_LABELS[p.listingType] : ''; };

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));

    this.svc.get(id).subscribe({
      next: (p) => {
        this.property.set(p);
        this.loading.set(false);
        this.http.get<any>(`${environment.apiUrl}/seller/${p.sellerId}`).subscribe({
          next: (s) => this.seller.set({ name: s.name, surname: s.surname, email: s.email }),
          error: ()  => this.seller.set(null),
        });
      },
      error: () => { this.property.set(null); this.loading.set(false); },
    });

    this.revSvc.list(id).subscribe({ next: (r) => this.reviews.set(r) });
  }

  openModal(): void {
    this.showModal.set(true);
    this.contactMsg   = '';
    this.contactSent.set(false);
    this.contactError.set(null);
  }

  closeModal(): void { this.showModal.set(false); }

  sendContact(): void {
    const user = this.authSvc.user();
    const p    = this.property();
    if (!user || !p) return;

    this.sending.set(true);
    this.contactError.set(null);

    this.contactSvc.send({
      senderName:    user.name,
      senderSurname: user.surname,
      message:       this.contactMsg,
      postId:        p.id,
      postTitle:     p.title,
    }).subscribe({
      next:  () => { this.contactSent.set(true); this.sending.set(false); },
      error: (err) => {
        this.contactError.set(err?.error ?? 'Errore durante l\'invio.');
        this.sending.set(false);
      },
    });
  }
}