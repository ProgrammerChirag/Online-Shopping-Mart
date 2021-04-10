package ModelClasses;

import java.io.Serializable;

public class PaymentData implements Serializable {
    String orderID;
    String signature;
    String paymentID;

    public PaymentData(){}

    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getPaymentID() {
        return paymentID;
    }

    public void setPaymentID(String paymentID) {
        this.paymentID = paymentID;
    }

    public PaymentData(String orderID, String signature, String paymentID) {
        this.orderID = orderID;
        this.signature = signature;
        this.paymentID = paymentID;
    }
}
