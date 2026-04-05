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
import { toSignal, rxResource } from "@angular/core/rxjs-interop";
import { map, of } from "rxjs";
import { ZardLoaderComponent } from "@shared/components/loader/loader.component";
import { ErrorStateComponent } from "@shared/components/error-state/error-state.component";
import { ProductDTO } from "../model/products.model";
import { ProductsService } from "../products.service";
import { CartService } from "@features/cart/cart.service";
import { PagedResponse } from "@core/model/pagination.model";
import { ZardButtonComponent } from "@shared/components/button/button.component";
import { ZardBadgeComponent } from "@shared/components/badge/badge.component";
import { ZardIconComponent } from "@shared/components/icon/icon.component";
import { ZardCarouselComponent } from "@shared/components/carousel/carousel.component";
import { ZardCarouselContentComponent } from "@shared/components/carousel/carousel-content.component";
import { ZardCarouselItemComponent } from "@shared/components/carousel/carousel-item.component";
import { ProductListComponent } from "@features/productlist/product-list.component";

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
        ProductListComponent,
        ZardLoaderComponent,
        ErrorStateComponent,
        CurrencyPipe,
        DatePipe,
        DecimalPipe,
    ],
})
export class ProductComponent {
    private readonly route = inject(ActivatedRoute);
    private readonly productsService = inject(ProductsService);
    private readonly cartService = inject(CartService);

    private readonly routeProductId = toSignal(
        this.route.paramMap.pipe(map((p) => Number(p.get("productId")))),
        { initialValue: 0 },
    );

    readonly productResource = rxResource({
        params: this.routeProductId,
        stream: ({ params: id }) => {
            if (!id) {
                return of(null);
            }
            return this.productsService.getProductById(id);
        },
    });

    readonly product = computed(() => this.productResource.value() ?? null);

    readonly productId = computed(() => this.product()?.id);

    readonly relatedProducts = rxResource({
        params: this.productId,
        stream: ({ params: id }) => {
            if (!id) {
                return of(null);
            }
            return this.productsService.getRelatedProducts(id);
        },
    });

    readonly relatedProductsPaged = computed<PagedResponse<ProductDTO> | null>(
        () => {
            const items = this.relatedProducts.value();
            if (!items) {
                return null;
            }
            return {
                content: items,
                page: {
                    totalElements: items.length,
                    totalPages: 1,
                    size: items.length,
                    number: 0,
                },
            };
        },
    );

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

    readonly addedToCart = signal(false);

    constructor() {
        effect(() => {
            this.product();
            this.addedToCart.set(false);
        });
    }

    addToCart(): void {
        const p = this.product();
        if (!p) {
            return;
        }
        this.cartService.addProduct(p);
        this.addedToCart.set(true);
    }
}
