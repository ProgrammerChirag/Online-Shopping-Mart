package DashBoard;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ebanx.swipebtn.OnActiveListener;
import com.ebanx.swipebtn.SwipeButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.selflearn.rpsstationary.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import DashBoard.Adapter.CartProductAdapter;
import ModelClasses.CartData;
import MemoryManagement.SettingMemoryData;
import ModelClasses.ImageData;
import ModelClasses.ProductData;
import MultiProductActivity.ChooseAddressActivity;
import Utils.CustomDialogForPayment;
import Utils.CustomProgressDialogSpinKit;

public class ActivityCart extends AppCompatActivity {

    Button ShopNowBtn;
    SwipeButton SwipeToPay;
    RecyclerView recyclerView;
    Toolbar toolbar;
    private static final String TAG = "ActivityCart";
    ConstraintLayout constraintLayout;
    TextView amount;
    List<ProductData> productDataList;
    float TotalAmount =0 ;
    List<List<String>> links;
    Map<String , Object> mpMap;
    ImageView cartMessage;
    List<CartData> cartDataList;
    List<String> keys;

    DatabaseReference CartDataReference ,ImageDataReference;

    ValueEventListener eventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {

            Log.d(TAG, "onDataChange: called");

//                    if (cartDataList.contains(snapshot1.getKey())) {
//
//                        map = (Map<String, Object>) snapshot1.getValue();
//
//                        List<String> images = new ArrayList<>();
//
//                        if (map != null) {
//
//                            for (String key : map.keySet())
//                            {
//                                images.add((String) map.get(key));
//                            }
//                            if (images.size() != 0)
//                            {
//                                links.add(images);
//                            }
//
//                        }
//
//                    }
//            for (DataSnapshot snapshot1 : snapshot.getChildren())
//            {
//                for (DataSnapshot snapshot2 : snapshot1.getChildren())
//                {
//                    for (DataSnapshot snapshot3 : snapshot2.getChildren())
//                    {
//                        for (DataSnapshot snapshot4 : snapshot3.getChildren())
//                        {
//                            for (DataSnapshot snapshot5 : snapshot4.getChildren())
//                            {
//                                for (DataSnapshot snapshot6 : snapshot5.getChildren())
//                                {
//                                    for (ProductData cartData : productDataList)
//                                    {
//                                        if (cartData.getProductName().equals(snapshot6.getKey()))
//                                        {
//                                            Map<String , Object> data;
//                                            Log.d(TAG, "onDataChange: "+snapshot6.getKey());
//
//                                            data = (Map<String, Object>) snapshot6.getValue();
//
//                                             images = new ArrayList<>();
//
//                                            if (data != null)
//                                            {
//                                                for (String key : data.keySet())
//                                                {
//                                                    images.add((String) data.get(key));
//                                                }
//                                                if (images.size() != 0)
//                                                {
//                                                    links.add(images);
//                                                }
//                                            }
//                                        }
//                                        else {
//                                            Log.d(TAG, "onDataChange: mismatch");
//                                        }
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//            }

            mpMap = (Map<String, Object>) snapshot.getValue();

            if (productDataList != null && mpMap != null)

                manipulateData(productDataList , mpMap , cartDataList);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };

    private void manipulateData(List<ProductData> productDataList, Map<String, Object> mpMap, List<CartData> cartDataList) {

        List<List<String>> imageData = new ArrayList<>();
        List<String> images ;

        for (int i=0 ; i < cartDataList.size() ; i++)
        {
            CartData cartData = cartDataList.get(i);
            String key = keys.get(i);
            Map<String , Object> map1 = (Map<String, Object>) mpMap.get(cartData.getProductData().getCategory());
            if (map1 == null)
            {
                cartDataList.remove(cartData);
                productDataList.remove(cartData.getProductData());
                keys.remove(key);
            }
            else {
                Map<String, Object> map2 = (Map<String, Object>) map1.get(cartData.getProductData().getBrand());
                if (map2 == null) {
                    cartDataList.remove(cartData);
                    productDataList.remove(cartData.getProductData());
                    keys.remove(key);
                } else {
                    Map<String, Object> map3 = (Map<String, Object>) map2.get(cartData.getProductData().getType());
                    if (map3 == null) {
                        cartDataList.remove(cartData);
                        productDataList.remove(cartData.getProductData());
                        keys.remove(key);
                    }else {

                        Map<String, Object> map4 = (Map<String, Object>) map3.get(cartData.getProductData().getSize());
                        if (map4 == null)
                        {
                            cartDataList.remove(cartData);
                            productDataList.remove(cartData.getProductData());
                            keys.remove(key);
                        }
                        else {
                            Map<String , Object> map5 = (Map<String, Object>) map4.get(cartData.getProductData().getColor());
                            if (map5 == null)
                            {
                                cartDataList.remove(cartData);
                                productDataList.remove(cartData.getProductData());
                                keys.remove(key);
                            }
                            else {
                                Map<String, Object> map6 = (Map<String, Object>) map5.get(cartData.getProductData().getProductName());
                                if (map6 == null) {
                                    cartDataList.remove(cartData);
                                    productDataList.remove(cartData.getProductData());
                                    keys.remove(key);
                                } else {
                                    Log.d(TAG, "manipulateData: " + map6);
                                    images = new ArrayList<>();
                                    for (String key_data : map6.keySet())
                                    {
                                        images.add((String) map6.get(key_data));
                                    }
                                    imageData.add(images);
                                    Log.d(TAG, "onDataChange: " + imageData.size());
                                }
                            }
                        }
                    }
                }
            }
        }

        if (imageData.size() == cartDataList.size() && cartDataList.size() == productDataList.size() && productDataList.size() == keys.size())
        {
            initiateRecyclerView(productDataList, imageData , cartDataList  ,keys);
        }
        else {
            if (links != null) {
                Log.d(TAG, "onDataChange: links size" + links.size());
            }
            Log.d(TAG, "onDataChange: " + productDataList.size());

        }
    }

    final ValueEventListener listener = new ValueEventListener() {
        @SuppressLint("SetTextI18n")
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            productDataList = new ArrayList<>();
            cartDataList = new ArrayList<>();
            keys= new ArrayList<>();

            Log.d(TAG, "onDataChange: called");

            Log.d(TAG, "onDataChange: " + snapshot.getValue());

            for (DataSnapshot snapshot1 : snapshot.getChildren()) {

                keys.add(snapshot1.getKey());
                cartDataList.add(snapshot1.getValue(CartData.class));
                productDataList.add(Objects.requireNonNull(snapshot1.getValue(CartData.class)).getProductData());

            }
            Log.d(TAG, "onDataChange: size  " + cartDataList.size());

            for (int i=0 ; i <cartDataList.size() ; i++)
            {
                Log.d(TAG, "onDataChange: index" +cartDataList.get(i).getQuantity());
            }
            if (productDataList != null && cartDataList != null && keys != null) {
                if (productDataList.size() > 0 && cartDataList.size() > 0 && cartDataList.size() == productDataList.size() && productDataList.size() == keys.size()) {
                    // set RecyclerView
                    Log.d(TAG, "onDataChange: data is non null and non zero");

                    CustomProgressDialogSpinKit customProgressDialogSpinKit = new CustomProgressDialogSpinKit(ActivityCart.this);
                    customProgressDialogSpinKit.dismissDialog();
                    constraintLayout.setAlpha(1.0f);
                    constraintLayout.setClickable(true);

                    for (CartData productData : cartDataList) {

                        if (productData != null) {
                            Log.d(TAG, "onDataChange: " + productData.getProductData().getSellingPrice());
                            TotalAmount = TotalAmount + Float.parseFloat(productData.getProductData().getSellingPrice()) * Integer.parseInt(productData.getQuantity());
                        }
                        amount.setText("Amount : " + TotalAmount + " INR " + "(including all taxes)");
                        //Log.d(TAG, "onDataChange: "+TotalAmount);
                        amount.setVisibility(View.GONE);

//                        initiateRecyclerView(cartDataList);

                        if (productData != null) {
                            Log.d(TAG, "onDataChange: " +productData.getQuantity());
                        }
                    }

                }
            }
            Log.d(TAG, "onDataChange: calling get Data");
            getImageData();
        }


        @Override
        public void onCancelled(@NonNull DatabaseError error) {

            Log.d(TAG, "onCancelled: " + error.getMessage() + " " + error.getClass().getName());
            Toast.makeText(getApplicationContext(), "no data in your cart", Toast.LENGTH_LONG).show();

            // process of showing button
            SwipeToPay.setVisibility(View.INVISIBLE);
            amount.setVisibility(View.INVISIBLE);
            CustomProgressDialogSpinKit customProgressDialogSpinKit = new CustomProgressDialogSpinKit(ActivityCart.this);
            customProgressDialogSpinKit.startLoadingDialog();
            constraintLayout.setAlpha(0.4f);
            constraintLayout.setClickable(false);

            ShopNowBtn.setVisibility(View.VISIBLE);

        }
    };

    private void getImageData() {

        Log.d(TAG, "getImageData: called");

        links = new ArrayList<>();

        ImageDataReference = FirebaseDatabase.getInstance().getReference("ProductImages");
        ImageDataReference = ImageDataReference.child("images");

        ImageDataReference.addValueEventListener(eventListener);

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cart_layout);

        findID();

        getDataFromDatabase(new SettingMemoryData(ActivityCart.this).getSharedPrefString(String.valueOf(R.string.USERNAME_KEY)));

    }

    private void findID() {

        cartMessage = findViewById(R.id.cart_is_empty_message);
        ShopNowBtn = findViewById(R.id.shop_now);
        SwipeToPay = findViewById(R.id.swipe_btn);
        recyclerView = findViewById(R.id.cart_products);
        toolbar = findViewById(R.id.toolbar6);
        constraintLayout = findViewById(R.id.constraint_layout4);
        amount = findViewById(R.id.amount_text);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        ShopNowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CustomProgressDialogSpinKit customProgressDialogSpinKit = new CustomProgressDialogSpinKit(ActivityCart.this);
                customProgressDialogSpinKit.startLoadingDialog();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                        Intent intent = new Intent(ActivityCart.this , DashBoardMainUI.class);
                        startActivity(intent);
                    }
                }, 3000);
            }
        });
    }

    private void getDataFromDatabase(String user) {

        CustomProgressDialogSpinKit customProgressDialogSpinKit = new CustomProgressDialogSpinKit(ActivityCart.this);
        customProgressDialogSpinKit.startLoadingDialog();
        constraintLayout.setAlpha(0.4f);
        constraintLayout.setClickable(false);

        Log.d(TAG, "getDataFromDatabase: called");

        CartDataReference = FirebaseDatabase.getInstance().getReference("CartData");
        CartDataReference = CartDataReference.child(user);
        CartDataReference.addValueEventListener(listener);

    }

    private void initiateRecyclerView(final List<ProductData> cartDataList, final List<List<String>> links, final List<CartData> dataList, List<String> keys) {

        Log.d(TAG, "initiateRecyclerView: called");

        if (cartDataList != null){
        if (cartDataList .size() == links.size()  && links.size() == dataList.size() && cartDataList .size() != 0) {
            if (recyclerView != null) {
                Log.d(TAG, "initiateRecyclerView: " + links.size() );
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new LinearLayoutManager(ActivityCart.this, RecyclerView.VERTICAL, false));
                CartProductAdapter adapter = new CartProductAdapter(cartDataList, links, ActivityCart.this , amount , dataList , keys);
                adapter.notifyDataSetChanged();
                recyclerView.setAdapter(adapter);

                SwipeToPay.setOnActiveListener(new OnActiveListener() {
                    @Override
                    public void onActive() {

                        // pay now...

                        CustomProgressDialogSpinKit customProgressDialogSpinKit = new CustomProgressDialogSpinKit(ActivityCart.this);
                        customProgressDialogSpinKit.startLoadingDialog();

                        constraintLayout.setAlpha(0.4f);
                        constraintLayout.setClickable(false);
                        constraintLayout.setEnabled(false);

                        recyclerView.setClickable(false);
                        recyclerView.setEnabled(false);
                        recyclerView.setAlpha(0.4f);

                        ShopNowBtn.setClickable(false);
                        ShopNowBtn.setEnabled(false);
                        ShopNowBtn.setAlpha(0.4f);

                        SwipeToPay.setClickable(false);
                        SwipeToPay.setEnabled(false);
                        SwipeToPay.setAlpha(0.4f);

                        // get all products information.

                        CustomDialogForPayment customDialogForPayment = new CustomDialogForPayment(ActivityCart.this , dataList  , links);
                        customDialogForPayment.startDialog();

                        // get all product price.

//                        float totalPrice =0;
//                        for (CartData cartData : dataList)
//                        {
//                            totalPrice = totalPrice + Float.parseFloat(cartData.getProductData().getSellingPrice()) * Integer.parseInt(cartData.getQuantity());
//                        }
//
//                        Log.d(TAG, "onActive: " + totalPrice);
//

                    }
                });

            }
        }
        else {

            Log.d(TAG, "initiateRecyclerView: " + " data is null");
            recyclerView.setVisibility(View.INVISIBLE);
            constraintLayout.setAlpha(1.0f);
            constraintLayout.setClickable(true);
            constraintLayout.setEnabled(true);

            recyclerView.setClickable(true);
            recyclerView.setEnabled(true);
            recyclerView.setAlpha(1.0f);
//            recyclerView.setVisibility(View.VISIBLE);

            ShopNowBtn.setClickable(true);
            ShopNowBtn.setEnabled(true);
            ShopNowBtn.setAlpha(1.0f);
            ShopNowBtn.setVisibility(View.VISIBLE);

            SwipeToPay.setClickable(true);
            SwipeToPay.setEnabled(true);
            SwipeToPay.setAlpha(1.0f);
            SwipeToPay.setVisibility(View.INVISIBLE);

            amount.setVisibility(View.INVISIBLE);

            new CustomProgressDialogSpinKit(ActivityCart.this).dismissDialog();

            cartMessage.setVisibility(View.VISIBLE);

        }
        }
    }

}
