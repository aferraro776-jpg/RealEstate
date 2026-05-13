import { Component, OnInit, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';

@Component({
    selector: 'app-oauth-callback',
    standalone: true,
    imports: [],
    template: `
    <div class="page-pad text-center">
      <p class="muted">Accesso in corso...</p>
    </div>
  `,
})
export class OAuthCallbackComponent implements OnInit {
    private route  = inject(ActivatedRoute);
    private auth   = inject(AuthService);
    private router = inject(Router);

    ngOnInit(): void {
        const token = this.route.snapshot.queryParamMap.get('token');
        if (token) {
            this.auth.handleOAuthCallback(token).subscribe({
                next: () => this.router.navigate(['/']),
                error: () => this.router.navigate(['/login']),
            });
        } else {
            this.router.navigate(['/login']);
        }
    }
}