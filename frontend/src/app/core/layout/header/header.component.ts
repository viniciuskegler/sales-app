import { AsyncPipe } from "@angular/common";
import { Component, inject } from "@angular/core";
import { RouterLink } from "@angular/router";
import { LayoutService } from "@core/services/layout-service.service";
import { AuthService } from "@features/auth/auth.service";
import { AuthModalComponent } from "@features/auth/auth-modal/auth-modal.component";
import { CartService } from "@features/cart/cart.service";
import { SearchBarComponent } from "@features/searchbar/search-bar.component";
import { ZardDialogService } from "@shared/components/dialog/dialog.service";
import { ZardIconComponent } from "@shared/components/icon/icon.component";
import { Observable } from "rxjs";

@Component({
    standalone: true,
    selector: "app-header",
    templateUrl: "header.component.html",
    imports: [AsyncPipe, RouterLink, SearchBarComponent, ZardIconComponent],
})
export class HeaderComponent {
    private readonly layoutService = inject(LayoutService);
    readonly authService = inject(AuthService);
    readonly cartService = inject(CartService);
    private readonly dialogService = inject(ZardDialogService);

    showHeaderObs: Observable<boolean>;

    constructor() {
        this.showHeaderObs = this.layoutService.showHeaderObservable;
    }

    openAuthModal(): void {
        this.dialogService.open(AuthModalComponent);
    }
}
