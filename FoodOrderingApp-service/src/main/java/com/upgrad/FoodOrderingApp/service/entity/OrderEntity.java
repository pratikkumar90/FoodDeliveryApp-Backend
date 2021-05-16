package com.upgrad.FoodOrderingApp.service.entity;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Entity
@Table(name = "orders")
@NamedQueries(
        @NamedQuery(name = "getOrdersByRestaurant", query = "select o from OrderEntity o where o.restaurant=:restaurant")
)
public class OrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    //@Column annotation specifies that the attribute will be mapped to the column in the database.
    //Here the column name is explicitly mentioned as 'id'
    @Column(name = "id")
    private Integer id;

    @Column(name = "uuid")
    private String uuid;

    @Column(name = "bill")
    private Double bill;

    @JoinColumn(name = "coupon_id")
    @ManyToOne
    private CouponEntity coupon;

    @Column(name = "discount")
    private Double discount;

    @Column(name = "date")
    private ZonedDateTime date;

    @JoinColumn(name = "payment_id")
    @ManyToOne
    private PaymentEntity payment;

    @JoinColumn(name = "customer_id")
    @ManyToOne
    private CustomerEntity customer;

    @JoinColumn(name = "address_id")
    @ManyToOne
    private AddressEntity address;

    @JoinColumn(name = "restaurant_id")
    @ManyToOne
    private RestaurantEntity restaurant;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Double getBill() {
        return bill;
    }

    public void setBill(Double bill) {
        this.bill = bill;
    }

    public CouponEntity getCoupon() {
        return coupon;
    }

    public void setCoupon(CouponEntity coupon) {
        this.coupon = coupon;
    }

    public Double getDiscount() {
        return discount;
    }

    public void setDiscount(Double discount) {
        this.discount = discount;
    }

    public ZonedDateTime getDate() {
        return date;
    }

    public void setDate(ZonedDateTime date) {
        this.date = date;
    }

    public PaymentEntity getPayment() {
        return payment;
    }

    public void setPayment(PaymentEntity payment) {
        this.payment = payment;
    }

    public CustomerEntity getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerEntity customer) {
        this.customer = customer;
    }

    public AddressEntity getAddress() {
        return address;
    }

    public void setAddress(AddressEntity address) {
        this.address = address;
    }

    public RestaurantEntity getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(RestaurantEntity restaurant) {
        this.restaurant = restaurant;
    }
}
