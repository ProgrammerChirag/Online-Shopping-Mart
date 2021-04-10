package MultiProductActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.selflearn.rpsstationary.R;

import java.util.List;

import DashBoard.DashBoardMainUI;
import MemoryManagement.SettingMemoryData;
import ModelClasses.paymentDataClass;
import PaymentS.PaymentSuccess;
import Utils.CustomProgressDialog;

public class CashOnDeliveryActivity extends AppCompatActivity {

    Button exploreMore;
    paymentDataClass dataClass;
    private static final String TAG = "CashOnDeliveryActivity";
    List<paymentDataClass> dataClassList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_success);

        exploreMore = findViewById(R.id.explore_more);

        dataClass = (paymentDataClass) getIntent().getSerializableExtra("data");
        dataClassList = (List<paymentDataClass>) getIntent().getSerializableExtra("data2");


        exploreMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dataClass != null)
                {
                    // upload data of paymentDataclass

                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("paymentAndOrder");
                    databaseReference = databaseReference.child(new SettingMemoryData(CashOnDeliveryActivity.this).getSharedPrefString(String.valueOf(R.string.USERNAME_KEY)));
                    String key = databaseReference.push().getKey();
                    if (key != null) {
                        databaseReference = databaseReference.child(key);
                        databaseReference.setValue(dataClass).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                                Intent intent = new Intent(CashOnDeliveryActivity.this, PaymentSuccess.class);
                                intent.putExtra("data", dataClass);
                                startActivity(intent);

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getApplicationContext() , "currently we are not able to complete your transaction \n" +
                                        "your money will be refund in 24 hours" , Toast.LENGTH_LONG).show();

                                DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("Issue");
                                databaseReference1 = databaseReference1.child("paymentIssue").child(new SettingMemoryData(CashOnDeliveryActivity.this).getSharedPrefString(String.valueOf(R.string.USERNAME_KEY)));
                                String key = databaseReference1.push().getKey();
                                if (key != null) {
                                    databaseReference1 = databaseReference1.child(key).child("refund");
                                    databaseReference1.setValue(dataClass.getPaymentData()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Intent intent = new Intent(CashOnDeliveryActivity.this , DashBoardMainUI.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            Log.d(TAG, "onSuccess: changing activity");
                                            finish();
                                            startActivity(intent);
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


                }else if (dataClassList != null)
                {
                    // upload data of paymentDataClass List

                    CustomProgressDialog customProgressDialog = new CustomProgressDialog(CashOnDeliveryActivity.this);
                    customProgressDialog.startLoadingDialog();

                    new UploadData(dataClassList, CashOnDeliveryActivity.this , customProgressDialog).execute();

                }else {
                    Log.d(TAG, "onCreate: " + "data is null");
                }
            }
        });
    }
}