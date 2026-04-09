package com.viniciuskegler.salesapp.payment.sqs;

import com.viniciuskegler.salesapp.payment.PaymentEvent;
import com.viniciuskegler.salesapp.payment.PaymentEventPublisher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import tools.jackson.databind.ObjectMapper;

@Component
@Profile("prod")
public class SqsPaymentEventPublisher implements PaymentEventPublisher {

    private final SqsClient sqsClient;
    private final ObjectMapper objectMapper;

    @Value("${SQS_QUEUE_URL}")
    private String queueUrl;

    public SqsPaymentEventPublisher(SqsClient sqsClient, ObjectMapper objectMapper) {
        this.sqsClient = sqsClient;
        this.objectMapper = objectMapper;
    }

    @Override
    public void publishPaymentRequest(Long orderId) {
        try {
            String body = objectMapper.writeValueAsString(new PaymentEvent(orderId));
            sqsClient.sendMessage(SendMessageRequest.builder()
                    .queueUrl(queueUrl)
                    .messageBody(body)
                    .build());
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize payment event for order " + orderId, e);
        }
    }
}