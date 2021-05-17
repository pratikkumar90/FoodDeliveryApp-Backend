package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.PaymentListResponse;
import com.upgrad.FoodOrderingApp.api.model.PaymentResponse;
import com.upgrad.FoodOrderingApp.service.businness.PaymentService;
import com.upgrad.FoodOrderingApp.service.entity.PaymentEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;
import java.util.UUID;

@Controller
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @RequestMapping(path = "/payment", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<PaymentListResponse> getAllPaymentModes() {

        List<PaymentEntity> patmentEntities = paymentService.getAllPaymentMethods();
        PaymentListResponse response = new PaymentListResponse();
        patmentEntities.forEach(i -> {
            PaymentResponse paymentResponse = new PaymentResponse()
                    .paymentName(i.getPaymentName())
                    .id(UUID.fromString(i.getUuid()));

            response.addPaymentMethodsItem(paymentResponse);
        });
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
