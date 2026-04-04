import { inject } from "@angular/core";
import { CanActivateFn } from "@angular/router";
import { AuthService } from "@features/auth/auth.service";
import { AuthModalComponent } from "@features/auth/auth-modal/auth-modal.component";
import { ZardDialogService } from "@shared/components/dialog/dialog.service";

export const authGuard: CanActivateFn = () => {
    const authService = inject(AuthService);

    if (authService.isLoggedIn()) {
        return true;
    }

    inject(ZardDialogService).open(AuthModalComponent);
    return false;
};
