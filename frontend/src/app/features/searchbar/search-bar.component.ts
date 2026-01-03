import { Component, computed, inject, signal } from "@angular/core";
import { FormControl, ReactiveFormsModule, Validators } from "@angular/forms";
import { ZardIconComponent } from "@shared/components/icon/icon.component";
import { ZardInputDirective } from "@shared/components/input/input.directive";
import { ProductsService } from "@features/products/products.service";
import { ZardPopoverDirective } from "@shared/components/popover/popover.component";
import { rxResource, toSignal } from "@angular/core/rxjs-interop";
import { debounceTime, distinctUntilChanged, of } from "rxjs";
import { Router } from "@angular/router";

@Component({
    selector: "search-bar",
    templateUrl: "search-bar.component.html",
    imports: [
        ReactiveFormsModule,
        ZardIconComponent,
        ZardInputDirective,
        ZardPopoverDirective,
    ],
})
export class SearchBarComponent {
    readonly showPopover = signal(false);

    searchFormControl = new FormControl<string>("", [
        Validators.pattern(/^[\p{L}\p{N} ]+$/u),
        Validators.maxLength(40),
    ]);

    readonly searchFormSignal = toSignal(
        this.searchFormControl.valueChanges.pipe(
            debounceTime(300),
            distinctUntilChanged(),
        ),
    );

    readonly params = computed(() => ({
        name: this.searchFormSignal(),
    }));

    readonly resource = rxResource({
        params: this.params,
        stream: (params) => {
            const productname = params.params.name;
            if (
                !productname ||
                productname.trim() === "" ||
                this.searchFormControl.invalid
            ) {
                return of(null);
            }
            return this.productsService.getProductsByName(productname!);
        },
    });

    readonly inputStatus = computed(() => {
        this.searchFormSignal();
        if (this.searchFormControl.invalid) {
            return "error";
        }
        return null;
    });

    readonly shouldShowPopover = computed(
        () => !!(this.resource.value()?.products?.length && this.showPopover()),
    );

    productsService = inject(ProductsService);
    private router = inject(Router);

    navigateToProduct(productId: number): void {
        this.searchFormControl.reset();
        this.router.navigate(["/product", productId], {
            onSameUrlNavigation: "reload",
        });
    }
}
