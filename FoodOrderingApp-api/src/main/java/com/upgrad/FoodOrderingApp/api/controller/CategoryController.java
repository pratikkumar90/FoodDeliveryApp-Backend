package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.CategoriesListResponse;
import com.upgrad.FoodOrderingApp.api.model.CategoryDetailsResponse;
import com.upgrad.FoodOrderingApp.api.model.CategoryListResponse;
import com.upgrad.FoodOrderingApp.api.model.ItemList;
import com.upgrad.FoodOrderingApp.service.businness.CategoryService;
import com.upgrad.FoodOrderingApp.service.businness.ItemService;
import com.upgrad.FoodOrderingApp.service.entity.CategoryEntity;
import com.upgrad.FoodOrderingApp.service.entity.ItemEntity;
import com.upgrad.FoodOrderingApp.service.exception.CategoryNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;
import java.util.UUID;

@Controller
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ItemService itemService;

    @RequestMapping(path = "/category", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<CategoriesListResponse> getAllCategories() {
        List<CategoryEntity> categories = categoryService.getAllCategoriesOrderedByName();
        CategoriesListResponse response = new CategoriesListResponse();
        categories.forEach(i -> {
            CategoryListResponse category = new CategoryListResponse()
                    .categoryName(i.getCategoryName())
                    .id(UUID.fromString(i.getUuid()));
            response.addCategoriesItem(category);
        });
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RequestMapping(path = "/category/{category_id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<CategoryDetailsResponse> getCategoryDetails(@PathVariable(name = "category_id") String categoryId) throws CategoryNotFoundException {
        CategoryEntity category = categoryService.getCategoryById(categoryId);
        CategoryDetailsResponse response = new CategoryDetailsResponse()
                .categoryName(category.getCategoryName())
                .id(UUID.fromString(category.getUuid()));

        List<ItemEntity> itemsForCategory = itemService.getItemsByCategory(category);

        itemsForCategory.forEach(i -> {
            ItemList item = new ItemList()
                    .itemType(ItemList.ItemTypeEnum.fromValue(i.getType().equals("0") ? "VEG" : "NON_VEG"))
                    .itemName(i.getItemName())
                    .price(i.getPrice())
                    .id(UUID.fromString(i.getUuid()));
            response.addItemListItem(item);
        });

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}

