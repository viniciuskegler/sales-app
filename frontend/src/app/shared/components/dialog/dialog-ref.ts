import type { OverlayRef } from "@angular/cdk/overlay";
import { isPlatformBrowser } from "@angular/common";
import { Inject, PLATFORM_ID } from "@angular/core";
import { filter, fromEvent, Subject, takeUntil } from "rxjs";

export class ZardDialogRef {
    private readonly destroy$ = new Subject<void>();
    private isClosing = false;

    constructor(
        private readonly overlayRef: OverlayRef,
        maskClosable: boolean,
        @Inject(PLATFORM_ID) platformId: object,
    ) {
        if (maskClosable && isPlatformBrowser(platformId)) {
            this.overlayRef
                .backdropClick()
                .pipe(takeUntil(this.destroy$))
                .subscribe(() => this.close());
        }

        if (isPlatformBrowser(platformId)) {
            fromEvent<KeyboardEvent>(document, "keydown")
                .pipe(
                    filter((e) => e.key === "Escape"),
                    takeUntil(this.destroy$),
                )
                .subscribe(() => this.close());
        }
    }

    close(): void {
        if (this.isClosing) {
            return;
        }
        this.isClosing = true;
        this.destroy$.next();
        this.destroy$.complete();
        this.overlayRef.dispose();
    }
}
