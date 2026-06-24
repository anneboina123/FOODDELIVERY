package com.fooddelivery.service;

import com.fooddelivery.dto.OrderRequest;
import com.fooddelivery.dto.OrderStatusRequest;
import com.fooddelivery.entity.*;
import com.fooddelivery.exception.BadRequestException;
import com.fooddelivery.exception.ResourceNotFoundException;
import com.fooddelivery.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final PaymentRepository paymentRepository;

    public OrderService(OrderRepository orderRepository,
                        OrderItemRepository orderItemRepository,
                        CartItemRepository cartItemRepository,
                        UserRepository userRepository,
                        AddressRepository addressRepository,
                        PaymentRepository paymentRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.cartItemRepository = cartItemRepository;
        this.userRepository = userRepository;
        this.addressRepository = addressRepository;
        this.paymentRepository = paymentRepository;
    }

    @Transactional
    public Order createOrder(Long userId, OrderRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<CartItem> cartItems = cartItemRepository.findByUserId(userId);
        if (cartItems.isEmpty()) {
            throw new BadRequestException("Cart is empty");
        }

        Restaurant restaurant = cartItems.get(0).getMenuItem().getRestaurant();
        for (CartItem item : cartItems) {
            if (!item.getMenuItem().getRestaurant().getId().equals(restaurant.getId())) {
                throw new BadRequestException("All items must be from the same restaurant");
            }
        }

        Address address = null;
        if (request.getAddressId() != null) {
            address = addressRepository.findById(request.getAddressId())
                    .orElseThrow(() -> new ResourceNotFoundException("Address not found"));
        }

        Double totalAmount = cartItemRepository.getCartTotal(userId);
        if (totalAmount == null) totalAmount = 0.0;

        Order order = new Order();
        order.setUser(user);
        order.setRestaurant(restaurant);
        order.setTotalAmount(totalAmount);
        order.setStatus(Order.OrderStatus.PLACED);
        order.setAddress(address);
        order.setPaymentMethod(request.getPaymentMethod() != null ? request.getPaymentMethod() : "CREDIT_CARD");
        order.setCreatedAt(LocalDateTime.now());
        order.setStatusUpdatedAt(LocalDateTime.now());
        order = orderRepository.save(order);

        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem cartItem : cartItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setMenuItem(cartItem.getMenuItem());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(cartItem.getMenuItem().getPrice());
            orderItems.add(orderItem);
        }
        orderItemRepository.saveAll(orderItems);

        // Create pending payment record
        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setAmount(totalAmount);
        payment.setMethod(order.getPaymentMethod());
        payment.setStatus(Payment.PaymentStatus.PENDING);
        paymentRepository.save(payment);

        cartItemRepository.deleteByUserId(userId);

        return orderRepository.findById(order.getId()).orElse(order);
    }

    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
    }

    public List<Order> getOrdersByUserId(Long userId) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAllByOrderByCreatedAtDesc();
    }

    @Transactional
    public Order updateOrderStatus(Long orderId, OrderStatusRequest request) {
        Order order = getOrderById(orderId);
        Order.OrderStatus newStatus = Order.OrderStatus.valueOf(request.getStatus().toUpperCase());
        order.setStatus(newStatus);
        order.setStatusUpdatedAt(LocalDateTime.now());
        return orderRepository.save(order);
    }

    @Transactional
    public Order cancelOrder(Long orderId, Long userId) {
        Order order = getOrderById(orderId);
        if (!order.getUser().getId().equals(userId)) {
            throw new BadRequestException("You can only cancel your own orders");
        }
        if (order.getStatus() != Order.OrderStatus.PLACED) {
            throw new BadRequestException("Only placed orders can be cancelled");
        }
        order.setStatus(Order.OrderStatus.CANCELLED);
        order.setStatusUpdatedAt(LocalDateTime.now());
        return orderRepository.save(order);
    }

    public List<Order> getOrdersByRestaurant(Long restaurantId) {
        return orderRepository.findByRestaurantId(restaurantId);
    }
}
