package com.viniciuskegler.salesapp.payment;

import com.viniciuskegler.salesapp.order.OrderRepository;
import com.viniciuskegler.salesapp.order.model.Order;
import com.viniciuskegler.salesapp.order.model.OrderStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;

@Component
public class PaymentSimulationConsumer {

    private static final Logger log = LoggerFactory.getLogger(PaymentSimulationConsumer.class);
    private static final Random random = new Random();

    private final OrderRepository orderRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Value("${payment.simulation.delay-ms:4000}")
    private long simulationDelayMs;

    public PaymentSimulationConsumer(OrderRepository orderRepository, SimpMessagingTemplate messagingTemplate) {
        this.orderRepository = orderRepository;
        this.messagingTemplate = messagingTemplate;
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

        boolean approved = random.nextDouble() < 0.8;
        order.setStatus(approved ? OrderStatus.CONFIRMED : OrderStatus.CANCELLED);
        orderRepository.save(order);

        messagingTemplate.convertAndSend(
                "/topic/orders/" + event.orderId(),
                new OrderStatusUpdateDTO(order.getStatus().name())
        );

        log.info("Payment {} for order #{}", approved ? "approved" : "declined", event.orderId());
    }
}