package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.RestaurantDao;
import com.upgrad.FoodOrderingApp.service.entity.CategoryEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantCategoryEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryService {

    @Autowired
    private RestaurantDao restaurantDao;

    public List<CategoryEntity> getCategoriesByRestaurant(String restaurantUUid) {
        List<RestaurantCategoryEntity> restaurantCategoryEntities = restaurantDao.getCategoriesForRestaurant(restaurantDao.getRestaurantByUUid(restaurantUUid));
        List<CategoryEntity> categories = new ArrayList<>();
        restaurantCategoryEntities.forEach(i -> {
            categories.add(i.getCategory());
        });
        return categories;
    }
}
