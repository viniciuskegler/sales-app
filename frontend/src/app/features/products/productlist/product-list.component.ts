import { HttpClient } from "@angular/common/http";
import { Component, inject, OnInit, signal } from "@angular/core";
import { Observable } from "rxjs";
import { ProductResponse } from "@features/products/products.model";
import { AsyncPipe, CommonModule, CurrencyPipe } from "@angular/common";
import { ZardCardComponent } from "@shared/components/card/card.component";
import { ZardButtonComponent } from "@shared/components/button/button.component";
import { RouterLink } from "@angular/router";
import { ProductsService } from "../products.service";

@Component({
    standalone: true,
    selector: "product-list",
    templateUrl: "product-list.component.html",
    styleUrl: "product-list.component.css",
    imports: [
        CommonModule,
        RouterLink,
        AsyncPipe,
        ZardCardComponent,
        CurrencyPipe,
        ZardButtonComponent,
    ],
})
export class ProductListComponent implements OnInit {
    productsObservable: Observable<ProductResponse | null>;

    readonly limit = signal(30);

    productsService = inject(ProductsService);

    constructor() {
        this.productsObservable = this.productsService.productsObservable;
    }

    ngOnInit(): void {
        this.getProducts(0);
    }

    alert(message: string) {
        alert(message);
    }

    getProducts(pagenumber: number): void {
        this.productsService.fetchProducts(pagenumber, this.limit());
    }

}
