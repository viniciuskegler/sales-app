import { Component, OnInit } from '@angular/core';
import { ProductListComponent } from '@features/products/productlist/product-list.component';
import { FilterListComponent } from '@features/filterlist/filter-list.component';

@Component({
    selector: 'products-component',
    templateUrl: 'products.component.html',
    imports: [ProductListComponent, FilterListComponent]
})

export class ProductsComponent implements OnInit {
    constructor() { }

    ngOnInit() { }
}