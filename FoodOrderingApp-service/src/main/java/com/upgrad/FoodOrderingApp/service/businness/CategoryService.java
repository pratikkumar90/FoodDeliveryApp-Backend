package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.CategoryDao;
import com.upgrad.FoodOrderingApp.service.dao.RestaurantDao;
import com.upgrad.FoodOrderingApp.service.entity.CategoryEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantCategoryEntity;
import com.upgrad.FoodOrderingApp.service.exception.CategoryNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryService {

    @Autowired
    private RestaurantDao restaurantDao;

    @Autowired
    private CategoryDao categoryDao;

    public List<CategoryEntity> getCategoriesByRestaurant(String restaurantUUid) {
        List<RestaurantCategoryEntity> restaurantCategoryEntities = restaurantDao.getCategoriesForRestaurant(restaurantDao.getRestaurantByUUid(restaurantUUid));
        List<CategoryEntity> categories = new ArrayList<>();
        restaurantCategoryEntities.forEach(i -> {
            categories.add(i.getCategory());
        });
        return categories;
    }

    public List<CategoryEntity> getAllCategoriesOrderedByName() {
        return categoryDao.getAllCategoriesOrderedByName();
    }

    public CategoryEntity getCategoryById(String categoryUUID) throws CategoryNotFoundException {
        if (StringUtils.isEmpty(categoryUUID)) {
            throw new CategoryNotFoundException("CNF-001", "Category id field should not be empty");
        }
        CategoryEntity category = categoryDao.getCategoryByUUid(categoryUUID);

        if (category == null) {
            throw new CategoryNotFoundException("CNF-002", "No category by this id");
        } else {
            return category;
        }
    }
}
