import { AsyncPipe } from "@angular/common";
import { Component } from "@angular/core";
import { LayoutService } from "@core/services/layout-service.service";
import { SearchBarComponent } from "@features/searchbar/search-bar.component";
import { ZardIconComponent } from "@shared/components/icon/icon.component";
import { Observable } from "rxjs";

@Component({
    standalone: true,
    selector: "app-header",
    templateUrl: "header.component.html",
    imports: [AsyncPipe, SearchBarComponent, ZardIconComponent],
})
export class HeaderComponent {
    showHeaderObs: Observable<boolean>;

    constructor(private layoutService: LayoutService) {
        this.showHeaderObs = layoutService.showHeaderObservable;
    }
}
