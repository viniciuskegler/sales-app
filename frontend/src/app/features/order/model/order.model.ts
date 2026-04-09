export enum OrderStatus {
    PENDING = "PENDING",
    CONFIRMED = "CONFIRMED",
    SHIPPED = "SHIPPED",
    DELIVERED = "DELIVERED",
    CANCELLED = "CANCELLED",
}

export interface OrderItemDTO {
    productId: number;
    title: string;
    thumbnail: string;
    quantity: number;
    unitPrice: number;
    totalPrice: number;
}

export interface OrderDTO {
    id: number;
    status: OrderStatus;
    total: number;
    createdAt: string;
    items: OrderItemDTO[];
}

export interface PlaceOrderItemRequest {
    productId: number;
    quantity: number;
}

export interface PlaceOrderRequest {
    items: PlaceOrderItemRequest[];
}
