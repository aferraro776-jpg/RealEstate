import { Routes } from '@angular/router';
import { roleGuard } from './core/guards/role.guard';
import {OAuthCallbackComponent} from "./pages/auth/oauth-callback.components";


export const routes: Routes = [
  {
    path: '',
    loadComponent: () =>
        import('./pages/home/home.component').then(m => m.HomeComponent),
  },
  {
    path: 'login',
    loadComponent: () =>
        import('./pages/auth/login.component').then(m => m.LoginComponent),
  },
  {
    path: 'register',
    loadComponent: () =>
        import('./pages/auth/register.component').then(m => m.RegisterComponent),
  },
  {
    path: 'forgot-password',
    loadComponent: () =>
        import('./pages/auth/forgot-password.component').then(m => m.ForgotPasswordComponent),
  },
  {
    path: 'properties',
    loadComponent: () =>
        import('./pages/properties/property-list.component').then(m => m.PropertyListComponent),
  },
  {
    path: 'properties/:id',
    loadComponent: () =>
        import('./pages/properties/property-detail.component').then(m => m.PropertyDetailComponent),
  },
  {
    path: 'seller',
    canActivate: [roleGuard('SELLER', 'ADMIN')],
    // ← shell component con router-outlet al posto di seller-dashboard direttamente
    loadComponent: () =>
        import('./pages/seller/seller-shell.component').then(m => m.SellerShellComponent),
    children: [
      {
        path: '',
        loadComponent: () =>
            import('./pages/seller/seller-dashboard.component').then(m => m.SellerDashboardComponent),
      },
      {
        path: 'new',
        loadComponent: () =>
            import('./pages/seller/property-edit.component').then(m => m.PropertyEditComponent),
      },
      {
        path: 'edit/:id',
        loadComponent: () =>
            import('./pages/seller/property-edit.component').then(m => m.PropertyEditComponent),
      },
    ],
  },
  {
    path: 'admin',
    canActivate: [roleGuard('ADMIN')],
    loadComponent: () =>
        import('./pages/admin/admin-dashboard.component').then(m => m.AdminDashboardComponent),
  },
  {
    path: 'oauth2/callback',
    component: OAuthCallbackComponent
  }
];