import { ApplicationConfig, provideZoneChangeDetection } from "@angular/core";
import { provideRouter } from "@angular/router";
import { routes } from "./app.routes";
import {
    provideClientHydration,
    withEventReplay,
} from "@angular/platform-browser";
import {
    provideHttpClient,
    withFetch,
    withInterceptors,
} from "@angular/common/http";
import { provideZard } from "@shared/core/provider/providezard";
import { loggingInterceptor } from "./core/interceptors/log-interceptor";
import { IMAGE_LOADER, ImageLoaderConfig } from "@angular/common";

export const appConfig: ApplicationConfig = {
    providers: [
        provideZoneChangeDetection({ eventCoalescing: true }),
        provideRouter(routes),
        provideClientHydration(withEventReplay()),
        provideHttpClient(withInterceptors([loggingInterceptor]), withFetch()),
        provideZard(),
        {
            provide: IMAGE_LOADER,
            useValue: (config: ImageLoaderConfig) => {
                return config.src;
            },
        },
    ],
};
