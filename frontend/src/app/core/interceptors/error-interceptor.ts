import { HttpErrorResponse, HttpEvent, HttpHandlerFn, HttpRequest } from "@angular/common/http";
import { inject } from "@angular/core";
import { Observable, tap } from "rxjs";
import { AuthService } from "@features/auth/auth.service";

export function errorInterceptor(
    req: HttpRequest<unknown>,
    next: HttpHandlerFn,
): Observable<HttpEvent<unknown>> {
    const authService = inject(AuthService);

    return next(req).pipe(
        tap({
            error: (error: unknown) => {
                if (error instanceof HttpErrorResponse && error.status === 401 && authService.isLoggedIn()) {
                    authService.expireSession();
                }
            },
        }),
    );
}
