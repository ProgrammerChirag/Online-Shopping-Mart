package ModelClasses;

import java.io.Serializable;
import java.util.List;

public class OrderData implements Serializable {

    String OrderID;
    String ReferenceID;
    String AmountPaid;
    String Discount;
    String Address;
    ProductData productData;
    String ShoppingDate;
    String deliveryCharge;
    String deliveryPersonName;
    String QuantityOrdered;
    String status;
    boolean isRefunded;

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public OrderData(String orderID, String referenceID, String amountPaid, String discount, String address, ProductData productData, String shoppingDate, String deliveryCharge, String deliveryPersonName, String quantityOrdered, String status, boolean isRefunded, List<String> images) {
        OrderID = orderID;
        ReferenceID = referenceID;
        AmountPaid = amountPaid;
        Discount = discount;
        Address = address;
        this.productData = productData;
        ShoppingDate = shoppingDate;
        this.deliveryCharge = deliveryCharge;
        this.deliveryPersonName = deliveryPersonName;
        QuantityOrdered = quantityOrdered;
        this.status = status;
        this.isRefunded = isRefunded;
        this.images = images;
    }

    List<String> images;

    public OrderData(){}


    public String getOrderID() {
        return OrderID;
    }

    public void setOrderID(String orderID) {
        OrderID = orderID;
    }

    public String getReferenceID() {
        return ReferenceID;
    }

    public void setReferenceID(String referenceID) {
        ReferenceID = referenceID;
    }

    public String getAmountPaid() {
        return AmountPaid;
    }

    public void setAmountPaid(String amountPaid) {
        AmountPaid = amountPaid;
    }

    public String getDiscount() {
        return Discount;
    }

    public void setDiscount(String discount) {
        Discount = discount;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public ProductData getProductData() {
        return productData;
    }

    public void setProductData(ProductData productData) {
        this.productData = productData;
    }

    public String getShoppingDate() {
        return ShoppingDate;
    }

    public void setShoppingDate(String shoppingDate) {
        ShoppingDate = shoppingDate;
    }

    public String getDeliveryCharge() {
        return deliveryCharge;
    }

    public void setDeliveryCharge(String deliveryCharge) {
        this.deliveryCharge = deliveryCharge;
    }

    public String getDeliveryPersonName() {
        return deliveryPersonName;
    }

    public void setDeliveryPersonName(String deliveryPersonName) {
        this.deliveryPersonName = deliveryPersonName;
    }

    public String getQuantityOrdered() {
        return QuantityOrdered;
    }

    public void setQuantityOrdered(String quantityOrdered) {
        QuantityOrdered = quantityOrdered;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isRefunded() {
        return isRefunded;
    }

    public void setRefunded(boolean refunded) {
        isRefunded = refunded;
    }
}
