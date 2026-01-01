import {
    Component,
    effect,
    input,
    linkedSignal,
    model,
    OnInit
} from "@angular/core";
import { CategoryFilterDTO, CategoryFilterValue } from "./model/filters.model";
import {
    FormArray,
    FormControl,
    FormGroup,
    ReactiveFormsModule,
} from "@angular/forms";
import { ZardCheckboxComponent } from "@shared/components/checkbox/checkbox.component";
import { ZardAccordionComponent } from "@shared/components/accordion/accordion.component";
import { ZardAccordionItemComponent } from "@shared/components/accordion/accordion-item.component";
import { ZardSliderComponent } from "@shared/components/slider/slider.component";
import { CurrencyPipe } from "@angular/common";
import { ZardIconComponent } from "@shared/components/icon/icon.component";
import { toSignal } from "@angular/core/rxjs-interop";

@Component({
    selector: "filter-list",
    templateUrl: "filter-list.component.html",
    imports: [
        ReactiveFormsModule,
        ZardAccordionComponent,
        ZardAccordionItemComponent,
        ZardCheckboxComponent,
        ZardSliderComponent,
        ZardIconComponent,
        CurrencyPipe,
    ],
})
export class FilterListComponent implements OnInit {
    readonly categoryList = input<CategoryFilterDTO[]>([]);
    readonly categoryFilterValue = model<CategoryFilterValue[]>();

    //TODO. Dummyjson doesnt support price filters
    readonly minPrice = input<number | null>(null);
    readonly maxPrice = input<number | null>(null);
    readonly priceSliderValue = model(this.minPrice());

    openFilters = ["prices", "categories"];

    filtersForm = new FormGroup({
        categories: new FormArray([]),
    });

    readonly categoriesValues = toSignal(
        this.filtersForm
            .get("categories")!
            .valueChanges
    );

    get categoriesFormArray(): FormGroup[] {
        return (this.filtersForm.get("categories") as FormArray)
            .controls as FormGroup[];
    }

    constructor() {
        effect(() => {
            const categories = this.categoriesValues() as CategoryFilterValue[];
            if (categories && categories.length > 0) {
                const selectedCategories = categories
                    .filter((c) => c.checked)
                    .map((c) => ({ id: c.id, checked: c.checked }));
                this.categoryFilterValue.set(selectedCategories);
            }
        });
    }

    ngOnInit() {
        const categories = this.categoryList();
        const categoriesFormArray = this.filtersForm.get(
            "categories",
        ) as FormArray;
        categories.forEach((category: CategoryFilterDTO) => {
            categoriesFormArray.push(
                new FormGroup({
                    id: new FormControl<string>(category.slug),
                    name: new FormControl<string>(category.name),
                    checked: new FormControl<boolean>(false),
                }),
                { emitEvent: false }
            );
        });

        const selected = this.categoryFilterValue();
        if (selected && selected.length) {
            this.selectCategories(selected);
        }
    }

    private selectCategories(selected: CategoryFilterValue[]) {
        const selectedSet = selected.map((c) => c.id);
        this.categoriesFormArray.forEach((group) => {
            const idControl = group.get("id") as FormControl<string>;
            const checkedControl = group.get("checked") as FormControl<boolean>;
            const shouldBeChecked = selectedSet.includes(idControl.value);
            if (checkedControl.value !== shouldBeChecked) {
                checkedControl.setValue(shouldBeChecked, { emitEvent: false });
            }
        });
    }
    
}
