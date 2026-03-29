import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../auth.service';

@Component({
    selector: 'app-register',
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [ReactiveFormsModule, RouterLink],
    template: `<div>register stub</div>`,
})
export class RegisterComponent {
    private readonly authService = inject(AuthService);
    private readonly router = inject(Router);

    readonly form = new FormGroup({
        email: new FormControl('', [Validators.required, Validators.email]),
        password: new FormControl('', [Validators.required, Validators.minLength(8)]),
        firstName: new FormControl('', [Validators.required]),
        lastName: new FormControl('', [Validators.required]),
        phoneNumber: new FormControl('', [Validators.required]),
    });

    submit(): void {
        if (this.form.invalid) { return; }
        const value = this.form.getRawValue();
        this.authService
            .register({
                email: value.email!,
                password: value.password!,
                firstName: value.firstName!,
                lastName: value.lastName!,
                phoneNumber: value.phoneNumber!,
            })
            .subscribe({
                next: () => this.router.navigate(['/']),
                error: (err) => console.error('Registration failed', err),
            });
    }
}
