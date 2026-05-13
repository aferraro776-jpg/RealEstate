import { CommonModule } from '@angular/common';
import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css'],
})
export class RegisterComponent {
  private auth   = inject(AuthService);
  private router = inject(Router);

  protected name            = '';
  protected surname         = '';
  protected email           = '';
  protected password        = '';
  protected confirmPassword = '';
  protected otp             = '';
  protected birthDate       = '';
  protected role            = 'BUYER';
  protected vatNumber       = '';

  protected showPassword  = signal(false);
  protected showConfirm   = signal(false);
  protected errors        = signal<string[]>([]);
  protected success       = signal(false);
  protected loading       = signal(false);
  protected otpSent       = signal(false);
  protected otpLoading    = signal(false);
  protected otpMsg        = signal<string | null>(null);

  requestOtp(): void {
    if (!this.email) { this.errors.set(['Inserisci l\'email prima di richiedere l\'OTP.']); return; }
    this.otpLoading.set(true);
    this.otpMsg.set(null);
    this.auth.requestRegistrationOtp(this.email).subscribe({
      next: () => {
        this.otpSent.set(true);
        this.otpMsg.set('OTP inviato! Controlla la tua email.');
        this.otpLoading.set(false);
      },
      error: (e) => {
        this.otpMsg.set(typeof e?.error === 'string' ? e.error : 'Errore invio OTP.');
        this.otpLoading.set(false);
      },
    });
  }

  submit(): void {
    this.errors.set([]);

    if (this.password !== this.confirmPassword) {
      this.errors.set(['Le password non coincidono.']);
      return;
    }

    if (!this.otp) {
      this.errors.set(['Inserisci il codice OTP ricevuto via email.']);
      return;
    }

    this.loading.set(true);
    const payload: Record<string, unknown> = {
      name: this.name, surname: this.surname, email: this.email,
      password: this.password, birthDate: this.birthDate,
      role: this.role, otp: this.otp,
    };
    if (this.role === 'SELLER') payload['vatNumber'] = this.vatNumber;

    this.auth.register(payload as any).subscribe({
      next: () => { this.success.set(true); this.loading.set(false); },
      error: (e) => {
        let msgs: string[];
        if (e?.error?.errors)                           msgs = e.error.errors;
        else if (typeof e?.error === 'string' && e.error.trim()) msgs = [e.error];
        else if (e?.error?.message)                     msgs = [e.error.message];
        else                                            msgs = ['Errore di registrazione.'];
        this.errors.set(msgs);
        this.loading.set(false);
      },
    });
  }

  loginWithGoogle(): void {
    this.auth.loginWithGoogle();
  }
}