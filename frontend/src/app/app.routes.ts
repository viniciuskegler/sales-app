import { Routes } from "@angular/router";

export const routes: Routes = [
    {
        path: "",
        loadChildren: () =>
            import("@layout/home/home-routing.module").then(
                (m) => m.HomeRoutingModule,
            ),
    },
    {
        path: "auth",
        children: [
            {
                path: "login",
                loadComponent: () =>
                    import("@features/auth/login/login.component").then(
                        (m) => m.LoginComponent,
                    ),
            },
            {
                path: "register",
                loadComponent: () =>
                    import("@features/auth/register/register.component").then(
                        (m) => m.RegisterComponent,
                    ),
            },
        ],
    },
];
