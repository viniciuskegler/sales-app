import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';

@Component({
    standalone: true,
    imports: [RouterLink],
    selector: 'app-footer',
    templateUrl: 'footer.component.html'
})
export class FooterComponent {}
