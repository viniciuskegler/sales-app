import { ResolveFn } from "@angular/router";
import { inject } from "@angular/core";
import { FiltersService } from "@features/filterlist/filters.service";
import { CategoryFilterDTO } from "@features/filterlist/model/filters.model";
import { catchError, of } from "rxjs";

export const filtersResolver: ResolveFn<CategoryFilterDTO[]> = () => {
    const filtersService = inject(FiltersService);
    return filtersService.getCategories().pipe(catchError(() => of([])));
};
