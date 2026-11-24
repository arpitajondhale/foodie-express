package com.foodie.service;

import com.foodie.dto.response.MenuCategoryResponse;
import com.foodie.dto.response.RestaurantResponse;
import com.foodie.entity.MenuCategory;
import com.foodie.entity.Restaurant;
import com.foodie.exception.ResourceNotFoundException;
import com.foodie.mapper.EntityDtoMapper;
import com.foodie.repository.MenuCategoryRepository;
import com.foodie.repository.RestaurantRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final MenuCategoryRepository menuCategoryRepository;
    private final EntityDtoMapper mapper;

    public RestaurantService(RestaurantRepository restaurantRepository,
                             MenuCategoryRepository menuCategoryRepository,
                             EntityDtoMapper mapper) {
        this.restaurantRepository = restaurantRepository;
        this.menuCategoryRepository = menuCategoryRepository;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "restaurants", key = "'all-' + #query + '-' + #city")
    public List<RestaurantResponse> getRestaurants(String query, String city) {
        List<Restaurant> list;
        if (city != null && !city.isBlank()) {
            list = restaurantRepository.findByCityIgnoreCase(city.trim());
        } else {
            list = restaurantRepository.findAll();
        }

        if (query != null && !query.isBlank()) {
            String q = query.toLowerCase().trim();
            list = list.stream().filter(r ->
                    r.getName().toLowerCase().contains(q) ||
                    (r.getDescription() != null && r.getDescription().toLowerCase().contains(q)) ||
                    r.getCuisineTypes().stream().anyMatch(c -> c.toLowerCase().contains(q))
            ).collect(Collectors.toList());
        }

        return list.stream()
                .map(r -> mapper.toRestaurantResponse(r, false))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public RestaurantResponse getRestaurantById(Long id) {
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found: " + id));
        return mapper.toRestaurantResponse(restaurant, true);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "restaurant-menus", key = "#restaurantId")
    public List<MenuCategoryResponse> getRestaurantMenu(Long restaurantId) {
        if (!restaurantRepository.existsById(restaurantId)) {
            throw new ResourceNotFoundException("Restaurant not found: " + restaurantId);
        }
        List<MenuCategory> categories = menuCategoryRepository.findByRestaurantIdOrderByDisplayOrderAsc(restaurantId);
        return categories.stream()
                .map(c -> mapper.toMenuCategoryResponse(c, true))
                .collect(Collectors.toList());
    }

    @CacheEvict(value = {"restaurants", "restaurant-menus"}, allEntries = true)
    public RestaurantResponse toggleRestaurantOpen(Long id) {
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found: " + id));
        restaurant.setOpen(!restaurant.isOpen());
        restaurant = restaurantRepository.save(restaurant);
        return mapper.toRestaurantResponse(restaurant, true);
    }
}