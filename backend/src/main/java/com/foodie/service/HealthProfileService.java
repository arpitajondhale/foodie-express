package com.foodie.service;

import com.foodie.dto.request.HealthProfileRequest;
import com.foodie.dto.response.HealthProfileResponse;
import com.foodie.entity.User;
import com.foodie.entity.UserHealthProfile;
import com.foodie.mapper.EntityDtoMapper;
import com.foodie.repository.UserHealthProfileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class HealthProfileService {

    private final UserHealthProfileRepository healthProfileRepository;
    private final EntityDtoMapper mapper;

    public HealthProfileService(UserHealthProfileRepository healthProfileRepository, EntityDtoMapper mapper) {
        this.healthProfileRepository = healthProfileRepository;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public HealthProfileResponse getProfile(User user) {
        UserHealthProfile profile = healthProfileRepository.findByUserId(user.getId())
                .orElseGet(() -> {
                    UserHealthProfile def = new UserHealthProfile();
                    def.setUser(user);
                    return healthProfileRepository.save(def);
                });
        return mapper.toHealthProfileResponse(profile);
    }

    public HealthProfileResponse updateProfile(User user, HealthProfileRequest request) {
        UserHealthProfile profile = healthProfileRepository.findByUserId(user.getId())
                .orElseGet(() -> {
                    UserHealthProfile p = new UserHealthProfile();
                    p.setUser(user);
                    return p;
                });

        if (request.getConditions() != null) {
            profile.setConditions(request.getConditions());
        }
        if (request.getAllergies() != null) {
            profile.setAllergies(request.getAllergies());
        }
        if (request.getDietPreference() != null) {
            profile.setDietPreference(request.getDietPreference());
        }
        if (request.getDailyCalorieTarget() != null) {
            profile.setDailyCalorieTarget(request.getDailyCalorieTarget());
        }
        if (request.getMaxSugarGramsPerMeal() != null) {
            profile.setMaxSugarGramsPerMeal(request.getMaxSugarGramsPerMeal());
        }
        if (request.getMaxSodiumMgPerMeal() != null) {
            profile.setMaxSodiumMgPerMeal(request.getMaxSodiumMgPerMeal());
        }

        profile = healthProfileRepository.save(profile);
        return mapper.toHealthProfileResponse(profile);
    }
}