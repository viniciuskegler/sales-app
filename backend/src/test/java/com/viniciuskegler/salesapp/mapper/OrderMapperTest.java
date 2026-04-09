package com.viniciuskegler.salesapp.mapper;

import com.viniciuskegler.salesapp.customer.model.Customer;
import com.viniciuskegler.salesapp.order.dto.OrderDTO;
import com.viniciuskegler.salesapp.order.dto.OrderItemDTO;
import com.viniciuskegler.salesapp.order.dto.mapper.OrderMapper;
import com.viniciuskegler.salesapp.order.model.Order;
import com.viniciuskegler.salesapp.order.model.OrderItem;
import com.viniciuskegler.salesapp.order.model.OrderStatus;
import com.viniciuskegler.salesapp.product.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class OrderMapperTest {

    private OrderMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new OrderMapper();
    }

    @Test
    void toOrderDTO_mapsAllFields() {
        Product product = new Product();
        product.setId(1L);
        product.setTitle("Wireless Mouse");
        product.setThumbnail("mouse.jpg");

        OrderItem item = new OrderItem();
        item.setProduct(product);
        item.setQuantity(2);
        item.setUnitPrice(new BigDecimal("29.99"));
        item.setTotalPrice(new BigDecimal("59.98"));

        Customer customer = new Customer();
        customer.setId(1L);

        LocalDateTime now = LocalDateTime.now();

        Order order = new Order();
        order.setId(10L);
        order.setCustomer(customer);
        order.setStatus(OrderStatus.PENDING);
        order.setTotal(new BigDecimal("59.98"));
        order.setCreatedAt(now);
        order.setItems(List.of(item));

        OrderDTO dto = mapper.toOrderDTO(order);

        assertThat(dto.id()).isEqualTo(10L);
        assertThat(dto.status()).isEqualTo("PENDING");
        assertThat(dto.total()).isEqualByComparingTo("59.98");
        assertThat(dto.createdAt()).isEqualTo(now);
        assertThat(dto.items()).hasSize(1);

        OrderItemDTO itemDTO = dto.items().getFirst();
        assertThat(itemDTO.productId()).isEqualTo(1L);
        assertThat(itemDTO.title()).isEqualTo("Wireless Mouse");
        assertThat(itemDTO.thumbnail()).isEqualTo("mouse.jpg");
        assertThat(itemDTO.quantity()).isEqualTo(2);
        assertThat(itemDTO.unitPrice()).isEqualByComparingTo("29.99");
        assertThat(itemDTO.totalPrice()).isEqualByComparingTo("59.98");
    }

    @Test
    void toOrderDTO_withMultipleItems_mapsAll() {
        Product p1 = new Product();
        p1.setId(1L);
        p1.setTitle("Keyboard");
        p1.setThumbnail("kb.jpg");

        Product p2 = new Product();
        p2.setId(2L);
        p2.setTitle("Monitor");
        p2.setThumbnail("mon.jpg");

        OrderItem item1 = new OrderItem();
        item1.setProduct(p1);
        item1.setQuantity(1);
        item1.setUnitPrice(new BigDecimal("50.00"));
        item1.setTotalPrice(new BigDecimal("50.00"));

        OrderItem item2 = new OrderItem();
        item2.setProduct(p2);
        item2.setQuantity(2);
        item2.setUnitPrice(new BigDecimal("200.00"));
        item2.setTotalPrice(new BigDecimal("400.00"));

        Order order = new Order();
        order.setId(1L);
        order.setStatus(OrderStatus.CONFIRMED);
        order.setTotal(new BigDecimal("450.00"));
        order.setCreatedAt(LocalDateTime.now());
        order.setItems(List.of(item1, item2));

        OrderDTO dto = mapper.toOrderDTO(order);

        assertThat(dto.status()).isEqualTo("CONFIRMED");
        assertThat(dto.items()).hasSize(2);
        assertThat(dto.items()).extracting(OrderItemDTO::productId).containsExactly(1L, 2L);
    }

    @Test
    void toOrderDTO_withNoItems_returnsEmptyList() {
        Order order = new Order();
        order.setId(1L);
        order.setStatus(OrderStatus.PENDING);
        order.setTotal(BigDecimal.ZERO);
        order.setCreatedAt(LocalDateTime.now());
        order.setItems(List.of());

        OrderDTO dto = mapper.toOrderDTO(order);

        assertThat(dto.items()).isEmpty();
    }
}
