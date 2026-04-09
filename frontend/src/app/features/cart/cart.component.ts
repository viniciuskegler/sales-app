import { ChangeDetectionStrategy, Component, inject, signal } from "@angular/core";
import { CurrencyPipe } from "@angular/common";
import { Router, RouterLink } from "@angular/router";
import { CartService } from "./cart.service";
import { OrderService } from "@features/order/order.service";
import { AuthService } from "@features/auth/auth.service";
import { AuthModalComponent } from "@features/auth/auth-modal/auth-modal.component";
import { ZardDialogService } from "@shared/components/dialog/dialog.service";
import { ZardButtonComponent } from "@shared/components/button/button.component";
import { ZardIconComponent } from "@shared/components/icon/icon.component";

@Component({
    selector: "app-cart",
    templateUrl: "cart.component.html",
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [CurrencyPipe, RouterLink, ZardButtonComponent, ZardIconComponent],
})
export class CartComponent {
    private readonly orderService = inject(OrderService);
    private readonly authService = inject(AuthService);
    private readonly router = inject(Router);
    private readonly dialogService = inject(ZardDialogService);
    readonly cartService = inject(CartService);

    readonly isCheckingOut = signal(false);
    readonly checkoutError = signal<string | null>(null);

    checkout(): void {
        if (!this.authService.isLoggedIn()) {
            this.dialogService.open(AuthModalComponent);
            return;
        }
        this.isCheckingOut.set(true);
        this.checkoutError.set(null);
        const request = {
            items: this.cartService.items().map((item) => ({
                productId: item.productId,
                quantity: item.quantity,
            })),
        };
        this.orderService.placeOrder(request).subscribe({
            next: () => {
                this.cartService.clear();
                this.router.navigate(["/account"]);
            },
            error: () => {
                this.checkoutError.set("Failed to place order. Please try again.");
                this.isCheckingOut.set(false);
            },
        });
    }
}
