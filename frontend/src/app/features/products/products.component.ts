import {
    ChangeDetectionStrategy,
    Component,
    computed,
    inject,
    OnInit,
    signal,
    TemplateRef,
    viewChild,
    ViewContainerRef,
} from "@angular/core";
import { ProductListComponent } from "@features/productlist/product-list.component";
import { FilterListComponent } from "@features/filterlist/filter-list.component";
import { ProductsService } from "./products.service";
import { rxResource } from "@angular/core/rxjs-interop";
import { FiltersService } from "@features/filterlist/filters.service";
import { ActivatedRoute } from "@angular/router";
import {
    CategoryFilterDTO,
    CategoryFilterValue,
} from "@features/filterlist/model/filters.model";
import { ZardButtonComponent } from "@shared/components/button/button.component";
import { ZardIconComponent } from "@shared/components/icon/icon.component";
import { ErrorStateComponent } from "@shared/components/error-state/error-state.component";
import { ZardSheetService } from "@shared/components/sheet/sheet.service";
import { ZardSheetRef } from "@shared/components/sheet/sheet-ref";
import { ZardLoaderComponent } from "@shared/components/loader/loader.component";

@Component({
    selector: "app-products-component",
    templateUrl: "products.component.html",
    styleUrl: "products.component.css",
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        ProductListComponent,
        FilterListComponent,
        ZardButtonComponent,
        ZardIconComponent,
        ErrorStateComponent,
        ZardLoaderComponent
    ],
})
export class ProductsComponent implements OnInit {
    readonly paginationValue = signal<string>("30");
    readonly categoriesValue = signal<CategoryFilterValue[]>([]);
    readonly currentPageValue = signal<number>(1);

    readonly categoryList = signal<CategoryFilterDTO[]>([]);

    readonly filtersSheetTemplate =
        viewChild<TemplateRef<unknown>>("filtersSheet");
    private sheetRef: ZardSheetRef<unknown> | null = null;

    readonly params = computed(() => ({
        limit: this.paginationValue(),
        categories: this.categoriesValue(),
        page: this.currentPageValue(),
    }));

    readonly resource = rxResource({
        params: this.params,
        stream: ({ params }) => {
            return this.productsService.fetchProductsStream(
                params.page - 1,
                parseInt(params.limit, 10),
                params.categories,
            );
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
            zViewContainerRef: this.viewContainerRef,
        });
    }

    onPaginationChange(value: string): void {
        if (this.paginationValue() !== value) {
            this.paginationValue.set(value);
        }
        if (this.currentPageValue() !== 1) {
            this.currentPageValue.set(1);
        }
    }
}
