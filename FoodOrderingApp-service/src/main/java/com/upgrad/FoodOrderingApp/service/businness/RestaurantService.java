package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.CategoryDao;
import com.upgrad.FoodOrderingApp.service.dao.RestaurantDao;
import com.upgrad.FoodOrderingApp.service.entity.CategoryEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantCategoryEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;
import com.upgrad.FoodOrderingApp.service.exception.CategoryNotFoundException;
import com.upgrad.FoodOrderingApp.service.exception.InvalidRatingException;
import com.upgrad.FoodOrderingApp.service.exception.RestaurantNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
public class RestaurantService {

    @Autowired
    private RestaurantDao restaurantDao;

    @Autowired
    private CategoryDao categoryDao;

    public List<RestaurantEntity> restaurantsByRating() {

        List<RestaurantEntity> restaurants = restaurantDao.getAllRestaurants();

        Collections.sort(restaurants, new Comparator<RestaurantEntity>() {
            @Override
            public int compare(RestaurantEntity r1, RestaurantEntity r2) {
                if (r1.getCustomerRating() == r2.getCustomerRating()) {
                    return 0;
                } else if (r1.getCustomerRating() > (r2.getCustomerRating())) {
                    return -1;
                } else {
                    return 1;
                }
            }
        });
        return restaurants;
    }


    public List<RestaurantEntity> restaurantsByName(String restaurantName) throws RestaurantNotFoundException {
        if (StringUtils.isEmpty(restaurantName)) {
            throw new RestaurantNotFoundException("RNF-003", "Restaurant name field should not be empty");
        }
        List<RestaurantEntity> allRestaurants = this.restaurantsByRating();
        List<RestaurantEntity> restaurantsByName = new ArrayList<>();
        allRestaurants.forEach(i -> {
            if (i.getRestaurantName().toLowerCase().contains(restaurantName)) {
                restaurantsByName.add(i);
            }
        });
        return restaurantsByName;
    }

    public List<RestaurantEntity> restaurantByCategory(String caregoryUUid) throws CategoryNotFoundException {
        if (StringUtils.isEmpty(caregoryUUid)) {
            throw new CategoryNotFoundException("CNF-001", "Category id field should not be empty");
        }
        CategoryEntity category = categoryDao.getCategoryByUUid(caregoryUUid);
        if (category == null) {
            throw new CategoryNotFoundException("CNF-002", "No category by this id");
        }
        List<RestaurantCategoryEntity> restaurantsByCategory = restaurantDao.getRestaurantsByCategory(category);
        List<RestaurantEntity> restaurants = new ArrayList<>();
        if (!CollectionUtils.isEmpty(restaurantsByCategory)) {
            restaurantsByCategory.forEach(i -> {
                restaurants.add(i.getRestaurant());
            });
        }

        Collections.sort(restaurants, new Comparator<RestaurantEntity>() {
            @Override
            public int compare(RestaurantEntity r1, RestaurantEntity r2) {
                if (r1.getRestaurantName().equalsIgnoreCase(r2.getRestaurantName())) {
                    return 0;
                } else if (r1.getRestaurantName().compareTo(r2.getRestaurantName()) < 0) {
                    return -1;
                } else {
                    return 1;
                }
            }
        });

        return restaurants;
    }

    public RestaurantEntity restaurantByUUID(String restaurantUuid) throws RestaurantNotFoundException {
        if (StringUtils.isEmpty(restaurantUuid)) {
            throw new RestaurantNotFoundException("RNF-002", "Restaurant id field should not be empty");
        }
        RestaurantEntity restaurantEntity = restaurantDao.getRestaurantByUUid(restaurantUuid);
        if (restaurantEntity == null) {
            throw new RestaurantNotFoundException("RNF-001", "No restaurant by this id");
        }
        return restaurantEntity;
    }

    public RestaurantEntity updateRestaurantRating(RestaurantEntity restaurant, double rating) throws RestaurantNotFoundException, InvalidRatingException {

        if (rating < 1 || rating > 5) {
            throw new InvalidRatingException("IRE-001", "Restaurant should be in the range of 1 to 5");
        }

        double currentRating = restaurant.getCustomerRating();
        int noOfCustomerRated = restaurant.getNumberCustomersRated();

        double updatedCustomerRating = ((currentRating * noOfCustomerRated) + rating) / (++noOfCustomerRated);
        restaurant.setCustomerRating(updatedCustomerRating);
        restaurant.setNumberCustomersRated(noOfCustomerRated);

        return restaurantDao.update(restaurant);
    }
}
