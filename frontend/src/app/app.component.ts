import { Component } from "@angular/core";
import { RouterOutlet } from "@angular/router";
import { FooterComponent } from "@layout/footer/footer.component";
import { HeaderComponent } from "@layout/header/header.component";
import { environment } from "environments/environment";

@Component({
    selector: "app-root",
    imports: [RouterOutlet, FooterComponent, HeaderComponent],
    templateUrl: "./app.component.html",
    styleUrl: "./app.component.css",
})
export class AppComponent {
    title = environment.name;
}
