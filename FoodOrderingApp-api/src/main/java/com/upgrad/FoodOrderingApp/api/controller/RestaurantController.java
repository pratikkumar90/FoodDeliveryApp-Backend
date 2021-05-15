package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.*;
import com.upgrad.FoodOrderingApp.service.businness.CategoryService;
import com.upgrad.FoodOrderingApp.service.businness.CustomerService;
import com.upgrad.FoodOrderingApp.service.businness.ItemService;
import com.upgrad.FoodOrderingApp.service.businness.RestaurantService;
import com.upgrad.FoodOrderingApp.service.entity.CategoryEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.entity.ItemEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.CategoryNotFoundException;
import com.upgrad.FoodOrderingApp.service.exception.InvalidRatingException;
import com.upgrad.FoodOrderingApp.service.exception.RestaurantNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Controller
public class RestaurantController {

    @Autowired
    private RestaurantService restaurantService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ItemService itemService;

    @Autowired
    private CustomerService customerService;

    @RequestMapping(path = "/restaurant", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<RestaurantListResponse> getAllRestaurants() {
        List<RestaurantEntity> restaurants = restaurantService.restaurantsByRating();
        RestaurantListResponse response = new RestaurantListResponse();
        List<RestaurantList> restaurantList = new ArrayList<>();
        restaurants.forEach(i -> {
            RestaurantList restaurant = setRestaurantDetails(i);

            restaurantList.add(restaurant);
        });
        response.setRestaurants(restaurantList);
        return new ResponseEntity<RestaurantListResponse>(response, HttpStatus.OK);
    }


    @RequestMapping(path = "/restaurant/name/{restaurant_name}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<RestaurantListResponse> getRestaurantsByName(@PathVariable(name = "restaurant_name") String restaurantName) throws RestaurantNotFoundException {
        List<RestaurantEntity> restaurants = restaurantService.restaurantsByName(restaurantName);
        List<RestaurantList> restaurantList = new ArrayList<>();
        restaurants.forEach(i -> {
            RestaurantList restaurant = setRestaurantDetails(i);

            restaurantList.add(restaurant);
        });

        RestaurantListResponse response = new RestaurantListResponse();
        response.setRestaurants(restaurantList);

        return new ResponseEntity<RestaurantListResponse>(response, HttpStatus.OK);
    }

    @RequestMapping(path = "/restaurant/category/{category_id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<RestaurantListResponse> getRestaurantsByCategory(@PathVariable(name = "category_id") String categoryId) throws RestaurantNotFoundException, CategoryNotFoundException {
        List<RestaurantEntity> restaurants = restaurantService.restaurantByCategory(categoryId);
        List<RestaurantList> restaurantList = new ArrayList<>();
        restaurants.forEach(i -> {
            RestaurantList restaurant = setRestaurantDetails(i);

            restaurantList.add(restaurant);
        });

        RestaurantListResponse response = new RestaurantListResponse();
        response.setRestaurants(restaurantList);

        return new ResponseEntity<RestaurantListResponse>(response, HttpStatus.OK);
    }

    @RequestMapping(path = "/restaurant/{restaurant_id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<RestaurantDetailsResponse> getRestaurantsByRestaurantId(@PathVariable(name = "restaurant_id") String restaurantId) throws RestaurantNotFoundException {

        RestaurantEntity restaurant = restaurantService.restaurantByUUID(restaurantId);

        List<CategoryEntity> categoryEntities = categoryService.getCategoriesByRestaurant(restaurantId);
        List<CategoryList> categories = new ArrayList<>();
        categoryEntities.forEach(i -> {
            List<ItemEntity> items = itemService.getItemsByCategoryAndRestaurant(restaurantId, i.getUuid());

            List<ItemList> itemsList = new ArrayList<>();
            items.forEach(j -> {
                ItemList item = new ItemList()
                        .itemName(j.getItemName())
                        .itemType(j.getType().equals("0") ? ItemList.ItemTypeEnum.VEG : ItemList.ItemTypeEnum.NON_VEG)
                        .id(UUID.fromString(j.getUuid()))
                        .price(j.getPrice());
                itemsList.add(item);
            });

            CategoryList category = new CategoryList()
                    .categoryName(i.getCategoryName())
                    .id(UUID.fromString(i.getUuid()))
                    .itemList(itemsList);

            categories.add(category);
        });

        RestaurantDetailsResponseAddressState stateDetails = new RestaurantDetailsResponseAddressState()
                .stateName(restaurant.getAddress().getState().getStateName())
                .id(UUID.fromString(restaurant.getAddress().getState().getUuid()));

        RestaurantDetailsResponseAddress responseAddress = new RestaurantDetailsResponseAddress()
                .city(restaurant.getAddress().getCity())
                .flatBuildingName(restaurant.getAddress().getFlatBuilNo())
                .id(UUID.fromString(restaurant.getAddress().getUuid()))
                .locality(restaurant.getAddress().getLocality())
                .pincode(restaurant.getAddress().getPincode())
                .state(stateDetails);

        RestaurantDetailsResponse response = new RestaurantDetailsResponse()
                .averagePrice(restaurant.getAvgPrice())
                .customerRating(new BigDecimal(restaurant.getCustomerRating()))
                .id(UUID.fromString(restaurant.getUuid()))
                .numberCustomersRated(restaurant.getNumberCustomersRated())
                .photoURL(restaurant.getPhotoUrl())
                .restaurantName(restaurant.getRestaurantName())
                .address(responseAddress)
                .categories(categories);


        return new ResponseEntity<RestaurantDetailsResponse>(response, HttpStatus.OK);
    }

    @RequestMapping(path = "/restaurant/{restaurant_id}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<RestaurantUpdatedResponse> updateCustomerRating(@PathVariable(name = "restaurant_id") String restaurantId,
                                                                          @RequestParam(name = "customer_rating") double customerRating,
                                                                          @RequestHeader final String authorization) throws RestaurantNotFoundException, AuthorizationFailedException, InvalidRatingException {
        String[] tokens = authorization.split("Bearer ");
        if (tokens.length != 2) {
            throw new AuthorizationFailedException("ATHR-001", "Customer is not Logged in.");
        } else {
            CustomerEntity customerEntity = customerService.getCustomer(tokens[1]);
            RestaurantEntity restaurant = restaurantService.restaurantByUUID(restaurantId);
            restaurant = restaurantService.updateRestaurantRating(restaurant, customerRating);

            RestaurantUpdatedResponse response = new RestaurantUpdatedResponse()
                    .id(UUID.fromString(restaurantId))
                    .status("RESTAURANT RATING UPDATED SUCCESSFULLY");

            return new ResponseEntity<RestaurantUpdatedResponse>(response, HttpStatus.OK);
        }
    }

    private void setCategories(RestaurantEntity i, RestaurantList restaurant) {
        List<CategoryEntity> categoryEntities = categoryService.getCategoriesByRestaurant(i.getUuid());
        List<String> categories = new ArrayList<>();
        categoryEntities.forEach(j -> {
            categories.add(j.getCategoryName());
        });
        Collections.sort(categories);
        String categoryString = "";
        for (String k : categories) {
            categoryString = categoryString + k + ",";
        }
        restaurant.setCategories(categoryString.substring(0, categoryString.length() - 1));
    }

    private RestaurantList setRestaurantDetails(RestaurantEntity i) {
        RestaurantList restaurant = new RestaurantList();
        restaurant.setAveragePrice(i.getAvgPrice());
        restaurant.setCustomerRating(new BigDecimal(i.getCustomerRating()));
        restaurant.setId(UUID.fromString(i.getUuid()));
        restaurant.setPhotoURL(i.getPhotoUrl());
        restaurant.setRestaurantName(i.getRestaurantName());
        restaurant.setNumberCustomersRated(i.getNumberCustomersRated());

        setAddress(i, restaurant);
        setCategories(i, restaurant);

        return restaurant;
    }

    private void setAddress(RestaurantEntity i, RestaurantList restaurant) {
        RestaurantDetailsResponseAddressState state = new RestaurantDetailsResponseAddressState()
                .stateName(i.getAddress().getState().getStateName())
                .id(UUID.fromString(i.getAddress().getState().getUuid()));

        RestaurantDetailsResponseAddress address = new RestaurantDetailsResponseAddress()
                .city(i.getAddress().getCity())
                .flatBuildingName(i.getAddress().getFlatBuilNo())
                .locality(i.getAddress().getLocality())
                .pincode(i.getAddress().getPincode())
                .id(UUID.fromString(i.getAddress().getUuid()))
                .state(state);

        restaurant.setAddress(address);
    }
}
