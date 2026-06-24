package com.fooddelivery.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsResponse {
    private long totalUsers;
    private long totalOrders;
    private long totalRestaurants;
    private long deliveredOrders;
    private Double totalRevenue;
    private List<RestaurantSales> restaurantSales;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RestaurantSales {
        private Long restaurantId;
        private String restaurantName;
        private long orderCount;
        private Double revenue;
    }
}
