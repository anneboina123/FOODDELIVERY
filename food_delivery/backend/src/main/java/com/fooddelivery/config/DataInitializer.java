package com.fooddelivery.config;

import com.fooddelivery.entity.MenuItem;
import com.fooddelivery.entity.Restaurant;
import com.fooddelivery.entity.User;
import com.fooddelivery.repository.MenuItemRepository;
import com.fooddelivery.repository.RestaurantRepository;
import com.fooddelivery.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;
    private final MenuItemRepository menuItemRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, RestaurantRepository restaurantRepository,
                          MenuItemRepository menuItemRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.restaurantRepository = restaurantRepository;
        this.menuItemRepository = menuItemRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        createDefaultUsers();
        createSampleRestaurants();
    }

    private void createDefaultUsers() {
        if (!userRepository.existsByEmail("admin@fooddelivery.com")) {
            User admin = new User();
            admin.setName("Admin");
            admin.setEmail("admin@fooddelivery.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole(User.Role.ADMIN);
            userRepository.save(admin);
            System.out.println("Default admin created: admin@fooddelivery.com / admin123");
        }

        if (!userRepository.existsByEmail("user@fooddelivery.com")) {
            User user = new User();
            user.setName("Test User");
            user.setEmail("user@fooddelivery.com");
            user.setPassword(passwordEncoder.encode("user123"));
            user.setRole(User.Role.USER);
            userRepository.save(user);
            System.out.println("Default user created: user@fooddelivery.com / user123");
        }
    }

    private void createSampleRestaurants() {
        if (restaurantRepository.count() == 0) {
            Restaurant r1 = new Restaurant();
            r1.setName("Pizza Palace");
            r1.setLocation("123 Main St");
            r1.setCategory("Italian");
            r1.setRating(4.5);
            r1.setImageUrl("https://images.unsplash.com/photo-1513104890138-7c749659a591?w=400");
            r1 = restaurantRepository.save(r1);

            createMenuItems(r1, "Pizza", new String[]{"Margherita", "Pepperoni", "Veggie Supreme", "BBQ Chicken"},
                    new double[]{12.99, 14.99, 13.99, 15.99});
            createMenuItems(r1, "Pasta", new String[]{"Spaghetti Bolognese", "Fettuccine Alfredo", "Penne Arrabbiata"},
                    new double[]{11.99, 12.99, 10.99});
            createMenuItems(r1, "Beverages", new String[]{"Italian Soda", "Lemonade", "Iced Tea"},
                    new double[]{3.99, 2.99, 2.99});

            Restaurant r2 = new Restaurant();
            r2.setName("Burger Barn");
            r2.setLocation("456 Oak Ave");
            r2.setCategory("American");
            r2.setRating(4.3);
            r2.setImageUrl("https://images.unsplash.com/photo-1568901346375-23c9450c58cd?w=400");
            r2 = restaurantRepository.save(r2);

            createMenuItems(r2, "Burgers", new String[]{"Classic Burger", "Cheese Burger", "Bacon Burger", "Veggie Burger"},
                    new double[]{9.99, 10.99, 11.99, 10.49});
            createMenuItems(r2, "Sides", new String[]{"French Fries", "Onion Rings", "Coleslaw"},
                    new double[]{3.99, 4.49, 2.99});
            createMenuItems(r2, "Drinks", new String[]{"Soft Drink", "Milkshake", "Iced Coffee"},
                    new double[]{2.49, 4.99, 3.99});

            Restaurant r3 = new Restaurant();
            r3.setName("Sushi Heaven");
            r3.setLocation("789 Cherry Ln");
            r3.setCategory("Japanese");
            r3.setRating(4.8);
            r3.setImageUrl("https://images.unsplash.com/photo-1579871494447-9811cf80d66c?w=400");
            r3 = restaurantRepository.save(r3);

            createMenuItems(r3, "Sushi Rolls", new String[]{"California Roll", "Spicy Tuna", "Dragon Roll", "Rainbow Roll"},
                    new double[]{12.99, 14.99, 16.99, 18.99});
            createMenuItems(r3, "Sashimi", new String[]{"Salmon", "Tuna", "Mixed"},
                    new double[]{15.99, 16.99, 18.99});
            createMenuItems(r3, "Drinks", new String[]{"Green Tea", "Sake", "Japanese Beer"},
                    new double[]{2.99, 8.99, 5.99});

            Restaurant r4 = new Restaurant();
            r4.setName("Taco Town");
            r4.setLocation("321 Elm St");
            r4.setCategory("Mexican");
            r4.setRating(4.2);
            r4.setImageUrl("https://images.unsplash.com/photo-1565299585323-38d6b0865b47?w=400");
            r4 = restaurantRepository.save(r4);

            createMenuItems(r4, "Tacos", new String[]{"Beef Taco", "Chicken Taco", "Fish Taco", "Veggie Taco"},
                    new double[]{3.99, 3.49, 4.49, 3.29});
            createMenuItems(r4, "Burritos", new String[]{"Carne Asada", "Carnitas", "Chicken", "Bean & Cheese"},
                    new double[]{9.99, 10.49, 9.49, 7.99});
            createMenuItems(r4, "Extras", new String[]{"Guacamole", "Sour Cream", "Extra Cheese"},
                    new double[]{2.99, 1.49, 1.99});

            Restaurant r5 = new Restaurant();
            r5.setName("Curry House");
            r5.setLocation("555 Spice Ave");
            r5.setCategory("Indian");
            r5.setRating(4.6);
            r5.setImageUrl("https://images.unsplash.com/photo-1585937421612-70a008356fbe?w=400");
            r5 = restaurantRepository.save(r5);

            createMenuItems(r5, "Curries", new String[]{"Butter Chicken", "Palak Paneer", "Lamb Rogan Josh", "Chicken Tikka Masala"},
                    new double[]{14.99, 13.99, 16.99, 15.99});
            createMenuItems(r5, "Breads", new String[]{"Naan", "Garlic Naan", "Roti", "Paratha"},
                    new double[]{2.99, 3.49, 2.49, 3.99});
            createMenuItems(r5, "Rice", new String[]{"Biryani", "Jeera Rice", "Plain Rice"},
                    new double[]{8.99, 4.99, 3.99});

            System.out.println("Sample restaurants and menu items created!");
        }
    }

    private void createMenuItems(Restaurant restaurant, String category, String[] names, double[] prices) {
        for (int i = 0; i < names.length; i++) {
            MenuItem item = new MenuItem();
            item.setRestaurant(restaurant);
            item.setName(names[i]);
            item.setCategory(category);
            item.setPrice(prices[i]);
            item.setDescription("Delicious " + names[i] + " - freshly prepared");
            item.setAvailable(true);
            menuItemRepository.save(item);
        }
    }
}
