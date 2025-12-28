import { HttpClient } from "@angular/common/http";
import { inject, Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { CategoryFilterDTO } from "./model/filters.model";

@Injectable({ providedIn: "root" })
export class FiltersService {
    private readonly apiUrl = "https://dummyjson.com/products/categories";

    httpService = inject(HttpClient);

    getCategories(): Observable<CategoryFilterDTO[]> {
        return this.httpService.get<CategoryFilterDTO[]>(this.apiUrl);
    }
}
