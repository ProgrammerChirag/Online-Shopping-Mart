package MultiProductActivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.selflearn.rpsstationary.R;

import java.util.List;

import ModelClasses.CartData;
import ModelClasses.DeliveryAddress;

public class ProductDetailAdapter extends RecyclerView.Adapter<ProductDetailAdapter.ProductViewHolder> {

    List<CartData> cartDataList;
    List<List<String>> images;
    Context context;
    DeliveryAddress deliveryAddress;

    public ProductDetailAdapter(List<CartData> cartDataList, List<List<String>> images, Context context, DeliveryAddress deliveryAddresses) {

        this.cartDataList = cartDataList;
        this.images = images;
        this.context = context;
        this.deliveryAddress = deliveryAddresses;

    }

    public ProductDetailAdapter(){}

    public ProductDetailAdapter(List<CartData> cartDataList, List<List<String>> images, Context context) {

        this.cartDataList = cartDataList;
        this.images = images;
        this.context = context;

    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return  new ProductViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(

                        R.layout.item_myorder , parent  , false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull final ProductViewHolder holder, final int position) {
        Runnable runnable = new Runnable() {

            @Override
            public void run() {

                Glide.with(context.getApplicationContext())
                        .asBitmap()
                        .load(images.get(position).get(0))
                        .into(new BitmapImageViewTarget(holder.productImage) {
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

        holder.deliveryDate.setText(String.format("Delivery Date : %s", "in next few working days"));
        holder.productName.setText(String.format("Product Name : %s", cartDataList.get(position).getProductData().getProductName()));
        holder.orderQuantity.setText(String.format("Order Quantity : %s", cartDataList.get(position).getQuantity()));

        String[] address = deliveryAddress.getAddress().split(",");
        StringBuilder resultantAddress = new StringBuilder();

        for (String Address : address)
        {
            resultantAddress.append(Address).append("\n");
        }

        holder.Address.setText(resultantAddress);
        holder.orderID.setText(String.format("Price : %s", Float.parseFloat(cartDataList.get(position).getProductData().getSellingPrice()) * Integer.parseInt(cartDataList.get(position).getQuantity())));

    }

    @Override
    public int getItemCount() {

        if (cartDataList != null)
        {
            return cartDataList.size();
        }
        else return  0;
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {

        TextView productName, orderStatus , orderQuantity , deliveryDate , orderID , Address;
        ImageView productImage;
        TextView rateBtn;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);

            // finding all id's of text view..

            productName = itemView.findViewById(R.id.productName);
            Address = itemView.findViewById(R.id.address);
            orderStatus = itemView.findViewById(R.id.orderStatus);
            orderQuantity = itemView.findViewById(R.id.quantity);
            deliveryDate = itemView.findViewById(R.id.orderDate);
            orderID = itemView.findViewById(R.id.orderID);
            productImage = itemView.findViewById(R.id.productImage);
            rateBtn = itemView.findViewById(R.id.rateBtn);

            //Address.setVisibility(View.GONE);
            //orderID.setVisibility(View.GONE);
            orderStatus.setVisibility(View.GONE);
            rateBtn.setVisibility(View.GONE);

        }
    }
}
