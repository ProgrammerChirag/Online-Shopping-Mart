package DashBoard;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.makeramen.roundedimageview.RoundedImageView;
import com.selflearn.rpsstationary.R;

import java.util.Objects;

import MemoryManagement.SettingMemoryData;
import ModelClasses.FeedbackData;
import ModelClasses.paymentDataClass;

public class Rating_Activity extends AppCompatActivity {

    TextView submit;
    EditText feedback;
    RatingBar ratingBar;
    float rating_data;
    TextView productName;
    RoundedImageView productImage;
    TextView ratingTag;
    Toolbar toolbar;
    //PaymentData paymentData;
    paymentDataClass paymentDataClass;
    private static final String TAG = "Rating_Activity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.feedback_and_rating_layout);

        //paymentData = (PaymentData) getIntent().getSerializableExtra("orderData");
        paymentDataClass = (ModelClasses.paymentDataClass) getIntent().getSerializableExtra("orderData");
        if (paymentDataClass != null)
        findID();

    }

    private void findID() {


        submit = findViewById(R.id.done);
        feedback = findViewById(R.id.feedBack);
        ratingBar = findViewById(R.id.ratingBar);
        ratingTag = findViewById(R.id.ratingTag);
        productName = findViewById(R.id.productName);
        productImage = findViewById(R.id.productImage);
        toolbar = findViewById(R.id.toolbar13);

        rating_data = ratingBar.getRating();

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                rating_data = rating;

                if (rating == 0)
                {
                    Log.d(TAG, "onRatingChanged: " + "rating is empty");
                }else if(rating == 1)
                {
                    ratingTag.setText("worst");
                }else if(rating == 2)
                {
                    ratingTag.setText("Bad");
                }else if (rating == 3)
                {
                    ratingTag.setText("Average");
                }else if (rating == 4)
                {
                    ratingTag.setText("Good");
                }else if (rating == 5)
                {
                    ratingTag.setText("Best");
                }

            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rating_data == 0)
                {
                    Toast.makeText(getApplicationContext() , "please rate at least one" , Toast.LENGTH_LONG).show();

                }else {
                    String feedBack = feedback.getText().toString().trim();

                    String orderID = paymentDataClass.getPaymentData().getOrderID();

                    if (orderID != null) {
                        FeedbackData data = new FeedbackData(feedBack , rating_data , orderID , paymentDataClass.getProductData());

                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("FeedbackAndRating");
                        String user = new SettingMemoryData(Rating_Activity.this).getSharedPrefString(String.valueOf(R.string.USERNAME_KEY));
                        databaseReference= databaseReference.child(user);
                        databaseReference = databaseReference.child(Objects.requireNonNull(databaseReference.push().getKey()));

                        databaseReference.setValue(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "onSuccess: " + "data added success");
                                onBackPressed();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG, "onFailure: " + e.getClass().getName()+ " " + e.getMessage());
                            }
                        });
                    }
                }
            }
        });
    }
}
