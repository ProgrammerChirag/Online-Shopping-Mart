package ModelClasses;

import java.io.Serializable;

public class paymentDataClass implements Serializable {

          ModelClasses.PaymentData paymentData;
          String orderStatus ;
          ProductData productData;
        DeliveryAddress deliveryAddress;
        String quantityOrdered;
        String totalPrice;
        String deliveryCharge;
        String paymentMode;
        String userName;
        String personName;
        String Email;
        String deliveryDate;

    public paymentDataClass(PaymentData paymentData, String orderStatus, ProductData productData, DeliveryAddress deliveryAddress, String quantityOrdered, String totalPrice, String deliveryCharge, String paymentMode, String userName, String personName, String email, String deliveryDate) {
        this.paymentData = paymentData;
        this.orderStatus = orderStatus;
        this.productData = productData;
        this.deliveryAddress = deliveryAddress;
        this.quantityOrdered = quantityOrdered;
        this.totalPrice = totalPrice;
        this.deliveryCharge = deliveryCharge;
        this.paymentMode = paymentMode;
        this.userName = userName;
        this.personName = personName;
        Email = email;
        this.deliveryDate = deliveryDate;
    }

    public String getDeliveryDate() { return deliveryDate;    }

    public void setDeliveryDate(String deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public paymentDataClass()
    {}

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public PaymentData getPaymentData() {
        return paymentData;
    }

    public void setPaymentData(PaymentData paymentData) {
        this.paymentData = paymentData;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public ProductData getProductData() {
        return productData;
    }

    public void setProductData(ProductData productData) {
        this.productData = productData;
    }

    public DeliveryAddress getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(DeliveryAddress deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public String getQuantityOrdered() {
        return quantityOrdered;
    }

    public void setQuantityOrdered(String quantityOrdered) {
        this.quantityOrdered = quantityOrdered;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getDeliveryCharge() {
        return deliveryCharge;
    }

    public void setDeliveryCharge(String deliveryCharge) {
        this.deliveryCharge = deliveryCharge;
    }

    public String getPaymentMode() {
        return paymentMode;
    }

    public void setPaymentMode(String paymentMode) {
        this.paymentMode = paymentMode;
    }
}
