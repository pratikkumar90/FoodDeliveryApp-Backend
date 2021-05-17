package com.upgrad.FoodOrderingApp.service.entity;

import javax.persistence.*;

@Entity
@Table(name = "payment")
@NamedQueries({
        @NamedQuery(name = "getPaymentByUUID", query = "select p from PaymentEntity p where p.uuid = :uuid"),
        @NamedQuery(name = "getAllPaymentMethods", query = "select p from PaymentEntity p")
})
public class PaymentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    //@Column annotation specifies that the attribute will be mapped to the column in the database.
    //Here the column name is explicitly mentioned as 'id'
    @Column(name = "id")
    private Integer id;

    @Column(name = "uuid")
    private String uuid;

    @Column(name = "payment_name")
    private String paymentName;

    public PaymentEntity() {

    }

    public PaymentEntity(String uuid, String name) {
        this.uuid = uuid;
        this.paymentName = name;
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

    public String getPaymentName() {
        return paymentName;
    }

    public void setPaymentName(String paymentName) {
        this.paymentName = paymentName;
    }
}
