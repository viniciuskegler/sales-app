package com.viniciuskegler.salesapp.payment.rabbitmq;

import com.viniciuskegler.salesapp.payment.PaymentEvent;
import com.viniciuskegler.salesapp.payment.PaymentEventPublisher;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("!prod")
public class RabbitMqPaymentEventPublisher implements PaymentEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public RabbitMqPaymentEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void publishPaymentRequest(Long orderId) {
        rabbitTemplate.convertAndSend(
                PaymentConfig.PAYMENT_EXCHANGE,
                PaymentConfig.PAYMENT_ROUTING_KEY,
                new PaymentEvent(orderId)
        );
    }
}