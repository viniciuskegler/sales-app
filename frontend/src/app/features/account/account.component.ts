import { ChangeDetectionStrategy, Component, inject, PLATFORM_ID, resource } from "@angular/core";
import { isPlatformBrowser } from "@angular/common";
import { CurrencyPipe, DatePipe } from "@angular/common";
import { firstValueFrom } from "rxjs";
import { AuthService } from "@features/auth/auth.service";
import { AuthModalComponent } from "@features/auth/auth-modal/auth-modal.component";
import { OrderService } from "@features/order/order.service";
import { OrderStatus } from "@features/order/model/order.model";
import { ZardButtonComponent } from "@shared/components/button/button.component";
import { ZardIconComponent } from "@shared/components/icon/icon.component";
import { ZardLoaderComponent } from "@shared/components/loader/loader.component";
import { ZardDialogService } from "@shared/components/dialog/dialog.service";
import { ZardAccordionComponent } from "@shared/components/accordion/accordion.component";
import { ZardAccordionItemComponent } from "@shared/components/accordion/accordion-item.component";
import type { ZardBadgeVariants } from "@shared/components/badge/badge.variants";

@Component({
    selector: "app-account",
    templateUrl: "account.component.html",
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [ZardButtonComponent, ZardIconComponent, ZardLoaderComponent, ZardAccordionComponent, ZardAccordionItemComponent, CurrencyPipe, DatePipe],
})
export class AccountComponent {
    readonly authService = inject(AuthService);
    private readonly orderService = inject(OrderService);
    private readonly dialogService = inject(ZardDialogService);
    private readonly isBrowser = isPlatformBrowser(inject(PLATFORM_ID));

    readonly ordersResource = resource({
        params: () => this.isBrowser && this.authService.isLoggedIn() ? true : undefined,
        loader: () => firstValueFrom(this.orderService.getMyOrders()),
    });

    openAuthModal(): void {
        this.dialogService.open(AuthModalComponent);
    }

    statusBadgeType(status: OrderStatus): ZardBadgeVariants["zType"] {
        switch (status) {
            case OrderStatus.CANCELLED: return "destructive";
            case OrderStatus.DELIVERED: return "default";
            case OrderStatus.CONFIRMED:
            case OrderStatus.SHIPPED: return "secondary";
            default: return "outline";
        }
    }

    statusLabel(status: OrderStatus): string {
        return status.charAt(0) + status.slice(1).toLowerCase();
    }
}
