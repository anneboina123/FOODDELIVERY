package com.fooddelivery.service;

import com.fooddelivery.dto.AnalyticsResponse;
import com.fooddelivery.repository.OrderRepository;
import com.fooddelivery.repository.RestaurantRepository;
import com.fooddelivery.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AnalyticsService {

    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final RestaurantRepository restaurantRepository;

    public AnalyticsService(UserRepository userRepository,
                            OrderRepository orderRepository,
                            RestaurantRepository restaurantRepository) {
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
        this.restaurantRepository = restaurantRepository;
    }

    public AnalyticsResponse getAnalytics() {
        AnalyticsResponse response = new AnalyticsResponse();

        response.setTotalUsers(userRepository.count());
        response.setTotalOrders(orderRepository.count());
        response.setTotalRestaurants(restaurantRepository.count());

        Long deliveredOrders = orderRepository.countDeliveredOrders();
        response.setDeliveredOrders(deliveredOrders != null ? deliveredOrders : 0L);

        Double totalRevenue = orderRepository.getTotalRevenue();
        response.setTotalRevenue(totalRevenue != null ? totalRevenue : 0.0);

        List<Object[]> restaurantSalesData = orderRepository.getRestaurantSales();
        List<AnalyticsResponse.RestaurantSales> restaurantSales = new ArrayList<>();

        for (Object[] row : restaurantSalesData) {
            AnalyticsResponse.RestaurantSales sale = new AnalyticsResponse.RestaurantSales();
            sale.setRestaurantId((Long) row[0]);
            sale.setRestaurantName((String) row[1]);
            sale.setOrderCount((Long) row[2]);
            sale.setRevenue((Double) row[3]);
            restaurantSales.add(sale);
        }

        response.setRestaurantSales(restaurantSales);

        return response;
    }
}
