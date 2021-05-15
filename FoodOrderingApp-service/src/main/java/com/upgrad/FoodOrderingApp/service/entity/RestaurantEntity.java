package com.upgrad.FoodOrderingApp.service.entity;

import javax.persistence.*;

@Entity
@Table(name = "restaurant")
@NamedQueries({
        @NamedQuery(name = "getAllRestaurants", query = "select r from RestaurantEntity r"),
        @NamedQuery(name = "getRestaurantByUuid", query = "select r from RestaurantEntity r where r.uuid=:uuid")
})

public class RestaurantEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    //@Column annotation specifies that the attribute will be mapped to the column in the database.
    //Here the column name is explicitly mentioned as 'id'
    @Column(name = "id")
    private Integer id;

    @Column(name = "uuid")
    private String uuid;

    @Column(name = "restaurant_name")
    private String restaurantName;

    @Column(name = "photo_url")
    private String photoUrl;

    @Column(name = "customer_rating")
    private double customerRating;

    @Column(name = "average_price_for_two")
    private int avgPrice;

    @Column(name = "number_of_customers_rated")
    private int numberCustomersRated;

    @JoinColumn(name = "address_id")
    @ManyToOne
    private AddressEntity address;

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

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public double getCustomerRating() {
        return customerRating;
    }

    public void setCustomerRating(double customerRating) {
        this.customerRating = customerRating;
    }

    public int getAvgPrice() {
        return avgPrice;
    }

    public void setAvgPrice(int avgPrice) {
        this.avgPrice = avgPrice;
    }

    public int getNumberCustomersRated() {
        return numberCustomersRated;
    }

    public void setNumberCustomersRated(int numberCustomersRated) {
        this.numberCustomersRated = numberCustomersRated;
    }

    public AddressEntity getAddress() {
        return address;
    }

    public void setAddress(AddressEntity address) {
        this.address = address;
    }
}
