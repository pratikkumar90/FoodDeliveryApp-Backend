package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.ItemEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;

@Repository
public class ItemDao {

    @PersistenceContext
    private EntityManager entityManager;

    public ItemEntity getItemByUUID(String itemUUID) {
        try {
            return entityManager.createNamedQuery("getItemById", ItemEntity.class)
                    .setParameter("uuid", itemUUID).getSingleResult();
        } catch (PersistenceException pe) {
            return null;
        }
    }
}
