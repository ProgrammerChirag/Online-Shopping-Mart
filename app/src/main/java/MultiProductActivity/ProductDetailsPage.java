package MultiProductActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.selflearn.rpsstationary.R;

import java.util.ArrayList;
import java.util.List;

import ModelClasses.CartData;
import ModelClasses.DeliveryAddress;
import ModelClasses.ImageData;

public class ProductDetailsPage extends AppCompatActivity {

    List<CartData> cartDataList;
    List<ImageData> imageData;
    DeliveryAddress deliveryAddress ;
    private static final String TAG = "ProductDetailsPage";
    List<List<String>> images;
    RecyclerView recyclerView;
    TextView payNow;
    TextView totalPrice;
    TextView deliveryCharge;
    Toolbar toolbar;
    TextView totalAmount;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_order_data);

        cartDataList = (List<CartData>) getIntent().getSerializableExtra("CartData");
        imageData = (List<ImageData>)getIntent().getSerializableExtra("images");
        deliveryAddress = (DeliveryAddress) getIntent().getSerializableExtra("DeliveryAddress");

        payNow = findViewById(R.id.pay_now);
        totalPrice = findViewById(R.id.totalPrice);
        deliveryCharge = findViewById(R.id.delivery_charge);
        totalAmount = findViewById(R.id.totalAmount);



        toolbar = findViewById(R.id.toolbar14);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        if (cartDataList != null && imageData != null && deliveryAddress != null)
        {
            Log.d(TAG, "onCreate: " + "data is not null");
            Log.d(TAG, "onCreate: imageData size" + imageData.size());

            images = new ArrayList<>();
            for (ImageData imageData1: imageData)
            {
                List<String> image = imageData1.getImageData();
                images.add(image);
            }



            Log.d(TAG, "onCreate: image size " + images.size());

            if (images.size() == cartDataList.size() )
            {
                recyclerView = findViewById(R.id.recycler_view_products);
                recyclerView.setLayoutManager(new LinearLayoutManager(this ,RecyclerView.VERTICAL , false));
                recyclerView.setAdapter(new ProductDetailAdapter(cartDataList , images , ProductDetailsPage.this , deliveryAddress));

                payNow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // payNow clicked....

                        Intent intent = new Intent(ProductDetailsPage.this , PaymentPage.class);
                        intent.putExtra("CartData" , new ArrayList<>(cartDataList));
                        intent.putExtra("images" , new ArrayList<>(imageData));
                        intent.putExtra("DeliveryAddress" , deliveryAddress);
                        intent.putExtra("charge" , getIntent().getFloatExtra("charge" ,0) );
                        startActivity(intent);

                    }
                });

                float total = 0f ;
                float delivery;
                for (CartData cartData : cartDataList)
                {
                    total = total + ( Float.parseFloat(cartData.getProductData().getSellingPrice()) * Integer.parseInt(cartData.getQuantity()) );
                }
                if (total < 500)
                {
                    delivery = 50.0f;
                }else delivery = 0f;

                Float payableAmount = total+ delivery;

                totalPrice.setText(String.format("Total Price : %s₹", total));
                deliveryCharge.setText(String.format("Delivery Charge : %s₹", delivery));
                totalAmount.setText(String.format("total Amount : %s₹", payableAmount));
                TextView totalSavings = findViewById(R.id.discount);
                totalSavings.setText("Total Discount : 0 INR");
            }


        }

    }
}
