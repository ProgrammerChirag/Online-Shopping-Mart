package MultiProductActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.selflearn.rpsstationary.R;

import java.util.ArrayList;
import java.util.List;

import DashBoard.DashBoardMainUI;
import MemoryManagement.SettingMemoryData;
import ModelClasses.paymentDataClass;
import Utils.CustomProgressDialog;

public class PaymentSuccess extends AppCompatActivity {

    List<paymentDataClass> dataClassList;
    private static final String TAG = "PaymentSuccess";
    Button exploreMore;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_success);

        dataClassList = (List<paymentDataClass>) getIntent().getSerializableExtra("data");

        if (dataClassList != null)
        {
            Log.d(TAG, "onCreate: " +"payment success uploading data");

            exploreMore = findViewById(R.id.explore_more);
            exploreMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    clearCartData(PaymentSuccess.this);

//                    Intent intent = new Intent(PaymentSuccess.this , DashBoardMainUI.class);
//                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                    Log.d(TAG, "onSuccess: changing activity");
//                    finish();
//                    startActivity(intent);





                }
            });
        }
    }

    private void clearCartData(final Activity activity) {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("CartData");
        databaseReference.child(new SettingMemoryData(activity).getSharedPrefString(String.valueOf(R.string.USERNAME_KEY)));
        databaseReference.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                if (!activity.isDestroyed())
                {
//            if (customProgressDialog != null)
//            customProgressDialog.dismissDialog();
                    Intent intent = new Intent(activity , DashBoardMainUI.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    Log.d(TAG, "onSuccess: changing activity");
                    activity.finish();
                    activity.startActivity(intent);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                if (!activity.isDestroyed())
                {
//            if (customProgressDialog != null)
//            customProgressDialog.dismissDialog();
                    Intent intent = new Intent(activity , DashBoardMainUI.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    Log.d(TAG, "onSuccess: changing activity");
                    activity.finish();
                    activity.startActivity(intent);
                }
            }

        });
    }

}

class UploadData extends AsyncTask<Context , Integer , Long>{

    List<paymentDataClass> dataClassList;
    @SuppressLint("StaticFieldLeak")
    Activity activity;
    private static final String TAG = "UploadData";
    CustomProgressDialog customProgressDialog;

    public UploadData(List<paymentDataClass> dataClassList, Activity activity, CustomProgressDialog customProgressDialog) {
        this.dataClassList = dataClassList;
        this.activity = activity;
        this.customProgressDialog = customProgressDialog;
    }

    public UploadData(){}

    @Override
    protected Long doInBackground(Context... contexts) {

        for (final paymentDataClass paymentData : dataClassList)
        {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("paymentAndOrder");
            databaseReference = databaseReference.child(new SettingMemoryData(activity).getSharedPrefString(String.valueOf(R.string.USERNAME_KEY)));
            String key = databaseReference.push().getKey();
            if (key != null) {
                databaseReference = databaseReference.child(key);
                databaseReference.setValue(paymentData).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        Log.d(TAG, "onSuccess: " + "data uploaded");


                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(activity.getApplicationContext() , "currently we are not able to complete your transaction \n" +
                                "your money will be refund in 24 hours" , Toast.LENGTH_LONG).show();

                        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("Issue");
                        databaseReference1 = databaseReference1.child("paymentIssue").child(new SettingMemoryData(activity).getSharedPrefString(String.valueOf(R.string.USERNAME_KEY)));
                        String key = databaseReference1.push().getKey();
                        if (key != null) {
                            databaseReference1 = databaseReference1.child(key).child("refund");
                            databaseReference1.setValue(paymentData.getPaymentData()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

//                                    if (customProgressDialog != null)
//                                        customProgressDialog.dismissDialog();

                                    Intent intent = new Intent(activity , DashBoardMainUI.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    Log.d(TAG, "onSuccess: changing activity");
                                    activity.finish();
                                    activity.startActivity(intent);



                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, "onFailure: " + e.getMessage() + " " +e.getClass().getName());
                                }
                            });
                        }

                    }
                });
            }
        }



        return null;
    }

    @Override
    protected void onPostExecute(Long aLong) {


        Intent intent = new Intent(activity, PaymentSuccess.class);
        intent.putExtra("data", new ArrayList<>(dataClassList));
        activity.startActivity(intent);


//     clearCartData(activity);

        super.onPostExecute(aLong);
    }

}
