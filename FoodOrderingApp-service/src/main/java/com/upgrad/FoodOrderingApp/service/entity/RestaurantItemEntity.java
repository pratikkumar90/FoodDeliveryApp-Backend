package com.upgrad.FoodOrderingApp.service.entity;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

@Entity
@Table(name = "restaurant_item")
@NamedQuery(name = "getItemsByRestaurant", query = "select i from RestaurantItemEntity  i where i.restaurant = :restaurant")
public class RestaurantItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    //@Column annotation specifies that the attribute will be mapped to the column in the database.
    //Here the column name is explicitly mentioned as 'id'
    @Column(name = "id")
    private Integer id;

    @JoinColumn(name = "restaurant_id")
    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    private RestaurantEntity restaurant;

    @JoinColumn(name = "item_id")
    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    private ItemEntity item;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public RestaurantEntity getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(RestaurantEntity restaurant) {
        this.restaurant = restaurant;
    }

    public ItemEntity getItem() {
        return item;
    }

    public void setItem(ItemEntity item) {
        this.item = item;
    }
}
