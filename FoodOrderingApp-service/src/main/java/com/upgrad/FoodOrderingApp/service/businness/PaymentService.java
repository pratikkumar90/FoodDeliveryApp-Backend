package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.OrderDao;
import com.upgrad.FoodOrderingApp.service.entity.PaymentEntity;
import com.upgrad.FoodOrderingApp.service.exception.PaymentMethodNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class PaymentService {

    @Autowired
    private OrderDao orderDao;

    public PaymentEntity getPaymentByUUID(String paymentId) throws PaymentMethodNotFoundException {
        if (StringUtils.isEmpty(paymentId)) {
            throw new PaymentMethodNotFoundException("PNF-001", "Payment name field should not be empty");
        }
        PaymentEntity payment = orderDao.getPaymentByUUID(paymentId);
        if (payment == null) {
            throw new PaymentMethodNotFoundException("PNF-002", "No payment method found by this id");
        } else {
            return payment;
        }
    }
}
