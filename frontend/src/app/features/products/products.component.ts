import { Component, computed, inject, OnInit, signal } from "@angular/core";
import { ProductListComponent } from "@features/productlist/product-list.component";
import { FilterListComponent } from "@features/filterlist/filter-list.component";
import { ProductsService } from "./products.service";
import { rxResource } from "@angular/core/rxjs-interop";
import { FiltersService } from "@features/filterlist/filters.service";
import { ActivatedRoute } from "@angular/router";
import { CategoryFilterDTO, CategoryFilterValue } from "@features/filterlist/model/filters.model";

@Component({
    selector: "products-component",
    templateUrl: "products.component.html",
    imports: [ProductListComponent, FilterListComponent],
})
export class ProductsComponent implements OnInit {
    readonly paginationValue = signal<number>(30);
    readonly categoriesValue = signal<CategoryFilterValue[]>([])

    readonly categoryList = signal<CategoryFilterDTO[]>([]);

    params = computed(() => ({
        limit: this.paginationValue(),
        categories: this.categoriesValue(),
    }));

    resource = rxResource({
        params: this.params,
        stream: (params) => {
            const pageNumber = 0; // Always fetch first page on param change
            this.getProducts(pageNumber, params.params.limit);
            return this.productsService.productsObservable;
        },
    });

    productsService = inject(ProductsService);
    filtersService = inject(FiltersService);
    route = inject(ActivatedRoute);

    ngOnInit(): void {
        const categories = this.route.snapshot.data["data"];
        this.categoryList.set(categories);
    }

    getProducts(pagenumber: number, pagination: number): void {
        this.productsService.fetchProducts(pagenumber, pagination);
    }

}
