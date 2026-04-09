import { ChangeDetectionStrategy, Component, inject, OnDestroy, signal } from "@angular/core";
import { ActivatedRoute, RouterLink } from "@angular/router";
import { CurrencyPipe } from "@angular/common";
import { Subject, takeUntil, takeWhile } from "rxjs";
import { OrderService } from "@features/order/order.service";
import { OrderDTO, OrderStatus } from "@features/order/model/order.model";
import { PaymentWebSocketService } from "@features/payment/payment-websocket.service";
import { ZardIconComponent } from "@shared/components/icon/icon.component";
import { ZardLoaderComponent } from "@shared/components/loader/loader.component";
import { ZardButtonComponent } from "@shared/components/button/button.component";

@Component({
    selector: "app-payment",
    templateUrl: "payment.component.html",
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [RouterLink, CurrencyPipe, ZardIconComponent, ZardLoaderComponent, ZardButtonComponent],
})
export class PaymentComponent implements OnDestroy {
    private readonly route = inject(ActivatedRoute);
    private readonly orderService = inject(OrderService);
    private readonly wsService = inject(PaymentWebSocketService);

    readonly order = signal<OrderDTO | null>(null);
    readonly loadError = signal(false);

    readonly OrderStatus = OrderStatus;

    private readonly destroy$ = new Subject<void>();

    constructor() {
        const orderId = Number(this.route.snapshot.paramMap.get("orderId"));

        this.orderService.getOrderById(orderId).subscribe({
            next: (o) => {
                this.order.set(o);
                if (o.status !== OrderStatus.CANCELLED && o.status !== OrderStatus.DELIVERED) {
                    this.subscribeToStatusUpdates(orderId);
                }
            },
            error: () => this.loadError.set(true),
        });
    }

    private subscribeToStatusUpdates(orderId: number): void {
        this.wsService.watchOrderStatus(orderId).pipe(
            takeUntil(this.destroy$),
            takeWhile(status => status !== OrderStatus.CANCELLED && status !== OrderStatus.DELIVERED, true),
        ).subscribe({
            next: (status) => {
                this.order.update((prev) => prev ? { ...prev, status: status as OrderStatus } : prev);
            },
            error: () => this.loadError.set(true),
        });
    }

    ngOnDestroy(): void {
        this.destroy$.next();
        this.destroy$.complete();
    }
}
