package PaymentS;

import android.annotation.SuppressLint;
import android.content.Intent;
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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Objects;

import DashBoard.DashBoardMainUI;
import MemoryManagement.SettingMemoryData;
import ModelClasses.DeliveryAddress;
import ModelClasses.ProductData;
import ModelClasses.UserData;
import ModelClasses.paymentDataClass;


public class PaymentPage extends AppCompatActivity implements PaymentResultWithDataListener {

    Button pay;
    Button cashOnDelivery;
    ProductData productData;
    List<String> images;
    DeliveryAddress deliveryAddress;
    String personName;
    String email;
    private static final String TAG = "PaymentPage";
    EditText Name;
    EditText Email;
    String userName;
    double totalPrice;
    Toolbar toolbar;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment_page);


        productData = (ProductData) getIntent().getSerializableExtra("ProductData");
        images = getIntent().getStringArrayListExtra("images");
        deliveryAddress = (DeliveryAddress) getIntent().getSerializableExtra("DeliveryAddress");

        pay = findViewById(R.id.pay_now);
        Name = findViewById(R.id.name);
        Email = findViewById(R.id.email);

        cashOnDelivery = findViewById(R.id.cod);

        toolbar = findViewById(R.id.toolbar8);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (productData != null && images != null) {

                    if (validateForm()) {

                        personName = Name.getText().toString();
                        email = Email.getText().toString();

                        // process to payment........
                        Checkout.preload(PaymentPage.this);
                        new PaymentTask(PaymentPage.this, productData, images, personName, email, deliveryAddress).execute();

                    }
                } else {
                    onBackPressed();
                }
            }
        });

        cashOnDelivery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (productData != null && images != null) {

                    if (validateForm()) {

                        personName = Name.getText().toString();
                        email = Email.getText().toString();

                        createOrder();

                    }
                } else {
                    onBackPressed();
                }

            }
        });

    }

    private void createOrder() {


        Runnable runnable = new Runnable() {
            @Override
            public void run() {

                Looper.prepare();

        Log.d(TAG, "createOrder: called");
        try {

            JSONObject orderRequest = new JSONObject();

            int Amount = Integer.parseInt(productData.getSellingPrice()) * Integer.parseInt(productData.getOrderQuantity()) * 100 ;

            if (Amount < 500){
                Amount = Amount + 50;
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

//                doPayment(order);

                ModelClasses.PaymentData paymentData = new ModelClasses.PaymentData(order.get("id" ).toString(), "no_signature" , "no_payment");


                Toast.makeText(PaymentPage.this, "payment success", Toast.LENGTH_LONG).show();

                final paymentDataClass paymentDataClass = new paymentDataClass();

                float delCharge = 0.0f;
                if (Float.parseFloat(productData.getSellingPrice()) < 500)
                {
                    delCharge = 50.00f;
                }

                paymentDataClass.setDeliveryAddress(deliveryAddress);
                paymentDataClass.setEmail(email);
                paymentDataClass.setPaymentMode("COD");
                paymentDataClass.setPersonName(personName);
                paymentDataClass.setProductData(productData);
                paymentDataClass.setPaymentData( paymentData);
                paymentDataClass.setDeliveryCharge(String.valueOf(delCharge));
                paymentDataClass.setPersonName(personName);
                paymentDataClass.setEmail(Email.getText().toString().trim());
                paymentDataClass.setDeliveryDate("In next few working days");
                paymentDataClass.setOrderStatus("Delivery Soon");


                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("paymentAndOrder");
                databaseReference = databaseReference.child(new SettingMemoryData(PaymentPage.this).getSharedPrefString(String.valueOf(R.string.USERNAME_KEY)));
                String key = databaseReference.push().getKey();
                if (key != null) {
                    databaseReference = databaseReference.child(key);
                    databaseReference.setValue(paymentDataClass).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            Intent intent = new Intent(PaymentPage.this, PaymentSuccess.class);
                            intent.putExtra("data", paymentDataClass);
                            startActivity(intent);

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext() , "currently we are not able to complete your transaction \n" +
                                    "your money will be refund in 24 hours" , Toast.LENGTH_LONG).show();

                            DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("Issue");
                            databaseReference1 = databaseReference1.child("paymentIssue").child(new SettingMemoryData(PaymentPage.this).getSharedPrefString(String.valueOf(R.string.USERNAME_KEY)));
                            String key = databaseReference1.push().getKey();
                            if (key != null) {
                                databaseReference1 = databaseReference1.child(key).child("refund");
                                databaseReference1.setValue(paymentDataClass.getPaymentData()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Intent intent = new Intent(PaymentPage.this , DashBoardMainUI.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        Log.d(TAG, "onSuccess: changing activity");
                                        finish();
                                        startActivity(intent);
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d(TAG, "onFailure: " + e.getMessage() + " " +e.getClass().getName());
                                    }
                                });
                            }

                        }
                    });
                }


            } catch (RazorpayException e) {
                e.printStackTrace();
                Log.d(TAG, "createOrder: "+e.getMessage() + " " + e.getClass().getName() );
            }

        } catch (JSONException e) {
            // Handle Exception
            Log.d(TAG, "createOrder: " + e.getMessage() + " " + e.getClass().getName());
        }

        Looper.loop();

            }
        };

        Thread thread = new Thread(runnable);
        thread.start();

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

    @Override
    public void onPaymentSuccess(String s, PaymentData paymentData) {
doPayment(paymentData , s);

    }

    @Override
    public void onPaymentError(int i, String s, PaymentData paymentData) {

        Toast.makeText(getApplicationContext() , "s" + i , Toast.LENGTH_LONG).show();
        Intent intent = new Intent(PaymentPage.this , DashBoardMainUI.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

    }

    public void doPayment(PaymentData paymentData , String s)
    {
        try {
            Log.d(TAG, "onPaymentSuccess: success" + paymentData.getData().toString() + s);
            Toast.makeText(PaymentPage.this, "payment success", Toast.LENGTH_LONG).show();

            final paymentDataClass paymentDataClass = new paymentDataClass();

            paymentDataClass.setDeliveryAddress(deliveryAddress);
            paymentDataClass.setEmail(email);
            paymentDataClass.setPaymentMode("online");
            paymentDataClass.setPersonName(personName);
            paymentDataClass.setProductData(productData);
            ModelClasses.PaymentData data = new ModelClasses.PaymentData(paymentData.getOrderId() ,paymentData.getSignature() ,  paymentData.getPaymentId());
            paymentDataClass.setPaymentData(data);


            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("UserData");
            databaseReference = databaseReference.child(new SettingMemoryData(PaymentPage.this).getSharedPrefString(String.valueOf(R.string.USERNAME_KEY)));
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    if (snapshot.getValue(UserData.class) != null) {
                        userName = Objects.requireNonNull(snapshot.getValue(UserData.class)).getName();
                    }

                    Log.d(TAG, "onDataChange: " + userName);
                    paymentDataClass.setUserName(userName);
                    totalPrice = Double.parseDouble(productData.getSellingPrice()) * Double.parseDouble(productData.getOrderQuantity());
                    paymentDataClass.setTotalPrice(String.valueOf(totalPrice));
                    paymentDataClass.setQuantityOrdered(productData.getOrderQuantity());
                    paymentDataClass.setOrderStatus("Delivery soon...");
                    paymentDataClass.setDeliveryDate("in next few working days");

                    double charge;

                    if (Double.parseDouble(productData.getSellingPrice()) < 500) {
                        charge = 50;
                    } else charge = 0;

                    paymentDataClass.setDeliveryCharge(String.valueOf(charge));

                    Log.d(TAG, "onPaymentSuccess: " + paymentDataClass.getPaymentData().getOrderID());

                    // upload payment Data.

                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("paymentAndOrder");
                    databaseReference = databaseReference.child(new SettingMemoryData(PaymentPage.this).getSharedPrefString(String.valueOf(R.string.USERNAME_KEY)));
                    String key = databaseReference.push().getKey();
                    if (key != null) {
                        databaseReference = databaseReference.child(key);
                        databaseReference.setValue(paymentDataClass).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Intent intent = new Intent(PaymentPage.this, PaymentSuccess.class);
                                intent.putExtra("data", paymentDataClass);
                                startActivity(intent);

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getApplicationContext() , "currently we are not able to complete your transaction \n" +
                                        "your money will be refund in 24 hours" , Toast.LENGTH_LONG).show();

                                DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("Issue");
                                databaseReference1 = databaseReference1.child("paymentIssue").child(new SettingMemoryData(PaymentPage.this).getSharedPrefString(String.valueOf(R.string.USERNAME_KEY)));
                                String key = databaseReference1.push().getKey();
                                if (key != null) {
                                    databaseReference1 = databaseReference1.child(key).child("refund");
                                    databaseReference1.setValue(paymentDataClass.getPaymentData()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Intent intent = new Intent(PaymentPage.this , DashBoardMainUI.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            Log.d(TAG, "onSuccess: changing activity");
                                            finish();
                                            startActivity(intent);
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.d(TAG, "onFailure: " + e.getMessage() + " " +e.getClass().getName());
                                        }
                                    });
                                }

                            }
                        });
                    }



                }

                @SuppressLint("DefaultLocale")
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getApplicationContext() , String.format("error code :%d", error.getCode()) , Toast.LENGTH_LONG).show();
                }
            });


        }catch (Exception e)
        {
            Log.d(TAG, "onPaymentSuccess: " + e.getClass().getName() + " " + e.getMessage());
        }
    }

}
