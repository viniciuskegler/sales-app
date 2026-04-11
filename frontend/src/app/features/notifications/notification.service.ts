import { computed, effect, inject, Injectable, PLATFORM_ID, signal } from "@angular/core";
import { isPlatformBrowser } from "@angular/common";
import { Client } from "@stomp/stompjs";
import { AuthService } from "@features/auth/auth.service";
import { OrderNotification } from "./notification.model";
import { environment } from "environments/environment";


@Injectable({ providedIn: "root" })
export class NotificationService {
    private readonly authService = inject(AuthService);
    private readonly isBrowser = isPlatformBrowser(inject(PLATFORM_ID));

    private client: Client | null = null;
    private readonly _notifications = signal<OrderNotification[]>([]);

    readonly notifications = this._notifications.asReadonly();
    readonly unreadCount = computed(() => this._notifications().filter(n => !n.read).length);

    constructor() {
        effect(() => {
            const user = this.authService.currentUser();
            if (user) {
                this.connect(user.id);
            } else {
                this.disconnect();
            }
        });
    }

    markAllAsRead(): void {
        this._notifications.update(prev => prev.map(n => ({ ...n, read: true })));
    }

    private connect(userId: number): void {
        if (!this.isBrowser || !environment.wsEnabled) {
            return;
        }

        const brokerURL = environment.wsUrl || (window as any).__WS_URL__ || "ws://localhost:8080/ws";

        this.client = new Client({
            brokerURL,
            reconnectDelay: 0,
            onConnect: () => {
                this.client!.subscribe(`/topic/users/${userId}/notifications`, (message) => {
                    const { orderId, status, message: text } = JSON.parse(message.body);
                    this._notifications.update(prev => [
                        { orderId, status, message: text, read: false, receivedAt: new Date() },
                        ...prev,
                    ]);
                });
            },
        });

        this.client.activate();
    }

    private disconnect(): void {
        this.client?.deactivate();
        this.client = null;
        this._notifications.set([]);
    }
}
