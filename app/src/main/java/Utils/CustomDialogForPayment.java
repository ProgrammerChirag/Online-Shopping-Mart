package Utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.selflearn.rpsstationary.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import DashBoard.ActivityCart;
import ModelClasses.CartData;
import ModelClasses.ImageData;
import MultiProductActivity.ChooseAddressActivity;

public class CustomDialogForPayment  {

    Activity activity;
    Dialog dialog;
    Drawable drawable;
    List<CartData> cartDataList;
    private static final String TAG = "CustomDialogForPayment";
    Float totalAmount = 0f ;
    float deliveryCharge ;
    List<List<String>> links;


    @SuppressLint("UseCompatLoadingForDrawables")
    public CustomDialogForPayment(Activity activity , List<CartData> cartDataList , List<List<String>> links)
    {
        this.activity = activity;
        this.cartDataList = cartDataList;
        drawable=activity.getDrawable(R.drawable.custom_dialog_style);
        this.links = links;

        for (CartData cartData : cartDataList)
        {
            totalAmount = totalAmount + ( Float.parseFloat(cartData.getProductData().getSellingPrice()) * Float.parseFloat(cartData.getQuantity()));
        }

        Log.d(TAG, "CustomDialogForPayment: " + totalAmount);

    }

    public void startDialog(){

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();

        Button button ;
        View view = inflater.inflate(R.layout.pay_now_custom_dialog,null);

        button = view.findViewById(R.id.pay_now_cartBtn);
        if (button != null)
        {
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {



                    // start process of payment...
//
                    Intent intent = new Intent(activity , ChooseAddressActivity.class);
                    intent.putExtra("CartData" , new ArrayList<>(cartDataList));
                    intent.putExtra("charge" , deliveryCharge);
                    List<ImageData> imageDataList = new ArrayList<>();

                    Log.d(TAG, "onActive: link" + links.size());

                    for (List<String> data : links)
                    {
                        ImageData imageData = new ImageData(data);
                        Log.d(TAG, "onActive: " + imageData.getImageData().size());
                        imageDataList.add(imageData);
                    }

                    Log.d(TAG, "onActive: " + imageDataList.size());

                    intent.putExtra("images" , new ArrayList<>(imageDataList));
                    dismissDialog();
                    activity.finish();
                    activity.startActivity(intent);

                }
            });
        }
        builder.setView(view);


        TextView textView = view.findViewById(R.id.payableAmount);
        if (textView != null)
        {
            if (totalAmount < 500)
            {
                deliveryCharge = 40;
            }
            else{
                deliveryCharge = 0;
            }

            textView.setText(String.format("Total Amount to Pay :\n\nAmount : %s\nDelivery Charge : %s\nAmount to Pay : %s", totalAmount, deliveryCharge, (totalAmount + deliveryCharge)));
        }

        builder.setCancelable(false);

        dialog=builder.create();

        Objects.requireNonNull(dialog.getWindow()).getAttributes().windowAnimations = R.style.CustomDialogAnimation;
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(drawable);
        dialog.show();

        if (dialog.isShowing())
        {
            Log.d(TAG, "startLoadingDialog: showing");
        }


    }

    public void dismissDialog()
    {
        Log.d(TAG, "dismissDialog: called");

        if (dialog != null && dialog.isShowing() && !activity.isDestroyed()) {
            dialog.dismiss();
            dialog = null;
        }
    }

}
