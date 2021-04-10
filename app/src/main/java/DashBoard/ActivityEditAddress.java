package DashBoard;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.selflearn.rpsstationary.R;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import AddressAndMap.TakingAddressActivity;
import DashBoard.Adapter.ShowAddressAdapter;
import MemoryManagement.SettingMemoryData;
import ModelClasses.DeliveryAddress;

public class ActivityEditAddress extends AppCompatActivity {

    private static final String TAG = "ActivityEditAddress";

    List<DeliveryAddress> deliveryAddresses;
    ViewPager2 slider;
    DotsIndicator dotsIndicator;
    DatabaseReference databaseReference;
    Button setPayment;
    Toolbar toolbar;
    Button AddAddress;


    ValueEventListener listener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {

            deliveryAddresses = new ArrayList<>();

            for (DataSnapshot snapshot1 : snapshot.getChildren())
            {
                deliveryAddresses.add(snapshot1.getValue(DeliveryAddress.class));
            }
            if (deliveryAddresses != null && deliveryAddresses.size() != 0)
            {

                slider.setAdapter( new ShowAddressAdapter(ActivityEditAddress.this , deliveryAddresses));
                dotsIndicator.setViewPager2(slider);
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
            Log.d(TAG, "onCancelled: "+ error.getMessage() + " " + error.getClass().getName());
            Toast.makeText(getApplicationContext() , "something went wrong" , Toast.LENGTH_LONG).show();
        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_address);

        fetchData();


        toolbar = findViewById(R.id.toolbar8);
        setPayment = findViewById(R.id.setPaymentBtn);
        dotsIndicator = findViewById(R.id.dot);
        deliveryAddresses = new ArrayList<>();
        AddAddress= findViewById(R.id.AddAddress);
        slider = findViewById(R.id.address_slider);

        AddAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // adding payment to database.
                Intent intent = new Intent(ActivityEditAddress.this , TakingAddressActivity.class);
                startActivityForResult(intent , 1234);

            }
        });

        setPayment.setVisibility(View.GONE);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


    }

    private void fetchData() {

        String user;
        user = new SettingMemoryData(ActivityEditAddress.this).getSharedPrefString(String.valueOf(R.string.USERNAME_KEY));
        databaseReference = FirebaseDatabase.getInstance().getReference("DeliveryAddress");
        databaseReference = databaseReference.child(user);
        databaseReference.addValueEventListener(listener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == 1234)
        {
            if (data !=null) {
                final String Address;
                final String name;
                final String Phone;

                Log.d(TAG, "onActivityResult: called");

                AlertDialog dialog;
                Address = data.getStringExtra("address");
                name = data.getStringExtra("name");
                Phone = data.getStringExtra("number");

                if (Address != null && name != null && Phone != null) {

                    Log.d(TAG, "onActivityResult: " + Address);

                    AlertDialog.Builder builder = new AlertDialog.Builder(ActivityEditAddress.this);
                    builder.setIcon(R.drawable.certified_icon2);

                    builder.setPositiveButton("Add address", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();

                            DeliveryAddress deliveryAddress = new DeliveryAddress(Address , name , Phone);
                            if (deliveryAddresses ==null)
                            {
                                deliveryAddresses = new ArrayList<>();
                            }
                            deliveryAddresses.add(deliveryAddress);

                            Log.d(TAG, "onClick: " + deliveryAddresses.size());
                            uploadData(deliveryAddress , new SettingMemoryData(ActivityEditAddress.this)
                                    .getSharedPrefString(String.valueOf(R.string.USERNAME_KEY)));
                            slider.setAdapter(new ShowAddressAdapter(ActivityEditAddress.this , deliveryAddresses));
                            dotsIndicator.setViewPager2(slider);
                        }
                    });
                    TextView textView = (TextView) getLayoutInflater().inflate(R.layout.textview, null);
                    if (textView != null) {

                        textView.setText(String.format("Address :\n%s\nName: %s\nPhone: %s", Address, name, Phone));
                        builder.setView(textView);

                        builder.setCancelable(false);

                        dialog = builder.create();
                        dialog.show();
                    }  // nothing will happen

                }
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void uploadData(DeliveryAddress deliveryAddress, String user) {

        if (user != null)
        {

                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("DeliveryAddress");
                databaseReference = databaseReference.child(user);
                databaseReference = databaseReference.child(Objects.requireNonNull(databaseReference.push().getKey()));

                databaseReference.setValue(deliveryAddress).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "onSuccess: address added");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: error adding data");
                        Log.d(TAG, "onFailure: " + e.getMessage() + "" +e.getClass().getName());
                    }
                });

        }
    }
}

