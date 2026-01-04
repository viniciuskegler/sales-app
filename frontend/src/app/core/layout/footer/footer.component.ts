import { AsyncPipe } from "@angular/common";
import { Component, inject } from "@angular/core";
import { LayoutService } from "@core/services/layout-service.service";
import { Observable } from "rxjs";

@Component({
    standalone: true,
    selector: "app-footer",
    templateUrl: "footer.component.html",
    imports: [AsyncPipe],
})
export class FooterComponent {
    showFooterObs: Observable<boolean>;

    layoutService = inject(LayoutService);

    constructor() {
        this.showFooterObs = this.layoutService.showFooterObservable;
    }
}
