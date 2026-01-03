import { Injectable } from "@angular/core";
import { BehaviorSubject, Observable } from "rxjs";

@Injectable({ providedIn: "root" })
export class LayoutService {
    private showFooterSubject = new BehaviorSubject<boolean>(false);
    showFooterObservable: Observable<boolean> =
        this.showFooterSubject.asObservable();

    private showHeaderSubject = new BehaviorSubject<boolean>(true);
    showHeaderObservable: Observable<boolean> =
        this.showHeaderSubject.asObservable();

    hideFooter() {
        this.showFooterSubject.next(false);
    }

    displayFooter() {
        this.showFooterSubject.next(true);
    }

    hideHeader() {
        this.showHeaderSubject.next(false);
    }

    displayHeader() {
        this.showHeaderSubject.next(true);
    }
}
