import { AsyncPipe } from "@angular/common";
import { Component } from "@angular/core";
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

    constructor(private layoutService: LayoutService) {
        this.showFooterObs = layoutService.showFooterObservable;
    }
}
