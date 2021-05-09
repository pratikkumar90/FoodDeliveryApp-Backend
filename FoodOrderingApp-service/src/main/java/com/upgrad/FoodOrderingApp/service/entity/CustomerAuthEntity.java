package com.upgrad.FoodOrderingApp.service.entity;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Entity
@Table(name="customer_auth")
@NamedQueries(
        {
                @NamedQuery(name = "customerAuthByToken", query = "select c from CustomerAuthEntity c where c.accessToken = :accessToken")
        }
)
public class CustomerAuthEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    //@Column annotation specifies that the attribute will be mapped to the column in the database.
    //Here the column name is explicitly mentioned as 'id'
    @Column(name = "id")
    private Integer id;

    @Column(name = "uuid")
    private String uuid;

    @JoinColumn(name = "customer_id")
    @ManyToOne
    private CustomerEntity customer;

    @Column(name = "access_token")
    private String accessToken;

    @Column(name = "expires_at")
    private ZonedDateTime expiresAt;

    @Column(name = "logout_at")
    private ZonedDateTime logoutAt;

    @Column(name = "login_at")
    private ZonedDateTime loginAt;

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

    public CustomerEntity getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerEntity customer) {
        this.customer = customer;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public ZonedDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(ZonedDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public ZonedDateTime getLogoutAt() {
        return logoutAt;
    }

    public void setLogoutAt(ZonedDateTime logoutAt) {
        this.logoutAt = logoutAt;
    }

    public ZonedDateTime getLoginAt() {
        return loginAt;
    }

    public void setLoginAt(ZonedDateTime loginAt) {
        this.loginAt = loginAt;
    }
}
