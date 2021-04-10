package Utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AlertDialog;

import com.selflearn.rpsstationary.R;

import java.util.Objects;


import static com.selflearn.rpsstationary.R.drawable.certified_icon2;

public class CustomDialogMaker {

    Activity activity;
    String password ;
    AlertDialog dialog;
     androidx.appcompat.app.AlertDialog.Builder builder;

    public CustomDialogMaker(Activity activity)
    {
        this.activity = activity;


    }

    public void createAndShowDialogSuccess(String message , final String ActivityName )
    {
        builder = new androidx.appcompat.app.AlertDialog.Builder(activity);
      ImageView Success_view = new ImageView(activity);
      Success_view.setImageDrawable(activity.getDrawable(R.drawable.certified_icon2));

      builder.setTitle("Success").setMessage(message).setPositiveButton("Done", new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
             dialog.dismiss();
//             change Activity

          }
      }).setCancelable(false).setView(Success_view);

      dialog = builder.create();
        Objects.requireNonNull(dialog.getWindow()).getAttributes().windowAnimations = R.style.CustomDialogAnimation;
      Objects.requireNonNull(dialog.getWindow()).
              setBackgroundDrawable( activity.getApplicationContext().getDrawable(R.drawable.custom_dialog_background_style) );
      dialog.show();
    }

    public void createAndShowDialogWarning(String message)
    {
        builder = new androidx.appcompat.app.AlertDialog.Builder(activity);
        builder.setTitle("warning");
        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setIcon(R.drawable.warning_icon);

        builder.setPositiveButton("done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        dialog = builder.create();
        Objects.requireNonNull(dialog.getWindow()).
                setBackgroundDrawable( activity.getApplicationContext().getDrawable(R.drawable.custom_dialog_background_style) );
        dialog.show();
    }

    public void dismissDialog()
    {
        if(dialog.isShowing() && dialog != null)
        dialog.dismiss();
    }

    public void EnterCurrentPassword(final String DataType)
    {

        builder = new AlertDialog.Builder(activity);
        builder.setTitle("password")
                .setMessage("please enter your current password")
                ;
        final EditText editText = new EditText(activity);
        editText.setBackground(activity.getDrawable(R.drawable.edit_text_style));
        editText.setHint("please enter your current password");
        editText.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
//        editText.setPadding(100,10,100,10);
        editText.setWidth(150);
//        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//        lp.setMargins(20, 10, 20, 10);
//        editText.setLayoutParams(lp);
        editText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

        builder.setView(editText);
        builder.setCancelable(false);

        builder.setPositiveButton("done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(editText.getText().toString().length()<10)
                {
                    editText.setError("please enter valid password");
                    editText.requestFocus();
                }
                else
                {
                    password = editText.getText().toString().trim();
//                    new CustomProgressDialog(activity).startLoadingDailog();


                    //new UserDataHandler(activity).checkPasswordAndChangeActivity(password , DataType);

                }
            }
        }).setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        dialog = builder.create();
        Objects.requireNonNull(dialog.getWindow()).
                setBackgroundDrawable( activity.getApplicationContext().getDrawable(R.drawable.custom_dialog_background_style) );
        dialog.show();

    }

    public static int convertPixelsToDp(int px, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        int dp = px / (metrics.densityDpi / 160);
        return dp;
    }

    public void createAndShowDialogWarningWithoutCancel(String message)
    {
        builder = new androidx.appcompat.app.AlertDialog.Builder(activity);
        builder.setTitle("warning");
        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setIcon(R.drawable.warning_icon);

        builder.setPositiveButton("done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
            }
        });

        dialog = builder.create();
        Objects.requireNonNull(dialog.getWindow()).
                setBackgroundDrawable( activity.getApplicationContext().getDrawable(R.drawable.custom_dialog_background_style) );
        dialog.show();
    }
}
