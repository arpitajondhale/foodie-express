package com.foodie.repository;

import com.foodie.entity.NutritionalInfo;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NutritionalInfoRepository extends JpaRepository<NutritionalInfo, Long> {
    Optional<NutritionalInfo> findByFoodItemId(Long foodItemId);
}