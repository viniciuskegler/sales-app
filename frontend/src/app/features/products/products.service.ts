import { HttpClient } from "@angular/common/http";
import { inject, Injectable, signal } from "@angular/core";
import { ProductDTO, ProductResponse } from "./model/products.model";
import { BehaviorSubject, Observable, of } from "rxjs";

@Injectable({ providedIn: "root" })
export class ProductsService {
    private readonly apiUrl = "https://dummyjson.com/products";
    private productsSubject = new BehaviorSubject<ProductResponse | null>(null);
    public productsObservable = this.productsSubject.asObservable();

    private isLoadingSubject = new BehaviorSubject<boolean>(false);
    public isLoadingObservable = this.isLoadingSubject.asObservable();

    httpClient = inject(HttpClient);

    getCachedProducts(): ProductResponse | null {
        return this.productsSubject.getValue();
    }

    fetchProducts(page: number, limit: number): void {
        this.startLoading();
        this.productsSubject.next(null);
        const skip = page * limit;
        this.httpClient
            .get<ProductResponse>(`${this.apiUrl}?limit=${limit}&skip=${skip}`)
            .subscribe((response) => {
                this.productsSubject.next(response);
            })
            .add(() => this.finishLoading());
    }

    fetchProductById(productId: number): Observable<ProductDTO> {
        return this.httpClient.get<ProductDTO>(`${this.apiUrl}/${productId}`);
    }

    getProductById(productId: number): Observable<ProductDTO> {
        let productObs: Observable<ProductDTO> =
            this.fetchProductById(productId);
        if (this.getCachedProducts() != null) {
            const cachedProduct = this.getCachedProducts()!.products.find(
                (product) => product.id === productId,
            );
            if (cachedProduct) {
                productObs = of(cachedProduct);
            }
        }
        return productObs;
    }

    getProductsByName(name: string): Observable<ProductResponse> {
        return this.httpClient.get<ProductResponse>(
            `${this.apiUrl}/search?q=${name}&limit=5`,
        );
    }

    startLoading(): void {
        this.isLoadingSubject.next(true);
    }

    finishLoading(): void {
        this.isLoadingSubject.next(false);
    }
}
