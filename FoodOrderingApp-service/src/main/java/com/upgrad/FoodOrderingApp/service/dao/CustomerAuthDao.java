package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthEntity;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;

@Repository
public class CustomerAuthDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public CustomerAuthEntity createCustomerAuthEntity(CustomerAuthEntity customerAuth) {
        entityManager.persist(customerAuth);
        return customerAuth;
    }

    public CustomerAuthEntity getCustomerAuthByAccessToken(String accessToken) {
        try {
            return entityManager.createNamedQuery("customerAuthByToken", CustomerAuthEntity.class)
                    .setParameter("accessToken", accessToken).getSingleResult();
        } catch (PersistenceException pe) {
            return null;
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerAuthEntity updateCustomerAuthEntity(CustomerAuthEntity customerAuth) {
        entityManager.merge(customerAuth);
        return customerAuth;
    }
}
