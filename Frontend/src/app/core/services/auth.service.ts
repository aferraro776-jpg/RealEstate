import { Injectable, computed, signal, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, switchMap, tap } from 'rxjs';
import { AuthResponse, LoginRequest, RegisterRequest, Role, User } from '../models';
import { environment } from '../../../environments/environment';

const USER_KEY  = 'hw26_user';
const TOKEN_KEY = 'hw26_token';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private http = inject(HttpClient);
  private base = `${environment.apiUrl}/auth`;

  private _user  = signal<User | null>(this.loadUser());
  private _token = signal<string | null>(localStorage.getItem(TOKEN_KEY));

  readonly user       = this._user.asReadonly();
  readonly token      = this._token.asReadonly();
  readonly isLoggedIn = computed(() => this._user() !== null);
  readonly role       = computed<Role | null>(() => this._user()?.role ?? null);

  login(req: LoginRequest): Observable<User> {
    return this.http.post<{ token: string }>(`${this.base}/login`, {
      email: req.email,
      password: req.password,
    }).pipe(
        switchMap((res) =>
            this.http.get<User>(`${this.base}/me`, {
              headers: { Authorization: `Bearer ${res.token}` },
            }).pipe(tap((user) => this.persist({ token: res.token, user })))
        )
    );
  }

  register(req: RegisterRequest): Observable<void> {
    const endpoint = req.role === 'SELLER'
        ? `${this.base}/register/seller`
        : `${this.base}/register/user`;
    return this.http.post<void>(endpoint, req, { responseType: 'text' as 'json' });
  }

  requestRegistrationOtp(email: string): Observable<string> {
    return this.http.post<string>(`${this.base}/register/request-otp`,
        { email }, { responseType: 'text' as 'json' });
  }

  forgotPassword(email: string): Observable<string> {
    return this.http.post<string>(`${this.base}/forgot-password`,
        { email }, { responseType: 'text' as 'json' });
  }

  resetPassword(email: string, otp: string, newPassword: string): Observable<string> {
    return this.http.post<string>(`${this.base}/reset-password`,
        { email, otp, newPassword }, { responseType: 'text' as 'json' });
  }

  logout(): void {
    localStorage.removeItem(USER_KEY);
    localStorage.removeItem(TOKEN_KEY);
    this._token.set(null);
    this._user.set(null);
  }

  hasRole(...roles: Role[]): boolean {
    const r = this._user()?.role;
    return r ? roles.includes(r) : false;
  }

  private persist(res: AuthResponse): void {
    localStorage.setItem(USER_KEY, JSON.stringify(res.user));
    localStorage.setItem(TOKEN_KEY, res.token);
    this._token.set(res.token);
    this._user.set(res.user);
  }

  private loadUser(): User | null {
    const raw = localStorage.getItem(USER_KEY);
    if (!raw) return null;
    try { return JSON.parse(raw) as User; } catch { return null; }
  }
}