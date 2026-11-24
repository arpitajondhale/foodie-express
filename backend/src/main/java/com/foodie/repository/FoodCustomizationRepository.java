package com.foodie.repository;

import com.foodie.entity.FoodCustomization;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FoodCustomizationRepository extends JpaRepository<FoodCustomization, Long> {
    List<FoodCustomization> findByFoodItemId(Long foodItemId);
}