import {
    ChangeDetectionStrategy,
    Component,
    input,
    output,
} from "@angular/core";
import { ZardButtonComponent } from "@shared/components/button/button.component";
import { ZardIconComponent } from "@shared/components/icon/icon.component";

@Component({
    selector: "app-error-state",
    templateUrl: "error-state.component.html",
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [ZardIconComponent, ZardButtonComponent],
})
export class ErrorStateComponent {
    readonly message = input<string>("Something went wrong");
    readonly retry = output<void>();
}
