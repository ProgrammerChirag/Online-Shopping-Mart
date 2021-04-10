package DashBoard;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.selflearn.rpsstationary.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import DashBoard.Adapter.MyOrdersAdapter;
import MemoryManagement.SettingMemoryData;
import ModelClasses.FeedbackData;
import ModelClasses.ProductData;
import ModelClasses.paymentDataClass;
import Utils.CustomDialogMaker;
import Utils.CustomProgressDialogSpinKit;

public class MyOrderActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    Toolbar toolbar;
    Map<String , Object> map;
    private static final String TAG = "MyOrderActivity";
    List<paymentDataClass> paymentDataClassList;
    List<ProductData> productDataList;
    List<String> images ;
    List<List<String>> imageData;
    CustomProgressDialogSpinKit customProgressDialogSpinKit;

    ValueEventListener listenerGetImage = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {

            map = (Map<String, Object>) snapshot.getValue();

            if (productDataList != null && map != null)
                manipulateData(productDataList, map);
            else {
                Log.d(TAG, "onDataChange: data is null");
            }

        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };

    private void manipulateData(List<ProductData> productDataList, Map<String, Object> mpMap) {

        Log.d(TAG, "manipulateData: called");

        imageData = new ArrayList<>();
        images = new ArrayList<>();


        for (ProductData productData : new ArrayList<>(productDataList)) {
            Map<String, Object> map1 = (Map<String, Object>) mpMap.get(productData.getCategory());
            if (map1 == null) {
                productDataList.remove(productData);
            } else {
                Map<String, Object> map2 = (Map<String, Object>) map1.get(productData.getBrand());
                if (map2 == null) {
                    productDataList.remove(productData);
                } else {
                    Map<String, Object> map3 = (Map<String, Object>) map2.get(productData.getType());
                    if (map3 == null) {
                        productDataList.remove(productData);
                    } else {

                        Map<String, Object> map4 = (Map<String, Object>) map3.get(productData.getSize());
                        if (map4 == null) {
                            productDataList.remove(productData);
                        } else {
                            Map<String, Object> map5 = (Map<String, Object>) map4.get(productData.getColor());
                            if (map5 == null) {
                                productDataList.remove(productData);
                            } else {
                                Map<String, Object> map6 = (Map<String, Object>) map5.get(productData.getProductName());
                                if (map6 == null) {
                                    productDataList.remove(productData);
                                } else {
                                    Log.d(TAG, "manipulateData: " + map6);
                                    images = new ArrayList<>();
                                    for (String key : map6.keySet()) {
                                        images.add((String) map6.get(key));
                                    }
                                    imageData.add(images);
                                }
                            }
                        }
                    }
                }
            }
        }

        Log.d(TAG, "manipulateData: " + imageData);

        if (imageData.size() == productDataList.size()) {
            Log.d(TAG, "manipulateData: manipulation success");

//            recyclerView.addItemDecoration(new DividerItemDecoration(MyOrderActivity.this,
//                    DividerItemDecoration.VERTICAL));
//
//            recyclerView.setHasFixedSize(true);
//            recyclerView.setLayoutManager(new LinearLayoutManager(this , RecyclerView.VERTICAL , false));
//            recyclerView.setAdapter(new MyOrdersAdapter(MyOrderActivity.this , imageData , paymentDataClassList));
//
//            ConstraintLayout constraintLayout = findViewById(R.id.constraint_layout6);
//            for (int i=0 ; i < constraintLayout.getChildCount(); i++)
//            {
//                View view = constraintLayout.getChildAt(i);
//                view.setEnabled(true);
//                view.setAlpha(1.0f);
//            }
//
//            customProgressDialogSpinKit.dismissDialog();
            getRatingData(MyOrderActivity.this);


        } else {
            Log.d(TAG, "manipulateData:  data not matching");
            Log.d(TAG, "manipulateData: " + imageData.size());
            Log.d(TAG, "manipulateData: " + productDataList.size());
        }


    }

    private void getRatingData(final Context context) {

        final List<FeedbackData> dataList = new ArrayList<>();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("FeedbackAndRating");
        databaseReference = databaseReference.child(new SettingMemoryData(MyOrderActivity.this).getSharedPrefString(String.valueOf(R.string.USERNAME_KEY)));
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1 :snapshot.getChildren())
                {
                    dataList.add(snapshot1.getValue(FeedbackData.class));
                }
                Log.d(TAG, "onDataChange: " + dataList.size());
                recyclerView.addItemDecoration(new DividerItemDecoration(MyOrderActivity.this,
                        DividerItemDecoration.VERTICAL));

                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new LinearLayoutManager(context , RecyclerView.VERTICAL , false));
                recyclerView.setAdapter(new MyOrdersAdapter(MyOrderActivity.this , imageData , paymentDataClassList , dataList));

                ConstraintLayout constraintLayout = findViewById(R.id.constraint_layout6);
                for (int i=0 ; i < constraintLayout.getChildCount(); i++)
                {
                    View view = constraintLayout.getChildAt(i);
                    view.setEnabled(true);
                    view.setAlpha(1.0f);
                }

                customProgressDialogSpinKit.dismissDialog();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    ValueEventListener listener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {

            paymentDataClassList = new ArrayList<>();

           for (DataSnapshot snapshot1 : snapshot.getChildren())
           {
               paymentDataClassList.add(snapshot1.getValue(paymentDataClass.class));
           }
            Log.d(TAG, "onDataChange: " + paymentDataClassList.size());

           if (paymentDataClassList.size() > 0)
           {
               getImageData(paymentDataClassList);
           }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

            Log.d(TAG, "onCancelled: " + error.getMessage()+ " " + error.getClass().getName());
            new CustomDialogMaker(MyOrderActivity.this).createAndShowDialogWarning("something went wrong please try again");
        }
    };

    private void getImageData(List<paymentDataClass> paymentDataClassList) {
        productDataList = new ArrayList<>();

        for (paymentDataClass paymentDataClass : paymentDataClassList)
        {
            productDataList.add(paymentDataClass.getProductData());
        }

        if (paymentDataClassList.size() == productDataList.size())
        {
            // data got successfully.
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("ProductImages");
            databaseReference = databaseReference.child("images");
            databaseReference.addValueEventListener(listenerGetImage);

        }

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_order);

        customProgressDialogSpinKit = new CustomProgressDialogSpinKit(MyOrderActivity.this);
        customProgressDialogSpinKit.startLoadingDialog();

        ConstraintLayout constraintLayout = findViewById(R.id.constraint_layout6);
        for (int i=0 ; i < constraintLayout.getChildCount(); i++)
        {
            View view = constraintLayout.getChildAt(i);
            view.setEnabled(false);
            view.setAlpha(0.5f);
        }

        getAllOrderList();

        recyclerView = findViewById(R.id.recycler_view_myOrders);


        toolbar = findViewById(R.id.toolbar7);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void getAllOrderList() {
        String user  = new SettingMemoryData(MyOrderActivity.this).getSharedPrefString(String.valueOf(R.string.USERNAME_KEY));


        if (user != null){
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("paymentAndOrder");
            databaseReference = databaseReference.child(user);
            databaseReference.addValueEventListener(listener);
        }
        else {
            new CustomDialogMaker(MyOrderActivity.this).createAndShowDialogWarning("something went wrong");
        }
    }

}