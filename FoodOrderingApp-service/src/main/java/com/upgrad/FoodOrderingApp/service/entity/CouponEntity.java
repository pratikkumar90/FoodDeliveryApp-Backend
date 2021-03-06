package com.upgrad.FoodOrderingApp.service.entity;

import javax.persistence.*;

@Entity
@Table(name = "coupon")
@NamedQueries({
        @NamedQuery(name = "getCouponByCouponName", query = "select c from CouponEntity c where c.couponName = :couponName"),
        @NamedQuery(name = "getCouponByCouponUUID", query = "select c from CouponEntity c where c.uuid = :uuid")
})
public class CouponEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    //@Column annotation specifies that the attribute will be mapped to the column in the database.
    //Here the column name is explicitly mentioned as 'id'
    @Column(name = "id")
    private Integer id;

    @Column(name = "uuid")
    private String uuid;

    @Column(name = "coupon_name")
    private String couponName;

    @Column(name = "percent")
    private Integer percent;

    public CouponEntity() {

    }

    public CouponEntity(String uuid, String couponName, Integer percent) {
        this.uuid = uuid;
        this.couponName = couponName;
        this.percent = percent;
    }

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

    public String getCouponName() {
        return couponName;
    }

    public void setCouponName(String couponName) {
        this.couponName = couponName;
    }

    public Integer getPercent() {
        return percent;
    }

    public void setPercent(Integer percent) {
        this.percent = percent;
    }
}
