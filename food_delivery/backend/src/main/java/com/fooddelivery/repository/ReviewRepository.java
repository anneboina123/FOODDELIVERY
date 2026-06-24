package com.fooddelivery.repository;

import com.fooddelivery.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByRestaurantId(Long restaurantId);
    
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.restaurant.id = ?1")
    Double getAverageRatingByRestaurantId(Long restaurantId);
}
