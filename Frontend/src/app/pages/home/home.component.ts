import { CommonModule } from '@angular/common';
import { Component, inject, OnInit, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { PropertyService } from '../../core/services/property.service';
import { Property } from '../../core/models';
import { PropertyCardComponent } from '../../shared/components/property-card.component';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, RouterLink, PropertyCardComponent],
  templateUrl: './home.component.html',
})
export class HomeComponent implements OnInit {
  private svc = inject(PropertyService);
  protected auth = inject(AuthService);

  protected recent = signal<Property[]>([]);
  protected loading = signal(true);
  protected error = signal<string | null>(null);
  protected apiHint = '';

  ngOnInit(): void {
    this.svc.list({ sort: 'newest' }).subscribe({
      next: (items) => {
        this.recent.set(items.slice(0, 8));
        this.loading.set(false);
      },
      error: (err) => {
        this.error.set(err.message ?? 'Errore di rete');
        this.loading.set(false);
      },
    });

    this.apiHint =
        (window as unknown as { __env?: { apiUrl?: string } }).__env?.apiUrl ?? '/api';
  }
}