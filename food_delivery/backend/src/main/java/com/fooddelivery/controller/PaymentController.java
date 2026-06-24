package com.fooddelivery.controller;

import com.fooddelivery.entity.Payment;
import com.fooddelivery.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/payment")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/process")
    public ResponseEntity<Payment> processPayment(
            @RequestParam Long orderId,
            @RequestParam String method) {
        return ResponseEntity.ok(paymentService.processPayment(orderId, method));
    }

    @GetMapping("/status/{orderId}")
    public ResponseEntity<Map<String, Object>> getPaymentStatus(@PathVariable Long orderId) {
        Payment payment = paymentService.getPaymentByOrderId(orderId);
        Map<String, Object> response = new HashMap<>();
        response.put("orderId", orderId);
        response.put("status", payment.getStatus());
        response.put("method", payment.getMethod());
        response.put("amount", payment.getAmount());
        return ResponseEntity.ok(response);
    }
}
