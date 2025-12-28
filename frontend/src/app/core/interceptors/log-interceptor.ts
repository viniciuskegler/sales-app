import {
    HttpEvent,
    HttpHandlerFn,
    HttpRequest,
    HttpResponse,
} from "@angular/common/http";
import { Observable, tap } from "rxjs";

export function loggingInterceptor(
    req: HttpRequest<unknown>,
    next: HttpHandlerFn,
): Observable<HttpEvent<unknown>> {
    return next(req).pipe(
        tap({
            next: (event: HttpEvent<any>) => {
                if (event instanceof HttpResponse) {
                    console.log("HTTP Request:", req);
                    console.log("Response Body:", event.body);
                }
            },
            error: (error: any) => {
                console.error("HTTP Request Error:", error);
            },
        }),
    );
}
