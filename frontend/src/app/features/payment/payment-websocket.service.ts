import { inject, Injectable, PLATFORM_ID } from "@angular/core";
import { isPlatformBrowser } from "@angular/common";
import { Observable, EMPTY } from "rxjs";
import { Client } from "@stomp/stompjs";
import { environment } from "environments/environment";

@Injectable({ providedIn: "root" })
export class PaymentWebSocketService {
    private readonly isBrowser = isPlatformBrowser(inject(PLATFORM_ID));

    watchOrderStatus(orderId: number): Observable<string> {
        if (!this.isBrowser) {
            return EMPTY;
        }

        return new Observable<string>((observer) => {
            const protocol = window.location.protocol === "https:" ? "wss:" : "ws:";
            const brokerURL = environment.wsUrl || `${protocol}//${window.location.host}/ws`;
            const client = new Client({
                brokerURL,
                onConnect: () => {
                    client.subscribe(`/topic/orders/${orderId}`, (message) => {
                        const { status } = JSON.parse(message.body);
                        observer.next(status);
                    });
                },
                onStompError: (frame) => observer.error(frame),
            });

            client.activate();

            return () => { client.deactivate(); };
        });
    }
}
