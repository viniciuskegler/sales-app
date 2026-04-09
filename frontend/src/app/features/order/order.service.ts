import { inject, Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { environment } from "environments/environment";
import { OrderDTO, PlaceOrderRequest } from "./model/order.model";

@Injectable({ providedIn: "root" })
export class OrderService {
    private readonly http = inject(HttpClient);

    placeOrder(request: PlaceOrderRequest) {
        return this.http.post<OrderDTO>(`${environment.baseurl}/orders`, request);
    }

    getMyOrders() {
        return this.http.get<OrderDTO[]>(`${environment.baseurl}/orders`);
    }

    getOrderById(id: number) {
        return this.http.get<OrderDTO>(`${environment.baseurl}/orders/${id}`);
    }
}
