import {
    Component,
    computed,
    inject,
    OnInit,
    signal,
    TemplateRef,
    ViewContainerRef,
    viewChild,
} from "@angular/core";
import { ProductListComponent } from "@features/productlist/product-list.component";
import { FilterListComponent } from "@features/filterlist/filter-list.component";
import { ProductsService } from "./products.service";
import { rxResource } from "@angular/core/rxjs-interop";
import { FiltersService } from "@features/filterlist/filters.service";
import { ActivatedRoute } from "@angular/router";
import { CategoryFilterDTO, CategoryFilterValue } from "@features/filterlist/model/filters.model";
import { ZardButtonComponent } from "@shared/components/button/button.component";
import { ZardIconComponent } from "@shared/components/icon/icon.component";
import { ZardSheetService } from "@shared/components/sheet/sheet.service";
import { ZardSheetRef } from "@shared/components/sheet/sheet-ref";

@Component({
    selector: "products-component",
    templateUrl: "products.component.html",
    styleUrl: "products.component.css",
    imports: [ProductListComponent, FilterListComponent, ZardButtonComponent, ZardIconComponent],
})
export class ProductsComponent implements OnInit {
    readonly paginationValue = signal<string>("30");
    readonly categoriesValue = signal<CategoryFilterValue[]>([])
    readonly priceSliderValue = signal<number | null>(null);

    readonly categoryList = signal<CategoryFilterDTO[]>([]);

    readonly filtersSheetTemplate = viewChild<TemplateRef<unknown>>("filtersSheet");
    private sheetRef: ZardSheetRef<unknown> | null = null;

    params = computed(() => ({
        limit: this.paginationValue(),
        categories: this.categoriesValue(),
    }));

    resource = rxResource({
        params: this.params,
        stream: (params) => {
            const pageNumber = 0; // Always fetch first page on param change
            this.getProducts(pageNumber, parseInt(params.params.limit, 10));
            return this.productsService.productsObservable;
        },
    });

    productsService = inject(ProductsService);
    filtersService = inject(FiltersService);
    route = inject(ActivatedRoute);
    sheetService = inject(ZardSheetService);
    viewContainerRef = inject(ViewContainerRef);

    ngOnInit(): void {
        const categories = this.route.snapshot.data["data"];
        this.categoryList.set(categories);
    }

    getProducts(pagenumber: number, pagination: number): void {
        this.productsService.fetchProducts(pagenumber, pagination);
    }

    openFiltersSheet(): void {
        const template = this.filtersSheetTemplate();
        if (!template) {
            return;
        }

        this.sheetRef?.close();
        this.sheetRef = this.sheetService.create({
            zContent: template,
            zHideFooter: true,
            zSide: "top",
            zMaskClosable: true,
            zViewContainerRef: this.viewContainerRef
        });
    }

}
