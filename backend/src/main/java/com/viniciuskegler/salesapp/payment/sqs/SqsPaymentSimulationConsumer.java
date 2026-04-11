package com.viniciuskegler.salesapp.payment.sqs;

import tools.jackson.databind.ObjectMapper;
import com.viniciuskegler.salesapp.order.OrderRepository;
import com.viniciuskegler.salesapp.order.OrderStatusPublisher;
import com.viniciuskegler.salesapp.order.model.Order;
import com.viniciuskegler.salesapp.order.model.OrderStatus;
import com.viniciuskegler.salesapp.payment.PaymentEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;

import java.util.List;
import java.util.Random;

@Component
@Profile("prod")
public class SqsPaymentSimulationConsumer {

    private static final Logger log = LoggerFactory.getLogger(SqsPaymentSimulationConsumer.class);
    private static final Random random = new Random();

    private final SqsClient sqsClient;
    private final OrderRepository orderRepository;
    private final OrderStatusPublisher statusPublisher;
    private final ObjectMapper objectMapper;

    @Value("${SQS_QUEUE_URL}")
    private String queueUrl;

    @Value("${payment.simulation.delay-ms:4000}")
    private long simulationDelayMs;

    public SqsPaymentSimulationConsumer(SqsClient sqsClient,
                                        OrderRepository orderRepository,
                                        OrderStatusPublisher statusPublisher,
                                        ObjectMapper objectMapper) {
        this.sqsClient = sqsClient;
        this.orderRepository = orderRepository;
        this.statusPublisher = statusPublisher;
        this.objectMapper = objectMapper;
    }

    @Scheduled(fixedDelay = 1000)
    public void poll() {
        List<Message> messages = sqsClient.receiveMessage(ReceiveMessageRequest.builder()
                .queueUrl(queueUrl)
                .maxNumberOfMessages(10)
                .waitTimeSeconds(20)
                .build()).messages();

        for (Message message : messages) {
            try {
                processPayment(message);
            } catch (Exception e) {
                log.error("Failed to process SQS message {}", message.messageId(), e);
            }
        }
    }

    @Transactional
    public void processPayment(Message message) throws Exception {
        PaymentEvent event = objectMapper.readValue(message.body(), PaymentEvent.class);

        if (simulationDelayMs > 0) {
            Thread.sleep(simulationDelayMs);
        }

        Order order = orderRepository.findByIdWithCustomer(event.orderId())
                .orElseThrow(() -> new RuntimeException("Order not found: " + event.orderId()));

        OrderStatus newStatus = random.nextDouble() < 0.8 ? OrderStatus.CONFIRMED : OrderStatus.CANCELLED;
        order.setStatus(newStatus);
        orderRepository.save(order);

        statusPublisher.publish(order, newStatus);

        sqsClient.deleteMessage(DeleteMessageRequest.builder()
                .queueUrl(queueUrl)
                .receiptHandle(message.receiptHandle())
                .build());

        log.info("Payment {} for order #{}", newStatus == OrderStatus.CONFIRMED ? "approved" : "declined", event.orderId());
    }
}
