package AuthenticationAndLogin;

import android.accounts.AccountAuthenticatorActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.chaos.view.PinView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthSettings;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.selflearn.rpsstationary.R;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import DashBoard.DashBoardMainUI;
import MemoryManagement.SettingMemoryData;
import ModelClasses.UserData;
import Utils.CustomDialogMaker;
import Utils.CustomProgressDialog;

public class MobileAuthenticationActivity extends AppCompatActivity {

    public static String varID;
    public static String code;
    public static PhoneAuthCredential credential;
    public static PinView pinView;
    Button Login;
    EditText Phone;
    private static final String TAG = "MobileAuthenticationAct";
    CustomProgressDialog customProgressDialog;
    UserData userData;
    DatabaseReference databaseReference;
    ValueEventListener listener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {

            for (DataSnapshot snapshot1 : snapshot.getChildren()) {

                Log.d(TAG, "onDataChange: called");

                userData = snapshot1.getValue(UserData.class);
                if (userData != null && Phone != null) {
                    Log.d(TAG, "onDataChange: data is not null");

                    if (userData.getPhoneNumber().equals("+91"+Phone.getText().toString().trim())) {

                        setSharedPref(userData);
                        databaseReference.removeEventListener(listener);

                    }
                }else {
                    Toast.makeText(getApplicationContext() , "please try again or check all details" , Toast.LENGTH_LONG).show();
                }
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };

    private void setSharedPref(UserData userData) {

        if (userData != null) {
            SettingMemoryData settingMemoryData = new SettingMemoryData(MobileAuthenticationActivity.this);

            settingMemoryData.setSharedPrefString(String.valueOf(R.string.ADDRESS_KEY), userData.getAddress());
            settingMemoryData.setSharedPrefString(String.valueOf(R.string.NAME_KEY), userData.getName());
            settingMemoryData.setSharedPrefString(String.valueOf(R.string.PHONE_KEY), userData.getPhoneNumber());
            settingMemoryData.setSharedPrefString(String.valueOf(R.string.USERNAME_KEY), userData.getUserName());

            changeActivity();
            }
        }

    private void changeActivity() {

        finish();
        startActivity(new Intent(MobileAuthenticationActivity.this , DashBoardMainUI.class));

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_mobile_auth);

        customProgressDialog = new CustomProgressDialog(MobileAuthenticationActivity.this);
        pinView = findViewById(R.id.pinView);
        Login = findViewById(R.id.getOTPBtn);
        Phone = findViewById(R.id.phone);

        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Log.d(TAG, "onClick: login process start...");
                if (Phone.getText().toString().trim().length() == 10)
                {
                    if (customProgressDialog != null)
                    customProgressDialog.startLoadingDialog();
                    getOTP();
                    Phone.setError(null);
                }
                else {
                    Phone.setError("please enter phone number");
                    Phone.requestFocus();
                }

            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void getOTP() {

        Log.d(TAG, "getOTP: getting otp");

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        FirebaseAuthSettings firebaseAuthSettings = firebaseAuth.getFirebaseAuthSettings();

       // firebaseAuthSettings.setAutoRetrievedSmsCodeForPhoneNumber("+919001851307" , "146752");
        //firebaseAuthSettings.setAutoRetrievedSmsCodeForPhoneNumber("+919982917736","917736");
        firebaseAuthSettings.setAutoRetrievedSmsCodeForPhoneNumber("+919829064583" , "917736");
//        firebaseAuthSettings.setAutoRetrievedSmsCodeForPhoneNumber("+918764498357" , "917736");
//        firebaseAuthSettings.setAutoRetrievedSmsCodeForPhoneNumber("+917976330044" , "991245");
//        firebaseAuthSettings.setAutoRetrievedSmsCodeForPhoneNumber("+918824135146" , "998291" );
//        firebaseAuthSettings.setAutoRetrievedSmsCodeForPhoneNumber("+919413609362" , "135790");
//        firebaseAuthSettings.setAutoRetrievedSmsCodeForPhoneNumber("+917985025413" , "073527");
//        firebaseAuthSettings.setAutoRetrievedSmsCodeForPhoneNumber("+919694533137" , "867542");

        PhoneAuthProvider.OnVerificationStateChangedCallbacks mcallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                Log.d(TAG, "onVerificationCompleted: verification completed");

                String sms = phoneAuthCredential.getSmsCode();

                assert sms != null;
                Log.d("code", String.valueOf(phoneAuthCredential));


//                credential = phoneAuthCredential;
//                code = credential.getSmsCode();

                if(phoneAuthCredential.getSmsCode() == null){
//                    customProgressDialog.startLoadingDialog();
                }
                else {
                    pinView.setText(phoneAuthCredential.getSmsCode());
                }

                signInWithCredentials(phoneAuthCredential);
            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);

                Log.d("code sent ", "code sent"+s);


            }

            @Override
            public void onCodeAutoRetrievalTimeOut(@NonNull String s) {

                Log.d(TAG, "onCodeAutoRetrievalTimeOut: "+s);

                if (customProgressDialog != null)
                customProgressDialog.dismissDialog();
//                super.onCodeAutoRetrievalTimeOut(s);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Log.d("error", Objects.requireNonNull(e.getMessage()));
            }
        };

        PhoneAuthProvider.getInstance().verifyPhoneNumber(

                "+91"+Phone.getText().toString().trim(),
                60,
                TimeUnit.SECONDS,
                MobileAuthenticationActivity.this,
                mcallbacks
        );

    }

    private void signInWithCredentials(PhoneAuthCredential phoneAuthCredential) {

        Log.d(TAG, "signInWithCredentials: called");

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signInWithCredential(phoneAuthCredential).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                if(Objects.requireNonNull(authResult.getAdditionalUserInfo()).isNewUser())
                {
                    // change activity to form UI
                    Toast.makeText(getApplicationContext() , "congratulation" , Toast.LENGTH_LONG).show();
                    finish();
                    startActivity(new Intent(MobileAuthenticationActivity.this , AccountCreationForm.class));

                }else
                {
                    // change activity to dashboard UI.
                    Toast.makeText(getApplicationContext() , "welcome Back" , Toast.LENGTH_LONG).show();
//                  Utils.ActivityNavigation.startSellerDashBoard(SellerLoginMobileAuth.this , "seller");
                    retrieveData();

                }
            }


        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                customProgressDialog.dismissDialog();
                new CustomDialogMaker(MobileAuthenticationActivity.this).createAndShowDialogWarning("something went wrong please try again");
                Log.d(TAG, "onFailure: " + e.getMessage() + " " + e.getClass().getName());
            }
        });
    }

    private void retrieveData() {

        Log.d(TAG, "retrieveData: called");
        databaseReference = FirebaseDatabase.getInstance().getReference("UserData");
        databaseReference.addValueEventListener(listener);

//        databaseReference.removeEventListener(listener);

    }
}

// 17 8 9 8
//  9 7 10 7
