package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.CategoryDao;
import com.upgrad.FoodOrderingApp.service.dao.RestaurantDao;
import com.upgrad.FoodOrderingApp.service.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ItemService {

    @Autowired
    private CategoryDao categoryDao;

    @Autowired
    private RestaurantDao restaurantDao;

    public List<ItemEntity> getItemsByCategoryAndRestaurant(String restaurantId, String categoryId) {

        RestaurantEntity restaurant = restaurantDao.getRestaurantByUUid(restaurantId);

        List<RestaurantItemEntity> restaurantItems = restaurantDao.getItemsByRestaurant(restaurant);
        List<ItemEntity> restaurantItemEntities = new ArrayList<>();
        restaurantItems.forEach(i -> restaurantItemEntities.add(i.getItem()));

        List<RestaurantCategoryEntity> categories = restaurantDao.getCategoriesForRestaurant(restaurant);

        CategoryEntity categoryEntity = null;
        for (RestaurantCategoryEntity category : categories) {
            if (category.getCategory().getUuid().equals(categoryId)) {
                categoryEntity = category.getCategory();
                break;
            }
        }

        List<ItemEntity> items = new ArrayList<>();

        List<CategoryItemEntity> categoryItems = categoryDao.getItemsForCategory(categoryEntity);
        List<ItemEntity> categoryItemEntities = new ArrayList<>();
        categoryItems.forEach(i -> categoryItemEntities.add(i.getItem()));

        categoryItemEntities.forEach(i -> {
            if (restaurantItemEntities.contains(i)) {
                items.add(i);
            }
        });

        return items;
    }
}
