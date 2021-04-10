package AdminPanel;

import android.annotation.SuppressLint;
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
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager2.widget.ViewPager2;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.selflearn.rpsstationary.R;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;

import java.util.ArrayList;
import java.util.List;

import DashBoard.ActivityCart;
import DashBoard.ActivityChooseAddress;
import DashBoard.Adapter.ImageSliderAdapter;
import MemoryManagement.SettingMemoryData;
import ModelClasses.ProductData;
import Utils.CustomProgressDialogSpinKit;

public class ShowProductDetail extends AppCompatActivity {

    Button delete, Edit;
    ConstraintLayout constraintLayout;
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
    private static final String TAG = "ShowProductDetail";

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        delete = findViewById(R.id.pay);
        Edit = findViewById(R.id.AddToCartButton);

        delete.setVisibility(View.VISIBLE);
        delete.setText("delete");
        Edit.setVisibility(View.VISIBLE);
        Edit.setText("edit");

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

        imageSlider.setAdapter(new ImageSliderAdapter(ShowProductDetail.this , list));
        dotsIndicator.setViewPager2(imageSlider);

    }

    private void setListeners()
    {

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteData(productData);
            }
        });

        Edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ShowProductDetail.this , EditDetailsActivity.class);
                intent.putExtra("productData" ,productData);
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


        cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ShowProductDetail.this , ActivityCart.class));
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
        delete = findViewById(R.id.pay);
        constraintLayout = findViewById(R.id.layout_payment_page);
        toolbar = findViewById(R.id.toolbar2);
        discount = findViewById(R.id.discount);
        Edit = findViewById(R.id.AddToCartButton);
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

        new CustomProgressDialogSpinKit(ShowProductDetail.this).dismissDialog();
        constraintLayout.setClickable(true);
        constraintLayout.setAlpha(1.0f);

    }

    public void deleteData(ProductData productData){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("ProductData");
        databaseReference = databaseReference.child(productData.getCategory()).child(productData.getBrand()).child(productData.getType())
                .child(productData.getSize()).child(productData.getColor());
        databaseReference.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "onSuccess: " + "data deleted success...");
                onBackPressed();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: "+ e.getMessage() + " " +e.getClass().getName());
            }
        });

    }



}


