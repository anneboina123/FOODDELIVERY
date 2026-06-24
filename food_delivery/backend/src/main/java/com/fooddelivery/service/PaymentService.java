package com.fooddelivery.service;

import com.fooddelivery.entity.Order;
import com.fooddelivery.entity.Payment;
import com.fooddelivery.exception.BadRequestException;
import com.fooddelivery.exception.ResourceNotFoundException;
import com.fooddelivery.repository.OrderRepository;
import com.fooddelivery.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    public PaymentService(PaymentRepository paymentRepository, OrderRepository orderRepository) {
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
    }

    @Transactional
    public Payment processPayment(Long orderId, String method) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found for order"));

        if (payment.getStatus() == Payment.PaymentStatus.SUCCESS) {
            throw new BadRequestException("Payment already processed");
        }

        payment.setStatus(Payment.PaymentStatus.SUCCESS);
        payment.setMethod(method != null ? method : "CREDIT_CARD");
        paymentRepository.save(payment);

        // Advance to PREPARING and stamp the time — scheduler uses this to auto-progress
        order.setStatus(Order.OrderStatus.PREPARING);
        order.setStatusUpdatedAt(LocalDateTime.now());
        orderRepository.save(order);

        return payment;
    }

    public Payment getPaymentByOrderId(Long orderId) {
        return paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found for order"));
    }
}
