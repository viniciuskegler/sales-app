import { inject, Injectable, PLATFORM_ID, signal, computed } from "@angular/core";
import { isPlatformBrowser } from "@angular/common";
import { CartItem } from "./cart.model";
import { ProductDTO } from "@features/products/model/products.model";

const CART_KEY = "cart";

@Injectable({ providedIn: "root" })
export class CartService {
    private readonly isBrowser = isPlatformBrowser(inject(PLATFORM_ID));

    readonly items = signal<CartItem[]>(this.loadCart());

    readonly totalItems = computed(() =>
        this.items().reduce((sum, item) => sum + item.quantity, 0),
    );

    readonly totalPrice = computed(() =>
        this.items().reduce((sum, item) => {
            const effectivePrice = item.discountPercentage
                ? item.price * (1 - item.discountPercentage / 100)
                : item.price;
            return sum + effectivePrice * item.quantity;
        }, 0),
    );

    addProduct(product: ProductDTO): void {
        this.addItem({
            productId: product.id,
            title: product.title,
            thumbnail: product.thumbnail,
            price: product.price,
            discountPercentage: product.discountPercentage,
            quantity: 1,
        });
    }

    addItem(product: CartItem): void {
        this.items.update((current) => {
            const existing = current.find((i) => i.productId === product.productId);
            if (existing) {
                return current.map((i) =>
                    i.productId === product.productId
                        ? { ...i, quantity: i.quantity + 1 }
                        : i,
                );
            }
            return [...current, { ...product, quantity: 1 }];
        });
        this.persistCart();
    }

    removeItem(productId: number): void {
        this.items.update((current) =>
            current.filter((i) => i.productId !== productId),
        );
        this.persistCart();
    }

    updateQuantity(productId: number, quantity: number): void {
        if (quantity < 1) {
            this.removeItem(productId);
            return;
        }
        this.items.update((current) =>
            current.map((i) =>
                i.productId === productId ? { ...i, quantity } : i,
            ),
        );
        this.persistCart();
    }

    clear(): void {
        this.items.set([]);
        this.persistCart();
    }

    private loadCart(): CartItem[] {
        if (!this.isBrowser) {
            return [];
        }
        try {
            const raw = localStorage.getItem(CART_KEY);
            return raw ? (JSON.parse(raw) as CartItem[]) : [];
        } catch {
            return [];
        }
    }

    private persistCart(): void {
        if (!this.isBrowser) {
            return;
        }
        localStorage.setItem(CART_KEY, JSON.stringify(this.items()));
    }
}
