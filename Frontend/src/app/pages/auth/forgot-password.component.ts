import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { inject } from '@angular/core';
import { AuthService } from '../../core/services/auth.service';

@Component({
    selector: 'app-forgot-password',
    standalone: true,
    imports: [CommonModule, FormsModule, RouterLink],
    templateUrl: './forgot-password.component.html',
    styleUrls: ['./forgot-password.component.css'],
})
export class ForgotPasswordComponent {
    private auth   = inject(AuthService);
    private router = inject(Router);

    // step 1 = inserisci email, step 2 = inserisci OTP + nuova password
    protected step        = signal<1 | 2>(1);
    protected email       = '';
    protected otp         = '';
    protected newPassword = '';
    protected confirm     = '';
    protected loading     = signal(false);
    protected error       = signal<string | null>(null);
    protected success     = signal<string | null>(null);

    sendOtp(): void {
        this.error.set(null);
        if (!this.email) { this.error.set('Inserisci la tua email.'); return; }
        this.loading.set(true);
        this.auth.forgotPassword(this.email).subscribe({
            next: () => { this.loading.set(false); this.step.set(2); },
            error: (e) => { this.loading.set(false); this.error.set(e.error || 'Errore invio OTP.'); },
        });
    }

    resetPassword(): void {
        this.error.set(null);
        if (this.newPassword !== this.confirm) {
            this.error.set('Le password non coincidono.'); return;
        }
        this.loading.set(true);
        this.auth.resetPassword(this.email, this.otp, this.newPassword).subscribe({
            next: () => {
                this.loading.set(false);
                this.success.set('Password aggiornata! Ora puoi accedere.');
                setTimeout(() => this.router.navigate(['/login']), 2000);
            },
            error: (e) => { this.loading.set(false); this.error.set(e.error || 'OTP non valido o scaduto.'); },
        });
    }
}