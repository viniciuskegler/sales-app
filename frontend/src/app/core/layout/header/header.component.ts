import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';

@Component({
    standalone: true,
    imports: [RouterLink],
    selector: 'app-header',
    templateUrl: 'header.component.html'
})

export class HeaderComponent {}
