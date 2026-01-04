import { Routes } from "@angular/router";

export const routes: Routes = [
    {
        path: "",
        loadChildren: () =>
            import("@layout/home/home-routing.module").then(
                (m) => m.HomeRoutingModule,
            ),
    },
];
