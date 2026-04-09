package com.viniciuskegler.salesapp.order;

import com.viniciuskegler.salesapp.customer.CustomerRepository;
import com.viniciuskegler.salesapp.customer.model.Customer;
import com.viniciuskegler.salesapp.order.dto.OrderDTO;
import com.viniciuskegler.salesapp.order.dto.PlaceOrderRequestDTO;
import com.viniciuskegler.salesapp.order.dto.mapper.OrderMapper;
import com.viniciuskegler.salesapp.order.model.Order;
import com.viniciuskegler.salesapp.order.model.OrderItem;
import com.viniciuskegler.salesapp.order.model.OrderStatus;
import com.viniciuskegler.salesapp.product.ProductRepository;
import com.viniciuskegler.salesapp.product.model.Product;
import com.viniciuskegler.salesapp.shared.exception.RecordNotFoundException;
import com.viniciuskegler.salesapp.user.model.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Validated
@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final OrderMapper orderMapper;

    public OrderService(OrderRepository orderRepository,
                        CustomerRepository customerRepository,
                        ProductRepository productRepository,
                        OrderMapper orderMapper) {
        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
        this.orderMapper = orderMapper;
    }

    @Transactional
    public OrderDTO placeOrder(PlaceOrderRequestDTO request, User currentUser) {
        Customer customer = customerRepository.findByUser(currentUser)
                .orElseThrow(() -> new RecordNotFoundException(currentUser.getId()));

        Order order = new Order();
        order.setCustomer(customer);
        order.setStatus(OrderStatus.PENDING);

        List<OrderItem> items = request.items().stream()
                .map(itemRequest -> {
                    Product product = productRepository.findById(itemRequest.productId())
                            .orElseThrow(() -> new RecordNotFoundException(itemRequest.productId()));
                    BigDecimal unitPrice = product.getPrice();
                    BigDecimal totalPrice = unitPrice.multiply(BigDecimal.valueOf(itemRequest.quantity()));
                    OrderItem item = new OrderItem();
                    item.setOrder(order);
                    item.setProduct(product);
                    item.setQuantity(itemRequest.quantity());
                    item.setUnitPrice(unitPrice);
                    item.setTotalPrice(totalPrice);
                    return item;
                })
                .collect(Collectors.toList());

        BigDecimal total = items.stream()
                .map(OrderItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        order.setTotal(total);
        order.setItems(items);

        return orderMapper.toOrderDTO(orderRepository.save(order));
    }

    public List<OrderDTO> getMyOrders(User currentUser) {
        Customer customer = customerRepository.findByUser(currentUser)
                .orElseThrow(() -> new RecordNotFoundException(currentUser.getId()));

        return orderRepository.findByCustomerOrderByCreatedAtDesc(customer).stream()
                .map(orderMapper::toOrderDTO)
                .collect(Collectors.toList());
    }

    public OrderDTO getOrderById(Long id, User currentUser) {
        Customer customer = customerRepository.findByUser(currentUser)
                .orElseThrow(() -> new RecordNotFoundException(currentUser.getId()));

        return orderRepository.findByIdAndCustomer(id, customer)
                .map(orderMapper::toOrderDTO)
                .orElseThrow(() -> new RecordNotFoundException(id));
    }
}
