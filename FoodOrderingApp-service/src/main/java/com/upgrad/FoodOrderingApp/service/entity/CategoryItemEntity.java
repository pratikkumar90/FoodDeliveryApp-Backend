package com.upgrad.FoodOrderingApp.service.entity;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

@Entity
@Table(name = "category_item")
@NamedQueries({
        @NamedQuery(name = "getItemsForCategory", query = "select i from CategoryItemEntity i where i.category=:category")
})
public class CategoryItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    //@Column annotation specifies that the attribute will be mapped to the column in the database.
    //Here the column name is explicitly mentioned as 'id'
    @Column(name = "id")
    private Integer id;

    @JoinColumn(name = "item_id")
    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    private ItemEntity item;

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

    public ItemEntity getItem() {
        return item;
    }

    public void setItem(ItemEntity item) {
        this.item = item;
    }

    public CategoryEntity getCategory() {
        return category;
    }

    public void setCategory(CategoryEntity category) {
        this.category = category;
    }
}
