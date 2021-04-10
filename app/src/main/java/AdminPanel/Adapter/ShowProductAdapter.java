package AdminPanel.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.makeramen.roundedimageview.RoundedImageView;
import com.selflearn.rpsstationary.R;

import java.util.ArrayList;
import java.util.List;

import AdminPanel.ShowProductDetail;
import ModelClasses.ProductData;


public class ShowProductAdapter extends RecyclerView.Adapter<ShowProductAdapter.ViewHolder> {

    List<List<String>> images;
    List<ProductData> productDataList;
    Context context;
    private static final String TAG = "ShowProductAdapter";

    public ShowProductAdapter(List<List<String>> images, List<ProductData> productDataList, Context context) {

        Log.d(TAG, "ShowProductAdapter: called");
        this.images = images;
        this.productDataList = productDataList;
        this.context = context;
        Log.d(TAG, "ShowProductAdapter: " + productDataList.size());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        Log.d(TAG, "onCreateViewHolder:  " + productDataList.size());

        return new ViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.fragment_product , parent , false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {


        Log.d(TAG, "onBindViewHolder: called" + position);

        holder.link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context , ShowProductDetail.class);
                Log.d(TAG, "onClick: " + images.get(position).size());
                ArrayList<String> linkData = (ArrayList<String>) images.get(position);
                if (linkData != null)
                {
                    intent.putExtra("images" , linkData);
                    intent.putExtra("obj" , productDataList.get(position));
                    ((Activity)(context)).startActivity(intent);
                }
            }
        });

        ((Activity)(context)).runOnUiThread(new Runnable() {
            @Override
            public void run() {

                Glide.with(context.getApplicationContext())
                        .asBitmap()
                        .load(images.get(position).get(0))
                        .into(new BitmapImageViewTarget(holder.imageView) {
                            @Override
                            protected void setResource(Bitmap resource) {
                                //Play with bitmap
                                super.setResource(resource);
                            }
                        });
            }
        });

//        Thread thread = new Thread(runnable);
//        thread.start();

        holder.name.setText(productDataList.get(position).getProductName());

        holder.price.setText(String.format("Price : %srs", productDataList.get(position).getSellingPrice()));




    }

    @Override
    public int getItemCount() {
        Log.d(TAG, "getItemCount: " + productDataList.size());
        return productDataList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        RoundedImageView  imageView;
        TextView name;
        TextView price;
        ConstraintLayout link;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            Log.d(TAG, "ViewHolder: called");

            imageView = itemView.findViewById(R.id.productImage);
            name = itemView.findViewById(R.id.productName);
            price = itemView.findViewById(R.id.Price);
            link = itemView.findViewById(R.id.cardView);

        }
    }
}
