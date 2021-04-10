package DashBoard.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.makeramen.roundedimageview.RoundedImageView;
import com.selflearn.rpsstationary.R;

import java.util.ArrayList;
import java.util.List;

import DashBoard.ActivityChooseAddress;
import ModelClasses.CartData;
import MemoryManagement.SettingMemoryData;
import ModelClasses.ProductData;

public class CartProductAdapter extends RecyclerView.Adapter<CartProductAdapter.CartViewHolder> {


    List<ProductData> productDataList;
    List<List<String>> links;
    Context context;
    private static final String TAG = "CartProductAdapter";
    TextView amount;
    float totalAmount = 0;
    List<CartData> cartDataList;
    List<String> keys;
    int num;
    String key_set;
    CartData cartData;
    DatabaseReference databaseReference;
    private ValueEventListener listener = new ValueEventListener() {
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
            Log.d(TAG, "onCancelled: " + error.getMessage() + " " + error.getClass().getName() );
        }
    };


    public CartProductAdapter(List<ProductData> list, List<List<String>> links, Context context, TextView amount, List<CartData> cartDataList, List<String> keys)
    {
        productDataList = list;
        this.context = context;
        this.links = links;
        this.amount = amount;
        this.keys = keys;
        this.amount = ((Activity)(context)).findViewById(R.id.amount_text);

        totalAmount = 0;

        for (int i = 0; i < productDataList.size() ; i++)
        {
            totalAmount = totalAmount+ Float.parseFloat(productDataList.get(i).getSellingPrice()) * Integer.parseInt(cartDataList.get(i).getQuantity());
        }

        this.cartDataList = cartDataList;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new CartViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.item_layout_cart , parent , false
                )
        );
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onBindViewHolder(@NonNull final CartViewHolder holder, final int position) {


            Log.d(TAG, "onBindViewHolder: " + cartDataList.get(position).getQuantity());

            holder.elegantNumberButton.setNumber(String.valueOf(cartDataList.get(position).getQuantity()));

            holder.elegantNumberButton.setOnValueChangeListener(new ElegantNumberButton.OnValueChangeListener() {
                @SuppressLint("DefaultLocale")
                @Override
                public void onValueChange(ElegantNumberButton view, int oldValue, int newValue) {

                    if (newValue > oldValue)
                    {
                        // plus button clicked.....
                        if (newValue > Integer.parseInt(productDataList.get(position).getOrderQuantity()))
                        {
                            holder.elegantNumberButton.setNumber(productDataList.get(position).getOrderQuantity());
                        }else if (newValue == 0)
                        {
                            Log.d(TAG, "onValueChange: true");
                            totalAmount = 0;
                            cartDataList.get(position).setQuantity("0");

                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("CartData");
                            databaseReference = databaseReference.child(new SettingMemoryData(context).getSharedPrefString(String.valueOf(R.string.USERNAME_KEY)));
                            databaseReference = databaseReference.child(keys.get(position));
                            databaseReference.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "onSuccess: " + "data removed success...");
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, "onFailure: " + "data removing failed.");
                                    Toast.makeText(context.getApplicationContext() , "something went wrong" , Toast.LENGTH_LONG).show();
                                    holder.elegantNumberButton.setNumber("1");
                                }
                            });


                        }else {

                            Log.d(TAG, "onValueChange plus clicked.: changing value");
                            holder.quantity.setText(String.format("Quantity: %d", newValue));


                            cartData = cartDataList.get(position);
                            key_set = keys.get(position);
                            uploadQuantity(String.valueOf(newValue));
                        }

                    }else if (newValue < oldValue)
                    {
                        Log.d(TAG, "onValueChange: "+"minus clicked");
                        // minus button clicked.
                        if (newValue > Integer.parseInt(productDataList.get(position).getOrderQuantity()))
                        {
                            holder.elegantNumberButton.setNumber(productDataList.get(position).getOrderQuantity());
                        }else if (newValue == 0)
                        {


                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("CartData");
                            databaseReference = databaseReference.child(new SettingMemoryData(context).getSharedPrefString(String.valueOf(R.string.USERNAME_KEY)));
                            databaseReference = databaseReference.child(keys.get(position));
                            databaseReference.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "onSuccess: " + "data removed success...");
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, "onFailure: " + "data removing failed.");
                                    Toast.makeText(context.getApplicationContext() , "something went wrong" , Toast.LENGTH_LONG).show();
                                    holder.elegantNumberButton.setNumber("1");
                                }
                            });


                        }else {
                            Log.d(TAG, "onValueChange: minus clicked changing value");
                            holder.quantity.setText(String.format("Quantity: %d", newValue));

                            cartData = cartDataList.get(position);
                            key_set = keys.get(position);
                            uploadQuantity(String.valueOf(newValue));
                        }
                    }
                }
            });

            holder.amount.setText(String.format("%s INR", String.format("Amount : %s", productDataList.get(position).getSellingPrice())));

            holder.quantity.setText(String.format("Quantity :%s", holder.elegantNumberButton.getNumber()));

            holder.productName.setText(productDataList.get(position).getProductName());

            Glide.with(holder.productImage)
                    .load(links.get(position).get(0))
                    .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                    .placeholder(context.getDrawable(R.drawable.product_image))
                    .centerCrop()
                    .into(holder.productImage);

            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // change activity to detail page.


                }
            });

            holder.buyNowBtn.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {

                                                        holder.buyNowBtn.setBackground(context.getDrawable(R.drawable.item_btn_style2));
                                                        holder.buyNowBtn.setTextColor(Color.WHITE);

                                                        new Handler().postDelayed(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                holder.buyNowBtn.setBackground(context.getDrawable(R.drawable.item_btn_style3));
                                                                holder.buyNowBtn.setTextColor(context.getColor(R.color.textcolor));
                                                            }
                                                        }, 200);

                                                        // process to take to the product shopping choose address

                                                        Intent intent = new Intent(context.getApplicationContext(), ActivityChooseAddress.class);
                                                        intent.putExtra("ProductData", productDataList.get(position));
                                                        intent.putExtra("images", new ArrayList<>(links.get(position)));
                                                        context.startActivity(intent);

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

        return productDataList.size();
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {

        RoundedImageView productImage;
        TextView productName;
        TextView quantity;
        TextView amount;
        ElegantNumberButton elegantNumberButton;
        CardView cardView;
        TextView buyNowBtn;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);

            productImage = itemView.findViewById(R.id.profile_image);
            productName = itemView.findViewById(R.id.productName);
            quantity = itemView.findViewById(R.id.quantity);
            amount = itemView.findViewById(R.id.Amount);
            elegantNumberButton = itemView.findViewById(R.id.elegantNumberButton);
            cardView = itemView.findViewById(R.id.cardView);
            buyNowBtn = itemView.findViewById(R.id.buy_now);
        }
    }
}
