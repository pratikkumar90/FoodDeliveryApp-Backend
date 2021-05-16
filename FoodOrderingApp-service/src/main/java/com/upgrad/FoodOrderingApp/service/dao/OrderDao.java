package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.OrderEntity;
import com.upgrad.FoodOrderingApp.service.entity.OrderItemEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import java.util.List;

@Repository
public class OrderDao {

    @PersistenceContext
    private EntityManager entityManager;

    public List<OrderEntity> ordersByRestaurant(RestaurantEntity restaurant) {
        try {
            return entityManager.createNamedQuery("getOrdersByRestaurant", OrderEntity.class)
                    .setParameter("restaurant", restaurant).getResultList();
        } catch (PersistenceException pe) {
            return null;
        }
    }

    public List<OrderItemEntity> itemsByOrder(OrderEntity order) {
        try {
            return entityManager.createNamedQuery("getItemsByOrder", OrderItemEntity.class)
                    .setParameter("order", order).getResultList();
        } catch (PersistenceException pe) {
            return null;
        }
    }
}
