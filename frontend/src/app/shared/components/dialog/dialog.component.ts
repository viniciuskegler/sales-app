import {
    BasePortalOutlet,
    CdkPortalOutlet,
    type ComponentPortal,
    PortalModule,
    type TemplatePortal,
} from "@angular/cdk/portal";
import {
    ChangeDetectionStrategy,
    Component,
    type ComponentRef,
    type EmbeddedViewRef,
    inject,
    viewChild,
} from "@angular/core";
import { ZardButtonComponent } from "@shared/components/button/button.component";
import { ZardIconComponent } from "@shared/components/icon/icon.component";
import { ZardDialogRef } from "./dialog-ref";

@Component({
    selector: "z-dialog",
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [PortalModule, ZardButtonComponent, ZardIconComponent],
    template: `
        <button
            type="button"
            z-button
            zType="ghost"
            zSize="sm"
            class="absolute top-2 right-2"
            (click)="dialogRef.close()">
            <z-icon zType="x" />
        </button>
        <ng-template cdkPortalOutlet />
    `,
    host: {
        class: "relative bg-background rounded-xl shadow-lg w-full max-w-sm p-6",
    },
})
export class ZardDialogComponent extends BasePortalOutlet {
    protected readonly dialogRef = inject(ZardDialogRef);
    readonly portalOutlet = viewChild.required(CdkPortalOutlet);

    attachComponentPortal<T>(portal: ComponentPortal<T>): ComponentRef<T> {
        return this.portalOutlet().attachComponentPortal(portal);
    }

    attachTemplatePortal<C>(portal: TemplatePortal<C>): EmbeddedViewRef<C> {
        return this.portalOutlet().attachTemplatePortal(portal);
    }
}
