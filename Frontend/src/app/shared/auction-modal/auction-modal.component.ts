import { Component, EventEmitter, Input, OnInit, Output, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuctionService, AuctionDto } from '../../core/services/auction.service';
import { Property } from '../../core/models';

@Component({
    selector: 'app-auction-modal',
    standalone: true,
    imports: [CommonModule, FormsModule],
    templateUrl: './auction-modal.component.html',
    styleUrls: ['./auction-modal.component.css'],
})
export class AuctionModalComponent implements OnInit {
    @Input() post!: Property;
    @Output() closed  = new EventEmitter<void>();
    @Output() created = new EventEmitter<AuctionDto>();
    @Output() deleted = new EventEmitter<number>();

    private auc = inject(AuctionService);

    startingPrice   = signal(0);
    durationDays    = signal(7);
    error           = signal<string | null>(null);
    loading         = signal(false);
    existingAuction = signal<AuctionDto | null>(null);

    ngOnInit(): void {
        this.startingPrice.set(this.post.price);
        this.auc.getByPostId(this.post.id).subscribe({
            next:  (a) => this.existingAuction.set(a),
            error: ()  => this.existingAuction.set(null),
        });
    }

    submit(): void {
        if (this.startingPrice() <= 0) {
            this.error.set('Il prezzo di partenza deve essere maggiore di zero.');
            return;
        }
        if (this.durationDays() < 1) {
            this.error.set('La durata minima è 1 giorno.');
            return;
        }
        this.loading.set(true);
        this.error.set(null);
        this.auc.create({
            postId: this.post.id,
            startingPrice: this.startingPrice(),
            durationDays: this.durationDays(),
        }).subscribe({
            next: (a) => {
                this.loading.set(false);
                this.created.emit(a);
                this.closed.emit();
            },
            error: (e) => {
                this.loading.set(false);
                this.error.set(e?.error?.message ?? 'Errore nella creazione dell\'asta.');
            },
        });
    }

    deleteAuction(): void {
        const a = this.existingAuction();
        if (!a) return;
        if (!confirm('Eliminare l\'asta? Questa azione è irreversibile.')) return;
        this.auc.delete(a.id).subscribe({
            next: () => {
                this.deleted.emit(this.post.id);
                this.closed.emit();
            },
            error: (e) => this.error.set(e?.error?.message ?? 'Impossibile eliminare l\'asta.'),
        });
    }

    close(): void { this.closed.emit(); }

    formatPrice(n: number): string {
        return new Intl.NumberFormat('it-IT', { style: 'currency', currency: 'EUR', maximumFractionDigits: 0 }).format(n);
    }

    formatDate(days: number): string {
        const d = new Date(Date.now() + days * 86400000);
        return d.toLocaleDateString('it-IT', { day: '2-digit', month: '2-digit', year: 'numeric' });
    }
}