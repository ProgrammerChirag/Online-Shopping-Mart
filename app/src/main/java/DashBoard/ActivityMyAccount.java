package DashBoard;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.selflearn.rpsstationary.R;

import MemoryManagement.SettingMemoryData;
import ModelClasses.UserData;
import de.hdodenhof.circleimageview.CircleImageView;

public class ActivityMyAccount extends AppCompatActivity {

    TextView Name;
    String user;
    CircleImageView circleImageView;
    private static final String TAG = "ActivityMyAccount";
    Toolbar toolbar;
    LinearLayout editAddress , shareApp , rateThisApp , getHelp , callUS , mailUS;
    ImageButton editProfile , cart , myOrders;

    DatabaseReference databaseReference ;
    ValueEventListener listener  = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {

            UserData userData = snapshot.getValue(UserData.class);

            if (userData != null)
            {
                setData(userData );
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };

    private void setData(UserData userData) {

        Name.setText(String.format("HI , %s", userData.getName()));
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("ProfileImage");
        databaseReference = databaseReference.child(user);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String link = (String) snapshot.getValue();

                Glide.with(circleImageView)
                .load(link)
                .override(Target.SIZE_ORIGINAL , Target.SIZE_ORIGINAL)
                .into(circleImageView);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "onCancelled: " + error.getClass().getName() + ""  + error.getMessage());
                Toast.makeText(getApplicationContext() , "unable to load image please retry" , Toast.LENGTH_LONG).show();
            }
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account);


        findID();
        getData();

    }

    private void findID() {

        Name = findViewById(R.id.userName);
        circleImageView = findViewById(R.id.profile_image);
        toolbar = findViewById(R.id.toolbar9);

        editAddress = findViewById(R.id.changeAddress);

        shareApp = findViewById(R.id.shareApp);
        rateThisApp = findViewById(R.id.rateApp);
        getHelp = findViewById(R.id.getHelp);
        callUS = findViewById(R.id.callUs);
        mailUS = findViewById(R.id.mail_us);

        editProfile = findViewById(R.id.edit_profile);
        cart = findViewById(R.id.cart);
        myOrders = findViewById(R.id.my_orders);



        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        editProfile.setClickable(true);
        editAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Context context = ActivityMyAccount.this;

                //finish();
                Log.d(TAG, "onClick: called");
                Intent intent = new Intent(context , ActivityEditAddress.class);
                startActivity(intent);

            }
        });

        shareApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        rateThisApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        getHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        callUS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        mailUS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ActivityMyAccount.this , ActivityEditProfile.class));
            }
        });

        cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ActivityMyAccount.this , ActivityCart.class));

            }
        });

        myOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ActivityMyAccount.this , MyOrderActivity.class));

            }
        });
    }

    private void getData() {

         user = new SettingMemoryData(ActivityMyAccount.this).getSharedPrefString(String.valueOf(R.string.USERNAME_KEY));


        databaseReference = FirebaseDatabase.getInstance().getReference("UserData");
        databaseReference = databaseReference.child(user);
        databaseReference.addValueEventListener(listener);
    }
}