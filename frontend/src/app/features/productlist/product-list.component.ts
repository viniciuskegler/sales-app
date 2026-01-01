import {
    Component,
    input,
    model,
    OnInit
} from "@angular/core";
import { ProductResponse } from "@features/products/model/products.model";
import { CommonModule, CurrencyPipe } from "@angular/common";
import { ZardCardComponent } from "@shared/components/card/card.component";
import { ZardButtonComponent } from "@shared/components/button/button.component";
import { RouterLink } from "@angular/router";
import { ZardSelectComponent } from "@shared/components/select/select.component";
import { ZardSelectItemComponent } from "@shared/components/select/select-item.component";

@Component({
    standalone: true,
    selector: "product-list",
    templateUrl: "product-list.component.html",
    styleUrl: "product-list.component.css",
    imports: [
        CommonModule,
        RouterLink,
        ZardCardComponent,
        CurrencyPipe,
        ZardButtonComponent,
        ZardSelectComponent,
        ZardSelectItemComponent
    ],
})
export class ProductListComponent implements OnInit {

    readonly products = input<ProductResponse | null>();
    readonly carrouselMode = input<boolean>(false);
    readonly showPagination = input<boolean>(true);

    readonly paginationOptions: string[] = ["30", "50", "100"];
    readonly pagination = model<string>(this.paginationOptions[0]);

    constructor() {}

    ngOnInit(): void {}

    alert(message: string) {
        alert(message);
    }

    onPaginationChange(value: string | string[]) {
        if (Array.isArray(value)) {
            value = value[0];
        }
        this.pagination.set(value || this.paginationOptions[0]);
    }  
}