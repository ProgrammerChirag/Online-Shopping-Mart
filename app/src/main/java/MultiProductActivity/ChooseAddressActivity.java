package MultiProductActivity;

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
import DashBoard.ActivityCart;
import DashBoard.Adapter.ShowAddressAdapter;
import ModelClasses.CartData;
import MemoryManagement.SettingMemoryData;
import ModelClasses.DeliveryAddress;
import ModelClasses.ImageData;
import ModelClasses.ProductData;


public class ChooseAddressActivity extends AppCompatActivity {

    List<CartData> cartDataList;
    List<ImageData> imageData;
    List<List<String>> images;
    Button AddAddress;
    private static final String TAG = "ActivityChooseAddress";
    List<DeliveryAddress> deliveryAddresses;
    ViewPager2 slider;
    DotsIndicator dotsIndicator;
    DatabaseReference databaseReference;
    Button setPayment;
    Toolbar toolbar;
    ProductData productData;


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
                slider.setAdapter( new ShowAddressAdapter(ChooseAddressActivity.this , deliveryAddresses));
                dotsIndicator.setViewPager2(slider);

                setPayment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Log.d(TAG, "onClick: " + slider.getCurrentItem());

                        int i = slider.getCurrentItem();
                        //Log.d(TAG, "onClick: " + productData.getOrderQuantity())


                        DeliveryAddress deliveryAddress = deliveryAddresses.get(i);
                        Log.d(TAG, "onClick: " + deliveryAddress.getAddress());

                        Intent intent = new Intent(ChooseAddressActivity.this, ProductDetailsPage.class);

                        intent.putExtra("CartData", new ArrayList<>(cartDataList));
                        intent.putExtra("DeliveryAddress", deliveryAddress);
                        intent.putExtra("images", new ArrayList<>(imageData));
                        intent.putExtra("charge", getIntent().getFloatExtra("charge", 0));

                        startActivity(intent);

                    }
                });
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
            Log.d(TAG, "onCancelled: "+ error.getMessage() + " " + error.getClass().getName());
            Toast.makeText(getApplicationContext() , "something went wrong" , Toast.LENGTH_LONG).show();
            onBackPressed();
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_address);

        fetchData();


        toolbar = findViewById(R.id.toolbar8);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        cartDataList = (List<CartData>) getIntent().getSerializableExtra("CartData");
        imageData = (List<ImageData>)getIntent().getSerializableExtra("images");



        images = new ArrayList<>();
        for (ImageData imageData1: imageData)
        {
            List<String> image = imageData1.getImageData();
            images.add(image);
        }

        toolbar = findViewById(R.id.toolbar8);
        setPayment = findViewById(R.id.setPaymentBtn);
        dotsIndicator = findViewById(R.id.dot);
        deliveryAddresses = new ArrayList<>();
        AddAddress= findViewById(R.id.AddAddress);
        slider = findViewById(R.id.address_slider);

        if (cartDataList == null || images == null)
        {
            setPayment.setVisibility(View.INVISIBLE);
            onBackPressed();
        }else{
            AddAddress.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // adding payment to database.
                    Intent intent = new Intent(ChooseAddressActivity.this , TakingAddressActivity.class);
                    startActivityForResult(intent , 1234);

                }
            });


            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }
    }

    private void fetchData() {
        String user;
        user = new SettingMemoryData(ChooseAddressActivity.this).getSharedPrefString(String.valueOf(R.string.USERNAME_KEY));
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


                AlertDialog dialog;
                Address = data.getStringExtra("address");
                name = data.getStringExtra("name");
                Phone = data.getStringExtra("number");

                if (Address != null && name != null && Phone != null) {

                    Log.d(TAG, "onActivityResult: " + Address);

                    AlertDialog.Builder builder = new AlertDialog.Builder(ChooseAddressActivity.this);
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
                            uploadData(deliveryAddress , new SettingMemoryData(ChooseAddressActivity.this)
                                    .getSharedPrefString(String.valueOf(R.string.USERNAME_KEY)));
                            slider.setAdapter(new ShowAddressAdapter(ChooseAddressActivity.this , deliveryAddresses));
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

    private void uploadData(DeliveryAddress  deliveryAddress, String user) {

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

    @Override
    public void onBackPressed() {

        Intent intent = new Intent(ChooseAddressActivity.this , ActivityCart.class);
        finish();
        startActivity(intent);

        //super.onBackPressed();
    }
}
