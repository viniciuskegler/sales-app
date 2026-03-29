import {
    ChangeDetectionStrategy,
    Component,
    computed,
    effect,
    inject,
    signal,
} from "@angular/core";
import { CurrencyPipe, DatePipe, DecimalPipe } from "@angular/common";
import { ActivatedRoute } from "@angular/router";
import { toSignal } from "@angular/core/rxjs-interop";
import { map } from "rxjs";
import { ProductDetailsDTO } from "../model/products.model";
import { ZardButtonComponent } from "@shared/components/button/button.component";
import { ZardBadgeComponent } from "@shared/components/badge/badge.component";
import { ZardIconComponent } from "@shared/components/icon/icon.component";
import { ZardCarouselComponent } from "@shared/components/carousel/carousel.component";
import { ZardCarouselContentComponent } from "@shared/components/carousel/carousel-content.component";
import { ZardCarouselItemComponent } from "@shared/components/carousel/carousel-item.component";

@Component({
    selector: "app-product-component",
    templateUrl: "product.component.html",
    styleUrl: "product.component.css",
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        ZardButtonComponent,
        ZardBadgeComponent,
        ZardIconComponent,
        ZardCarouselComponent,
        ZardCarouselContentComponent,
        ZardCarouselItemComponent,
        CurrencyPipe,
        DatePipe,
        DecimalPipe,
    ],
})
export class ProductComponent {
    private readonly route = inject(ActivatedRoute);

    readonly product = toSignal(
        this.route.data.pipe(map((d) => d["data"] as ProductDetailsDTO)),
        { initialValue: null },
    );
    readonly addedToCart = signal(false);

    constructor() {
        effect(() => {
            this.product();
            this.addedToCart.set(false);
        });
    }

    readonly images = computed(() => {
        const p = this.product();
        if (!p) {
            return [];
        }
        return p.images?.length ? p.images : [p.thumbnail];
    });

    readonly discountedPrice = computed(() => {
        const p = this.product();
        if (!p || !p.discountPercentage) {
            return null;
        }
        return p.price * (1 - p.discountPercentage / 100);
    });

    readonly stars = computed(() => {
        const rating = this.product()?.rating ?? 0;
        return Array.from({ length: 5 }, (_, i) => i < Math.round(rating));
    });

    addToCart(): void {
        // TODO: wire up CartService once implemented
        this.addedToCart.set(true);
    }
}
