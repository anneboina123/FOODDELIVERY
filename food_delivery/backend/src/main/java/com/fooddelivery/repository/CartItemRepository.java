package com.fooddelivery.repository;

import com.fooddelivery.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findByUserId(Long userId);
    
    Optional<CartItem> findByUserIdAndMenuItemId(Long userId, Long menuItemId);
    
    void deleteByUserId(Long userId);
    
    @Query("SELECT SUM(c.quantity * c.menuItem.price) FROM CartItem c WHERE c.user.id = ?1")
    Double getCartTotal(Long userId);
}
