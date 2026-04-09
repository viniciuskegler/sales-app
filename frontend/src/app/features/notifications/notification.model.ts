export interface OrderNotification {
    orderId: number;
    status: string;
    message: string;
    read: boolean;
    receivedAt: Date;
}
