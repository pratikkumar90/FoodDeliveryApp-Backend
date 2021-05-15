package com.upgrad.FoodOrderingApp.service.entity;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

@Entity
@Table(name = "restaurant_category")
@NamedQueries({
        @NamedQuery(name = "getCategoriesForRestaurant", query = "select c from RestaurantCategoryEntity c where c.restaurant=:restaurant"),
        @NamedQuery(name = "getRestaurantsByCategory", query = "select c from RestaurantCategoryEntity c where c.category=:category")
})
public class RestaurantCategoryEntity {

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

    @JoinColumn(name = "category_id")
    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    private CategoryEntity category;

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

    public CategoryEntity getCategory() {
        return category;
    }

    public void setCategory(CategoryEntity category) {
        this.category = category;
    }
}
