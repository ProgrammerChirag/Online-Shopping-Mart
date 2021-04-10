package ModelClasses;

import java.io.Serializable;

import ModelClasses.ProductData;

public class CartData implements Serializable {

    ProductData productData;
    String Quantity;


    public CartData() { }

    public CartData(ProductData productData, String quantity) {
        this.productData = productData;
        Quantity = quantity;
    }

    public ProductData getProductData() {
        return productData;
    }

    public void setProductData(ProductData productData) {
        this.productData = productData;
    }

    public String getQuantity() {
        return Quantity;
    }

    public void setQuantity(String quantity) {
        Quantity = quantity;
    }
}
