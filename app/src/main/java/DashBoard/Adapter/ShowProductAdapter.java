package DashBoard.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.selflearn.rpsstationary.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import DashBoard.ActivityCart;
import DashBoard.ActivityProductDetail;
import ModelClasses.CartData;
import MemoryManagement.SettingMemoryData;
import ModelClasses.ProductData;

public class ShowProductAdapter extends RecyclerView.Adapter<ShowProductAdapter.ProductHolder> {

    int n;
    int arr[];
    private static final String TAG = "ShowProductAdapter";

    List<List<String>> links ;
    List<ProductData> productDataList;
    Context context;
    List<List<Bitmap>> bitmaps ;
    List<CartData> cartDataList;
    List<String> keys;
    private int num;
    CartData cartData;
    DatabaseReference databaseReference;
    ValueEventListener listener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {

            for (DataSnapshot snapshot1 : snapshot.getChildren())
            {
                String  key = snapshot1.getKey();
                if (key_set.equals(key))
                {
                    Log.d(TAG, "onDataChange: data matched");
                    DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("CartData").
                            child(new SettingMemoryData(context).getSharedPrefString(String.valueOf(R.string.USERNAME_KEY))) .child(key);
                    cartData.setQuantity(String.valueOf(num));
                    databaseReference1.setValue(cartData).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "onSuccess: " + "data updated");
                            databaseReference.removeEventListener(listener);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "onFailure: " + e.getMessage() + " " + e.getClass().getName() );
                        }
                    });
                    break;
                }else {
                    Log.d(TAG, "onDataChange: data not matched");
                }
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };
    private String key_set;


    public ShowProductAdapter(int numProducts , Context context) {
        this.arr  = new int[numProducts];
        Log.d(TAG, "ShowProductAdapter: " + numProducts);
        Log.d(TAG, "ShowProductAdapter: " + arr.length);
        this.context = context;

    }

//    public ShowProductAdapter(List<List<String>> lists , List<ProductData> productDataList , Context context ,List<List<Bitmap>> bitmaps  , List<CartData> cartDataList) {
//        this.cartDataList = cartDataList;
//        this.productDataList = productDataList;
//        this.links = lists;
//        this.context =context;
//        this.bitmaps = bitmaps;
//        Log.d(TAG, "ShowProductAdapter: " + bitmaps.size());
//
//    }

    public ShowProductAdapter(List<List<String>> lists , List<ProductData> productDataList , Context context)
    {
        this.productDataList = productDataList;
        this.links = lists;
        this.context =context;
        n = 50;
        arr = new int[n];
    }

    public ShowProductAdapter(List<List<String>> lists, List<ProductData> productDataList, Context context, List<CartData> cartDataList , List<String> keys) {
        this.keys = keys;
        this.cartDataList = cartDataList;
        this.productDataList = productDataList;
        this.links = lists;
        this.context =context;
//        Log.d(TAG, "ShowProductAdapter: " + bitmaps.size());
        Log.d(TAG, "ShowProductAdapter: links" + lists.size() + "productDataList" + productDataList.size());
    }

    @NonNull
    @Override
    public ProductHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new ProductHolder(
                LayoutInflater.from(parent.getContext()).
                        inflate(R.layout.layout_product_show , parent , false)
        );
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final ProductHolder holder, final int position) {


        if (cartDataList!= null && cartDataList.size() > 0) {

            for (int i=0 ; i < cartDataList.size() ; i++) {

                cartData = cartDataList.get(i);
                key_set = keys.get(i);

                ProductData productData = productDataList.get(position);

                if (cartData.getProductData().getCategory().equals(productData.getCategory()) &&
                        cartData.getProductData().getSellingPrice().equals(productData.getSellingPrice()) &&
                        cartData.getProductData().getProductName().equals(productData.getProductName()) &&
                        cartData.getProductData().getBrand().equals(productData.getBrand()) &&
                        cartData.getProductData().getMRP().equals(productData.getMRP())
                ) {
                    holder.AddToBag.setVisibility(View.INVISIBLE);
                    holder.elegantNumberButton.setVisibility(View.VISIBLE);
                    holder.elegantNumberButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            context.startActivity(new Intent(context , ActivityCart.class));

                        }
                    });

                    break;
                }else Log.d(TAG, "onBindViewHolder: not matched");

            }
        }
        Log.d(TAG, "onBindViewHolder: called");

        holder.cuttedPrice.setText(productDataList.get(position).getMRP());
        holder.cuttedPrice.setPaintFlags(holder.cuttedPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        holder.SellingPrice.setText("PRICE : " +productDataList.get(position).getSellingPrice() + " \u20B9");
        holder.discount.setText(productDataList.get(position).getDiscount() + " rs.");


        Log.d(TAG, "onBindViewHolder: " + links.get(position).size());

        Runnable runnable = new Runnable() {

            @Override
            public void run() {

                Glide.with(context.getApplicationContext())
                        .asBitmap()
                        .load(links.get(position).get(0))
                        .into(new BitmapImageViewTarget(holder.imageView) {
                            @Override
                            protected void setResource(Bitmap resource) {
                                //Play with bitmap
                                super.setResource(resource);
                            }
                        });
            }
        };

        Thread thread = new Thread(runnable);
        thread.start();

        //holder.imageView.setImageBitmap(bitmaps.get(position).get(0));

//        Log.d(TAG, "onBindViewHolder:  " + links.get(position).get(0));


        holder.productName.setText(productDataList.get(position).getProductName());


        holder.AddToBag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // adding data to bag.
                // this code only for testing purpose.

                ProductData productData = productDataList.get(position);
                AddDataToCart(productData);
            }
        });

        holder.constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context , ActivityProductDetail.class);
                Log.d(TAG, "onClick: " + links.get(position).size());
                ArrayList<String> linkData = (ArrayList<String>) links.get(position);

                if (linkData != null)
                {
                    intent.putExtra("images" , linkData);
                    intent.putExtra("obj" , productDataList.get(position));
                    ((Activity)(context)).startActivity(intent);
                }
            }
        });
    }

    private void uploadQuantity(String number) {

         num = Integer.parseInt(number);
        Log.d(TAG, "uploadQuantity: called");

        databaseReference = FirebaseDatabase.getInstance().getReference("CartData");
        Log.d(TAG, "uploadQuantity: "+new SettingMemoryData(context).getSharedPrefString(String.valueOf(R.string.USERNAME_KEY)));
        databaseReference = databaseReference.child(new SettingMemoryData(context).getSharedPrefString(String.valueOf(R.string.USERNAME_KEY)));
        databaseReference.addValueEventListener(listener);
    }


    @Override
    public int getItemCount() {

        if (links != null)
        return links.size();
    else return arr.length;

    }

    public class ProductHolder extends RecyclerView.ViewHolder {

        Button AddToBag;
        TextView cuttedPrice;
        TextView SellingPrice;
        ImageView imageView;
        TextView discount;
        TextView productName;
        ConstraintLayout constraintLayout;
        Button elegantNumberButton;

        public ProductHolder(@NonNull View itemView) {
            super(itemView);
            // initializing .
            AddToBag = itemView.findViewById(R.id.AddToCart);
            cuttedPrice = itemView.findViewById(R.id.cuttedprice);
            cuttedPrice.setPaintFlags(cuttedPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

            SellingPrice = itemView.findViewById(R.id.Price);
            discount = itemView.findViewById(R.id.discount);
            imageView = itemView.findViewById(R.id.product_images);
            productName = itemView.findViewById(R.id.product_name);
            constraintLayout = itemView.findViewById(R.id.constraint_layout5);
            elegantNumberButton = itemView.findViewById(R.id.goToCart);
        }
    }

    private void AddDataToCart(ProductData productData)
    {
        CartData cartData;
        cartData = new CartData(productData , "1");

        if (productData != null)
        {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("CartData");
            String user = new SettingMemoryData(context).getSharedPrefString(String.valueOf(R.string.USERNAME_KEY));
            if (user != null)
            {
                databaseReference = databaseReference.child(user);

                String dirName;
                Date today = new Date();
                @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a");
                dirName = format.format(today);

                databaseReference = databaseReference.child(dirName);

                databaseReference.setValue(cartData).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        context.startActivity(new Intent(context , ActivityCart.class));

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: "+e.getClass().getName() + " " + e.getMessage());


                    }
                });
            }
        }
    }

}

