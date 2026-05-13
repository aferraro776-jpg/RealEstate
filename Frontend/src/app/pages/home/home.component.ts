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
  template: `
    <section class="hero">
      <img
        class="hero-img"
        src="https://images.unsplash.com/photo-1600585154340-be6161a56a0c?auto=format&fit=crop&w=2000&q=80"
        alt="Casa"
      />
      <div class="container py-5 d-flex flex-column justify-content-end" style="min-height:520px;">
        <div class="hw-reveal" style="max-width:720px;">
          <p class="text-uppercase small mb-3" style="letter-spacing:.2em; opacity:.85;">
            Annunci immobiliari in Italia
          </p>
          <h1 class="display-3 mb-3 serif">Trova la casa giusta per te.</h1>
          <p class="lead mb-4" style="opacity:.9;">
            Vendita e affitto di appartamenti, ville, terreni edificabili e box auto.
            Cerca per categoria, ordina per prezzo o metratura, contatta direttamente i venditori.
          </p>
          <div class="d-flex gap-2 flex-wrap">
            <a routerLink="/properties" class="btn btn-hw">Esplora gli annunci</a>
            @if (!auth.isLoggedIn()) {
              <a routerLink="/register" class="btn btn-light">Registrati</a>
            }
          </div>
        </div>
      </div>
    </section>

    <section class="page-pad">
      <div class="container">
        <div class="d-flex align-items-end justify-content-between mb-4">
          <h2 class="section-title mb-0">Annunci recenti</h2>
          <a routerLink="/properties" class="btn btn-link text-decoration-none">Vedi tutti →</a>
        </div>

        @if (loading()) {
          <p class="muted">Caricamento in corso...</p>
        } @else if (error()) {
          <div class="alert alert-warning">
            Impossibile contattare il backend ({{ error() }}).<br>
            Verifica che il server Spring Boot sia attivo su <code>{{ apiHint }}</code>.
          </div>
        } @else if (recent().length === 0) {
          <p class="muted">Nessun annuncio disponibile.</p>
        } @else {
          <div class="row g-4">
            @for (p of recent(); track p.id) {
              <div class="col-sm-6 col-lg-4 col-xl-3">
                <app-property-card [property]="p"></app-property-card>
              </div>
            }
          </div>
        }
      </div>
    </section>

    <section class="page-pad bg-white border-top">
      <div class="container text-center">
        <h2 class="section-title">Tre tipi di utenza</h2>
        <div class="row g-4 mt-2">
          <div class="col-md-4">
            <i class="bi bi-search fs-1 text-primary"></i>
            <h4 class="serif mt-3">Acquirenti</h4>
            <p class="muted">Cercano e filtrano gli immobili, contattano i venditori, scrivono recensioni.</p>
          </div>
          <div class="col-md-4">
            <i class="bi bi-house-add fs-1 text-primary"></i>
            <h4 class="serif mt-3">Venditori</h4>
            <p class="muted">Pubblicano annunci, ribassano i prezzi, creano aste, ricevono richieste di contatto.</p>
          </div>
          <div class="col-md-4">
            <i class="bi bi-shield-lock fs-1 text-primary"></i>
            <h4 class="serif mt-3">Amministratori</h4>
            <p class="muted">Gestiscono utenti e annunci, possono bannare e nominare altri amministratori.</p>
          </div>
        </div>
      </div>
    </section>
  `,
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
    this.apiHint = (window as unknown as { __env?: { apiUrl?: string } }).__env?.apiUrl ?? '/api';
  }
}