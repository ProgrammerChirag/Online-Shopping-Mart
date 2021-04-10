package DashBoard;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.selflearn.rpsstationary.R;
import com.selflearn.rpsstationary.SplashScreenActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import AuthenticationAndLogin.MobileAuthenticationActivity;
import DashBoard.Adapter.ShowProductAdapter;
import DashBoard.Adapter.ViewPager2Adapter;
import DashBoard.Algorithms.SearchAlgorithm;
import MemoryManagement.SettingMemoryData;
import ModelClasses.CartData;
import ModelClasses.ProductData;
import ModelClasses.SliderItem;
import ModelClasses.UserData;
import Utils.CustomProgressDialog;
import Utils.CustomProgressDialogSpinKit;
import de.hdodenhof.circleimageview.CircleImageView;

public class DashBoardMainUI extends AppCompatActivity {


    private static final int REQUEST_CODE = 1234;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    ViewPager2 pager;
    RecyclerView products;
    CircleImageView circleImageView;
    TextView name;
    TextView email;
    CustomProgressDialog customProgressDialog;
    ConstraintLayout constraintLayout;
    TextView exploringButton;
    DataLoader dataLoader;
    List<String> categories;
    List<String> productType;
    List<String> productSize;
    List<String> productColor;
    boolean CAN_FILTER_APPLIED = false;
    private static final String TAG = "DashBoardMainUI";
    LinearLayout office_items , school_items , general_items;
    ImageView cart;


    @Override
    protected void onStart() {

        if (FirebaseAuth.getInstance().getCurrentUser() == null)
        {
            finish();
            startActivity(new Intent(DashBoardMainUI.this , SplashScreenActivity.class));
        }

        super.onStart();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_dashboard_main_ui);

        findID();

    }

    public void applyFilter()
    {
        Log.d(TAG, "applyFilter: called");
        if (CAN_FILTER_APPLIED)
        {
            Intent intent = new Intent(DashBoardMainUI.this , ApplyFilterActivity.class);

            intent.putExtra("category" , new ArrayList<>(categories));
            intent.putExtra("productType" , new ArrayList<>(productType));
            intent.putExtra("productSize" , new ArrayList<>(productSize));
            intent.putExtra("productColor" , new ArrayList<>(productColor));

            startActivityForResult(intent , REQUEST_CODE);

        }
    }

    private void findID() {




        cart = findViewById(R.id.cart);

        cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(DashBoardMainUI.this , ActivityCart.class));
//                break;
            }
        });

        // finding id for explore btn and layout cat
        constraintLayout = findViewById(R.id.item_cat);
        exploringButton = findViewById(R.id.explore);

        exploringButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                if (constraintLayout.getVisibility() == View.GONE)
                {
                    constraintLayout.setVisibility(View.VISIBLE);
                    exploringButton.setText("Explore Products");
                }
                else if (constraintLayout.getVisibility() == View.VISIBLE)
                {
                    constraintLayout.setVisibility(View.GONE);
                    exploringButton.setText("Show Categories");
                }
            }
        });

        circleImageView  = findViewById(R.id.profile_image);

        drawerLayout = findViewById(R.id.drawer_layout);
        toolbar = findViewById(R.id.toolbar);
        navigationView = findViewById(R.id.nav_view);

        setSupportActionBar(toolbar);

        navigationView.bringChildToFront(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this , drawerLayout , toolbar , R.string.open , R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();


        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {


                Log.d(TAG, "onNavigationItemSelected: " + menuItem);

                switch (menuItem.getItemId())
                {
                    case R.id.my_orders:
                        Intent intent = new Intent(DashBoardMainUI.this , MyOrderActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.cart:
                        startActivity(new Intent(DashBoardMainUI.this , ActivityCart.class));
                        break;
                    case R.id.mail_us:
                        Intent emailIntent = new Intent(Intent.ACTION_SEND );
                        emailIntent.setDataAndType(Uri.parse("officialrpsstationary@gmail.com"),"text/plain");
                        startActivity(emailIntent);
                        break;
                    case R.id.log_out:
                        new SettingMemoryData(DashBoardMainUI.this).removeSharedPref();
                        FirebaseAuth.getInstance().signOut();
                        finish();
                        startActivity(new Intent(DashBoardMainUI.this  , MobileAuthenticationActivity.class));
                        break;
                    case R.id.my_account :
                        Intent intent1 = new Intent(DashBoardMainUI.this , ActivityMyAccount.class);
                        startActivity(intent1);
                        break;
                    case R.id.applyFilter:
                        getFilterData(dataLoader.getProductDataList());
                        break;
                    case R.id.edit_profile:
                        Intent intent2 = new Intent(DashBoardMainUI.this , ActivityEditProfile.class);
                        startActivity(intent2);
                        break;

                }

                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        pager =findViewById(R.id.viewPager);
        List<SliderItem> sliderItems = new ArrayList<>();
        sliderItems.add(new SliderItem(R.drawable.image1));
        sliderItems.add(new SliderItem(R.drawable.image2));
        sliderItems.add(new SliderItem(R.drawable.image3));
        sliderItems.add(new SliderItem(R.drawable.image4));

        pager.setAdapter(new ViewPager2Adapter(pager , sliderItems));

        pager.setClipToPadding(false);
        pager.setClipChildren(false);
        pager.setOffscreenPageLimit(3);
        pager.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);

        CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
        compositePageTransformer.addTransformer(new MarginPageTransformer(10));
        compositePageTransformer.addTransformer(new ViewPager2.PageTransformer() {
            @Override
            public void transformPage(@NonNull View page, float position) {
                float r = 1-Math.abs(position);
                page.setScaleY(0.85f + r * 0.15f);

            }
        });

        pager.setPageTransformer(compositePageTransformer);

        products = findViewById(R.id.product_list);

        products.setHasFixedSize(true);
        GridLayoutManager layoutManager;
        layoutManager = new GridLayoutManager(this , 2 , RecyclerView.VERTICAL, false);
        products.setLayoutManager(layoutManager);


        customProgressDialog = new CustomProgressDialog(DashBoardMainUI.this);
        customProgressDialog.startLoadingDialog();


                dataLoader = new DataLoader(products , DashBoardMainUI.this , customProgressDialog, constraintLayout , exploringButton);
                dataLoader.execute();


        View view= navigationView.getHeaderView(0);

        name = view.findViewById(R.id.userName);
        email = view.findViewById(R.id.mail);

        if (name != null && email != null) {
            getUserData();
        }
        else if(findViewById(R.id.userName) == null){
            Log.d(TAG, "findID: data is null");
        }
    }

    public  void getFilterData(List<ProductData> productDataList) {

        if (productDataList != null) {

            Log.d(TAG, "getFilterData: called");
            categories = new ArrayList<>();
            productSize = new ArrayList<>();
            productType = new ArrayList<>();
            productColor = new ArrayList<>();

            for (ProductData productData : productDataList) {
                Log.d(TAG, "getFilterData: called");
                if (!categories.contains(productData.getCategory())) {
                    Log.d(TAG, "getFilterData: true for first");
                    categories.add(productData.getCategory());
                }
                if (!productSize.contains(productData.getSize())) {
                    Log.d(TAG, "getFilterData: true for second");

                    productSize.add(productData.getSize());
                }
                if (!productType.contains(productData.getType())) {
                    Log.d(TAG, "getFilterData: true for third");

                    productType.add(productData.getType());
                }
                if (!productColor.contains(productData.getColor())) {
                    Log.d(TAG, "getFilterData: true for fourth");

                    productColor.add(productData.getColor());
                }

            }
            if (productColor.size() > 0 && productSize.size() > 0 && categories.size() > 0 && productType.size() > 0) {

                Log.d(TAG, "getFilterData: filter is ready to apply");
                CAN_FILTER_APPLIED = true;

                applyFilter();
            } else {
                Log.d(TAG, "getFilterData: " + productType.size());
                Log.d(TAG, "getFilterData: " + categories.size());
                Log.d(TAG, "getFilterData: " + productSize.size());
                Log.d(TAG, "getFilterData: " + productColor.size());
            }

            Log.d(TAG, "getFilterData: " + CAN_FILTER_APPLIED);
        }
        else {
            Log.d(TAG, "getFilterData: " + "data is null");
        }

    }

    private void getUserData() {


        if (new SettingMemoryData(DashBoardMainUI.this).getSharedPrefString(String.valueOf(R.string.USERNAME_KEY)) == null) {
            finish();
            startActivity(new Intent(DashBoardMainUI.this , MobileAuthenticationActivity.class));
        }
        else {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("UserData");
            databaseReference.child(new SettingMemoryData(DashBoardMainUI.this).getSharedPrefString(String.valueOf(R.string.USERNAME_KEY)))
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            UserData userData = snapshot.getValue(UserData.class);

                            if (userData != null) {
                                name.setText(userData.getName());
                                email.setText(userData.getEmail());
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.d(TAG, "onCancelled: " + error.getMessage() + " " + error.getClass().getName());
                        }
                    });
        }
    }

    @Override
    public void onBackPressed() {

        if (drawerLayout.isDrawerOpen(GravityCompat.START))
        {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == REQUEST_CODE)
        {
            if (resultCode == RESULT_OK)
            {
                String cat , type , size , color;

                if (data != null) {
                    cat = data.getStringExtra("category");
                    type = data.getStringExtra("productType");
                    size = data.getStringExtra("productSize");
                    color = data.getStringExtra("productColor");

                    if (color != null && size != null && type != null && cat != null && !cat.isEmpty() && !type.isEmpty() && !size.isEmpty() && !color.isEmpty()) {
                        // pass them to another activity which will show the data ..........

                        List<ProductData> productDataList = dataLoader.getProductDataList();

                        // apply filter .....
                        List<ProductData> productDataS = new ArrayList<>();
                        List<List<String>> imageData = new ArrayList<>();
                        Map<String , Object> map = dataLoader.getMpMap();

                        if (productDataList == null && map == null)
                        {
                            Log.d(TAG, "onActivityResult: " + " data is null");
                        }else {
                            Log.d(TAG, "onActivityResult: " +"data is not null");
                        }


                        if (productDataList != null) {
                            for (ProductData productData : productDataList)
                            {
                                if (productData.getSize().equals(size) && productData.getColor().equals(color) && productData.getType().equals(type) && productData.getCategory().equals(cat))
                                {
                                    productDataS.add(productData);
                                }
                            }
                        }

                        Log.d(TAG, "onActivityResult: " + productDataS.size());

                        // data manipulation process......

                        for (ProductData productData : new ArrayList<>(productDataS)) {
                            Map<String, Object> map1 = (Map<String, Object>) map.get(productData.getCategory());
                            if (map1 == null) {
                                productDataS.remove(productData);
                            } else {
                                Map<String, Object> map2 = (Map<String, Object>) map1.get(productData.getBrand());
                                if (map2 == null) {
                                    productDataS.remove(productData);
                                } else {
                                    Map<String, Object> map3 = (Map<String, Object>) map2.get(productData.getType());
                                    if (map3 == null) {
                                        productDataS.remove(productData);
                                    } else {

                                        Map<String, Object> map4 = (Map<String, Object>) map3.get(productData.getSize());
                                        if (map4 == null) {
                                            productDataS.remove(productData);
                                        } else {
                                            Map<String, Object> map5 = (Map<String, Object>) map4.get(productData.getColor());
                                            if (map5 == null) {
                                                productDataS.remove(productData);
                                            } else {
                                                Map<String, Object> map6 = (Map<String, Object>) map5.get(productData.getProductName());
                                                if (map6 == null) {
                                                    productDataS.remove(productData);
                                                } else {
                                                    Log.d(TAG, "manipulateData: " + map6);
                                                    List<String> images = new ArrayList<>();
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

                        Log.d(TAG, "onActivityResult: " +productDataS.size());
                        Log.d(TAG, "onActivityResult: " + imageData.size());

                        if (productDataS.size() == imageData.size()) {

                            Intent intent = new Intent(DashBoardMainUI.this, ShowProducts.class);
                            intent.putExtra("productDataList", new ArrayList<>(productDataS));
                            intent.putExtra("image", new ArrayList<>(imageData));

                            startActivity(intent);

                        }

                    }
                }
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}

 class DataLoader extends AsyncTask<Context , Long , Integer > {

     List<String> keys;
     @SuppressLint("StaticFieldLeak")
     RecyclerView recyclerView;
     @SuppressLint("StaticFieldLeak")
     Context context;
     Map<String, Object> mpMap;
     @SuppressLint("StaticFieldLeak")
     EditText Search_view;
     List<ProductData> productDataList;
     private ArrayList<ProductData> productDataList2;
     private static final String TAG = "DataLoader";
     List<List<String>> imageData;
     List<String> images;
     Map<String, Object> data;
     DatabaseReference loadImage, LoadProductData;
     CustomProgressDialog customProgressDialog;
     @SuppressLint("StaticFieldLeak")
     ConstraintLayout constraintLayout;
     @SuppressLint("StaticFieldLeak")
     TextView exploringButton;
     private Map<String, Object> mpMap2;
     private ArrayList<CartData> cartDataList;


     LinearLayout school_items , office_items , general_items;
     private List<List<String>> links;

     DatabaseReference ImageDataReference , CartDataReference;

     private ValueEventListener eventListener = new ValueEventListener() {
         @Override
         public void onDataChange(@NonNull DataSnapshot snapshot) {

             mpMap2 = (Map<String, Object>) snapshot.getValue();

             if (productDataList != null && mpMap2 != null)
                 manipulateData(productDataList , mpMap , cartDataList);
             else Log.d(TAG, "onDataChange: data is null");
         }

     @Override
         public void onCancelled(@NonNull DatabaseError error) {

         }
     };
     private ValueEventListener listener = new ValueEventListener() {
         @Override
         public void onDataChange(@NonNull DataSnapshot snapshot) {
             productDataList2 = new ArrayList<>();
             cartDataList = new ArrayList<>();
             keys= new ArrayList<>();

             Log.d(TAG, "onDataChange: called");

             Log.d(TAG, "onDataChange: " + snapshot.getValue());

             for (DataSnapshot snapshot1 : snapshot.getChildren()) {

                 keys.add(snapshot1.getKey());
                 cartDataList.add(snapshot1.getValue(CartData.class));
                 productDataList2.add(Objects.requireNonNull(snapshot1.getValue(CartData.class)).getProductData());

             }
             Log.d(TAG, "onDataChange: size  " + cartDataList.size());

             for (int i=0 ; i <cartDataList.size() ; i++)
             {
                 Log.d(TAG, "onDataChange: index" +cartDataList.get(i).getQuantity());
             }

             if (cartDataList.size() != 0)
             {
               recyclerView.setAdapter(new ShowProductAdapter(imageData, productDataList, context  , cartDataList , keys));
             }else {
                 recyclerView.setAdapter(new ShowProductAdapter(imageData , productDataList , context));
             }
             new CustomProgressDialogSpinKit(((Activity)(context))).dismissDialog();
             constraintLayout.setAlpha(1.0f);
             constraintLayout.setClickable(true);

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

         if (imageData.size() == cartDataList.size() && cartDataList.size() == productDataList2.size() && productDataList2.size() == keys.size())
         {
//             initiateRecyclerView(productDataList, imageData , cartDataList  ,keys);

             Log.d(TAG, "manipulateData: true");
             recyclerView.setHasFixedSize(true);
             ShowProductAdapter adapter = new ShowProductAdapter(imageData, productDataList, context  , cartDataList , keys);
             adapter.notifyDataSetChanged();
             recyclerView.setAdapter(adapter);

         }
         else {
             if (links != null) {
                 Log.d(TAG, "onDataChange: links size" + links.size());
             }

             Log.d(TAG, "onDataChange: " + productDataList.size());

         }
     }

     private void getDataFromDatabase(String user) {

         CustomProgressDialogSpinKit customProgressDialogSpinKit = new CustomProgressDialogSpinKit(((Activity)(context)));
         customProgressDialogSpinKit.startLoadingDialog();
         constraintLayout.setAlpha(0.4f);
         constraintLayout.setClickable(false);

         Log.d(TAG, "getDataFromDatabase: called");

         CartDataReference = FirebaseDatabase.getInstance().getReference("CartData");
         CartDataReference = CartDataReference.child(user);
         CartDataReference.addValueEventListener(listener);

     }

     public DataLoader(RecyclerView recyclerView, Context context, CustomProgressDialog customProgressDialog , ConstraintLayout constraintLayout , TextView exploringButton) {
         this.recyclerView = recyclerView;
         this.context = context;
         this.customProgressDialog = customProgressDialog;
         this.constraintLayout = constraintLayout;
         this.exploringButton = exploringButton;
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
     private void manipulateData(final List<ProductData> productDataList, Map<String, Object> mpMap) {

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


             school_items = ((Activity)(context)).findViewById(R.id.ll2);
             office_items = ((Activity)(context)).findViewById(R.id.ll1);
             general_items = ((Activity)(context)).findViewById(R.id.ll3);

             school_items.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {

                     Log.d(TAG, "onClick: " + "clicked");
                     List<ProductData> dataList = new ArrayList<>();

                     for (ProductData productData : productDataList)
                     {
                         Log.d(TAG, "onClick: " + productData.getCategory());
                         if (productData.getCategory().toLowerCase().equals("school"))
                         {
                             dataList.add(productData);
                         }
                     }

                     for (ProductData productData : productDataList)
                     {
                         Log.d(TAG, "onClick: " + productData.getCategory());
                         if (productData.getCategory().toLowerCase().equals("both") )
                         {
                             dataList.add(productData);
                         }
                     }

                     if (dataList.size() != 0)
                     {
                         Log.d(TAG, "onClick: data manipulation start...");

                         // manipulation process....

                         Map<String , Object> mpMap = getMpMap();
                         List<List<String>> imageData = new ArrayList<>();

                         if (mpMap != null) {
                             for (ProductData productData : new ArrayList<>(dataList)) {
                                 Map<String, Object> map1 = (Map<String, Object>) mpMap.get(productData.getCategory());
                                 if (map1 == null) {
                                     dataList.remove(productData);
                                 } else {
                                     Map<String, Object> map2 = (Map<String, Object>) map1.get(productData.getBrand());
                                     if (map2 == null) {
                                         dataList.remove(productData);
                                     } else {
                                         Map<String, Object> map3 = (Map<String, Object>) map2.get(productData.getType());
                                         if (map3 == null) {
                                             dataList.remove(productData);
                                         } else {

                                             Map<String, Object> map4 = (Map<String, Object>) map3.get(productData.getSize());
                                             if (map4 == null) {
                                                 dataList.remove(productData);
                                             } else {
                                                 Map<String, Object> map5 = (Map<String, Object>) map4.get(productData.getColor());
                                                 if (map5 == null) {
                                                     dataList.remove(productData);
                                                 } else {
                                                     Map<String, Object> map6 = (Map<String, Object>) map5.get(productData.getProductName());
                                                     if (map6 == null) {
                                                         dataList.remove(productData);
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
                         }

                         if (imageData.size() == dataList.size())
                         {
                             Intent intent = new Intent(context.getApplicationContext(), ShowProducts.class);
                             intent.putExtra("productDataList", new ArrayList<>(dataList));
                             intent.putExtra("image", new ArrayList<>(imageData));
                             context.startActivity(intent);

                         }
                     }else {
                         Log.d(TAG, "onClick: " + "size is zero....");
                     }
                 }
             });
             office_items.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {
                     Log.d(TAG, "onClick: " + "clicked");
                     List<ProductData> dataList = new ArrayList<>();

                     for (ProductData productData : productDataList)
                     {
                         Log.d(TAG, "onClick: " + productData.getCategory());
                         if (productData.getCategory().toLowerCase().equals("office"))
                         {
                             dataList.add(productData);
                         }
                     }

                     for (ProductData productData : productDataList)
                     {
                         Log.d(TAG, "onClick: " + productData.getCategory());
                         if (productData.getCategory().toLowerCase().equals("both") )
                         {
                             dataList.add(productData);
                         }
                     }
                     if (dataList.size() != 0)
                     {
                         Log.d(TAG, "onClick: data manipulation start...");

                         // manipulation process....

                         Map<String , Object> mpMap = getMpMap();
                         List<List<String>> imageData = new ArrayList<>();

                         if (mpMap != null) {
                             for (ProductData productData : new ArrayList<>(dataList)) {
                                 Map<String, Object> map1 = (Map<String, Object>) mpMap.get(productData.getCategory());
                                 if (map1 == null) {
                                     dataList.remove(productData);
                                 } else {
                                     Map<String, Object> map2 = (Map<String, Object>) map1.get(productData.getBrand());
                                     if (map2 == null) {
                                         dataList.remove(productData);
                                     } else {
                                         Map<String, Object> map3 = (Map<String, Object>) map2.get(productData.getType());
                                         if (map3 == null) {
                                             dataList.remove(productData);
                                         } else {

                                             Map<String, Object> map4 = (Map<String, Object>) map3.get(productData.getSize());
                                             if (map4 == null) {
                                                 dataList.remove(productData);
                                             } else {
                                                 Map<String, Object> map5 = (Map<String, Object>) map4.get(productData.getColor());
                                                 if (map5 == null) {
                                                     dataList.remove(productData);
                                                 } else {
                                                     Map<String, Object> map6 = (Map<String, Object>) map5.get(productData.getProductName());
                                                     if (map6 == null) {
                                                         dataList.remove(productData);
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
                         }

                         if (imageData.size() == dataList.size())
                         {
                             Intent intent = new Intent(context.getApplicationContext(), ShowProducts.class);
                             intent.putExtra("productDataList", new ArrayList<>(dataList));
                             intent.putExtra("image", new ArrayList<>(imageData));
                             context.startActivity(intent);

                         }
                     }else {
                         Log.d(TAG, "onClick: " + "size is zero....");
                     }
                 }
             });

             general_items.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {
                     Log.d(TAG, "onClick: " + "clicked");
                     List<ProductData> dataList = new ArrayList<>();

                     for (ProductData productData : productDataList)
                     {
                         Log.d(TAG, "onClick: " + productData.getCategory());
                         if (productData.getCategory().toLowerCase().equals("general") )
                         {
                             dataList.add(productData);
                         }
                     }

                     for (ProductData productData : productDataList)
                     {
                         Log.d(TAG, "onClick: " + productData.getCategory());
                         if (productData.getCategory().toLowerCase().equals("both") )
                         {
                             dataList.add(productData);
                         }
                     }

                     if (dataList.size() != 0)
                     {
                         Log.d(TAG, "onClick: data manipulation start...");

                         // manipulation process....

                         Map<String , Object> mpMap = getMpMap();
                         List<List<String>> imageData = new ArrayList<>();

                         if (mpMap != null) {
                             for (ProductData productData : new ArrayList<>(dataList)) {
                                 Map<String, Object> map1 = (Map<String, Object>) mpMap.get(productData.getCategory());
                                 if (map1 == null) {
                                     dataList.remove(productData);
                                 } else {
                                     Map<String, Object> map2 = (Map<String, Object>) map1.get(productData.getBrand());
                                     if (map2 == null) {
                                         dataList.remove(productData);
                                     } else {
                                         Map<String, Object> map3 = (Map<String, Object>) map2.get(productData.getType());
                                         if (map3 == null) {
                                             dataList.remove(productData);
                                         } else {

                                             Map<String, Object> map4 = (Map<String, Object>) map3.get(productData.getSize());
                                             if (map4 == null) {
                                                 dataList.remove(productData);
                                             } else {
                                                 Map<String, Object> map5 = (Map<String, Object>) map4.get(productData.getColor());
                                                 if (map5 == null) {
                                                     dataList.remove(productData);
                                                 } else {
                                                     Map<String, Object> map6 = (Map<String, Object>) map5.get(productData.getProductName());
                                                     if (map6 == null) {
                                                         dataList.remove(productData);
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
                         }

                         if (imageData.size() == dataList.size())
                         {
                             Intent intent = new Intent(context.getApplicationContext(), ShowProducts.class);
                             intent.putExtra("productDataList", new ArrayList<>(dataList));
                             intent.putExtra("image", new ArrayList<>(imageData));
                             context.startActivity(intent);

                         }
                     }else {
                         Log.d(TAG, "onClick: " + "size is zero....");
                     }
                 }
             });



             if (context != null) {
                 recyclerView.addItemDecoration(new DividerItemDecoration(context,
                         DividerItemDecoration.VERTICAL));

                 recyclerView.setHasFixedSize(true);

//             recyclerView.setAdapter(new ShowProductAdapter(imageData, productDataList, context  , cartDataList));

                 String name = new SettingMemoryData(context).getSharedPrefString(String.valueOf(R.string.USERNAME_KEY));
                 Log.d(TAG, "manipulateData: name" + name);
                 getDataFromDatabase(name);

             }

             loadProfileImage();
             Log.d(TAG, "manipulateData: dismissing the dialog box");

             if (customProgressDialog != null )
             customProgressDialog.dismissDialog();

         } else {
             Log.d(TAG, "manipulateData:  data not matching");
             Log.d(TAG, "manipulateData: " + imageData.size());
             Log.d(TAG, "manipulateData: " + productDataList.size());
         }


     }


     private void loadProfileImage() {

         String user = new SettingMemoryData(context).getSharedPrefString(String.valueOf(R.string.USERNAME_KEY));

         DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("ProfileImage");
         if (user != null) {
             databaseReference = databaseReference.child(user);
             databaseReference.addValueEventListener(new ValueEventListener() {
                 @Override
                 public void onDataChange(@NonNull DataSnapshot snapshot) {

                     String image = (String) snapshot.getValue();

                     if (context != null) {
                         Glide.with(((Activity) (context)).findViewById(R.id.profile_image))
                                 .load(image)
                                 .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                                 .into((CircleImageView) ((Activity) (context)).findViewById(R.id.profile_image));
                     }
                 }

                 @Override
                 public void onCancelled(@NonNull DatabaseError error) {
                     Log.d(TAG, "onCancelled: " + error.getMessage() + " " + error.getClass().getName());
                 }
             });
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


                     Search_view = ((Activity) context).findViewById(R.id.search_view);

                     Search_view.addTextChangedListener(new TextWatcher() {
                         @Override
                         public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                         }

                         @Override
                         public void onTextChanged(CharSequence s, int start, int before, int count) {
                             if (s.length() == 0) {
                                 Log.d(TAG, "onTextChanged: called");
                                 loadData();
                             }
                         }

                         @Override
                         public void afterTextChanged(Editable s) {

                         }
                     });

                     Search_view.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                         @SuppressLint("NewApi")
                         @Override
                         public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                             if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                                 Log.d(TAG, "onEditorAction: searching element");
                                 if (!Search_view.getText().toString().isEmpty()) {
                                     Log.d(TAG, "onEditorAction: " + Search_view.getText().toString().trim());
                                     Search_view.setActivated(false);

                                     productDataList = new SearchAlgorithm(Search_view.getText().toString().trim(), productDataList  , constraintLayout , exploringButton ).getData();
                                     getImageLinkData();
                                 }
                             }

                             return false;
                         }
                     });

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