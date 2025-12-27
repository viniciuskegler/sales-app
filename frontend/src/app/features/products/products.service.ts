import { HttpClient } from "@angular/common/http";
import { inject, Injectable, signal } from "@angular/core";
import { ProductDTO, ProductResponse } from "./products.model";
import { BehaviorSubject, Observable, of } from "rxjs";

@Injectable({ providedIn: "root" })
export class ProductsService {
    //TODO guardar as infos de paginação em uma variavel do tipo de paginação 
    //e as infos do produto em si em outra, ai quando faz a chamada pega a pagina, o skip e faz os calculos.

    private readonly apiUrl = "https://dummyjson.com/products";
    private productsSubject = new BehaviorSubject<ProductResponse | null>(null);
    // private lastPage = signal<number>(0);
    public productsObservable = this.productsSubject.asObservable();

    httpClient = inject(HttpClient);

    getCachedProducts(): ProductResponse | null {
        return this.productsSubject.getValue();
    }

    fetchProducts(page: number, limit: number): void {
        const skip = page * limit;
        this.httpClient
            .get<ProductResponse>(`${this.apiUrl}?limit=${limit}&skip=${skip}`)
            .subscribe((response) => {
                this.productsSubject.next(response);
            });
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
}
