import { HttpEvent, HttpHandlerFn, HttpRequest } from "@angular/common/http";
import { inject } from "@angular/core";
import { Observable } from "rxjs";
import { AuthService } from "@features/auth/auth.service";

export function authInterceptor(
    req: HttpRequest<unknown>,
    next: HttpHandlerFn,
): Observable<HttpEvent<unknown>> {
    const token = inject(AuthService).getToken();

    if (!token) {
        return next(req);
    }

    return next(
        req.clone({ setHeaders: { Authorization: `Bearer ${token}` } }),
    );
}
