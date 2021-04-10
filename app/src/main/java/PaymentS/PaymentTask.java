package PaymentS;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.razorpay.Checkout;
import com.razorpay.Order;
import com.razorpay.PaymentData;
import com.razorpay.PaymentResultWithDataListener;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.selflearn.rpsstationary.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import ModelClasses.DeliveryAddress;
import ModelClasses.ProductData;


public class PaymentTask extends AsyncTask<Context, Integer , Long> implements PaymentResultWithDataListener {

    private static final String TAG = "PaymentTask";
    @SuppressLint("StaticFieldLeak")
    Activity activity;
    ProductData productData;
    List<String> images;
    String name , email;
    DeliveryAddress deliveryAddress;
    String userName ;
    double totalPrice;


    public PaymentTask(Activity activity, ProductData productData, List<String> images, String name, String email, DeliveryAddress deliveryAddress) {
        this.activity = activity;
        this.productData = productData;
        this.images = images;
        this.name = name;
        this.email = email;
        this.deliveryAddress = deliveryAddress;
    }

    //
//
//    @Override
//    protected Long doInBackground(URL... urls) {
//
//
//
//        return null;
//    }

    void initiatePayment()
    {
        //Checkout.preload(context.getApplicationContext());

        Log.d(TAG, "initiatePayment: called");
        createOrder();

    }

    private void createOrder() {


        Log.d(TAG, "createOrder: called");
        try {

            JSONObject orderRequest = new JSONObject();

            double Amount = Double.parseDouble(productData.getSellingPrice()) * Integer.parseInt(productData.getOrderQuantity()) * 100 ;

            if (Amount < 50000){
                Amount = Amount + 5000;
            }

            orderRequest.put("amount", Amount); // amount in the smallest currency unit
            orderRequest.put("currency", "INR");
            orderRequest.put("receipt", "Brand :" + productData.getProductName());
            orderRequest.put("payment_capture", true);

            RazorpayClient razor;
            try {
                razor = new RazorpayClient("rzp_test_bOsLxT1U9qq0p9" , "XPPDg3t61qj5MaI8fsxn0bHQ");
                Order order = razor.Orders.create(orderRequest);

                Log.d(TAG, "createOrder: "+ order );

                doPayment(order);

            } catch (RazorpayException e) {
                e.printStackTrace();
                Log.d(TAG, "createOrder: "+e.getMessage() + " " + e.getClass().getName() );
            }

        } catch (JSONException e) {
            // Handle Exception
            Log.d(TAG, "createOrder: " + e.getMessage() + " " + e.getClass().getName());
        }
    }

    private void doPayment(Order order) {

        final Checkout checkout = new Checkout();


        checkout.setKeyID("rzp_test_bOsLxT1U9qq0p9");

//        Glide.with(activity)
//                .asBitmap()
//                .load(images.get(0))
//                .into(new CustomTarget<Bitmap>() {
//                    @Override
//                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
//
//
//
//                    }
//
//                    @Override
//                    public void onLoadCleared(@Nullable Drawable placeholder) {
//
//                    }
//                });

        checkout.setImage(R.drawable.logo);

        //final Activity activity = this;

        try {
            JSONObject options = new JSONObject();

            options.put("name", "RPS Stationery");
            options.put("description", productData.getProductName() );
            options.put("image", R.drawable.logo);
            Log.d(TAG, "doPayment: "+order.get("id"));
            options.put("order_id", order.get("id"));//from response of step 3.
            options.put("theme.color", "#3399cc");
            options.put("currency", "INR");
            options.put("amount", order.get("amount"));//pass amount in currency subunits
            options.put("prefill.email", "junejachirag020@gmail.com");
            options.put("prefill.contact","9982917736");

            checkout.open(activity, options);

        } catch(Exception e) {
            Log.e(TAG, "Error in starting Razorpay Checkout", e);
        }

    }

    @Override
    public void onPaymentSuccess(String s, PaymentData paymentData) {


    }

    @Override
    public void onPaymentError(int i, String s, PaymentData paymentData) {

        Log.d(TAG, "onPaymentError: "+i);
        Toast.makeText(activity , "payment error :" +i , Toast.LENGTH_LONG ).show();
    }


    @Override
    protected Long doInBackground(Context... contexts) {

        initiatePayment();

        return null;
    }
}
