package DashBoard;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager2.widget.ViewPager2;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.selflearn.rpsstationary.R;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;

import java.util.ArrayList;
import java.util.List;

import DashBoard.Adapter.ImageSliderAdapter;
import ModelClasses.CartData;
import MemoryManagement.SettingMemoryData;
import ModelClasses.ProductData;
import Utils.CustomProgressDialogSpinKit;

public class ActivityProductDetail extends AppCompatActivity{

    private static final String TAG = "ActivityProductDetail";
    ConstraintLayout constraintLayout;
    Button pay , AddToCart;
    TextView Amount , discount , MRP ;
    TextView details;
    ViewPager2 imageSlider;
    TextView productName;
    ElegantNumberButton elegantNumberButton;
    Toolbar toolbar;
    ImageButton cart;
    ProductData productData;
    ArrayList<String> list;
    DotsIndicator dotsIndicator;
    int maxOrderQuantity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        findID();
        setListeners();

        list = getIntent().getStringArrayListExtra("images");
        productData = (ProductData) getIntent().getSerializableExtra("obj");

        if (productData != null) {
            maxOrderQuantity = Integer.parseInt(productData.getOrderQuantity());
            productData.setOrderQuantity("1");
            Log.d(TAG, "onCreate: " + maxOrderQuantity);
        }

        if (list!=null && productData != null)
        {
            Log.d(TAG, "onCreate: data got successfully");
            setData(productData , list);
        }
    }

    private void setData(ProductData productData, List<String> list) {

        productName.setText(productData.getProductName());
        double  price =Double.parseDouble( productData.getSellingPrice());
        price = price -0.01;
        Amount.setText(String.valueOf(price));
        discount.setText(String.format("%s rs", productData.getDiscount()));
        MRP.setText(productData.getMRP());
        MRP.setPaintFlags(MRP.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        Log.d(TAG, "setData: " + list.size());

        details.setText(String.format("Brand : %s\nAvailable color : %s\nPcs in one Pack : %s\navailable Size : %s\nproduct Type : %s",
                productData.getBrand(), productData.getColor(), productData.getQuantityInOnePack(), productData.getSize(), productData.getType())
        );

        imageSlider.setAdapter(new ImageSliderAdapter(ActivityProductDetail.this , list));
        dotsIndicator.setViewPager2(imageSlider);

    }

    private void setListeners()
    {

        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                new CustomProgressDialogSpinKit(ActivityProductDetail.this).startLoadingDialog();
                constraintLayout.setClickable(false);
                constraintLayout.setAlpha(0.4f);

                Intent intent = new Intent(ActivityProductDetail.this , ActivityChooseAddress.class);
                intent.putExtra("ProductData" , productData);
                intent.putExtra("images" , list);
                startActivity(intent);

            }
        });

        elegantNumberButton.setNumber("1");

        elegantNumberButton.setOnValueChangeListener(new ElegantNumberButton.OnValueChangeListener() {
            @Override
            public void onValueChange(ElegantNumberButton view, int oldValue, int newValue) {

                elegantNumberButton.setNumber(String.valueOf(newValue));
                productData.setOrderQuantity(String.valueOf(newValue));

                if (newValue < 1) {
                    elegantNumberButton.setNumber("1");
                }
                Log.d(TAG, "onValueChange: " + productData.getOrderQuantity());

                if (newValue > maxOrderQuantity)
                {
                    elegantNumberButton.setNumber(String.valueOf(maxOrderQuantity));
                }
            }
        });

        AddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new CustomProgressDialogSpinKit(ActivityProductDetail.this).startLoadingDialog();
                constraintLayout.setClickable(false);
                constraintLayout.setAlpha(0.4f);

                String user = new SettingMemoryData(ActivityProductDetail.this).getSharedPrefString(String.valueOf(R.string.USERNAME_KEY));
                if (user != null)
                {
                    Log.d(TAG, "onClick: " + user);

                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("CartData");
                    databaseReference = databaseReference.child(user);

                    String key = databaseReference.push().getKey();
                    if (key != null) {
                        CartData cartData = new CartData(productData , elegantNumberButton.getNumber());
                        databaseReference.child(key).setValue(cartData).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "onSuccess: successfully added to cart");
                                finish();
                                startActivity(new Intent(ActivityProductDetail.this , ActivityCart.class));
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG, "onFailure: " + e.getClass().getName() + " " +e.getMessage());
                                Toast.makeText(getApplicationContext() , "something went wrong" , Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }
                else {
                    new CustomProgressDialogSpinKit(ActivityProductDetail.this).dismissDialog();
                    constraintLayout.setClickable(true);
                    constraintLayout.setAlpha(1.0f);
                }

            }
        });

        cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ActivityProductDetail.this , ActivityCart.class));
            }
        });

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    private void findID() {

        dotsIndicator = findViewById(R.id.dot);
        pay = findViewById(R.id.pay);
        constraintLayout = findViewById(R.id.layout_payment_page);
        toolbar = findViewById(R.id.toolbar2);
        discount = findViewById(R.id.discount);
        AddToCart = findViewById(R.id.AddToCartButton);
        imageSlider = findViewById(R.id.product_images);
        Amount = findViewById(R.id.RealPrice);
        MRP = findViewById(R.id.cuttedprice);
        productName = findViewById(R.id.product_name);
        details = findViewById(R.id.details);
        elegantNumberButton = findViewById(R.id.elegantNumberButton);
        cart = findViewById(R.id.goToCart);

    }

    @Override
    protected void onResume() {
        super.onResume();

        new CustomProgressDialogSpinKit(ActivityProductDetail.this).dismissDialog();
        constraintLayout.setClickable(true);
        constraintLayout.setAlpha(1.0f);

    }
}