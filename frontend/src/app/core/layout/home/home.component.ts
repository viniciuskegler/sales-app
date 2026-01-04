import { Component } from "@angular/core";
import { RouterOutlet } from "@angular/router";
@Component({
    standalone: true,
    selector: "app-home-component",
    templateUrl: "home.component.html",
    styleUrl: "home.component.css",
    imports: [RouterOutlet],
})
export class HomeComponent {}
