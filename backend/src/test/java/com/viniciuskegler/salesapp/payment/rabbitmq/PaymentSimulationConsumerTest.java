package com.viniciuskegler.salesapp.payment.rabbitmq;

import com.viniciuskegler.salesapp.order.OrderRepository;
import com.viniciuskegler.salesapp.order.OrderStatusPublisher;
import com.viniciuskegler.salesapp.order.model.Order;
import com.viniciuskegler.salesapp.order.model.OrderStatus;
import com.viniciuskegler.salesapp.payment.PaymentEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentSimulationConsumerTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderStatusPublisher statusPublisher;

    private PaymentSimulationConsumer consumer;

    @BeforeEach
    void setUp() {
        consumer = new PaymentSimulationConsumer(orderRepository, statusPublisher);
        ReflectionTestUtils.setField(consumer, "simulationDelayMs", 0L);
    }

    @RepeatedTest(10)
    void processPayment_setsStatusToConfirmedOrCancelled() {
        Order order = new Order();
        order.setId(1L);
        order.setStatus(OrderStatus.PENDING);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        consumer.processPayment(new PaymentEvent(1L));

        assertThat(order.getStatus()).isIn(OrderStatus.CONFIRMED, OrderStatus.CANCELLED);
        verify(orderRepository).save(order);
    }

    @Test
    void processPayment_throwsWhenOrderNotFound() {
        when(orderRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> consumer.processPayment(new PaymentEvent(99L)));
    }
}