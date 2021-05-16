package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.CategoryDao;
import com.upgrad.FoodOrderingApp.service.dao.ItemDao;
import com.upgrad.FoodOrderingApp.service.dao.OrderDao;
import com.upgrad.FoodOrderingApp.service.dao.RestaurantDao;
import com.upgrad.FoodOrderingApp.service.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ItemService {

    @Autowired
    private CategoryDao categoryDao;

    @Autowired
    private RestaurantDao restaurantDao;

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private ItemDao itemDao;

    public List<ItemEntity> getItemsByCategoryAndRestaurant(String restaurantId, String categoryId) {

        RestaurantEntity restaurant = restaurantDao.getRestaurantByUUid(restaurantId);

        List<RestaurantItemEntity> restaurantItems = restaurantDao.getItemsByRestaurant(restaurant);
        List<ItemEntity> restaurantItemEntities = new ArrayList<>();
        restaurantItems.forEach(i -> restaurantItemEntities.add(i.getItem()));

        List<RestaurantCategoryEntity> categories = restaurantDao.getCategoriesForRestaurant(restaurant);

        CategoryEntity categoryEntity = null;
        for (RestaurantCategoryEntity category : categories) {
            if (category.getCategory().getUuid().equals(categoryId)) {
                categoryEntity = category.getCategory();
                break;
            }
        }

        List<ItemEntity> items = new ArrayList<>();

        List<CategoryItemEntity> categoryItems = categoryDao.getItemsForCategory(categoryEntity);
        List<ItemEntity> categoryItemEntities = new ArrayList<>();
        categoryItems.forEach(i -> categoryItemEntities.add(i.getItem()));

        categoryItemEntities.forEach(i -> {
            if (restaurantItemEntities.contains(i)) {
                items.add(i);
            }
        });

        return items;
    }

    public List<ItemEntity> getItemsByPopularity(RestaurantEntity restaurantEntity) {
        List<ItemEntity> itemsOrdered = new ArrayList<>();

        List<OrderEntity> ordersForRestaurant = orderDao.ordersByRestaurant(restaurantEntity);
        ordersForRestaurant.forEach(i -> {
            List<OrderItemEntity> orderedItems = orderDao.itemsByOrder(i);
            orderedItems.forEach(j -> itemsOrdered.add(j.getItem()));
        });

        Map<String, Integer> itemsOrderedMap = new HashMap<>();
        itemsOrdered.forEach(i -> {
            Integer count = itemsOrderedMap.get(i.getUuid());
            itemsOrderedMap.put(i.getUuid(), (count == null) ? 1 : count + 1);
        });

        Map<String, Integer> sortedItemsOrdered = sortByValue(itemsOrderedMap);
        List<ItemEntity> top5Items = new ArrayList<>();

        for (Map.Entry<String, Integer> entry : sortedItemsOrdered.entrySet()) {
            ItemEntity item = itemDao.getItemByUUID(entry.getKey());
            top5Items.add(item);
            if (top5Items.size() == 5) {
                break;
            }
        }

        return top5Items;
    }

    private Map<String, Integer> sortByValue(Map<String, Integer> hm) {
        // Create a list from elements of HashMap
        List<Map.Entry<String, Integer>> list =
                new LinkedList<>(hm.entrySet());

        // Sort the list in descending order
        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
            public int compare(Map.Entry<String, Integer> o1,
                               Map.Entry<String, Integer> o2) {
                return (o2.getValue()).compareTo(o1.getValue());
            }
        });

        // put data from sorted list to hashmap
        HashMap<String, Integer> temp = new LinkedHashMap<String, Integer>();
        for (Map.Entry<String, Integer> aa : list) {
            temp.put(aa.getKey(), aa.getValue());
        }
        return temp;
    }

    public List<ItemEntity> getItemsByCategory(CategoryEntity categoryEntity) {
        List<CategoryItemEntity> categoryItems = categoryDao.getItemsForCategory(categoryEntity);
        List<ItemEntity> categoryItemEntities = new ArrayList<>();
        categoryItems.forEach(i -> categoryItemEntities.add(i.getItem()));
        return categoryItemEntities;
    }
}
