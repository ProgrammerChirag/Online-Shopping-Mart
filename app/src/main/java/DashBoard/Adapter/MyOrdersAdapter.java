package DashBoard.Adapter;

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
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.selflearn.rpsstationary.R;

import java.util.List;

import DashBoard.Rating_Activity;
import ModelClasses.FeedbackData;
import ModelClasses.paymentDataClass;

public class MyOrdersAdapter extends RecyclerView.Adapter<MyOrdersAdapter.OrderViewHolder> {

    int n;
    Context context;
    List<List<String>> images;
    List<paymentDataClass> paymentDataClassList;
    private static final String TAG = "MyOrdersAdapter";
    List<FeedbackData> feedbackDataList;

    public MyOrdersAdapter( Context context, List<List<String>> images, List<paymentDataClass> paymentDataClassList, List<FeedbackData> feedbackDataList) {
        this.context = context;
        this.images = images;
        this.paymentDataClassList = paymentDataClassList;
        this.feedbackDataList = feedbackDataList;
    }


    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new OrderViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(

                        R.layout.item_myorder , parent  , false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull final OrderViewHolder holder, final int position) {

       for (FeedbackData data : feedbackDataList)
       {
           Log.d(TAG, "onBindViewHolder: " + data.getProductData().getProductName());
           Log.d(TAG, "onBindViewHolder: " + paymentDataClassList.get(position).getProductData().getProductName());
           if (paymentDataClassList.get(position).getProductData().getProductName().equals(data.getProductData().getProductName()) && paymentDataClassList.get(position).getProductData().getOrderQuantity().equals(data.getProductData().getOrderQuantity())
           && paymentDataClassList.get(position).getProductData().getColor().equals(data.getProductData().getColor())
           )
           {
               holder.rateBtn.setEnabled(false);
               holder.rateBtn.setAlpha(0.5f);
               break;
           }
       }

        holder.rateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context.getApplicationContext() , Rating_Activity.class);
                intent.putExtra("orderData" , paymentDataClassList.get(position));
                context.startActivity(intent);
            }
        });

        Log.d(TAG, "onBindViewHolder: " + paymentDataClassList.get(position).getDeliveryAddress().getAddress());
        String[] addresses = paymentDataClassList.get(position).getDeliveryAddress().getAddress().split(",");

        StringBuilder resultantAddress = new StringBuilder();

        for (String Address : addresses)
        {
            resultantAddress.append(Address).append("\n");
        }

        Log.d(TAG, "onBindViewHolder: " + resultantAddress);
        holder.Address.setText(resultantAddress);
        holder.orderStatus.setText(String.format("Order Status : %s", paymentDataClassList.get(position).getOrderStatus()));

        holder.deliveryDate.setText(String.format("Delivery Date : %s", paymentDataClassList.get(position).getDeliveryDate()));
        holder.productName.setText(String.format("Product Name : %s", paymentDataClassList.get(position).getProductData().getProductName()));
        holder.orderQuantity.setText(String.format("Order Quantity : %s", paymentDataClassList.get(position).getQuantityOrdered()));
        holder.orderID.setText(String.format("Order ID :%s", paymentDataClassList.get(position).getPaymentData().getOrderID()));

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

    }

    @Override
    public int getItemCount() {
        if (paymentDataClassList != null)
        return paymentDataClassList.size();
        else return  n;

    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {

        TextView productName, orderStatus , orderQuantity , deliveryDate , orderID , Address;
        ImageView productImage;
        TextView rateBtn;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);

            productName = itemView.findViewById(R.id.productName);
            Address = itemView.findViewById(R.id.address);
            orderStatus = itemView.findViewById(R.id.orderStatus);
            orderQuantity = itemView.findViewById(R.id.quantity);
            deliveryDate = itemView.findViewById(R.id.orderDate);
            orderID = itemView.findViewById(R.id.orderID);
            productImage = itemView.findViewById(R.id.productImage);
            rateBtn = itemView.findViewById(R.id.rateBtn);
        }
    }
}
