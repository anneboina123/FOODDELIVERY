package com.fooddelivery.repository;

import com.fooddelivery.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("SELECT o FROM Order o WHERE o.user.id = :userId ORDER BY o.createdAt DESC")
    List<Order> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<Order> findByRestaurantId(Long restaurantId);

    // Used by scheduler to find orders needing status progression
    List<Order> findByStatus(Order.OrderStatus status);

    @Query("SELECT o FROM Order o ORDER BY o.createdAt DESC")
    List<Order> findAllByOrderByCreatedAtDesc();

    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.status != 'CANCELLED'")
    Double getTotalRevenue();

    @Query("SELECT COUNT(o) FROM Order o WHERE o.status = 'DELIVERED'")
    Long countDeliveredOrders();

    @Query("SELECT o.restaurant.id, o.restaurant.name, COUNT(o), SUM(o.totalAmount) " +
           "FROM Order o WHERE o.status != 'CANCELLED' GROUP BY o.restaurant.id, o.restaurant.name")
    List<Object[]> getRestaurantSales();
}
