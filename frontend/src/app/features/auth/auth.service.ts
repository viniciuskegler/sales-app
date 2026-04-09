import { inject, Injectable, PLATFORM_ID, signal } from "@angular/core";
import { isPlatformBrowser } from "@angular/common";
import { HttpClient } from "@angular/common/http";
import { Router } from "@angular/router";
import { tap } from "rxjs";
import { toast } from "ngx-sonner";
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

    readonly currentUser = signal<UserDetails | null>(this.restoreUser());
    readonly isLoggedIn = signal<boolean>(this.getToken() !== null);
    readonly sessionExpired = signal(false);

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
        this.clearSession();
        this.router.navigate(["/"]);
    }

    expireSession(): void {
        this.clearSession();
        this.sessionExpired.set(true);
        if (this.isBrowser) {
            toast.warning("Session expired", {
                description: "Please sign in again to continue.",
            });
        }
    }

    private clearSession(): void {
        if (this.isBrowser) {
            localStorage.removeItem(TOKEN_KEY);
        }
        this.currentUser.set(null);
        this.isLoggedIn.set(false);
    }

    getToken(): string | null {
        return this.isBrowser ? localStorage.getItem(TOKEN_KEY) : null;
    }

    private restoreUser(): UserDetails | null {
        const token = this.getToken();
        if (!token) {
            return null;
        }
        try {
            const base64 = token.split(".")[1].replace(/-/g, "+").replace(/_/g, "/");
            const bytes = Uint8Array.from(atob(base64), (c) => c.charCodeAt(0));
            const payload = JSON.parse(new TextDecoder().decode(bytes));
            return {
                id: Number(payload["id"]),
                email: payload["username"],
                role: payload["role"],
                fullName: payload["fullName"],
            };
        } catch {
            return null;
        }
    }

    private handleAuthResponse(response: AuthResponse): void {
        if (this.isBrowser) {
            localStorage.setItem(TOKEN_KEY, response.token);
        }
        this.currentUser.set(response.userDetails);
        this.isLoggedIn.set(true);
        this.sessionExpired.set(false);
    }
}
