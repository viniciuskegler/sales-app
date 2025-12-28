import { NgModule } from "@angular/core";
import { RouterModule, Routes } from "@angular/router";
import { HomeComponent } from "./home.component";
import { ProductsComponent } from "@features/products/products.component";
import { ProductComponent } from "@features/products/product/product.component";
import { productResolver } from "@features/products/product/product.resolver";
import { filtersResolver } from "@features/products/filters.resolver";

const routes: Routes = [
    {
        path: "",
        component: HomeComponent,
        children: [
            {
                path: "",
                redirectTo: "products",
                pathMatch: "full",
            },
            {
                path: "products",
                component: ProductsComponent,
                resolve: { data: filtersResolver },
            },
            {
                path: "product/:productId",
                component: ProductComponent,
                resolve: { data: productResolver },
            },
        ],
    },
];

@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule],
})
export class HomeRoutingModule {}
