package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.CategoryEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantCategoryEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantItemEntity;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import java.util.List;

@Repository
public class RestaurantDao {

    @PersistenceContext
    private EntityManager entityManager;

    public List<RestaurantEntity> getAllRestaurants() {
        try {
            return entityManager.createNamedQuery("getAllRestaurants").getResultList();
        } catch (PersistenceException e) {
            return null;
        }
    }

    public List<RestaurantCategoryEntity> getCategoriesForRestaurant(RestaurantEntity restaurant) {
        try {
            return entityManager.createNamedQuery("getCategoriesForRestaurant").setParameter("restaurant", restaurant).getResultList();
        } catch (PersistenceException e) {
            return null;
        }
    }

    public List<RestaurantCategoryEntity> getRestaurantsByCategory(CategoryEntity categoryEntity) {
        try {
            return entityManager.createNamedQuery("getRestaurantsByCategory").setParameter("category", categoryEntity).getResultList();
        } catch (PersistenceException e) {
            return null;
        }
    }

    public CategoryEntity getCategoryByUUid(String categoryUUid) {
        try {
            return entityManager.createNamedQuery("getCategoryByUUid", CategoryEntity.class).setParameter("uuid", categoryUUid).getSingleResult();
        } catch (PersistenceException e) {
            return null;
        }
    }

    public RestaurantEntity getRestaurantByUUid(String restaurantUuid) {
        try {
            return entityManager.createNamedQuery("getRestaurantByUuid", RestaurantEntity.class).setParameter("uuid", restaurantUuid).getSingleResult();
        } catch (PersistenceException e) {
            return null;
        }
    }

    public List<RestaurantItemEntity> getItemsByRestaurant(RestaurantEntity restaurantEntity) {
        try {
            return entityManager.createNamedQuery("getItemsByRestaurant", RestaurantItemEntity.class).setParameter("restaurant", restaurantEntity).getResultList();
        } catch (PersistenceException e) {
            return null;
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public RestaurantEntity update(RestaurantEntity restaurantEntity) {
        entityManager.merge(restaurantEntity);
        return restaurantEntity;
    }
}
