import { ChangeDetectionStrategy, Component, inject } from "@angular/core";
import { CurrencyPipe } from "@angular/common";
import { RouterLink } from "@angular/router";
import { CartService } from "./cart.service";
import { ZardButtonComponent } from "@shared/components/button/button.component";
import { ZardIconComponent } from "@shared/components/icon/icon.component";

@Component({
    selector: "app-cart",
    templateUrl: "cart.component.html",
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [CurrencyPipe, RouterLink, ZardButtonComponent, ZardIconComponent],
})
export class CartComponent {
    readonly cartService = inject(CartService);
}
