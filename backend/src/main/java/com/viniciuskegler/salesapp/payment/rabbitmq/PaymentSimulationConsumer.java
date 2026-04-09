package com.viniciuskegler.salesapp.payment.rabbitmq;

import com.viniciuskegler.salesapp.order.OrderRepository;
import com.viniciuskegler.salesapp.order.OrderStatusPublisher;
import com.viniciuskegler.salesapp.order.model.Order;
import com.viniciuskegler.salesapp.order.model.OrderStatus;
import com.viniciuskegler.salesapp.payment.PaymentEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;

@Component
@Profile("!prod")
public class PaymentSimulationConsumer {

    private static final Logger log = LoggerFactory.getLogger(PaymentSimulationConsumer.class);
    private static final Random random = new Random();

    private final OrderRepository orderRepository;
    private final OrderStatusPublisher statusPublisher;

    @Value("${payment.simulation.delay-ms:4000}")
    private long simulationDelayMs;

    public PaymentSimulationConsumer(OrderRepository orderRepository, OrderStatusPublisher statusPublisher) {
        this.orderRepository = orderRepository;
        this.statusPublisher = statusPublisher;
    }

    @RabbitListener(queues = PaymentConfig.PAYMENT_QUEUE)
    @Transactional
    public void processPayment(PaymentEvent event) {
        if (simulationDelayMs > 0) {
            try {
                Thread.sleep(simulationDelayMs);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }

        Order order = orderRepository.findById(event.orderId())
                .orElseThrow(() -> new RuntimeException("Order not found: " + event.orderId()));

        OrderStatus newStatus = random.nextDouble() < 0.8 ? OrderStatus.CONFIRMED : OrderStatus.CANCELLED;
        order.setStatus(newStatus);
        orderRepository.save(order);

        statusPublisher.publish(order, newStatus);

        log.info("Payment {} for order #{}", newStatus == OrderStatus.CONFIRMED ? "approved" : "declined", event.orderId());
    }
}
