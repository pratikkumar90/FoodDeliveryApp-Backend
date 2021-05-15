package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.CategoryEntity;
import com.upgrad.FoodOrderingApp.service.entity.CategoryItemEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import java.util.List;

@Repository
public class CategoryDao {

    @PersistenceContext
    private EntityManager entityManager;


    public List<CategoryItemEntity> getItemsForCategory(CategoryEntity categoryEntity) {
        try {
            return entityManager.createNamedQuery("getItemsForCategory", CategoryItemEntity.class).setParameter("category", categoryEntity).getResultList();
        } catch (PersistenceException e) {
            return null;
        }
    }
}
