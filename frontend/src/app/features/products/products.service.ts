import { HttpClient } from "@angular/common/http";
import { inject, Injectable } from "@angular/core";
import { ProductDetailsDTO, ProductDTO, ProductResponse } from "./model/products.model";
import { BehaviorSubject, Observable } from "rxjs";

@Injectable({ providedIn: "root" })
export class ProductsService {
    private readonly apiUrl = "api/products";
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
        this.httpClient
            .get<ProductResponse>(
                `${this.apiUrl}?page=${page}&pageSize=${limit}`,
            )
            .subscribe((response) => {
                this.productsSubject.next(response);
            })
            .add(() => this.finishLoading());
    }

    fetchProductById(productId: number): Observable<ProductDetailsDTO> {
        return this.httpClient.get<ProductDetailsDTO>(`${this.apiUrl}/${productId}`);
    }

    getProductById(productId: number): Observable<ProductDetailsDTO> {
        return this.fetchProductById(productId);
    }

    getRelatedProducts(productId: number): Observable<ProductDTO[]> {
        return this.httpClient.get<ProductDTO[]>(`${this.apiUrl}/${productId}/related`);
    }

    getProductsByName(name: string): Observable<ProductResponse> {
        return this.httpClient.get<ProductResponse>(
            `${this.apiUrl}?title=${name}&pageSize=5`,
        );
    }

    startLoading(): void {
        this.isLoadingSubject.next(true);
    }

    finishLoading(): void {
        this.isLoadingSubject.next(false);
    }
}
