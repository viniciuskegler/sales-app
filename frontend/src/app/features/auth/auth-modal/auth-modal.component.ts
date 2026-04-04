import {
    ChangeDetectionStrategy,
    Component,
    inject,
    signal,
} from "@angular/core";
import {
    FormControl,
    FormGroup,
    ReactiveFormsModule,
    Validators,
} from "@angular/forms";
import { AuthService } from "../auth.service";
import { ZardDialogRef } from "@shared/components/dialog/dialog-ref";
import { ZardInputDirective } from "@shared/components/input/input.directive";
import { ZardButtonComponent } from "@shared/components/button/button.component";
import { ZardIconComponent } from "@shared/components/icon/icon.component";

type Tab = "login" | "register";

@Component({
    selector: "app-auth-modal",
    templateUrl: "auth-modal.component.html",
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        ReactiveFormsModule,
        ZardInputDirective,
        ZardButtonComponent,
        ZardIconComponent,
    ],
})
export class AuthModalComponent {
    private readonly authService = inject(AuthService);
    private readonly dialogRef = inject(ZardDialogRef);

    readonly activeTab = signal<Tab>("login");
    readonly isLoading = signal(false);
    readonly errorMessage = signal<string | null>(null);
    readonly showPassword = signal(false);

    readonly loginForm = new FormGroup({
        email: new FormControl("", [Validators.required, Validators.email]),
        password: new FormControl("", [Validators.required]),
    });

    readonly registerForm = new FormGroup({
        firstName: new FormControl("", [Validators.required]),
        lastName: new FormControl("", [Validators.required]),
        email: new FormControl("", [Validators.required, Validators.email]),
        phoneNumber: new FormControl("", [Validators.required]),
        password: new FormControl("", [
            Validators.required,
            Validators.minLength(8),
        ]),
    });

    get lEmail() { return this.loginForm.controls.email; }
    get lPassword() { return this.loginForm.controls.password; }
    get rFirstName() { return this.registerForm.controls.firstName; }
    get rLastName() { return this.registerForm.controls.lastName; }
    get rEmail() { return this.registerForm.controls.email; }
    get rPhoneNumber() { return this.registerForm.controls.phoneNumber; }
    get rPassword() { return this.registerForm.controls.password; }

    switchTab(tab: Tab): void {
        this.activeTab.set(tab);
        this.errorMessage.set(null);
        this.showPassword.set(false);
    }

    submitLogin(): void {
        if (this.loginForm.invalid) {
            this.loginForm.markAllAsTouched();
            return;
        }
        this.isLoading.set(true);
        this.errorMessage.set(null);
        const { email, password } = this.loginForm.getRawValue();
        this.authService.login({ email: email!, password: password! }).subscribe({
            next: () => this.dialogRef.close(),
            error: () => {
                this.errorMessage.set("Invalid email or password.");
                this.isLoading.set(false);
            },
        });
    }

    submitRegister(): void {
        if (this.registerForm.invalid) {
            this.registerForm.markAllAsTouched();
            return;
        }
        this.isLoading.set(true);
        this.errorMessage.set(null);
        const value = this.registerForm.getRawValue();
        this.authService
            .register({
                email: value.email!,
                password: value.password!,
                firstName: value.firstName!,
                lastName: value.lastName!,
                phoneNumber: value.phoneNumber!,
            })
            .subscribe({
                next: () => this.dialogRef.close(),
                error: (err) => {
                    this.errorMessage.set(
                        err?.status === 409
                            ? "An account with this email already exists."
                            : "Registration failed. Please try again.",
                    );
                    this.isLoading.set(false);
                },
            });
    }
}
