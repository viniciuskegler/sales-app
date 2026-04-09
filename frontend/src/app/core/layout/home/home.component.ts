import { Component } from "@angular/core";
import { RouterOutlet } from "@angular/router";
import { ZardToastComponent } from "@shared/components/toast/toast.component";

@Component({
    standalone: true,
    selector: "app-home-component",
    templateUrl: "home.component.html",
    styleUrl: "home.component.css",
    imports: [RouterOutlet, ZardToastComponent],
})
export class HomeComponent {}
