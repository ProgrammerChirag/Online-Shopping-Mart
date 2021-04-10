package ModelClasses;

import java.io.Serializable;

public class ProductData implements Serializable {

    // we will make it later

    String ProductName;
    String category;
    String type;
    String Size;
    String color;
    String QuantityInOnePack;
    String Brand;
    String MRP;
    String SellingPrice;
    String discount;
    String OrderQuantity;

    public ProductData(){}

    public ProductData(String productName, String category, String type, String size
            , String color, String quantityInOnePack, String brand, String MRP, String sellingPrice, String discount, String orderQuantity) {
        ProductName = productName;
        this.category = category;
        this.type = type;
        Size = size;
        this.color = color;
        QuantityInOnePack = quantityInOnePack;
        Brand = brand;
        this.MRP = MRP;
        SellingPrice = sellingPrice;
        this.discount = discount;
        OrderQuantity = orderQuantity;
    }

    public String getProductName() {
        return ProductName;
    }

    public void setProductName(String productName) {
        ProductName = productName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSize() {
        return Size;
    }

    public void setSize(String size) {
        Size = size;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getQuantityInOnePack() {
        return QuantityInOnePack;
    }

    public void setQuantityInOnePack(String quantityInOnePack) {
        QuantityInOnePack = quantityInOnePack;
    }

    public String getBrand() {
        return Brand;
    }

    public void setBrand(String brand) {
        Brand = brand;
    }

    public String getMRP() {
        return MRP;
    }

    public void setMRP(String MRP) {
        this.MRP = MRP;
    }

    public String getSellingPrice() {
        return SellingPrice;
    }

    public void setSellingPrice(String sellingPrice) {
        SellingPrice = sellingPrice;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getOrderQuantity() {
        return OrderQuantity;
    }

    public void setOrderQuantity(String orderQuantity) {
        OrderQuantity = orderQuantity;
    }
}
