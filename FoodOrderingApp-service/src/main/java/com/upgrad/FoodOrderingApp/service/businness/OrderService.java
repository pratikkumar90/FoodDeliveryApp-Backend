package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.CustomerDao;
import com.upgrad.FoodOrderingApp.service.dao.OrderDao;
import com.upgrad.FoodOrderingApp.service.entity.*;
import com.upgrad.FoodOrderingApp.service.exception.CouponNotFoundException;
import com.upgrad.FoodOrderingApp.service.exception.PaymentMethodNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private CustomerDao customerDao;

    public CouponEntity getCouponByCouponName(String couponName) throws CouponNotFoundException {
        if (StringUtils.isEmpty(couponName)) {
            throw new CouponNotFoundException("CPF-002", "Coupon name field should not be empty");
        }
        CouponEntity coupon = orderDao.getCouponByCouponName(couponName);
        if (coupon == null) {
            throw new CouponNotFoundException("CPF-001", "No coupon by this name");
        } else {
            return coupon;
        }
    }

    public CouponEntity getCouponByCouponId(String couponId) throws CouponNotFoundException {
        return orderDao.getCouponByCouponUUID(couponId);
    }

    public List<OrderEntity> getOrdersByCustomers(String customerId) {
        CustomerEntity customer = customerDao.getCustomerByUUID(customerId);
        return orderDao.ordersByCustomer(customer);
    }

    public OrderEntity saveOrder(OrderEntity order) {
        return orderDao.saveOrder(order);
    }

    public OrderItemEntity saveOrderItem(OrderItemEntity orderItem) {
        return orderDao.saveOrderItem(orderItem);
    }
}
