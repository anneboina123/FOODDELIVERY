package com.fooddelivery.service;

import com.fooddelivery.dto.RestaurantRequest;
import com.fooddelivery.entity.Restaurant;
import com.fooddelivery.exception.ResourceNotFoundException;
import com.fooddelivery.repository.RestaurantRepository;
import com.fooddelivery.repository.ReviewRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final ReviewRepository reviewRepository;

    public RestaurantService(RestaurantRepository restaurantRepository, ReviewRepository reviewRepository) {
        this.restaurantRepository = restaurantRepository;
        this.reviewRepository = reviewRepository;
    }

    public List<Restaurant> getAllRestaurants() {
        return restaurantRepository.findAll();
    }

    public List<Restaurant> searchRestaurants(String query) {
        if (query == null || query.isEmpty()) {
            return restaurantRepository.findAll();
        }
        return restaurantRepository.findByNameContainingIgnoreCaseOrCategoryContainingIgnoreCase(query, query);
    }

    public Restaurant getRestaurantById(Long id) {
        return restaurantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found with id: " + id));
    }

    @Transactional
    public Restaurant createRestaurant(RestaurantRequest request) {
        Restaurant restaurant = new Restaurant();
        restaurant.setName(request.getName());
        restaurant.setLocation(request.getLocation());
        restaurant.setCategory(request.getCategory());
        restaurant.setImageUrl(request.getImageUrl());
        restaurant.setRating(0.0);
        return restaurantRepository.save(restaurant);
    }

    @Transactional
    public Restaurant updateRestaurant(Long id, RestaurantRequest request) {
        Restaurant restaurant = getRestaurantById(id);
        restaurant.setName(request.getName());
        restaurant.setLocation(request.getLocation());
        restaurant.setCategory(request.getCategory());
        if (request.getImageUrl() != null) {
            restaurant.setImageUrl(request.getImageUrl());
        }
        return restaurantRepository.save(restaurant);
    }

    @Transactional
    public void deleteRestaurant(Long id) {
        Restaurant restaurant = getRestaurantById(id);
        restaurantRepository.delete(restaurant);
    }

    public void updateRating(Long restaurantId) {
        Restaurant restaurant = getRestaurantById(restaurantId);
        Double avgRating = reviewRepository.getAverageRatingByRestaurantId(restaurantId);
        restaurant.setRating(avgRating != null ? avgRating : 0.0);
        restaurantRepository.save(restaurant);
    }
}
