package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.AddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import java.util.List;

@Repository
public class AddressDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public CustomerAddressEntity createCustomerAddress(CustomerAddressEntity customerAddressEntity) {
        entityManager.persist(customerAddressEntity);
        return customerAddressEntity;
    }

    @Transactional
    public AddressEntity createAddress(AddressEntity addressEntity) {
        entityManager.persist(addressEntity);
        return addressEntity;
    }

    public List<CustomerAddressEntity> getAllCustomerAddresses(CustomerEntity customerEntity) {
        try {
            return entityManager.createNamedQuery("getAdderessesForCustomer", CustomerAddressEntity.class)
                    .setParameter("customer", customerEntity).getResultList();
        } catch (PersistenceException pe) {
            return null;
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void removeAddress(AddressEntity addressEntity) {
        entityManager.remove(addressEntity);
    }
}
