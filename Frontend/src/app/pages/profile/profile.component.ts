import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { ProfileService, ProfileDto } from '../../core/services/profile.service';

@Component({
    selector: 'app-profile',
    standalone: true,
    imports: [CommonModule, FormsModule],
    templateUrl: './profile.component.html',
    styleUrls: ['./profile.component.css'],
})
export class ProfileComponent implements OnInit {
    private profileSvc = inject(ProfileService);
    private router     = inject(Router);
    protected authSvc  = inject(AuthService);

    // stato
    profile     = signal<ProfileDto | null>(null);
    loading     = signal(true);
    activeTab   = signal<'info' | 'password' | 'danger'>('info');

    // form dati personali
    editName      = signal('');
    editSurname   = signal('');
    editBirthDate = signal('');
    savingInfo    = signal(false);
    infoSuccess   = signal<string | null>(null);
    infoError     = signal<string | null>(null);

    // form cambio password
    oldPassword  = signal('');
    newPassword  = signal('');
    newPassword2 = signal('');
    savingPwd    = signal(false);
    pwdSuccess   = signal<string | null>(null);
    pwdError     = signal<string | null>(null);

    // elimina account
    confirmDelete = signal(false);
    deleting      = signal(false);
    deleteError   = signal<string | null>(null);

    ngOnInit(): void {
        this.profileSvc.get().subscribe({
            next: (p) => {
                this.profile.set(p);
                this.editName.set(p.name);
                this.editSurname.set(p.surname);
                this.editBirthDate.set(p.birthDate ?? '');
                this.loading.set(false);
            },
            error: () => this.loading.set(false),
        });
    }

    isOAuth(): boolean {
        const p = this.profile();
        return !!p?.authProvider && p.authProvider !== 'LOCAL';
    }

    saveInfo(): void {
        this.savingInfo.set(true);
        this.infoSuccess.set(null);
        this.infoError.set(null);

        this.profileSvc.update({
            name:      this.isOAuth() ? undefined : this.editName(),
            surname:   this.isOAuth() ? undefined : this.editSurname(),
            email:     this.profile()!.email,
            birthDate: this.editBirthDate()
                ? this.editBirthDate() + 'T12:00:00'
                : null,
        }).subscribe({
            next: (msg) => {
                this.infoSuccess.set(msg);
                this.savingInfo.set(false);
                this.profile.update(p => p ? {
                    ...p,
                    name:      this.isOAuth() ? p.name : this.editName(),
                    surname:   this.isOAuth() ? p.surname : this.editSurname(),
                    birthDate: this.editBirthDate() || null,
                } : p);
            },
            error: (err) => {
                this.infoError.set(err?.error ?? 'Errore durante il salvataggio.');
                this.savingInfo.set(false);
            },
        });
    }

    savePassword(): void {
        if (this.newPassword() !== this.newPassword2()) {
            this.pwdError.set('Le due password non coincidono.');
            return;
        }
        if (this.newPassword().length < 6) {
            this.pwdError.set('La nuova password deve avere almeno 6 caratteri.');
            return;
        }

        this.savingPwd.set(true);
        this.pwdSuccess.set(null);
        this.pwdError.set(null);

        this.profileSvc.changePassword(this.oldPassword(), this.newPassword()).subscribe({
            next: (msg) => {
                this.pwdSuccess.set(msg);
                this.savingPwd.set(false);
                this.oldPassword.set('');
                this.newPassword.set('');
                this.newPassword2.set('');
            },
            error: (err) => {
                this.pwdError.set(err?.error ?? 'Errore durante il cambio password.');
                this.savingPwd.set(false);
            },
        });
    }

    deleteAccount(): void {
        this.deleting.set(true);
        this.deleteError.set(null);

        this.profileSvc.deleteAccount().subscribe({
            next: () => {
                this.authSvc.logout();
                this.router.navigate(['/']);
            },
            error: (err) => {
                this.deleteError.set(err?.error ?? 'Errore durante l\'eliminazione.');
                this.deleting.set(false);
            },
        });
    }
}