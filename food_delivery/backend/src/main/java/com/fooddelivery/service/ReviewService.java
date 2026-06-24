package com.fooddelivery.service;

import com.fooddelivery.dto.ReviewRequest;
import com.fooddelivery.entity.Restaurant;
import com.fooddelivery.entity.Review;
import com.fooddelivery.entity.User;
import com.fooddelivery.exception.BadRequestException;
import com.fooddelivery.exception.ResourceNotFoundException;
import com.fooddelivery.repository.RestaurantRepository;
import com.fooddelivery.repository.ReviewRepository;
import com.fooddelivery.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final RestaurantRepository restaurantRepository;
    private final UserRepository userRepository;

    public ReviewService(ReviewRepository reviewRepository, RestaurantRepository restaurantRepository,
                        UserRepository userRepository) {
        this.reviewRepository = reviewRepository;
        this.restaurantRepository = restaurantRepository;
        this.userRepository = userRepository;
    }

    public List<Review> getReviewsByRestaurant(Long restaurantId) {
        return reviewRepository.findByRestaurantId(restaurantId);
    }

    @Transactional
    public Review createReview(Long userId, Long restaurantId, ReviewRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found"));

        Review existingReview = reviewRepository.findAll().stream()
                .filter(r -> r.getUser().getId().equals(userId) && r.getRestaurant().getId().equals(restaurantId))
                .findFirst()
                .orElse(null);

        if (existingReview != null) {
            throw new BadRequestException("You have already reviewed this restaurant");
        }

        Review review = new Review();
        review.setUser(user);
        review.setRestaurant(restaurant);
        review.setRating(request.getRating());
        review.setComment(request.getComment());

        review = reviewRepository.save(review);

        updateRestaurantRating(restaurantId);

        return review;
    }

    @Transactional
    public Review updateReview(Long userId, Long reviewId, ReviewRequest request) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found"));

        if (!review.getUser().getId().equals(userId)) {
            throw new BadRequestException("You can only update your own reviews");
        }

        if (request.getRating() != null) {
            review.setRating(request.getRating());
        }
        if (request.getComment() != null) {
            review.setComment(request.getComment());
        }

        review = reviewRepository.save(review);

        updateRestaurantRating(review.getRestaurant().getId());

        return review;
    }

    @Transactional
    public void deleteReview(Long userId, Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found"));

        if (!review.getUser().getId().equals(userId)) {
            throw new BadRequestException("You can only delete your own reviews");
        }

        Long restaurantId = review.getRestaurant().getId();
        reviewRepository.delete(review);
        updateRestaurantRating(restaurantId);
    }

    private void updateRestaurantRating(Long restaurantId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found"));

        Double avgRating = reviewRepository.getAverageRatingByRestaurantId(restaurantId);
        restaurant.setRating(avgRating != null ? avgRating : 0.0);
        restaurantRepository.save(restaurant);
    }
}
