package com.foodie;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class FoodieExpressApplication {
    public static void main(String[] args) {
        SpringApplication.run(FoodieExpressApplication.class, args);
    }
}