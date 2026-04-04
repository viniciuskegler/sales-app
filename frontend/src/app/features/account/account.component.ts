import { ChangeDetectionStrategy, Component, inject } from "@angular/core";
import { AuthService } from "@features/auth/auth.service";
import { ZardButtonComponent } from "@shared/components/button/button.component";
import { ZardIconComponent } from "@shared/components/icon/icon.component";

@Component({
    selector: "app-account",
    templateUrl: "account.component.html",
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [ZardButtonComponent, ZardIconComponent],
})
export class AccountComponent {
    readonly authService = inject(AuthService);
}
