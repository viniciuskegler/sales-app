package com.viniciuskegler.salesapp.payment;

public interface PaymentEventPublisher {
    void publishPaymentRequest(Long orderId);
}
