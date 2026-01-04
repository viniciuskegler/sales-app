import { Component, computed, input, model } from "@angular/core";
import { ProductResponse } from "@features/products/model/products.model";
import { CommonModule, CurrencyPipe, NgOptimizedImage } from "@angular/common";
import { ZardCardComponent } from "@shared/components/card/card.component";
import { ZardButtonComponent } from "@shared/components/button/button.component";
import { RouterLink } from "@angular/router";
import { ZardSelectComponent } from "@shared/components/select/select.component";
import { ZardSelectItemComponent } from "@shared/components/select/select-item.component";
import { ZardLoaderComponent } from "@shared/components/loader/loader.component";
import { ZardPaginationComponent } from "@shared/components/pagination/pagination.component";

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
    ],
})
export class ProductListComponent {
    readonly products = input<ProductResponse | null>();
    readonly isLoading = input.required<boolean>();

    readonly carrouselMode = input<boolean>(false);
    readonly showPagination = input<boolean>(true);

    readonly paginationOptions: string[] = ["30", "50", "100"];
    readonly pagination = model<string>(this.paginationOptions[0]);

    readonly totalPages = computed(() => {
        const totalProducts = this.products()?.total || 0;
        const perPage = parseInt(this.pagination(), 10);
        return Math.ceil(totalProducts / perPage);
    });

    readonly currentPage = model<number>(1);

    onPaginationChange(value: string | string[]) {
        if (Array.isArray(value)) {
            value = value[0];
        }
        this.pagination.set(value || this.paginationOptions[0]);
    }
}
