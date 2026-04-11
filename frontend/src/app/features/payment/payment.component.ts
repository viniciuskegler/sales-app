import { ChangeDetectionStrategy, Component, inject, OnDestroy, PLATFORM_ID, signal } from "@angular/core";
import { isPlatformBrowser } from "@angular/common";
import { ActivatedRoute, RouterLink } from "@angular/router";
import { CurrencyPipe } from "@angular/common";
import { interval, Subject, switchMap, takeUntil, takeWhile } from "rxjs";
import { OrderService } from "@features/order/order.service";
import { OrderDTO, OrderStatus } from "@features/order/model/order.model";
import { PaymentWebSocketService } from "@features/payment/payment-websocket.service";
import { ZardIconComponent } from "@shared/components/icon/icon.component";
import { ZardLoaderComponent } from "@shared/components/loader/loader.component";
import { ZardButtonComponent } from "@shared/components/button/button.component";
import { environment } from "environments/environment";

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

    private readonly isBrowser = isPlatformBrowser(inject(PLATFORM_ID));

    constructor() {
        if (!this.isBrowser) {
            return;
        }

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
        if (environment.wsEnabled) {
            this.wsService.watchOrderStatus(orderId).pipe(
                takeUntil(this.destroy$),
                takeWhile(status => status !== OrderStatus.CANCELLED && status !== OrderStatus.DELIVERED, true),
            ).subscribe({
                next: (status) => {
                    this.order.update((prev) => prev ? { ...prev, status: status as OrderStatus } : prev);
                },
                error: () => this.loadError.set(true),
            });
        } else {
            interval(3000).pipe(
                takeUntil(this.destroy$),
                switchMap(() => this.orderService.getOrderById(orderId)),
                takeWhile(o => o.status !== OrderStatus.CANCELLED && o.status !== OrderStatus.DELIVERED, true),
            ).subscribe({
                next: (o) => this.order.set(o),
                error: () => this.loadError.set(true),
            });
        }
    }

    ngOnDestroy(): void {
        this.destroy$.next();
        this.destroy$.complete();
    }
}
