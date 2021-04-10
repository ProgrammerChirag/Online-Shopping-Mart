package AdminPanel;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.selflearn.rpsstationary.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import AdminPanel.Adapter.ShowProductAdapter;
import ModelClasses.ProductData;
import Utils.CustomProgressDialog;

public class ShowAllProduct extends AppCompatActivity {

    RecyclerView recyclerView;
    Toolbar toolbar;
    FloatingActionButton button;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_products);

        button = findViewById(R.id.addNewItem);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ShowAllProduct.this , AddNewProduct.class);
                startActivity(intent);
            }
        });

        recyclerView = findViewById(R.id.products);
//        recyclerView.setHasFixedSize(true);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this , RecyclerView.VERTICAL , false));
//        recyclerView.setAdapter(new ShowProductAdapter());

        toolbar =findViewById(R.id.toolbar12);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        new DataLoader(recyclerView , ShowAllProduct.this , new CustomProgressDialog(ShowAllProduct.this)).execute();

    }
}

class DataLoader extends AsyncTask<Context, Long , Integer > {

    @SuppressLint("StaticFieldLeak")
    RecyclerView recyclerView;
    @SuppressLint("StaticFieldLeak")
    Context context;
    Map<String, Object> mpMap;
    @SuppressLint("StaticFieldLeak")
    EditText Search_view;
    List<ProductData> productDataList;
    private static final String TAG = "DataLoader";
    List<List<String>> imageData;
    List<String> images;
    Map<String, Object> data;
    DatabaseReference loadImage, LoadProductData;
    CustomProgressDialog customProgressDialog;

    public DataLoader(RecyclerView recyclerView, Context context, CustomProgressDialog customProgressDialog) {
        this.recyclerView = recyclerView;
        this.context = context;
        this.customProgressDialog = customProgressDialog;
    }

    private ValueEventListener getImageData = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {

            Log.d(TAG, "onDataChange: called");

            data = new HashMap<>();
            mpMap = new HashMap<>();

            mpMap = (Map<String, Object>) snapshot.getValue();

            Log.d(TAG, "onDataChange: " + mpMap);

            if (productDataList != null && mpMap != null)
                manipulateData(productDataList, mpMap);

        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };

    // function for manipulate data from server.
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

//        recyclerView = ((Activity)(context)).findViewById(R.id.products);
//        recyclerView.setHasFixedSize(true);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this , RecyclerView.VERTICAL , false));
//        recyclerView.setAdapter(new ShowProductAdapter());

            recyclerView.addItemDecoration(new DividerItemDecoration(context,
                    DividerItemDecoration.VERTICAL));

            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(context.getApplicationContext() , RecyclerView.VERTICAL , false));
            final ShowProductAdapter myAdapter = new ShowProductAdapter(imageData, productDataList, context);
            recyclerView.setAdapter(myAdapter);

            recyclerView.post(new Runnable() {
                @Override
                public void run() {
                    myAdapter.notifyDataSetChanged();
                }
            });

           // loadProfileImage();
            Log.d(TAG, "manipulateData: dismissing the dialog box");

            if (customProgressDialog != null )
                customProgressDialog.dismissDialog();

        } else {
            Log.d(TAG, "manipulateData:  data not matching");
            Log.d(TAG, "manipulateData: " + imageData.size());
            Log.d(TAG, "manipulateData: " + productDataList.size());
        }


    }



    private void getImageLinkData() {

        Log.d(TAG, "getImageLinkData: called");

        loadImage = FirebaseDatabase.getInstance().getReference("ProductImages");
        loadImage = loadImage.child("images");
        loadImage.addValueEventListener(getImageData);
    }

    private ValueEventListener loadProductData = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {

            productDataList = new ArrayList<>();


            for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                for (DataSnapshot snapshot2 : snapshot1.getChildren()) {
                    for (DataSnapshot snapshot3 : snapshot2.getChildren()) {
                        for (DataSnapshot snapshot4 : snapshot3.getChildren()) {
                            for (DataSnapshot snapshot5 : snapshot4.getChildren()) {
                                for (DataSnapshot snapshot6 : snapshot5.getChildren()) {
                                    ProductData productData = snapshot6.getValue(ProductData.class);

                                    Log.d(TAG, "onDataChange: " + productData);
                                    productDataList.add(productData);
                                }
                            }
                        }
                    }
                }
            }

            if (productDataList != null) {
                if (productDataList.size() > 0) {
                    Log.d(TAG, "onDataChange: data non zero and non null");


                    //Search_view = ((Activity) context).findViewById(R.id.search_view);

//                    Search_view.addTextChangedListener(new TextWatcher() {
//                        @Override
//                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//                        }
//
//                        @Override
//                        public void onTextChanged(CharSequence s, int start, int before, int count) {
//                            if (s.length() == 0) {
//                                Log.d(TAG, "onTextChanged: called");
//                                loadData();
//                            }
//                        }
//
//                        @Override
//                        public void afterTextChanged(Editable s) {
//
//                        }
//                    });

//                    Search_view.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//                        @SuppressLint("NewApi")
//                        @Override
//                        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//
//                            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
//                                Log.d(TAG, "onEditorAction: searching element");
//                                if (!Search_view.getText().toString().isEmpty()) {
//                                    Log.d(TAG, "onEditorAction: " + Search_view.getText().toString().trim());
//                                    Search_view.setActivated(false);
//
//                                    productDataList = new SearchAlgorithm(Search_view.getText().toString().trim(), productDataList).getData();
//                                    getImageLinkData();
//                                }
//                            }
//
//                            return false;
//                        }
//                    });

                    getImageLinkData();

                    //loadImage.removeEventListener(getImageData);
                }
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
            Log.d(TAG, "onCancelled: " + error.getMessage() + " " + error.getClass().getName());
        }
    };

    public void loadData() {

        LoadProductData = FirebaseDatabase.getInstance().getReference("ProductData");
        LoadProductData.addValueEventListener(loadProductData);
    }

    @Override
    protected Integer doInBackground(Context... contexts) {

        loadData();
        return null;
    }

    @Override
    protected void onProgressUpdate(Long... values) {

        Log.d(TAG, "onProgressUpdate: " + Arrays.toString(values));
        super.onProgressUpdate(values);
    }

    public List<ProductData> getProductDataList()
    {
        return  productDataList;
    }

    public List<List<String>> getImageLinks()
    {
        return imageData;
    }

    public Map<String , Object> getMpMap()
    {
        return  mpMap;
    }



}
