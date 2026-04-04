import { type ComponentType, Overlay } from "@angular/cdk/overlay";
import { ComponentPortal } from "@angular/cdk/portal";
import { isPlatformBrowser } from "@angular/common";
import {
    inject,
    Injectable,
    InjectionToken,
    Injector,
    PLATFORM_ID,
} from "@angular/core";
import { ZardDialogComponent } from "./dialog.component";
import { ZardDialogRef } from "./dialog-ref";

export const Z_DIALOG_DATA = new InjectionToken<unknown>("Z_DIALOG_DATA");

@Injectable({ providedIn: "root" })
export class ZardDialogService {
    private readonly overlay = inject(Overlay);
    private readonly injector = inject(Injector);
    private readonly platformId = inject(PLATFORM_ID);

    open<T>(component: ComponentType<T>, data?: unknown): ZardDialogRef {
        if (!isPlatformBrowser(this.platformId)) {
            return new ZardDialogRef(null as any, false, this.platformId);
        }

        const overlayRef = this.overlay.create({
            hasBackdrop: true,
            backdropClass: "cdk-overlay-dark-backdrop",
            positionStrategy: this.overlay
                .position()
                .global()
                .centerHorizontally()
                .centerVertically(),
            scrollStrategy: this.overlay.scrollStrategies.block(),
        });

        const dialogRef = new ZardDialogRef(overlayRef, true, this.platformId);

        const containerInjector = Injector.create({
            parent: this.injector,
            providers: [{ provide: ZardDialogRef, useValue: dialogRef }],
        });

        const containerPortal = new ComponentPortal(
            ZardDialogComponent,
            null,
            containerInjector,
        );
        const containerRef = overlayRef.attach(containerPortal);

        const contentInjector = Injector.create({
            parent: this.injector,
            providers: [
                { provide: ZardDialogRef, useValue: dialogRef },
                { provide: Z_DIALOG_DATA, useValue: data },
            ],
        });

        containerRef.instance.attachComponentPortal(
            new ComponentPortal(component, null, contentInjector),
        );

        return dialogRef;
    }
}
