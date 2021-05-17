package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.*;
import com.upgrad.FoodOrderingApp.service.businness.*;
import com.upgrad.FoodOrderingApp.service.entity.*;
import com.upgrad.FoodOrderingApp.service.exception.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Controller
public class OrderController {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private ItemService itemService;

    @Autowired
    private AddressService addressService;

    @Autowired
    private RestaurantService restaurantService;

    @Autowired
    private PaymentService paymentService;

    @RequestMapping(method = RequestMethod.GET, path = "/order/coupon/{coupon_name}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<CouponDetailsResponse> getCouponDetails(@RequestHeader final String authorization,
                                                                  @PathVariable(name = "coupon_name") String couponName) throws AuthorizationFailedException, CouponNotFoundException {
        String[] tokens = authorization.split("Bearer ");
        if (tokens.length != 2) {
            throw new AuthorizationFailedException("ATHR-001", "Customer is not Logged in.");
        } else {
            CustomerEntity customerEntity = customerService.getCustomer(tokens[1]);
            CouponEntity coupon = orderService.getCouponByCouponName(couponName);

            CouponDetailsResponse response = new CouponDetailsResponse()
                    .couponName(couponName)
                    .id(UUID.fromString(coupon.getUuid()))
                    .percent(coupon.getPercent());

            return new ResponseEntity<>(response, HttpStatus.OK);
        }
    }

    @RequestMapping(path = "/order", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<CustomerOrderResponse> getOldOrders(@RequestHeader final String authorization) throws AuthorizationFailedException, CouponNotFoundException {
        String[] tokens = authorization.split("Bearer ");
        if (tokens.length != 2) {
            throw new AuthorizationFailedException("ATHR-001", "Customer is not Logged in.");
        } else {
            CustomerEntity customerEntity = customerService.getCustomer(tokens[1]);
            List<OrderEntity> orders = orderService.getOrdersByCustomers(customerEntity.getUuid());

            CustomerOrderResponse response = new CustomerOrderResponse();
            orders.forEach(i -> {
                OrderListCoupon coupon = new OrderListCoupon();
                if (i.getCoupon() != null) {
                    coupon.couponName(i.getCoupon().getCouponName())
                            .percent(i.getCoupon().getPercent())
                            .id(UUID.fromString(i.getCoupon().getUuid()));
                }

                OrderListPayment payment = new OrderListPayment();
                if (i.getPayment() != null) {
                    payment.paymentName(i.getPayment().getPaymentName())
                            .id(UUID.fromString(i.getPayment().getUuid()));
                }

                OrderListCustomer customer = new OrderListCustomer();
                if (i.getCustomer() != null) {

                    customer.contactNumber(i.getCustomer().getContactNumber())
                            .emailAddress(i.getCustomer().getEmail())
                            .firstName(i.getCustomer().getFirstName())
                            .id(UUID.fromString(i.getCustomer().getUuid()))
                            .lastName(i.getCustomer().getLastName());
                }

                OrderListAddressState state = new OrderListAddressState();
                OrderListAddress address = new OrderListAddress();
                if (i.getAddress() != null && i.getAddress().getState() != null) {
                    state.stateName(i.getAddress().getState().getStateName())
                            .id(UUID.fromString(i.getAddress().getState().getUuid()));

                    address.city(i.getAddress().getCity())
                            .flatBuildingName(i.getAddress().getFlatBuilNo())
                            .id(UUID.fromString(i.getAddress().getUuid()))
                            .locality(i.getAddress().getLocality())
                            .pincode(i.getAddress().getPincode())
                            .state(state);
                }
                List<ItemQuantityResponse> itemQuantities = new ArrayList<>();
                List<OrderItemEntity> items = itemService.itemsByOrder(i);
                items.forEach(item -> {
                    ItemQuantityResponseItem itemQuantityResponseItem = new ItemQuantityResponseItem()
                            .itemName(item.getItem().getItemName())
                            .itemPrice(item.getItem().getPrice())
                            .id(UUID.fromString(item.getItem().getUuid()))
                            .type(ItemQuantityResponseItem.TypeEnum.fromValue(item.getItem().getType().equals("0") ? "VEG" : "NON_VEG"));

                    ItemQuantityResponse itemQuantity = new ItemQuantityResponse()
                            .quantity(item.getQuantity())
                            .price(item.getPrice())
                            .item(itemQuantityResponseItem);
                    itemQuantities.add(itemQuantity);
                });

                OrderList order = new OrderList()
                        .bill(new BigDecimal(i.getBill()))
                        .date(i.getDate().toString())
                        .address(address)
                        .coupon(coupon)
                        .customer(customer)
                        .discount(new BigDecimal(i.getDiscount()))
                        .id(UUID.fromString(i.getUuid()))
                        .itemQuantities(itemQuantities)
                        .payment(payment);
                response.addOrdersItem(order);
            });


            return new ResponseEntity<>(response, HttpStatus.OK);
        }
    }

    @RequestMapping(path = "/order", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SaveOrderResponse> createOrder(@RequestHeader final String authorization,
                                                         @RequestBody SaveOrderRequest saveOrderRequest) throws AuthorizationFailedException, CouponNotFoundException, AddressNotFoundException, PaymentMethodNotFoundException, RestaurantNotFoundException, ItemNotFoundException {
        String[] tokens = authorization.split("Bearer ");
        if (tokens.length != 2) {
            throw new AuthorizationFailedException("ATHR-001", "Customer is not Logged in.");
        } else {
            CustomerEntity customerEntity = customerService.getCustomer(tokens[1]);
            CouponEntity couponEntity = orderService.getCouponByCouponId(saveOrderRequest.getCouponId().toString());
            PaymentEntity paymentEntity = paymentService.getPaymentByUUID(saveOrderRequest.getPaymentId().toString());
            AddressEntity addressEntity = addressService.getAddressByUUID(saveOrderRequest.getAddressId().toString(), customerEntity);

            RestaurantEntity restaurantEntity = restaurantService.restaurantByUUID(saveOrderRequest.getRestaurantId().toString());

            OrderEntity orderEntity = new OrderEntity();
            orderEntity.setAddress(addressEntity);
            orderEntity.setBill(saveOrderRequest.getBill().doubleValue());
            orderEntity.setCoupon(couponEntity);
            orderEntity.setCustomer(customerEntity);
            orderEntity.setDate(ZonedDateTime.now());
            orderEntity.setDiscount(saveOrderRequest.getDiscount().doubleValue());
            orderEntity.setPayment(paymentEntity);
            orderEntity.setRestaurant(restaurantEntity);
            orderEntity.setUuid(UUID.randomUUID().toString());

            orderEntity = orderService.saveOrder(orderEntity);


            for (ItemQuantity i : saveOrderRequest.getItemQuantities()) {
                OrderItemEntity item = new OrderItemEntity();
                item.setItem(itemService.itemByUUID(i.getItemId().toString()));
                item.setOrder(orderEntity);
                item.setPrice(i.getPrice());
                item.setQuantity(i.getQuantity());

                orderService.saveOrderItem(item);
            }

            SaveOrderResponse response = new SaveOrderResponse()
                    .id(orderEntity.getUuid())
                    .status("ORDER SUCCESSFULLY PLACED");

            return new ResponseEntity<>(response, HttpStatus.CREATED);
        }
    }
}
