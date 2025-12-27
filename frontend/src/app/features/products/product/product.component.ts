import { CommonModule } from "@angular/common";
import { Component, inject, OnInit, signal } from "@angular/core";
import { ActivatedRoute } from "@angular/router";
import { ProductDTO } from "../products.model";

@Component({
    standalone: true,
    selector: "product-component",
    templateUrl: "product.component.html",
    styleUrl: "product.component.css",
    imports: [CommonModule],
})
export class ProductComponent implements OnInit {
    product = signal<ProductDTO | null>(null);

    private route = inject(ActivatedRoute);

    ngOnInit(): void {
        const product = this.route.snapshot.data["data"];
        this.product.set(product);
    }
}
