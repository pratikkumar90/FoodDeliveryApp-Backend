package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.*;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

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

    public CouponEntity getCouponByCouponName(String couponName) {
        try {
            return entityManager.createNamedQuery("getCouponByCouponName", CouponEntity.class)
                    .setParameter("couponName", couponName).getSingleResult();
        } catch (PersistenceException pe) {
            return null;
        }
    }

    public CouponEntity getCouponByCouponUUID(String couponId) {
        try {
            return entityManager.createNamedQuery("getCouponByCouponUUID", CouponEntity.class)
                    .setParameter("uuid", couponId).getSingleResult();
        } catch (PersistenceException pe) {
            return null;
        }
    }

    public List<OrderEntity> ordersByCustomer(CustomerEntity customer) {
        try {
            return entityManager.createNamedQuery("getOrdersByCustomers", OrderEntity.class)
                    .setParameter("customer", customer).getResultList();
        } catch (PersistenceException pe) {
            return null;
        }
    }

    public PaymentEntity getPaymentByUUID(String paymentId) {
        try {
            return entityManager.createNamedQuery("getPaymentByUUID", PaymentEntity.class)
                    .setParameter("uuid", paymentId).getSingleResult();
        } catch (PersistenceException pe) {
            return null;
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public OrderEntity saveOrder(OrderEntity order) {
        entityManager.persist(order);
        return order;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public OrderItemEntity saveOrderItem(OrderItemEntity orderItem) {
        entityManager.persist(orderItem);
        return orderItem;
    }
}
