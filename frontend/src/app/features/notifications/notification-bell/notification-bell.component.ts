import { ChangeDetectionStrategy, Component, inject, signal } from "@angular/core";
import { DatePipe } from "@angular/common";
import { NotificationService } from "@features/notifications/notification.service";
import { ZardIconComponent } from "@shared/components/icon/icon.component";

@Component({
    selector: "app-notification-bell",
    templateUrl: "notification-bell.component.html",
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [DatePipe, ZardIconComponent],
})
export class NotificationBellComponent {
    readonly notificationService = inject(NotificationService);
    readonly isOpen = signal(false);

    toggle(): void {
        const opening = !this.isOpen();
        this.isOpen.set(opening);
        if (opening) {
            this.notificationService.markAllAsRead();
        }
    }
}
