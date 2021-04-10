package MultiProductActivity;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.razorpay.Checkout;
import com.razorpay.Order;
import com.razorpay.PaymentData;
import com.razorpay.PaymentResultWithDataListener;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.selflearn.rpsstationary.R;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import MemoryManagement.SettingMemoryData;
import ModelClasses.CartData;
import ModelClasses.DeliveryAddress;
import ModelClasses.ImageData;
import ModelClasses.ProductData;
import ModelClasses.UserData;
import ModelClasses.paymentDataClass;
import Utils.CustomProgressDialog;

public class PaymentPage extends AppCompatActivity implements PaymentResultWithDataListener {

    Button pay;
    Button cashOnDelivery;
    List<CartData> cartDataList;
    ProductData productData;
    List<ImageData> imageData;
    List<List<String>> images;
    DeliveryAddress deliveryAddress;
    String personName;
    String email;
    private static final String TAG = "PaymentPage";
    EditText Name;
    EditText Email;
    Toolbar toolbar;
    String userName;
    double totalPrice;
    CustomProgressDialog customProgressDialog;
    Float charge;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment_page);

        cartDataList = (List<CartData>) getIntent().getSerializableExtra("CartData");
        imageData = (List<ImageData>) getIntent().getSerializableExtra("images");
        deliveryAddress = (DeliveryAddress) getIntent().getSerializableExtra("DeliveryAddress");
        charge = getIntent().getFloatExtra("charge" , 0);

        cashOnDelivery = findViewById(R.id.cod);

//        cashOnDelivery.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });

        toolbar = findViewById(R.id.toolbar8);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        pay = findViewById(R.id.pay_now);
        Name = findViewById(R.id.name);
        Email = findViewById(R.id.email);

        Checkout.preload(PaymentPage.this);

        if (cartDataList!= null && imageData!=null && deliveryAddress != null && cartDataList.size() == imageData.size())
        {
            // process is payable.
            pay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (cartDataList != null && imageData != null) {

                        if (validateForm()) {

                             customProgressDialog = new CustomProgressDialog(PaymentPage.this);
                            customProgressDialog.startLoadingDialog();

                            personName = Name.getText().toString();
                            email = Email.getText().toString();

                            // process to payment........


                            Log.d(TAG, "onClick: " +"executing task");
                            //new PaymentTask(PaymentPage.this, productData, images ,  personName , email , deliveryAddress).execute();
                            new MultiPaymentTask(email, personName, deliveryAddress, PaymentPage.this, imageData, cartDataList).execute();
                        }
                    }
                    else {
                        onBackPressed();
                    }
                }

                private boolean validateForm() {

                    boolean result;

                    if (Email.getText().toString().isEmpty())
                    {
                        Email.setError("please enter email address");
                        Email.requestFocus();
                        result = false;
                    }else if (Name.getText().toString().isEmpty())
                    {
                        result = false;
                        Name.setError("please enter valid name");
                        Name.requestFocus();
                    }else {
                        result = true;
                        Email.setError(null);
                        Name.setError(null);
                    }

                    return  result;
                }
            });

            cashOnDelivery.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (cartDataList != null && imageData != null){
                        if (validateForm())
                        {
                            customProgressDialog = new CustomProgressDialog(PaymentPage.this);
                            customProgressDialog.startLoadingDialog();

                            personName = Name.getText().toString();
                            email = Email.getText().toString();

                            // process to payment........
                            Log.d(TAG, "onClick: executing task");


                            Runnable runnable = new Runnable() {
                                @Override
                                public void run() {

                                    Looper.prepare();

                                    createOrder();

                                    Looper.loop();
                                }
                            };

                            Thread thread = new Thread(runnable);
                            thread.start();

                        }
                    }
                }

                private void createOrder() {


                    Float DeliveryCharge;
                    Log.d(TAG, "createOrder: called");

                    StringBuilder receipt_data= new StringBuilder();

                    receipt_data.append("Number of Items are :" + cartDataList.size());

                    Log.d(TAG, "createOrder: " + receipt_data.toString());

                    float totalAmount =0;
                    for (CartData cartData : cartDataList)
                    {
                        totalAmount = totalAmount + ( Float.parseFloat(cartData.getProductData().getSellingPrice()) * Float.parseFloat(cartData.getQuantity()) );
                    }
                    if (totalAmount == 0)
                    {
                        Log.d(TAG, "createOrder: "+"payment error ");
                        Toast.makeText(getApplicationContext() , "payment Error" , Toast.LENGTH_LONG).show();

                    }else{

                        Log.d(TAG, "createOrder: " + "creating Json Object");

                        if (totalAmount < 500) {
                            DeliveryCharge = 50.00f;
                            totalAmount = totalAmount + DeliveryCharge;
                            Log.d(TAG, "createOrder: "+totalAmount);
                        }

                        JSONObject orderRequest = new JSONObject();

                        try{

                            Log.d(TAG, "createOrder: " + "creating client");

                            orderRequest.put("amount", totalAmount* 100); // amount in the smallest currency unit
                            orderRequest.put("currency", "INR");
                            orderRequest.put("receipt", "Brand :" +receipt_data);
                            orderRequest.put("payment_capture", true);

                            RazorpayClient razor;

                            try {

                                Log.d(TAG, "createOrder: " + "creating order");

                                razor = new RazorpayClient("rzp_test_bOsLxT1U9qq0p9" , "XPPDg3t61qj5MaI8fsxn0bHQ");
                                final Order order = razor.Orders.create(orderRequest);

                                Log.d(TAG, "createOrder: "+order);

//                                doPayment(order);

                                final String orderID = order.get("id");


                                try {

                                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("UserData");
                                    databaseReference = databaseReference.child(new SettingMemoryData(PaymentPage.this).getSharedPrefString(String.valueOf(R.string.USERNAME_KEY)));
                                    databaseReference.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                                            if (snapshot.getValue(UserData.class) != null) {
                                                userName = Objects.requireNonNull(snapshot.getValue(UserData.class)).getName();
                                            }

                                            List<paymentDataClass> dataClassList = new ArrayList<>();

                                            for (CartData cartData : cartDataList) {

                                                final paymentDataClass paymentDataClass = new paymentDataClass();

                                                paymentDataClass.setDeliveryAddress(deliveryAddress);
                                                paymentDataClass.setEmail(email);
                                                paymentDataClass.setPaymentMode("online");
                                                paymentDataClass.setPersonName(personName);
                                                paymentDataClass.setProductData(cartData.getProductData());
                                                ModelClasses.PaymentData data = new ModelClasses.PaymentData(orderID, "no_signature", "no_ID");
                                                paymentDataClass.setPaymentData(data);
                                                Log.d(TAG, "onDataChange: " + userName);
                                                paymentDataClass.setUserName(userName);
                                                totalPrice = Double.parseDouble(cartData.getProductData().getSellingPrice()) * Double.parseDouble(cartData.getQuantity());
                                                paymentDataClass.setTotalPrice(String.valueOf(totalPrice));
                                                paymentDataClass.setQuantityOrdered(cartData.getQuantity());
                                                paymentDataClass.setOrderStatus("Delivery soon...");
                                                paymentDataClass.setDeliveryDate("in next 7 working days..");
                                                paymentDataClass.setDeliveryCharge(String.valueOf(charge));


                                                dataClassList.add(paymentDataClass);

                                                CustomProgressDialog customProgressDialog = new CustomProgressDialog(PaymentPage.this);
//                    customProgressDialog.startLoadingDialog();

                                                new UploadData(dataClassList, PaymentPage.this , customProgressDialog).execute();
                                            }

//                    Intent intent = new Intent(PaymentPage.this, PaymentSuccess.class);
//                    intent.putExtra("data", new ArrayList<>(dataClassList));
//                    startActivity(intent);

                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            Toast.makeText(getApplicationContext() , "error code :" +error.getCode() , Toast.LENGTH_LONG).show();
                                        }
                                    });

                                }catch (Exception e)
                                {
                                    Log.d(TAG, "onPaymentSuccess: "+e.getClass().getName() + " " + e.getMessage());
                                }


                            } catch (RazorpayException e) {
                                e.printStackTrace();
                                Log.d(TAG, "createOrder: "+e.getMessage() + " " + e.getClass().getName() );
                            }

                        }catch (Exception e) {
                            Log.d(TAG, "createOrder: " + e.getClass().getName() + " " +e.getMessage());
                            Toast.makeText(getApplicationContext() , "something went wrong unable to process payment" , Toast.LENGTH_LONG).show();
                            onBackPressed();
                        }
                    }
                }

                private boolean validateForm() {

                    boolean result;

                    if (Email.getText().toString().isEmpty())
                    {
                        Email.setError("please enter email address");
                        Email.requestFocus();
                        result = false;
                    }else if (Name.getText().toString().isEmpty())
                    {
                        result = false;
                        Name.setError("please enter valid name");
                        Name.requestFocus();
                    }else {
                        result = true;
                        Email.setError(null);
                        Name.setError(null);
                    }

                    return  result;
                }
            });

        }else {
            // process is not payable.
            Log.d(TAG, "onCreate: "+"can't pay ");


        }

    }

    @Override
    public void onPaymentSuccess(String s, final PaymentData paymentData) {
        Log.d(TAG, "onPaymentSuccess: " + "payment got success.");

        try {

            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("UserData");
            databaseReference = databaseReference.child(new SettingMemoryData(PaymentPage.this).getSharedPrefString(String.valueOf(R.string.USERNAME_KEY)));
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    if (snapshot.getValue(UserData.class) != null) {
                        userName = Objects.requireNonNull(snapshot.getValue(UserData.class)).getName();
                    }

                    List<paymentDataClass> dataClassList = new ArrayList<>();

                    for (CartData cartData : cartDataList) {

                        final paymentDataClass paymentDataClass = new paymentDataClass();


                        paymentDataClass.setDeliveryAddress(deliveryAddress);
                        paymentDataClass.setEmail(email);
                        paymentDataClass.setPaymentMode("online");
                        paymentDataClass.setPersonName(personName);
                        paymentDataClass.setProductData(cartData.getProductData());
                        ModelClasses.PaymentData data = new ModelClasses.PaymentData(paymentData.getOrderId(), paymentData.getSignature(), paymentData.getPaymentId());
                        paymentDataClass.setPaymentData(data);
                        Log.d(TAG, "onDataChange: " + userName);
                        paymentDataClass.setUserName(userName);
                        totalPrice = Double.parseDouble(cartData.getProductData().getSellingPrice()) * Double.parseDouble(cartData.getQuantity());
                        paymentDataClass.setTotalPrice(String.valueOf(totalPrice));
                        paymentDataClass.setQuantityOrdered(cartData.getQuantity());
                        paymentDataClass.setOrderStatus("Delivery soon...");
                        paymentDataClass.setDeliveryDate("in next 7 working days..");
                        paymentDataClass.setDeliveryCharge(String.valueOf(charge));


                        dataClassList.add(paymentDataClass);

                        CustomProgressDialog customProgressDialog = new CustomProgressDialog(PaymentPage.this);
//                    customProgressDialog.startLoadingDialog();

                        new UploadData(dataClassList, PaymentPage.this , customProgressDialog).execute();
                    }

//                    Intent intent = new Intent(PaymentPage.this, PaymentSuccess.class);
//                    intent.putExtra("data", new ArrayList<>(dataClassList));
//                    startActivity(intent);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getApplicationContext() , "error code :" +error.getCode() , Toast.LENGTH_LONG).show();
                }
            });

        }catch (Exception e)
        {
            Log.d(TAG, "onPaymentSuccess: "+e.getClass().getName() + " " + e.getMessage());
        }
    }

    @Override
    public void onPaymentError(int i, String s, PaymentData paymentData) {
        Log.d(TAG, "onPaymentError: " + "payment failed...");
    }

    @Override
    protected void onDestroy() {

        customProgressDialog.dismissDialog();

        super.onDestroy();



    }
}

class MultiPaymentTask extends AsyncTask<Context , Integer , Long>
{

    String name , email;
    DeliveryAddress deliveryAddress;
    Activity activity;
    List<ImageData> imageDataList;
    List<CartData> cartDataList;
    private static final String TAG = "MultiPaymentTask";

    public MultiPaymentTask(String name, String email, DeliveryAddress deliveryAddress, Activity activity, List<ImageData> imageDataList, List<CartData> cartDataList) {
        this.name = name;
        this.email = email;
        this.deliveryAddress = deliveryAddress;
        this.activity = activity;
        this.imageDataList = imageDataList;
        this.cartDataList = cartDataList;
    }

    public MultiPaymentTask(){}

    @Override
    protected Long doInBackground(Context... contexts) {

        // payment process.
        initiatePayment();

        return null;
    }

    private void initiatePayment() {
        //Checkout.preload(context.getApplicationContext());

        Log.d(TAG, "initiatePayment: called");
        createOrder();
    }

    private void createOrder() {
        Float DeliveryCharge;
        Log.d(TAG, "createOrder: called");

        StringBuilder receipt_data= new StringBuilder();

        receipt_data.append("Number of Items are :" + cartDataList.size());

        Log.d(TAG, "createOrder: " + receipt_data.toString());

        float totalAmount =0;
        for (CartData cartData : cartDataList)
        {
            totalAmount = totalAmount + ( Float.parseFloat(cartData.getProductData().getSellingPrice()) * Float.parseFloat(cartData.getQuantity()) );
        }
        if (totalAmount == 0)
        {
            Log.d(TAG, "createOrder: "+"payment error ");
            Toast.makeText(activity.getApplicationContext() , "payment Error" , Toast.LENGTH_LONG).show();

        }else{

            Log.d(TAG, "createOrder: " + "creating Json Object");

            if (totalAmount < 500) {
                DeliveryCharge = 50.00f;
                totalAmount = totalAmount + DeliveryCharge;
            }

            JSONObject orderRequest = new JSONObject();

                try{

                    Log.d(TAG, "createOrder: " + "creating client");

                    orderRequest.put("amount", totalAmount* 100); // amount in the smallest currency unit
                    orderRequest.put("currency", "INR");
                    orderRequest.put("receipt", "Brand :" +receipt_data);
                    orderRequest.put("payment_capture", true);

                    RazorpayClient razor;

                    try {

                        Log.d(TAG, "createOrder: " + "creating order");

                        razor = new RazorpayClient("rzp_test_bOsLxT1U9qq0p9" , "XPPDg3t61qj5MaI8fsxn0bHQ");
                        Order order = razor.Orders.create(orderRequest);

                        Log.d(TAG, "createOrder: "+order);

                        doPayment(order);

                    } catch (RazorpayException e) {
                        e.printStackTrace();
                        Log.d(TAG, "createOrder: "+e.getMessage() + " " + e.getClass().getName() );
                    }

                }catch (Exception e) {
                    Log.d(TAG, "createOrder: " + e.getClass().getName() + " " +e.getMessage());
                    Toast.makeText(activity.getApplicationContext() , "something went wrong unable to process payment" , Toast.LENGTH_LONG).show();
                    activity.onBackPressed();
                }
        }
    }

    private void doPayment(Order order) {


        Log.d(TAG, "doPayment: called");

        final Checkout checkout = new Checkout();
        checkout.setKeyID("rzp_test_bOsLxT1U9qq0p9");

        checkout.setImage(R.drawable.logo);

        try {

            JSONObject options = new JSONObject();

            options.put("name", "RPS Stationery");
            options.put("description", cartDataList.size() + " cart product payment" );
            options.put("image", R.drawable.logo);
            Log.d(TAG, "doPayment: "+order.get("id"));
            options.put("order_id", order.get("id"));//from response of step 3.
            options.put("theme.color", "#3399cc");
            options.put("currency", "INR");
            options.put("amount", order.get("amount"));//pass amount in currency subunits
            options.put("prefill.email", email);
            options.put("prefill.contact",new SettingMemoryData(activity).getSharedPrefString(String.valueOf(R.string.PHONE_KEY)));

            checkout.open(activity, options);

        } catch(Exception e) {

            Log.e(TAG, "Error in starting Razor pay Checkout "+ e.getMessage() + " "  + e.getClass().getName());
        }

    }

    @Override
    protected void onPreExecute() {

        Checkout.preload(activity);

        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Long aLong) {
        super.onPostExecute(aLong);

        Log.d(TAG, "onPostExecute: "+"process completed.");
    }

    @Override
    protected void onCancelled(Long aLong) {
        super.onCancelled(aLong);
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }

}
