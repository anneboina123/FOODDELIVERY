package com.fooddelivery.scheduler;

import com.fooddelivery.entity.Order;
import com.fooddelivery.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Auto-progresses orders through delivery lifecycle:
 *   PLACED          -> PREPARING        (immediately on payment — done by PaymentService)
 *   PREPARING       -> OUT_FOR_DELIVERY (after 1 minute)
 *   OUT_FOR_DELIVERY -> DELIVERED        (after 2 minutes)
 *
 * Runs every 20 seconds.
 */
@Component
public class OrderStatusScheduler {

    private static final Logger logger = LoggerFactory.getLogger(OrderStatusScheduler.class);

    // How long (minutes) an order stays in each status before auto-advancing
    private static final long PREPARING_MINUTES = 1;
    private static final long OUT_FOR_DELIVERY_MINUTES = 2;

    private final OrderRepository orderRepository;

    public OrderStatusScheduler(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Scheduled(fixedDelay = 20000) // runs every 20 seconds
    @Transactional
    public void progressOrderStatuses() {
        LocalDateTime now = LocalDateTime.now();

        // PREPARING -> OUT_FOR_DELIVERY
        List<Order> preparingOrders = orderRepository.findByStatus(Order.OrderStatus.PREPARING);
        for (Order order : preparingOrders) {
            LocalDateTime since = order.getStatusUpdatedAt() != null
                    ? order.getStatusUpdatedAt()
                    : order.getCreatedAt();

            if (since != null && since.plusMinutes(PREPARING_MINUTES).isBefore(now)) {
                order.setStatus(Order.OrderStatus.OUT_FOR_DELIVERY);
                order.setStatusUpdatedAt(LocalDateTime.now());
                orderRepository.save(order);
                logger.info("Order #{} status: PREPARING -> OUT_FOR_DELIVERY", order.getId());
            }
        }

        // OUT_FOR_DELIVERY -> DELIVERED
        List<Order> outOrders = orderRepository.findByStatus(Order.OrderStatus.OUT_FOR_DELIVERY);
        for (Order order : outOrders) {
            LocalDateTime since = order.getStatusUpdatedAt() != null
                    ? order.getStatusUpdatedAt()
                    : order.getCreatedAt();

            if (since != null && since.plusMinutes(OUT_FOR_DELIVERY_MINUTES).isBefore(now)) {
                order.setStatus(Order.OrderStatus.DELIVERED);
                order.setStatusUpdatedAt(LocalDateTime.now());
                orderRepository.save(order);
                logger.info("Order #{} status: OUT_FOR_DELIVERY -> DELIVERED", order.getId());
            }
        }

        logger.debug("Scheduler run complete at {}", now);
    }
}
