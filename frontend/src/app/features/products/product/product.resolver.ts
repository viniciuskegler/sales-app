import { ResolveFn } from "@angular/router";
import { inject } from "@angular/core";
import { ProductsService } from "@features/products/products.service";
import { ProductDTO } from "@features/products/products.model";

export const productResolver: ResolveFn<ProductDTO> = (route, state) => {
    const productService = inject(ProductsService);
    const productId = Number(route.paramMap.get("productId"));
    return productService.getProductById(productId);
};
