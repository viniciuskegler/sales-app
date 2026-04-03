import { inject, Injectable, PLATFORM_ID, signal } from "@angular/core";
import { isPlatformBrowser } from "@angular/common";
import { HttpClient } from "@angular/common/http";
import { Router } from "@angular/router";
import { tap } from "rxjs";
import { environment } from "environments/environment";
import {
    AuthResponse,
    LoginRequest,
    RegisterRequest,
    UserDetails,
} from "./model/auth.model";

const TOKEN_KEY = "auth_token";

@Injectable({ providedIn: "root" })
export class AuthService {
    private readonly http = inject(HttpClient);
    private readonly router = inject(Router);
    private readonly isBrowser = isPlatformBrowser(inject(PLATFORM_ID));

    readonly currentUser = signal<UserDetails | null>(null);

    constructor() {
        const token = this.getToken();
        if (token) {
            // TODO: decode token claims to restore currentUser on page reload
        }
    }

    login(request: LoginRequest) {
        return this.http
            .post<AuthResponse>(`${environment.baseurl}/auth/login`, request)
            .pipe(tap((response) => this.handleAuthResponse(response)));
    }

    register(request: RegisterRequest) {
        return this.http
            .post<AuthResponse>(
                `${environment.baseurl}/auth/register-customer`,
                request,
            )
            .pipe(tap((response) => this.handleAuthResponse(response)));
    }

    logout(): void {
        if (this.isBrowser) {
            localStorage.removeItem(TOKEN_KEY);
        }
        this.currentUser.set(null);
        this.router.navigate(["/auth/login"]);
    }

    getToken(): string | null {
        return this.isBrowser ? localStorage.getItem(TOKEN_KEY) : null;
    }

    isLoggedIn(): boolean {
        return this.getToken() !== null;
    }

    private handleAuthResponse(response: AuthResponse): void {
        if (this.isBrowser) {
            localStorage.setItem(TOKEN_KEY, response.token);
        }
        this.currentUser.set(response.userDetails);
    }
}
