package com.midtrans.sdk.corekit.core.api.merchant.model.checkout.request.optional.customer;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class CustomerDetails implements Serializable {

    @SerializedName("first_name")
    private String firstName;

    @SerializedName("last_name")
    private String lastName;
    private String email;
    private String phone;

    @SerializedName("shipping_address")
    private ShippingAddress shippingAddress;
    @SerializedName("billing_address")
    private BillingAddress billingAddress;

    public CustomerDetails(String firstName,
                           String lastName,
                           String email,
                           String phone,
                           ShippingAddress shippingAddress,
                           BillingAddress billingAddress) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.shippingAddress = shippingAddress;
        this.billingAddress = billingAddress;
    }

    public CustomerDetails(String firstName,
                           String lastName,
                           String email,
                           String phone) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public ShippingAddress getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(ShippingAddress shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public BillingAddress getBillingAddress() {
        return billingAddress;
    }

    public void setBillingAddress(BillingAddress billingAddress) {
        this.billingAddress = billingAddress;
    }
}