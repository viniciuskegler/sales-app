import {
    Component,
    computed,
    DestroyRef,
    ElementRef,
    inject,
    input,
    model,
    PLATFORM_ID,
    signal,
    ViewChild,
} from "@angular/core";
import { isPlatformBrowser } from "@angular/common";
import { ProductDTO } from "@features/products/model/products.model";
import { CommonModule, CurrencyPipe, NgOptimizedImage } from "@angular/common";
import { ZardCardComponent } from "@shared/components/card/card.component";
import { ZardButtonComponent } from "@shared/components/button/button.component";
import { RouterLink } from "@angular/router";
import { ZardSelectComponent } from "@shared/components/select/select.component";
import { ZardSelectItemComponent } from "@shared/components/select/select-item.component";
import { ZardLoaderComponent } from "@shared/components/loader/loader.component";
import { ZardPaginationComponent } from "@shared/components/pagination/pagination.component";
import { PagedResponse } from "@core/model/pagination.model";
import { ZardIconComponent } from "@shared/components/icon/icon.component";
import { CartService } from "@features/cart/cart.service";

@Component({
    standalone: true,
    selector: "app-product-list",
    templateUrl: "product-list.component.html",
    styleUrl: "product-list.component.css",
    imports: [
        CommonModule,
        RouterLink,
        ZardCardComponent,
        CurrencyPipe,
        ZardButtonComponent,
        ZardSelectComponent,
        ZardSelectItemComponent,
        ZardLoaderComponent,
        ZardPaginationComponent,
        NgOptimizedImage,
        ZardIconComponent,
    ],
})
export class ProductListComponent {
    readonly products = input<PagedResponse<ProductDTO> | null>();
    readonly isLoading = input.required<boolean>();

    readonly carrouselMode = input<boolean>(false);
    readonly showPagination = input<boolean>(true);

    readonly paginationOptions: string[] = ["30", "50", "100"];
    readonly pagination = model<string>(this.paginationOptions[0]);

    readonly totalProducts = computed(() => {
        return this.products()?.page?.totalElements;
    });

    readonly totalPages = computed(() => {
        const totalProducts = this.products()?.page?.totalElements || 0;
        const perPage = parseInt(this.pagination(), 10);
        return Math.ceil(totalProducts / perPage);
    });

    readonly currentPage = model<number>(1);

    private _carouselTrack?: ElementRef<HTMLElement>;

    @ViewChild("carouselTrack") set carouselTrack(
        el: ElementRef<HTMLElement> | undefined,
    ) {
        this._carouselTrack = el;
        this.resizeObserver?.disconnect();
        this.resizeObserver = undefined;
        if (!el) {
            this.canScroll.set(false);
            return;
        }
        const native = el.nativeElement;
        const update = () =>
            this.canScroll.set(native.scrollWidth > native.clientWidth);
        update();
        if (this.isBrowser) {
            this.resizeObserver = new ResizeObserver(update);
            this.resizeObserver.observe(native);
        }
    }

    readonly canScroll = signal(false);
    private resizeObserver?: ResizeObserver;
    private readonly destroyRef = inject(DestroyRef);
    private readonly isBrowser = isPlatformBrowser(inject(PLATFORM_ID));
    private readonly cartService = inject(CartService);

    readonly cartProductIds = computed(
        () => new Set(this.cartService.items().map((i) => i.productId)),
    );

    addToCart(product: ProductDTO): void {
        this.cartService.addProduct(product);
    }

    constructor() {
        this.destroyRef.onDestroy(() => this.resizeObserver?.disconnect());
    }

    scrollCarousel(direction: -1 | 1): void {
        this._carouselTrack?.nativeElement.scrollBy({
            left: direction * 240,
            behavior: "smooth",
        });
    }

    onPaginationChange(value: string | string[]) {
        if (Array.isArray(value)) {
            value = value[0];
        }
        this.pagination.set(value || this.paginationOptions[0]);
    }
}
