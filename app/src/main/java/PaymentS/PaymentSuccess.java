package PaymentS;

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

import DashBoard.DashBoardMainUI;
import MemoryManagement.SettingMemoryData;

public class PaymentSuccess extends AppCompatActivity {

    Button exploreMore;
    private static final String TAG = "PaymentSuccess";
     ModelClasses.paymentDataClass paymentDataClass ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_success);


        paymentDataClass = (ModelClasses.paymentDataClass) getIntent().getSerializableExtra("data");

        exploreMore = findViewById(R.id.explore_more);
        exploreMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                finish();
//                startActivity(new Intent(PaymentSuccess.this , DashBoardMainUI.class));

//                // upload payment Data.
//
//                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("paymentAndOrder");
//                databaseReference = databaseReference.child(new SettingMemoryData(PaymentSuccess.this).getSharedPrefString(String.valueOf(R.string.USERNAME_KEY)));
//                String key = databaseReference.push().getKey();
//                if (key != null) {
//                    databaseReference = databaseReference.child(key);
//                    databaseReference.setValue(paymentDataClass).addOnSuccessListener(new OnSuccessListener<Void>() {
//                        @Override
//                        public void onSuccess(Void aVoid) {
//                            Intent intent = new Intent(PaymentSuccess.this , DashBoardMainUI.class);
//                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                            Log.d(TAG, "onSuccess: changing activity");
//                            finish();
//                            startActivity(intent);
//                        }
//                    }).addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            Toast.makeText(getApplicationContext() , "currently we are not able to complete your transaction \n" +
//                                    "your money will be refund in 24 hours" , Toast.LENGTH_LONG).show();
//
//                            DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("Issue");
//                            databaseReference1 = databaseReference1.child("paymentIssue").child(new SettingMemoryData(PaymentSuccess.this).getSharedPrefString(String.valueOf(R.string.USERNAME_KEY)));
//                            String key = databaseReference1.push().getKey();
//                            if (key != null) {
//                                databaseReference1 = databaseReference1.child(key).child("refund");
//                                databaseReference1.setValue(paymentDataClass.getPaymentData()).addOnSuccessListener(new OnSuccessListener<Void>() {
//                                    @Override
//                                    public void onSuccess(Void aVoid) {
//                                        Intent intent = new Intent(PaymentSuccess.this , DashBoardMainUI.class);
//                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                                        Log.d(TAG, "onSuccess: changing activity");
//                                        finish();
//                                        startActivity(intent);
//                                    }
//                                }).addOnFailureListener(new OnFailureListener() {
//                                    @Override
//                                    public void onFailure(@NonNull Exception e) {
//                                        Log.d(TAG, "onFailure: " + e.getMessage() + " " +e.getClass().getName());
//                                    }
//                                });
//                            }
//
//                        }
//                    });
//                }

                Intent intent = new Intent(PaymentSuccess.this , DashBoardMainUI.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                Log.d(TAG, "onSuccess: changing activity");
                finish();
                startActivity(intent);



            }
        });
    }
}