import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { PropertyService } from '../../core/services/property.service';
import { ReviewService } from '../../core/services/review.service';
import { ContactService } from '../../core/services/contact.service';
import { AuthService } from '../../core/services/auth.service';
import { AuctionService, AuctionDto } from '../../core/services/auction.service';
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
  private auc        = inject(AuctionService);
  private http       = inject(HttpClient);
  private route      = inject(ActivatedRoute);
  protected authSvc  = inject(AuthService);

  property     = signal<Property | null>(null);
  reviews      = signal<Review[]>([]);
  loading      = signal(true);
  seller       = signal<SellerInfo | null>(null);
  auction      = signal<AuctionDto | null>(null);
  placeholder  = 'https://placehold.co/900x420/eee/999?text=hw26';
  currentPhoto = signal(0);
  showModal    = signal(false);
  contactMsg   = '';
  contactSent  = signal(false);
  contactError = signal<string | null>(null);
  sending      = signal(false);
  showReviewForm  = signal(false);
  newTitle        = signal<string>('');
  newRating       = signal<number>(5);
  newComment      = signal<string>('');
  reviewError     = signal<string | null>(null);
  savingReview    = signal(false);
  editTitle = signal<string>('');
  editingReviewId = signal<number | null>(null);
  editRating      = signal<number>(5);
  editComment     = signal<string>('');

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
        this.auc.getByPostId(id).subscribe({
          next: (a) => { if (!a.closed) this.auction.set(a); },
          error: () => {},
        });
      },
      error: () => { this.property.set(null); this.loading.set(false); },
    });

    this.revSvc.list(id).subscribe({ next: (r) => this.reviews.set(r) });
  }

  formatPrice(n: number): string {
    return new Intl.NumberFormat('it-IT', {
      style: 'currency', currency: 'EUR', maximumFractionDigits: 0,
    }).format(n);
  }

  openModal(): void {
    this.showModal.set(true);
    this.contactMsg = '';
    this.contactSent.set(false);
    this.contactError.set(null);
  }

  closeModal(): void { this.showModal.set(false); }

  prevPhoto(): void {
    const photos = this.property()?.photos ?? [];
    this.currentPhoto.update(i => (i - 1 + photos.length) % photos.length);
  }

  nextPhoto(): void {
    const photos = this.property()?.photos ?? [];
    this.currentPhoto.update(i => (i + 1) % photos.length);
  }

  goToPhoto(index: number): void { this.currentPhoto.set(index); }

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
  openReviewForm(): void {
    this.showReviewForm.set(true);
    this.newTitle.set('');
    this.newRating.set(5);
    this.newComment.set('');
    this.reviewError.set(null);
  }

  closeReviewForm(): void { this.showReviewForm.set(false); }

  submitReview(): void {
    const user = this.authSvc.user();
    const p    = this.property();
    if (!user || !p) return;

    this.savingReview.set(true);
    this.reviewError.set(null);

    this.revSvc.create({
      postId:      p.id,
      userId:      user.id,
      author:      `${user.name} ${user.surname}`,
      title:       this.newTitle(),
      rating:      this.newRating(),
      description: this.newComment(),
    }).subscribe({
      next: (r) => {
        this.reviews.update(list => [r, ...list]);
        this.savingReview.set(false);
        this.showReviewForm.set(false);
      },
      error: (err) => {
        this.reviewError.set(err?.error ?? 'Errore durante il salvataggio.');
        this.savingReview.set(false);
      },
    });
  }
  isReviewAuthor(review: Review): boolean {
    const user = this.authSvc.user();
    return !!user && user.id === review.userId;
  }

  startEdit(review: Review): void {
    this.editingReviewId.set(review.id);
    this.editRating.set(review.rating);
    this.editComment.set(review.description);
    this.editTitle.set(review.title);
  }

  cancelEdit(): void {
    this.editingReviewId.set(null);
  }

  saveEdit(review: Review): void {
    const updated: Review = {
      ...review,
      rating:  this.editRating(),
      title: this.editTitle(),
      description: this.editComment(),
    };
    this.revSvc.update(updated).subscribe({
      next: (r) => {
        this.reviews.update(list => list.map(x => x.id === r.id ? r : x));
        this.editingReviewId.set(null);
      },
      error: (err) => console.error('Errore aggiornamento recensione', err),
    });
  }
}