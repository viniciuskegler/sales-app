import { Component } from '@angular/core';
import { Router, RouterOutlet } from '@angular/router';
@Component({
    standalone: true,
    selector: 'home-component',
    templateUrl: 'home.component.html',
    styleUrl: 'home.component.css',
    imports: [RouterOutlet],
})
export class HomeComponent {
    constructor(private readonly router: Router){}
}
