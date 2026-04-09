import { ChangeDetectionStrategy, Component, inject, PLATFORM_ID } from "@angular/core";
import { isPlatformBrowser } from "@angular/common";
import { rxResource } from "@angular/core/rxjs-interop";
import { CurrencyPipe, DatePipe } from "@angular/common";
import { of } from "rxjs";
import { AuthService } from "@features/auth/auth.service";
import { AuthModalComponent } from "@features/auth/auth-modal/auth-modal.component";
import { OrderService } from "@features/order/order.service";
import { OrderStatus } from "@features/order/model/order.model";
import { ZardButtonComponent } from "@shared/components/button/button.component";
import { ZardIconComponent } from "@shared/components/icon/icon.component";
import { ZardBadgeComponent } from "@shared/components/badge/badge.component";
import { ZardLoaderComponent } from "@shared/components/loader/loader.component";
import { ZardDialogService } from "@shared/components/dialog/dialog.service";
import type { ZardBadgeVariants } from "@shared/components/badge/badge.variants";

@Component({
    selector: "app-account",
    templateUrl: "account.component.html",
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [ZardButtonComponent, ZardIconComponent, ZardBadgeComponent, ZardLoaderComponent, CurrencyPipe, DatePipe],
})
export class AccountComponent {
    readonly authService = inject(AuthService);
    private readonly orderService = inject(OrderService);
    private readonly dialogService = inject(ZardDialogService);
    private readonly isBrowser = isPlatformBrowser(inject(PLATFORM_ID));

    readonly ordersResource = rxResource({
        stream: () => {
            if (!this.isBrowser) {
                return of([]);
            }
            return this.orderService.getMyOrders();
        },
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
