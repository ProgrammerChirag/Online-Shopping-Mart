package ModelClasses;

public class FeedbackData {
    String feedback;
    float rating;
    String orderID;
    ProductData productData;

    public FeedbackData(){}

    public FeedbackData(String feedback, float rating, String orderID, ProductData productData) {
        this.feedback = feedback;
        this.rating = rating;
        this.orderID = orderID;
        this.productData = productData;
    }

    public ProductData getProductData() {
        return productData;
    }

    public void setProductData(ProductData productData) {
        this.productData = productData;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }
}
