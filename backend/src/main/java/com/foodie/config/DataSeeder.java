package com.foodie.config;

import com.foodie.entity.*;
import com.foodie.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.*;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner initData(UserRepository userRepository,
                              UserHealthProfileRepository healthProfileRepository,
                              AddressRepository addressRepository,
                              RestaurantRepository restaurantRepository,
                              MenuCategoryRepository categoryRepository,
                              FoodItemRepository foodItemRepository,
                              CouponRepository couponRepository,
                              PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.count() > 0) return;

            String encodedPassword = passwordEncoder.encode("Password@123");

            // 1. Seed Users (Demo Accounts for 4 Roles)
            User customer = new User();
            customer.setName("Aarav Sharma");
            customer.setEmail("customer@foodie.com");
            customer.setPhone("9876543210");
            customer.setPasswordHash(encodedPassword);
            customer.setRole(Role.ROLE_CUSTOMER);
            customer = userRepository.save(customer);

            // User Health Profile for Customer (Diabetic + Hypertension + Nut Allergy)
            UserHealthProfile healthProfile = new UserHealthProfile();
            healthProfile.setUser(customer);
            healthProfile.setConditions(Set.of(HealthCondition.DIABETES, HealthCondition.HYPERTENSION));
            healthProfile.setAllergies(Set.of(Allergen.NUTS));
            healthProfile.setDietPreference(DietPreference.VEG);
            healthProfile.setDailyCalorieTarget(1800);
            healthProfile.setMaxSugarGramsPerMeal(12.0);
            healthProfile.setMaxSodiumMgPerMeal(500.0);
            healthProfileRepository.save(healthProfile);

            Address address1 = new Address();
            address1.setUser(customer);
            address1.setLabel("Home");
            address1.setStreet("Flat 402, Palm Meadows, Whitefield");
            address1.setCity("Bengaluru");
            address1.setPincode("560066");
            address1.setDefaultAddress(true);
            addressRepository.save(address1);

            User owner = new User();
            owner.setName("Chef Vikram Oberoi");
            owner.setEmail("owner@foodie.com");
            owner.setPhone("9876543211");
            owner.setPasswordHash(encodedPassword);
            owner.setRole(Role.ROLE_RESTAURANT_OWNER);
            owner = userRepository.save(owner);

            User rider = new User();
            rider.setName("Rajesh Kumar");
            rider.setEmail("rider@foodie.com");
            rider.setPhone("9876543212");
            rider.setPasswordHash(encodedPassword);
            rider.setRole(Role.ROLE_DELIVERY_PARTNER);
            rider = userRepository.save(rider);

            User admin = new User();
            admin.setName("Priya Nair (SuperAdmin)");
            admin.setEmail("admin@foodie.com");
            admin.setPhone("9876543213");
            admin.setPasswordHash(encodedPassword);
            admin.setRole(Role.ROLE_ADMIN);
            admin = userRepository.save(admin);

            // 2. Seed Coupons
            Coupon c1 = new Coupon();
            c1.setCode("WELCOME50");
            c1.setDiscountType(DiscountType.PERCENTAGE);
            c1.setDiscountValue(50.0);
            c1.setMinOrderAmount(199.0);
            c1.setMaxDiscount(150.0);
            c1.setActive(true);
            couponRepository.save(c1);

            Coupon c2 = new Coupon();
            c2.setCode("HEALTHY20");
            c2.setDiscountType(DiscountType.PERCENTAGE);
            c2.setDiscountValue(20.0);
            c2.setMinOrderAmount(250.0);
            c2.setMaxDiscount(100.0);
            c2.setActive(true);
            couponRepository.save(c2);

            Coupon c3 = new Coupon();
            c3.setCode("DIABETICCARE");
            c3.setDiscountType(DiscountType.FLAT);
            c3.setDiscountValue(75.0);
            c3.setMinOrderAmount(300.0);
            c3.setActive(true);
            couponRepository.save(c3);

            // 3. Seed Restaurants & 35+ Food Items
            // Restaurant 1: Aura Pure Veg & Diabetic Delights
            Restaurant r1 = new Restaurant();
            r1.setOwnerId(owner.getId());
            r1.setName("Aura Pure Veg & Diabetic Delights");
            r1.setDescription("Holistic, nutrient-rich South Indian and North Indian cuisine with zero refined sugar and clinical diabetic options.");
            r1.setCuisineTypes(Set.of("South Indian", "Healthy Bowls", "Diabetic Specials", "North Indian"));
            r1.setAddress("88 100ft Road, Indiranagar");
            r1.setCity("Bengaluru");
            r1.setRating(4.8);
            r1.setTotalRatings(342);
            r1.setAvgPrepTimeMins(20);
            r1.setOpen(true);
            r1.setImageUrl("https://images.unsplash.com/photo-1546069901-ba9599a7e63c?w=600");
            r1 = restaurantRepository.save(r1);

            // Restaurant 2: The Royal Biryani & Kebab Palace
            Restaurant r2 = new Restaurant();
            r2.setOwnerId(owner.getId());
            r2.setName("The Royal Biryani & Kebab Palace");
            r2.setDescription("Authentic dum biryanis, slow-cooked gravies, and aromatic clay-oven tandoori grills.");
            r2.setCuisineTypes(Set.of("Mughlai", "Biryani", "North Indian", "Kebabs"));
            r2.setAddress("45 Koramangala 5th Block");
            r2.setCity("Bengaluru");
            r2.setRating(4.6);
            r2.setTotalRatings(620);
            r2.setAvgPrepTimeMins(30);
            r2.setOpen(true);
            r2.setImageUrl("https://images.unsplash.com/photo-1563379091339-03b21ab4a4f8?w=600");
            r2 = restaurantRepository.save(r2);

            // Restaurant 3: Green Root Vegan & Keto Kitchen
            Restaurant r3 = new Restaurant();
            r3.setOwnerId(owner.getId());
            r3.setName("Green Root Vegan & Keto Kitchen");
            r3.setDescription("Clean, plant-based macro-balanced bowls, cold-pressed elixirs, and keto-certified entrees.");
            r3.setCuisineTypes(Set.of("Healthy Bowls", "Salads", "Vegan", "Keto", "Continental"));
            r3.setAddress("12 HSR Layout Sector 4");
            r3.setCity("Bengaluru");
            r3.setRating(4.9);
            r3.setTotalRatings(185);
            r3.setAvgPrepTimeMins(18);
            r3.setOpen(true);
            r3.setImageUrl("https://images.unsplash.com/photo-1512621776951-a57141f2eefd?w=600");
            r3 = restaurantRepository.save(r3);

            // Restaurant 4: Artisan Crust Pizza & Pasta Co.
            Restaurant r4 = new Restaurant();
            r4.setOwnerId(owner.getId());
            r4.setName("Artisan Crust Pizza & Pasta Co.");
            r4.setDescription("Wood-fired gourmet pizzas, handmade pasta, sourdough garlic breads, and Italian desserts.");
            r4.setCuisineTypes(Set.of("Italian", "Pizza", "Pasta", "Desserts"));
            r4.setAddress("19 Church Street, Shanthala Nagar");
            r4.setCity("Bengaluru");
            r4.setRating(4.5);
            r4.setTotalRatings(450);
            r4.setAvgPrepTimeMins(25);
            r4.setOpen(true);
            r4.setImageUrl("https://images.unsplash.com/photo-1513104890138-7c749659a591?w=600");
            r4 = restaurantRepository.save(r4);

            // --- CATEGORIES & ITEMS FOR RESTAURANT 1 (Aura Pure Veg) ---
            MenuCategory r1Cat1 = createCategory(r1, "Diabetic-Friendly Specials", 1, categoryRepository);
            MenuCategory r1Cat2 = createCategory(r1, "South Indian Classics", 2, categoryRepository);
            MenuCategory r1Cat3 = createCategory(r1, "Healthy Main Course", 3, categoryRepository);
            MenuCategory r1Cat4 = createCategory(r1, "Beverages & Elixirs", 4, categoryRepository);

            // Item 1: Ragi Millet Dosa (Diabetic safe)
            createFood(r1, r1Cat1, "Ragi Millet Masala Dosa",
                    "Crisp finger-millet fermented crepe stuffed with low-GI spiced potato & methi seeds. Served with coconut chutney and flaxseed podi.",
                    145.0, 129.0, DietType.VEGAN, SpiceLevel.MEDIUM, 18, 4.8,
                    "https://images.unsplash.com/photo-1589301760014-d929f3979dbc?w=400",
                    createNutrition(210, 6.5, 32.0, 1.2, 3.8, 0.8, 6.2, 290.0, GlycemicIndex.LOW,
                            Set.of(), Set.of(HealthCondition.DIABETES, HealthCondition.HYPERTENSION, HealthCondition.HEART_DISEASE, HealthCondition.PCOS, HealthCondition.CELIAC_GLUTEN_INTOLERANCE), Set.of()),
                    List.of(createCustomization("Salt Level", false, List.of("Low Rock Salt (+₹0)", "Standard (+₹0)"))),
                    foodItemRepository);

            // Item 2: Oats & Moong Dal Khichdi (High fiber, low sodium, heart healthy)
            createFood(r1, r1Cat1, "Steel-Cut Oats & Moong Dal Khichdi",
                    "Slow-simmered whole grain oats with yellow moong lentils, roasted cumin, and ginger. Light on the stomach and heart-healthy.",
                    180.0, 160.0, DietType.VEG, SpiceLevel.MILD, 15, 4.9,
                    "https://images.unsplash.com/photo-1626777552726-4a6b54c97e46?w=400",
                    createNutrition(240, 12.0, 36.0, 2.0, 4.0, 0.9, 7.5, 310.0, GlycemicIndex.LOW,
                            Set.of(), Set.of(HealthCondition.DIABETES, HealthCondition.HYPERTENSION, HealthCondition.HEART_DISEASE, HealthCondition.KIDNEY_FRIENDLY), Set.of()),
                    List.of(createCustomization("Ghee Option", false, List.of("Pure A2 Cow Ghee (+₹20)", "Cold Pressed Sesame (+₹0)", "No Added Fat (+₹0)"))),
                    foodItemRepository);

            // Item 3: Methi Paneer Brown Rice Bowl
            createFood(r1, r1Cat1, "Methi Paneer with Brown Rice Bowl",
                    "Fresh fenugreek leaves and low-fat cottage cheese cooked with aromatic whole spices, paired with steamed organic brown rice.",
                    240.0, 219.0, DietType.VEG, SpiceLevel.MEDIUM, 20, 4.7,
                    "https://images.unsplash.com/photo-1546833999-b9f581a1996d?w=400",
                    createNutrition(340, 18.0, 40.0, 2.5, 10.0, 3.5, 5.0, 420.0, GlycemicIndex.LOW,
                            Set.of(Allergen.DAIRY), Set.of(HealthCondition.DIABETES, HealthCondition.PCOS), Set.of()),
                    List.of(createCustomization("Portion", false, List.of("Regular (+₹0)", "High Protein Paneer (+₹50)"))),
                    foodItemRepository);

            // Item 4: Steamed Idli (3 pcs) with Sambar
            createFood(r1, r1Cat2, "Steamed Brown Rice & Dal Idli",
                    "Fluffy steamed cakes of brown rice and de-husked black gram. Served with vegetable-rich drumstick sambar.",
                    95.0, null, DietType.VEGAN, SpiceLevel.MILD, 10, 4.6,
                    "https://images.unsplash.com/photo-1589301760014-d929f3979dbc?w=400",
                    createNutrition(190, 7.0, 38.0, 1.8, 1.5, 0.3, 4.0, 320.0, GlycemicIndex.LOW,
                            Set.of(), Set.of(HealthCondition.DIABETES, HealthCondition.HEART_DISEASE, HealthCondition.CELIAC_GLUTEN_INTOLERANCE), Set.of()),
                    List.of(), foodItemRepository);

            // Item 5: Masala Medu Vada (Contains Nuts & Deep fried - High Fat warning for Heart Disease)
            createFood(r1, r1Cat2, "Classic Crispy Medu Vada (2 pcs)",
                    "Golden crisp fried urad dal donuts served with peanut-coconut chutney.",
                    90.0, null, DietType.VEG, SpiceLevel.MEDIUM, 12, 4.5,
                    "https://images.unsplash.com/photo-1589301760014-d929f3979dbc?w=400",
                    createNutrition(320, 8.0, 28.0, 1.0, 18.0, 6.0, 3.0, 580.0, GlycemicIndex.MEDIUM,
                            Set.of(Allergen.NUTS), Set.of(), Set.of(HealthCondition.HEART_DISEASE, HealthCondition.HYPERTENSION)),
                    List.of(), foodItemRepository);

            // Item 6: Palak Tofu & Multigrain Roti
            createFood(r1, r1Cat3, "Garlic Palak Tofu with Multigrain Roti (2 pcs)",
                    "Silken tofu cubes simmered in a vibrant iron-rich spinach garlic gravy with zero cream.",
                    220.0, 199.0, DietType.VEGAN, SpiceLevel.MEDIUM, 20, 4.8,
                    "https://images.unsplash.com/photo-1546833999-b9f581a1996d?w=400",
                    createNutrition(290, 16.0, 32.0, 2.2, 8.0, 1.2, 7.0, 380.0, GlycemicIndex.LOW,
                            Set.of(Allergen.SOY, Allergen.GLUTEN), Set.of(HealthCondition.DIABETES, HealthCondition.HYPERTENSION, HealthCondition.HEART_DISEASE, HealthCondition.PCOS), Set.of()),
                    List.of(), foodItemRepository);

            // Item 7: Bitter Gourd (Karela) Crisps with Chaat Masala (Diabetes protective)
            createFood(r1, r1Cat1, "Air-Fried Karela Crisps with Roasted Cumin",
                    "Thinly sliced bitter gourd dehydrated with mild spices and roasted cumin. Clinical insulin sensitizer.",
                    110.0, 99.0, DietType.VEGAN, SpiceLevel.HOT, 10, 4.7,
                    "https://images.unsplash.com/photo-1546069901-ba9599a7e63c?w=400",
                    createNutrition(120, 3.5, 16.0, 0.8, 3.0, 0.5, 5.5, 260.0, GlycemicIndex.LOW,
                            Set.of(), Set.of(HealthCondition.DIABETES, HealthCondition.PCOS, HealthCondition.CELIAC_GLUTEN_INTOLERANCE), Set.of()),
                    List.of(), foodItemRepository);

            // Item 8: Fresh Tender Coconut Water
            createFood(r1, r1Cat4, "Pure Kerala Tender Coconut Water",
                    "Hydrating natural electrolytes with natural potassium and zero added sugar.",
                    70.0, null, DietType.VEGAN, SpiceLevel.MILD, 5, 4.9,
                    "https://images.unsplash.com/photo-1525385133512-2f3bdd039054?w=400",
                    createNutrition(45, 1.0, 9.0, 6.0, 0.2, 0.0, 2.0, 105.0, GlycemicIndex.LOW,
                            Set.of(), Set.of(HealthCondition.HYPERTENSION, HealthCondition.HEART_DISEASE, HealthCondition.CELIAC_GLUTEN_INTOLERANCE), Set.of()),
                    List.of(), foodItemRepository);

            // Item 9: Cinnamon Spiced Herbal Tea
            createFood(r1, r1Cat4, "Ceylon Cinnamon & Tulsi Infusion",
                    "Warm organic brew of Ceylon cinnamon bark, fresh holy basil, and ginger.",
                    60.0, null, DietType.VEGAN, SpiceLevel.MILD, 5, 4.7,
                    "https://images.unsplash.com/photo-1576092768241-dec231879fc3?w=400",
                    createNutrition(10, 0.2, 2.0, 0.1, 0.0, 0.0, 0.5, 15.0, GlycemicIndex.LOW,
                            Set.of(), Set.of(HealthCondition.DIABETES, HealthCondition.HYPERTENSION, HealthCondition.HEART_DISEASE, HealthCondition.PCOS), Set.of()),
                    List.of(), foodItemRepository);


            // --- CATEGORIES & ITEMS FOR RESTAURANT 2 (Royal Biryani Palace) ---
            MenuCategory r2Cat1 = createCategory(r2, "Dum Biryani & Rice", 1, categoryRepository);
            MenuCategory r2Cat2 = createCategory(r2, "Clay-Oven Kebabs", 2, categoryRepository);
            MenuCategory r2Cat3 = createCategory(r2, "Rich Curries & Breads", 3, categoryRepository);
            MenuCategory r2Cat4 = createCategory(r2, "Royal Desserts", 4, categoryRepository);

            // Item 10: Hyderabadi Dum Chicken Biryani (High Sodium & Spices)
            createFood(r2, r2Cat1, "Nawabi Dum Chicken Biryani",
                    "Aged basmati rice layered with tender bone-in chicken marinated in saffron, caramelized onions, and royal spices.",
                    299.0, 279.0, DietType.NON_VEG, SpiceLevel.HOT, 25, 4.7,
                    "https://images.unsplash.com/photo-1563379091339-03b21ab4a4f8?w=400",
                    createNutrition(580, 32.0, 65.0, 4.0, 18.0, 5.5, 3.5, 880.0, GlycemicIndex.MEDIUM,
                            Set.of(Allergen.DAIRY), Set.of(), Set.of(HealthCondition.HYPERTENSION, HealthCondition.HEART_DISEASE)),
                    List.of(createCustomization("Spice Level", false, List.of("Royal Medium (+₹0)", "Hyderabadi Hot (+₹0)"))),
                    foodItemRepository);

            // Item 11: Brown Rice Chicken Tikka Biryani (Healthier Biryani option)
            createFood(r2, r2Cat1, "Brown Rice Chicken Tikka Biryani",
                    "Charcoal-grilled lean chicken breast cubes tossed with low-GI brown basmati, fresh mint, and mild whole spices.",
                    320.0, 299.0, DietType.NON_VEG, SpiceLevel.MEDIUM, 22, 4.8,
                    "https://images.unsplash.com/photo-1563379091339-03b21ab4a4f8?w=400",
                    createNutrition(410, 36.0, 48.0, 2.5, 8.5, 2.0, 5.5, 450.0, GlycemicIndex.LOW,
                            Set.of(Allergen.DAIRY), Set.of(HealthCondition.DIABETES, HealthCondition.PCOS), Set.of()),
                    List.of(), foodItemRepository);

            // Item 12: Jackfruit (Kathal) Dum Biryani (Vegetarian / High Fiber)
            createFood(r2, r2Cat1, "Awadhi Jackfruit (Kathal) Dum Biryani",
                    "Tender young raw jackfruit marinated like meat, dum-cooked with long grain basmati and whole spices. High in dietary fiber.",
                    260.0, 239.0, DietType.VEGAN, SpiceLevel.MEDIUM, 25, 4.6,
                    "https://images.unsplash.com/photo-1563379091339-03b21ab4a4f8?w=400",
                    createNutrition(380, 9.0, 68.0, 3.2, 7.0, 1.5, 8.0, 480.0, GlycemicIndex.MEDIUM,
                            Set.of(), Set.of(HealthCondition.CELIAC_GLUTEN_INTOLERANCE), Set.of()),
                    List.of(), foodItemRepository);

            // Item 13: Tandoori Chicken Tikka (High protein, low carb, diabetic friendly)
            createFood(r2, r2Cat2, "Tandoori Chicken Tikka (6 pcs)",
                    "Succulent boneless chicken chunks marinated in hung curd, Kashmiri chilies, and roasted gram flour, charred in tandoor.",
                    280.0, 250.0, DietType.NON_VEG, SpiceLevel.MEDIUM, 18, 4.8,
                    "https://images.unsplash.com/photo-1599488615731-7e5c2823ff28?w=400",
                    createNutrition(280, 38.0, 4.0, 1.0, 11.0, 3.0, 1.5, 460.0, GlycemicIndex.LOW,
                            Set.of(Allergen.DAIRY), Set.of(HealthCondition.DIABETES, HealthCondition.PCOS, HealthCondition.CELIAC_GLUTEN_INTOLERANCE), Set.of()),
                    List.of(), foodItemRepository);

            // Item 14: Paneer Malai Tikka (Contains Cashew Paste / Nuts allergy trigger)
            createFood(r2, r2Cat2, "Royal Paneer Malai Tikka (6 pcs)",
                    "Soft malai paneer cubes marinated in rich cashew nut paste, heavy cream, cardamom, and white pepper.",
                    270.0, null, DietType.VEG, SpiceLevel.MILD, 18, 4.6,
                    "https://images.unsplash.com/photo-1599488615731-7e5c2823ff28?w=400",
                    createNutrition(420, 16.0, 12.0, 3.0, 32.0, 14.0, 2.0, 520.0, GlycemicIndex.LOW,
                            Set.of(Allergen.DAIRY, Allergen.NUTS), Set.of(), Set.of(HealthCondition.HEART_DISEASE)),
                    List.of(), foodItemRepository);

            // Item 15: Murgh Makhani / Butter Chicken (High saturated fat & sodium)
            createFood(r2, r2Cat3, "Old Delhi Butter Chicken",
                    "Tandoori chicken pulled and simmered in a velvety tomato, butter, and cashew nut gravy.",
                    340.0, 310.0, DietType.NON_VEG, SpiceLevel.MILD, 20, 4.7,
                    "https://images.unsplash.com/photo-1588166524941-3bf61a9c41db?w=400",
                    createNutrition(520, 30.0, 16.0, 8.5, 36.0, 16.0, 2.5, 920.0, GlycemicIndex.MEDIUM,
                            Set.of(Allergen.DAIRY, Allergen.NUTS), Set.of(), Set.of(HealthCondition.HYPERTENSION, HealthCondition.HEART_DISEASE)),
                    List.of(), foodItemRepository);

            // Item 16: Tandoori Roti (Wheat)
            createFood(r2, r2Cat3, "Whole Wheat Tandoori Roti",
                    "Traditional unleavened whole wheat flatbread baked directly in the clay oven.",
                    35.0, null, DietType.VEGAN, SpiceLevel.MILD, 10, 4.5,
                    "https://images.unsplash.com/photo-1565557623262-b51c2513a641?w=400",
                    createNutrition(110, 3.5, 22.0, 0.4, 0.8, 0.2, 3.0, 140.0, GlycemicIndex.LOW,
                            Set.of(Allergen.GLUTEN), Set.of(HealthCondition.DIABETES, HealthCondition.HYPERTENSION, HealthCondition.HEART_DISEASE), Set.of(HealthCondition.CELIAC_GLUTEN_INTOLERANCE)),
                    List.of(), foodItemRepository);

            // Item 17: Shahi Gulab Jamun (Very high sugar - Contraindicated for Diabetes)
            createFood(r2, r2Cat4, "Shahi Gulab Jamun with Pista (2 pcs)",
                    "Mawa dumplings golden fried and soaked in thick saffron-cardamom sugar syrup, garnished with pistachio slivers.",
                    110.0, 95.0, DietType.VEG, SpiceLevel.MILD, 5, 4.8,
                    "https://images.unsplash.com/photo-1589301760014-d929f3979dbc?w=400",
                    createNutrition(390, 6.0, 62.0, 38.0, 14.0, 6.5, 1.0, 180.0, GlycemicIndex.HIGH,
                            Set.of(Allergen.DAIRY, Allergen.NUTS, Allergen.GLUTEN), Set.of(), Set.of(HealthCondition.DIABETES, HealthCondition.PCOS, HealthCondition.HEART_DISEASE)),
                    List.of(), foodItemRepository);


            // --- CATEGORIES & ITEMS FOR RESTAURANT 3 (Green Root Vegan & Keto) ---
            MenuCategory r3Cat1 = createCategory(r3, "Keto & Low-Carb Specials", 1, categoryRepository);
            MenuCategory r3Cat2 = createCategory(r3, "Wholesome Salad Bowls", 2, categoryRepository);
            MenuCategory r3Cat3 = createCategory(r3, "Warm Healthy Entrees", 3, categoryRepository);
            MenuCategory r3Cat4 = createCategory(r3, "Sugar-Free Smoothies & Drinks", 4, categoryRepository);

            // Item 18: Keto Cauliflower Rice Power Bowl
            createFood(r3, r3Cat1, "Keto Grilled Paneer & Cauli-Rice Bowl",
                    "Riced cauliflower sautéed with olive oil, turmeric, and garlic, topped with paprika grilled paneer, avocado, and toasted seeds.",
                    290.0, 260.0, DietType.VEG, SpiceLevel.MEDIUM, 15, 4.9,
                    "https://images.unsplash.com/photo-1512621776951-a57141f2eefd?w=400",
                    createNutrition(280, 20.0, 9.0, 2.5, 18.0, 4.0, 5.0, 320.0, GlycemicIndex.LOW,
                            Set.of(Allergen.DAIRY), Set.of(HealthCondition.DIABETES, HealthCondition.PCOS, HealthCondition.CELIAC_GLUTEN_INTOLERANCE, HealthCondition.HYPERTENSION), Set.of()),
                    List.of(createCustomization("Cheese Choice", false, List.of("Grilled Paneer (+₹0)", "Smoked Organic Tofu (Vegan) (+₹0)"))),
                    foodItemRepository);

            // Item 19: High-Protein Tofu & Edamame Quinoa Salad
            createFood(r3, r3Cat2, "Edamame, Sprouted Moong & Quinoa Superfood Bowl",
                    "Tri-color organic quinoa tossed with Japanese edamame beans, steamed moong sprouts, baby spinach, and lemon-tahini dressing.",
                    280.0, 249.0, DietType.VEGAN, SpiceLevel.MILD, 12, 4.8,
                    "https://images.unsplash.com/photo-1540420773420-3366772f4999?w=400",
                    createNutrition(310, 19.0, 36.0, 3.0, 8.5, 1.2, 8.5, 240.0, GlycemicIndex.LOW,
                            Set.of(Allergen.SOY), Set.of(HealthCondition.DIABETES, HealthCondition.HYPERTENSION, HealthCondition.HEART_DISEASE, HealthCondition.PCOS, HealthCondition.CELIAC_GLUTEN_INTOLERANCE), Set.of()),
                    List.of(), foodItemRepository);

            // Item 20: Avocado & Walnut Crunch Salad (Contains NUTS)
            createFood(r3, r3Cat2, "Hass Avocado, Cranberry & Walnut Salad",
                    "Fresh Hass avocado chunks with organic mixed salad greens, roasted California walnuts, and orange balsamic vinaigrette.",
                    320.0, null, DietType.VEGAN, SpiceLevel.MILD, 10, 4.7,
                    "https://images.unsplash.com/photo-1512621776951-a57141f2eefd?w=400",
                    createNutrition(350, 6.0, 18.0, 7.0, 28.0, 3.5, 9.0, 190.0, GlycemicIndex.LOW,
                            Set.of(Allergen.NUTS), Set.of(HealthCondition.HEART_DISEASE, HealthCondition.HYPERTENSION, HealthCondition.CELIAC_GLUTEN_INTOLERANCE), Set.of()),
                    List.of(), foodItemRepository);

            // Item 21: Zucchini Noodle (Zoodle) Aglio e Olio
            createFood(r3, r3Cat1, "Spiralized Zucchini Zoodles Aglio e Olio",
                    "Fresh spiralized zucchini noodles sautéed with extra virgin olive oil, golden garlic slivers, chili flakes, and nutritional yeast.",
                    240.0, 219.0, DietType.VEGAN, SpiceLevel.MEDIUM, 15, 4.8,
                    "https://images.unsplash.com/photo-1551183053-bf91a1d81141?w=400",
                    createNutrition(160, 4.5, 11.0, 3.8, 11.0, 1.5, 4.0, 210.0, GlycemicIndex.LOW,
                            Set.of(), Set.of(HealthCondition.DIABETES, HealthCondition.HYPERTENSION, HealthCondition.HEART_DISEASE, HealthCondition.PCOS, HealthCondition.CELIAC_GLUTEN_INTOLERANCE), Set.of()),
                    List.of(), foodItemRepository);

            // Item 22: High-Protein Hemp & Chia Seed Pudding (Diabetic dessert)
            createFood(r3, r3Cat4, "Almond Milk Chia Seed Pudding with Fresh Berries",
                    "Overnight chia pudding made with unsweetened almond milk and pure stevia, topped with fresh raspberries and toasted hemp seeds.",
                    190.0, 169.0, DietType.VEGAN, SpiceLevel.MILD, 5, 4.9,
                    "https://images.unsplash.com/photo-1546069901-ba9599a7e63c?w=400",
                    createNutrition(180, 7.5, 14.0, 2.0, 9.0, 1.0, 8.0, 65.0, GlycemicIndex.LOW,
                            Set.of(Allergen.NUTS), Set.of(HealthCondition.DIABETES, HealthCondition.HYPERTENSION, HealthCondition.PCOS, HealthCondition.CELIAC_GLUTEN_INTOLERANCE), Set.of()),
                    List.of(), foodItemRepository);

            // Item 23: Cold-Pressed ABC Glow Juice (Apple Beet Carrot)
            createFood(r3, r3Cat4, "Cold-Pressed ABC Immunity Elixir",
                    "Pure cold-pressed extract of green apple, beetroot, and carrot with a twist of fresh ginger and Meyer lemon. Zero water or sugar.",
                    130.0, 115.0, DietType.VEGAN, SpiceLevel.MILD, 5, 4.7,
                    "https://images.unsplash.com/photo-1525385133512-2f3bdd039054?w=400",
                    createNutrition(95, 1.8, 22.0, 14.0, 0.4, 0.0, 3.5, 55.0, GlycemicIndex.MEDIUM,
                            Set.of(), Set.of(HealthCondition.HYPERTENSION, HealthCondition.HEART_DISEASE, HealthCondition.CELIAC_GLUTEN_INTOLERANCE), Set.of()),
                    List.of(), foodItemRepository);


            // --- CATEGORIES & ITEMS FOR RESTAURANT 4 (Artisan Crust Pizza & Pasta) ---
            MenuCategory r4Cat1 = createCategory(r4, "Gourmet Artisan Pizzas", 1, categoryRepository);
            MenuCategory r4Cat2 = createCategory(r4, "Handmade Pastas", 2, categoryRepository);
            MenuCategory r4Cat3 = createCategory(r4, "Sides & Appetizers", 3, categoryRepository);
            MenuCategory r4Cat4 = createCategory(r4, "Italian Desserts & Drinks", 4, categoryRepository);

            // Item 24: Margherita Pizza (Wheat & Dairy)
            createFood(r4, r4Cat1, "Classic Neapolitan Margherita Pizza (10\")",
                    "Slow-fermented sourdough crust topped with San Marzano tomato sugo, fresh buffalo mozzarella, and sweet Genovese basil.",
                    320.0, 289.0, DietType.VEG, SpiceLevel.MILD, 20, 4.7,
                    "https://images.unsplash.com/photo-1513104890138-7c749659a591?w=400",
                    createNutrition(650, 24.0, 78.0, 4.5, 26.0, 11.0, 4.5, 760.0, GlycemicIndex.MEDIUM,
                            Set.of(Allergen.GLUTEN, Allergen.DAIRY), Set.of(), Set.of(HealthCondition.CELIAC_GLUTEN_INTOLERANCE, HealthCondition.HYPERTENSION)),
                    List.of(createCustomization("Crust Choice", false, List.of("Neapolitan Sourdough (+₹0)", "Gluten-Free Almond Flour Crust (+₹60)"))),
                    foodItemRepository);

            // Item 25: Gluten-Free Farmhouse Veggie Pizza (Celiac & Diabetic Safe option)
            createFood(r4, r4Cat1, "Gluten-Free Almond-Crust Primavera Pizza",
                    "Grain-free almond and arrowroot crust with sugar-free marinara, roasted zucchini, bell peppers, baby spinach, and vegan almond cheese.",
                    390.0, 349.0, DietType.VEGAN, SpiceLevel.MEDIUM, 22, 4.8,
                    "https://images.unsplash.com/photo-1513104890138-7c749659a591?w=400",
                    createNutrition(380, 16.0, 26.0, 3.5, 22.0, 3.0, 9.0, 410.0, GlycemicIndex.LOW,
                            Set.of(Allergen.NUTS), Set.of(HealthCondition.DIABETES, HealthCondition.CELIAC_GLUTEN_INTOLERANCE, HealthCondition.PCOS), Set.of()),
                    List.of(), foodItemRepository);

            // Item 26: Fiery Pepperoni & Jalapeno Pizza (High Sodium, High Saturated Fat)
            createFood(r4, r4Cat1, "Spicy Smoked Chicken Pepperoni Pizza (10\")",
                    "Zesty marinara base layered with double smoked chicken pepperoni, mozzarella, and spicy pickled jalapenos.",
                    420.0, 380.0, DietType.NON_VEG, SpiceLevel.HOT, 20, 4.6,
                    "https://images.unsplash.com/photo-1628840042765-356cda07504e?w=400",
                    createNutrition(780, 34.0, 72.0, 5.0, 38.0, 15.0, 3.5, 1150.0, GlycemicIndex.HIGH,
                            Set.of(Allergen.GLUTEN, Allergen.DAIRY), Set.of(), Set.of(HealthCondition.HYPERTENSION, HealthCondition.HEART_DISEASE, HealthCondition.DIABETES)),
                    List.of(), foodItemRepository);

            // Item 27: Penne all'Arrabbiata (Gluten)
            createFood(r4, r4Cat2, "Penne all'Arrabbiata with Olives",
                    "Bronze-die penne pasta tossed in a fiery San Marzano tomato, garlic, and red chili sauce with Kalamata olives and extra virgin olive oil.",
                    290.0, 260.0, DietType.VEGAN, SpiceLevel.HOT, 18, 4.6,
                    "https://images.unsplash.com/photo-1621996346565-e3d5d6281745?w=400",
                    createNutrition(440, 13.0, 74.0, 4.0, 8.0, 1.2, 5.0, 680.0, GlycemicIndex.MEDIUM,
                            Set.of(Allergen.GLUTEN), Set.of(), Set.of(HealthCondition.CELIAC_GLUTEN_INTOLERANCE, HealthCondition.HYPERTENSION)),
                    List.of(createCustomization("Pasta Type", false, List.of("Durum Wheat Penne (+₹0)", "Gluten-Free Chickpea Penne (+₹50)"))),
                    foodItemRepository);

            // Item 28: Truffle Mushroom Fettuccine (Dairy & Gluten)
            createFood(r4, r4Cat2, "Creamy Truffle & Wild Porcini Fettuccine",
                    "Handcrafted fettuccine ribbon pasta folded into a rich wild mushroom cream sauce infused with black truffle oil and aged parmesan.",
                    380.0, 340.0, DietType.VEG, SpiceLevel.MILD, 20, 4.8,
                    "https://images.unsplash.com/photo-1556761175-5973dc0f32e7?w=400",
                    createNutrition(590, 18.0, 62.0, 3.5, 29.0, 13.0, 3.8, 720.0, GlycemicIndex.MEDIUM,
                            Set.of(Allergen.GLUTEN, Allergen.DAIRY), Set.of(), Set.of(HealthCondition.CELIAC_GLUTEN_INTOLERANCE, HealthCondition.HEART_DISEASE)),
                    List.of(), foodItemRepository);

            // Item 29: Roasted Garlic Sourdough Bread (4 pcs)
            createFood(r4, r4Cat3, "Confit Garlic & Rosemary Sourdough Bread",
                    "Slow-fermented artisan sourdough slices topped with slow-roasted garlic confit, rosemary, and olive oil.",
                    160.0, 140.0, DietType.VEGAN, SpiceLevel.MILD, 12, 4.7,
                    "https://images.unsplash.com/photo-1573140247632-f8fd74997d5c?w=400",
                    createNutrition(260, 7.0, 44.0, 1.5, 6.0, 0.9, 3.5, 420.0, GlycemicIndex.MEDIUM,
                            Set.of(Allergen.GLUTEN), Set.of(), Set.of(HealthCondition.CELIAC_GLUTEN_INTOLERANCE)),
                    List.of(), foodItemRepository);

            // Item 30: Traditional Venetian Tiramisu (High sugar, Dairy, Gluten)
            createFood(r4, r4Cat4, "Classic Venetian Espresso Tiramisu",
                    "Layers of espresso-soaked savoiardi ladyfingers and whipped mascarpone cream dusted with Dutch cocoa.",
                    240.0, 210.0, DietType.VEG, SpiceLevel.MILD, 5, 4.9,
                    "https://images.unsplash.com/photo-1571877227200-a0d98ea607e9?w=400",
                    createNutrition(410, 6.5, 46.0, 32.0, 22.0, 12.0, 1.2, 160.0, GlycemicIndex.HIGH,
                            Set.of(Allergen.GLUTEN, Allergen.DAIRY, Allergen.EGGS), Set.of(), Set.of(HealthCondition.DIABETES, HealthCondition.PCOS, HealthCondition.CELIAC_GLUTEN_INTOLERANCE)),
                    List.of(), foodItemRepository);

            // Item 31: Italian Sparkling Blood Orange Soda
            createFood(r4, r4Cat4, "Sparkling Citrus Blood Orange Soda",
                    "Refreshing Italian sparkling mineral water infused with fresh blood orange extract.",
                    120.0, null, DietType.VEGAN, SpiceLevel.MILD, 5, 4.6,
                    "https://images.unsplash.com/photo-1556881286-fc6915169721?w=400",
                    createNutrition(110, 0.0, 26.0, 24.0, 0.0, 0.0, 0.0, 25.0, GlycemicIndex.HIGH,
                            Set.of(), Set.of(HealthCondition.CELIAC_GLUTEN_INTOLERANCE), Set.of(HealthCondition.DIABETES, HealthCondition.PCOS)),
                    List.of(), foodItemRepository);

            // Additional items to exceed 35 items
            // Item 32: Dal Tadka with Jeera Brown Rice (Aura)
            createFood(r1, r1Cat3, "Homestyle Yellow Dal Tadka & Jeera Brown Rice",
                    "Double-tempered yellow toor dal with garlic, cumin, and hing, served with organic cumin-scented brown rice.",
                    180.0, 165.0, DietType.VEGAN, SpiceLevel.MEDIUM, 15, 4.7,
                    "https://images.unsplash.com/photo-1546833999-b9f581a1996d?w=400",
                    createNutrition(310, 13.0, 48.0, 2.0, 6.0, 1.0, 6.5, 340.0, GlycemicIndex.LOW,
                            Set.of(), Set.of(HealthCondition.DIABETES, HealthCondition.HYPERTENSION, HealthCondition.HEART_DISEASE, HealthCondition.CELIAC_GLUTEN_INTOLERANCE), Set.of()),
                    List.of(), foodItemRepository);

            // Item 33: Tandoori Fish Tikka (Royal Biryani)
            createFood(r2, r2Cat2, "Amritsari Ajwaini Fish Tikka (5 pcs)",
                    "Fresh river sole fillet cubes marinated with carom seeds, ginger-garlic, and hung curd, roasted in clay oven.",
                    360.0, 320.0, DietType.NON_VEG, SpiceLevel.MEDIUM, 18, 4.8,
                    "https://images.unsplash.com/photo-1599488615731-7e5c2823ff28?w=400",
                    createNutrition(240, 34.0, 3.0, 0.5, 9.0, 2.0, 1.0, 420.0, GlycemicIndex.LOW,
                            Set.of(Allergen.DAIRY), Set.of(HealthCondition.DIABETES, HealthCondition.HEART_DISEASE, HealthCondition.PCOS, HealthCondition.CELIAC_GLUTEN_INTOLERANCE), Set.of()),
                    List.of(), foodItemRepository);

            // Item 34: Cold-Pressed Organic Green Detox Juice (Green Root)
            createFood(r3, r3Cat4, "Alkalizing Celery, Cucumber & Green Apple Tonic",
                    "Hydrating cold-pressed juice of organic celery, English cucumber, baby spinach, mint, and green apple with zero added salt or sugar.",
                    140.0, 120.0, DietType.VEGAN, SpiceLevel.MILD, 5, 4.9,
                    "https://images.unsplash.com/photo-1525385133512-2f3bdd039054?w=400",
                    createNutrition(60, 2.0, 12.0, 7.0, 0.2, 0.0, 2.5, 80.0, GlycemicIndex.LOW,
                            Set.of(), Set.of(HealthCondition.DIABETES, HealthCondition.HYPERTENSION, HealthCondition.HEART_DISEASE, HealthCondition.PCOS, HealthCondition.CELIAC_GLUTEN_INTOLERANCE), Set.of()),
                    List.of(), foodItemRepository);

            // Item 35: Minestrone Soup with White Beans (Artisan Crust)
            createFood(r4, r4Cat3, "Tuscan White Bean & Kale Minestrone",
                    "Hearty Italian vegetable stew with slow-simmered cannellini beans, cavolo nero kale, and garden herbs.",
                    190.0, 169.0, DietType.VEGAN, SpiceLevel.MILD, 15, 4.8,
                    "https://images.unsplash.com/photo-1547592166-23ac45744acd?w=400",
                    createNutrition(190, 9.5, 30.0, 3.8, 3.0, 0.5, 7.0, 360.0, GlycemicIndex.LOW,
                            Set.of(), Set.of(HealthCondition.DIABETES, HealthCondition.HYPERTENSION, HealthCondition.HEART_DISEASE, HealthCondition.CELIAC_GLUTEN_INTOLERANCE), Set.of()),
                    List.of(), foodItemRepository);

            // Item 36: Saffron Badam Milk (Aura - Contains Dairy & Nuts)
            createFood(r1, r1Cat4, "Kashmiri Kesar Badam Milk",
                    "Warm milk infused with pure Kashmiri saffron strands, crushed almonds, and cardamom. Lightly sweetened with unrefined palm jaggery.",
                    110.0, null, DietType.VEG, SpiceLevel.MILD, 5, 4.7,
                    "https://images.unsplash.com/photo-1576092768241-dec231879fc3?w=400",
                    createNutrition(220, 8.5, 20.0, 15.0, 11.0, 5.0, 2.0, 120.0, GlycemicIndex.MEDIUM,
                            Set.of(Allergen.DAIRY, Allergen.NUTS), Set.of(), Set.of()),
                    List.of(), foodItemRepository);
        };
    }

    private MenuCategory createCategory(Restaurant r, String name, int order, MenuCategoryRepository repo) {
        MenuCategory c = new MenuCategory();
        c.setRestaurant(r);
        c.setName(name);
        c.setDisplayOrder(order);
        return repo.save(c);
    }

    private void createFood(Restaurant r, MenuCategory c, String name, String desc,
                            Double price, Double discountPrice, DietType diet, SpiceLevel spice,
                            int prepTime, double rating, String image,
                            NutritionalInfo nut, List<FoodCustomization> customizations,
                            FoodItemRepository repo) {
        FoodItem f = new FoodItem();
        f.setRestaurant(r);
        f.setCategory(c);
        f.setName(name);
        f.setDescription(desc);
        f.setPrice(price);
        f.setDiscountPrice(discountPrice);
        f.setDietType(diet);
        f.setSpiceLevel(spice);
        f.setPreparationTimeMins(prepTime);
        f.setAverageRating(rating);
        f.setImageUrl(image);
        f.setAvailable(true);
        f.setNutritionalInfo(nut);
        f.setCustomizations(customizations);
        repo.save(f);
    }

    private NutritionalInfo createNutrition(int cal, double protein, double carbs, double sugar,
                                           double fat, double satFat, double fiber, double sodium,
                                           GlycemicIndex gi, Set<Allergen> allergens,
                                           Set<HealthCondition> suitable, Set<HealthCondition> contraindicated) {
        NutritionalInfo n = new NutritionalInfo();
        n.setCalories(cal);
        n.setProteinGrams(protein);
        n.setCarbsGrams(carbs);
        n.setSugarGrams(sugar);
        n.setFatGrams(fat);
        n.setSaturatedFatGrams(satFat);
        n.setFiberGrams(fiber);
        n.setSodiumMg(sodium);
        n.setGlycemicIndex(gi);
        n.setAllergens(allergens);
        n.setSuitableForConditions(suitable);
        n.setContraindicatedConditions(contraindicated);
        return n;
    }

    private FoodCustomization createCustomization(String group, boolean required, List<String> optStrings) {
        FoodCustomization c = new FoodCustomization();
        c.setGroupName(group);
        c.setRequired(required);

        List<CustomizationOption> opts = new ArrayList<>();
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("(.*?)\\s*\\(\\+[^0-9]*([0-9.]+)\\)");
        for (String s : optStrings) {
            CustomizationOption opt = new CustomizationOption();
            opt.setFoodCustomization(c);
            java.util.regex.Matcher m = pattern.matcher(s);
            if (m.find()) {
                opt.setName(m.group(1).trim());
                try {
                    opt.setExtraPrice(Double.parseDouble(m.group(2)));
                } catch (Exception ex) {
                    opt.setExtraPrice(0.0);
                }
            } else {
                opt.setName(s.replaceAll("\\s*\\(.*\\)", "").trim());
                opt.setExtraPrice(0.0);
            }
            opts.add(opt);
        }
        c.setOptions(opts);
        return c;
    }
}