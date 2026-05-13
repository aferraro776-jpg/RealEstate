import { CommonModule } from '@angular/common';
import { Component, OnInit, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { UserService } from '../../core/services/user.service';
import { PropertyService } from '../../core/services/property.service';
import { Property, ROLE_LABELS, Role, User } from '../../core/models';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './admin-dashboard.component.html',
  styleUrls: ['./admin-dashboard.component.css'],
})
export class AdminDashboardComponent implements OnInit {
  private userSvc = inject(UserService);
  private propSvc = inject(PropertyService);

  protected tab        = signal<'users' | 'properties'>('users');
  protected users      = signal<User[]>([]);
  protected properties = signal<Property[]>([]);
  protected roles: Role[] = ['ADMIN', 'SELLER', 'BUYER'];

  ngOnInit(): void {
    this.userSvc.list().subscribe({ next: (r) => this.users.set(r) });
    this.propSvc.list().subscribe({ next: (r) => this.properties.set(r) });
  }

  roleLabel(r: Role): string { return ROLE_LABELS[r]; }

  toggleBan(u: User): void {
    this.userSvc.ban(u.id, !u.banned).subscribe({
      next: (nu) => this.users.update((arr) => arr.map((x) => (x.id === nu.id ? nu : x))),
    });
  }

  changeRole(u: User, role: Role): void {
    this.userSvc.setRole(u.id, role).subscribe({
      next: (nu) => this.users.update((arr) => arr.map((x) => (x.id === nu.id ? nu : x))),
    });
  }

  deleteUser(u: User): void {
    if (!confirm(`Eliminare ${u.name} ${u.surname}?`)) return;
    this.userSvc.delete(u.id).subscribe({
      next: () => this.users.update((arr) => arr.filter((x) => x.id !== u.id)),
    });
  }

  deleteProperty(p: Property): void {
    if (!confirm(`Eliminare "${p.title}"?`)) return;
    this.propSvc.delete(p.id).subscribe({
      next: () => this.properties.update((arr) => arr.filter((x) => x.id !== p.id)),
    });
  }
}