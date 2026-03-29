import { ResolveFn } from "@angular/router";
import { inject } from "@angular/core";
import { ProductsService } from "@features/products/products.service";
import { ProductDetailsDTO } from "@features/products/model/products.model";

export const productResolver: ResolveFn<ProductDetailsDTO> = (route) => {
    const productService = inject(ProductsService);
    const productId = Number(route.paramMap.get("productId"));
    return productService.getProductById(productId);
};
