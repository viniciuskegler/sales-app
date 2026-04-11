import { ChangeDetectionStrategy, Component, inject, signal } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { environment } from "environments/environment";

@Component({
    selector: "app-debug",
    templateUrl: "debug.component.html",
    changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DebugComponent {
    private readonly http = inject(HttpClient);

    readonly buildTime = signal<string | null>(null);
    readonly env = environment;

    constructor() {
        this.http.get<{ buildTime: string }>("/version.json").subscribe({
            next: (v) => this.buildTime.set(v.buildTime),
            error: () => this.buildTime.set("unavailable"),
        });
    }
}