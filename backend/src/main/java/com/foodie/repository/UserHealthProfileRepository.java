package com.foodie.repository;

import com.foodie.entity.UserHealthProfile;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserHealthProfileRepository extends JpaRepository<UserHealthProfile, Long> {
    Optional<UserHealthProfile> findByUserId(Long userId);
}