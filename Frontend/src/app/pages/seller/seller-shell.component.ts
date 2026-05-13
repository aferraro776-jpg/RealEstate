import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';

@Component({
    selector: 'app-seller-shell',
    standalone: true,
    imports: [RouterOutlet],
    template: `<router-outlet />`,
})
export class SellerShellComponent {}