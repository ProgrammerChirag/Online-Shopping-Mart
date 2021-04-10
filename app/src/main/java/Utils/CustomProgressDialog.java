package Utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;

import com.selflearn.rpsstationary.R;

import java.util.Objects;

public class CustomProgressDialog{

    Activity activity;
    AlertDialog dialog;
    Drawable drawable;

    private static final String TAG = "CustomProgressDialog";


    @SuppressLint("UseCompatLoadingForDrawables")
    public CustomProgressDialog(Activity activity){
        this.activity = activity;

        drawable=activity.getDrawable(R.drawable.custom_dialog_style);

    }

    @SuppressLint("InflateParams")
    public void startLoadingDialog(){

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();

        builder.setView(inflater.inflate(R.layout.custom_dialog,null));

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

    public void dismissDialog(){

        Log.d(TAG, "dismissDialog: called");

        if (dialog != null && dialog.isShowing() && !activity.isDestroyed()) {
            dialog.dismiss();
            dialog = null;
        }
    }

}