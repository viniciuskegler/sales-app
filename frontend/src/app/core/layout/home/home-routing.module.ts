import { NgModule } from "@angular/core";
import { RouterModule, Routes } from "@angular/router";
import { HomeComponent } from "./home.component";
import { ProductsComponent } from "@features/products/products.component";
import { ProductComponent } from "@features/products/product/product.component";
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
            },
            {
                path: "account",
                loadComponent: () =>
                    import("@features/account/account.component").then(
                        (m) => m.AccountComponent,
                    ),
            },
            {
                path: "cart",
                loadComponent: () =>
                    import("@features/cart/cart.component").then(
                        (m) => m.CartComponent,
                    ),
            },
            {
                path: "payment/:orderId",
                loadComponent: () =>
                    import("@features/payment/payment.component").then(
                        (m) => m.PaymentComponent,
                    ),
            },
        ],
    },
];

@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule],
})
export class HomeRoutingModule {}
