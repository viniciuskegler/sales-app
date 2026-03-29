import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../auth.service';

@Component({
    selector: 'app-login',
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [ReactiveFormsModule, RouterLink],
    template: `<div>login stub</div>`,
})
export class LoginComponent {
    private readonly authService = inject(AuthService);
    private readonly router = inject(Router);

    readonly form = new FormGroup({
        email: new FormControl('', [Validators.required, Validators.email]),
        password: new FormControl('', [Validators.required]),
    });

    submit(): void {
        if (this.form.invalid) { return; }
        const { email, password } = this.form.getRawValue();
        this.authService.login({ email: email!, password: password! }).subscribe({
            next: () => this.router.navigate(['/']),
            error: (err) => console.error('Login failed', err),
        });
    }
}
