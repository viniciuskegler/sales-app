package com.viniciuskegler.salesapp.order;

import com.viniciuskegler.salesapp.customer.CustomerRepository;
import com.viniciuskegler.salesapp.customer.model.Customer;
import com.viniciuskegler.salesapp.order.dto.OrderDTO;
import com.viniciuskegler.salesapp.order.dto.OrderItemRequestDTO;
import com.viniciuskegler.salesapp.order.dto.PlaceOrderRequestDTO;
import com.viniciuskegler.salesapp.order.dto.mapper.OrderMapper;
import com.viniciuskegler.salesapp.order.model.Order;
import com.viniciuskegler.salesapp.order.model.OrderItem;
import com.viniciuskegler.salesapp.order.model.OrderStatus;
import com.viniciuskegler.salesapp.payment.PaymentEventPublisher;
import com.viniciuskegler.salesapp.product.ProductRepository;
import com.viniciuskegler.salesapp.product.model.Product;
import com.viniciuskegler.salesapp.shared.exception.RecordNotFoundException;
import com.viniciuskegler.salesapp.user.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private OrderMapper orderMapper;

    @Mock
    private PaymentEventPublisher paymentEventPublisher;

    private OrderService orderService;

    @BeforeEach
    void setUp() {
        orderService = new OrderService(orderRepository, customerRepository, productRepository, orderMapper, paymentEventPublisher);
    }

    @Test
    void placeOrder_throwsWhenCustomerNotFound() {
        User user = new User();
        PlaceOrderRequestDTO request = new PlaceOrderRequestDTO(List.of(new OrderItemRequestDTO(1L, 1)));

        when(customerRepository.findByUser(user)).thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class, () -> orderService.placeOrder(request, user));
    }

    @Test
    void placeOrder_throwsWhenProductNotFound() {
        User user = new User();
        Customer customer = new Customer();
        PlaceOrderRequestDTO request = new PlaceOrderRequestDTO(List.of(new OrderItemRequestDTO(999L, 1)));

        when(customerRepository.findByUser(user)).thenReturn(Optional.of(customer));
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class, () -> orderService.placeOrder(request, user));
    }

    @Test
    void placeOrder_setsStatusToPending() {
        User user = new User();
        Customer customer = new Customer();
        Product product = productWithPrice(1L, "10.00");

        PlaceOrderRequestDTO request = new PlaceOrderRequestDTO(List.of(new OrderItemRequestDTO(1L, 1)));

        when(customerRepository.findByUser(user)).thenReturn(Optional.of(customer));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        ArgumentCaptor<Order> captor = ArgumentCaptor.forClass(Order.class);
        when(orderRepository.save(captor.capture())).thenAnswer(inv -> inv.getArgument(0));
        when(orderMapper.toOrderDTO(any())).thenReturn(new OrderDTO(1L, "PENDING", null, null, null));

        orderService.placeOrder(request, user);

        assertThat(captor.getValue().getStatus()).isEqualTo(OrderStatus.PENDING);
    }

    @Test
    void placeOrder_snapshotsPriceAtOrderTime() {
        User user = new User();
        Customer customer = new Customer();
        Product product = productWithPrice(1L, "49.99");

        PlaceOrderRequestDTO request = new PlaceOrderRequestDTO(List.of(new OrderItemRequestDTO(1L, 3)));

        when(customerRepository.findByUser(user)).thenReturn(Optional.of(customer));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        ArgumentCaptor<Order> captor = ArgumentCaptor.forClass(Order.class);
        when(orderRepository.save(captor.capture())).thenAnswer(inv -> inv.getArgument(0));
        when(orderMapper.toOrderDTO(any())).thenReturn(new OrderDTO(1L, "PENDING", null, null, null));

        orderService.placeOrder(request, user);

        OrderItem item = captor.getValue().getItems().getFirst();
        assertThat(item.getUnitPrice()).isEqualByComparingTo("49.99");
        assertThat(item.getTotalPrice()).isEqualByComparingTo("149.97");
        assertThat(item.getQuantity()).isEqualTo(3);
    }

    @Test
    void placeOrder_withMultipleItems_sumsTotalCorrectly() {
        User user = new User();
        Customer customer = new Customer();
        Product p1 = productWithPrice(1L, "10.00");
        Product p2 = productWithPrice(2L, "5.00");

        PlaceOrderRequestDTO request = new PlaceOrderRequestDTO(List.of(
                new OrderItemRequestDTO(1L, 2),  // 20.00
                new OrderItemRequestDTO(2L, 4)   // 20.00
        ));

        when(customerRepository.findByUser(user)).thenReturn(Optional.of(customer));
        when(productRepository.findById(1L)).thenReturn(Optional.of(p1));
        when(productRepository.findById(2L)).thenReturn(Optional.of(p2));
        ArgumentCaptor<Order> captor = ArgumentCaptor.forClass(Order.class);
        when(orderRepository.save(captor.capture())).thenAnswer(inv -> inv.getArgument(0));
        when(orderMapper.toOrderDTO(any())).thenReturn(new OrderDTO(1L, "PENDING", null, null, null));

        orderService.placeOrder(request, user);

        Order saved = captor.getValue();
        assertThat(saved.getTotal()).isEqualByComparingTo("40.00");
        assertThat(saved.getItems()).hasSize(2);
    }

    @Test
    void getOrderById_throwsWhenOrderBelongsToAnotherCustomer() {
        User user = new User();
        Customer customer = new Customer();
        customer.setId(1L);

        when(customerRepository.findByUser(user)).thenReturn(Optional.of(customer));
        when(orderRepository.findByIdAndCustomer(99L, customer)).thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class, () -> orderService.getOrderById(99L, user));
    }

    @Test
    void getOrderById_throwsWhenCustomerNotFound() {
        User user = new User();

        when(customerRepository.findByUser(user)).thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class, () -> orderService.getOrderById(1L, user));
    }

    private Product productWithPrice(Long id, String price) {
        Product product = new Product();
        product.setId(id);
        product.setPrice(new BigDecimal(price));
        return product;
    }
}
