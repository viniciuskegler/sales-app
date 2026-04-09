package com.viniciuskegler.salesapp.payment;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class PaymentEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public PaymentEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishPaymentRequest(Long orderId) {
        rabbitTemplate.convertAndSend(
                PaymentConfig.PAYMENT_EXCHANGE,
                PaymentConfig.PAYMENT_ROUTING_KEY,
                new PaymentEvent(orderId)
        );
    }
}
