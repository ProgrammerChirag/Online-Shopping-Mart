package com.selflearn.rpsstationary;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import AuthenticationAndLogin.AccountCreationForm;
import AuthenticationAndLogin.MobileAuthenticationActivity;
import DashBoard.DashBoardMainUI;
import MemoryManagement.SettingMemoryData;
import ModelClasses.UserData;
import in.codeshuffle.typewriterview.TypeWriterListener;
import in.codeshuffle.typewriterview.TypeWriterView;

public class SplashScreenActivity extends AppCompatActivity implements TypeWriterListener {

    String uID;
    UserData userData;
    private static final String TAG = "SplashScreenActivity";
    TypeWriterView writerView;

    DatabaseReference databaseReference;
    ValueEventListener listener = new ValueEventListener() {
         @Override
         public void onDataChange(@NonNull final DataSnapshot snapshot) {

             Log.d(TAG, "onDataChange: called");

             new Handler().postDelayed(new Runnable() {
                 @Override
                 public void run() {
//                     writerView.removeAnimation();
                     userData = snapshot.getValue(UserData.class);
                     if (userData != null)
                         changeActivity();
                     else {
                         finish();
                         startActivity(new Intent(SplashScreenActivity.this , AccountCreationForm.class));
                     }
                 }
             }, 4000);

         }
         @Override
         public void onCancelled(@NonNull DatabaseError error) {

         }
     };

    @Override
    protected void onStart() {

        super.onStart();

        Log.d(TAG, "onStart: called");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null)
        {
            Log.d(TAG, "onStart: user exists");
            uID = "user@"  + user.getPhoneNumber();
            Log.d(TAG, "onStart: " +uID);
            checkForFirebaseValue();
        }
        else
            ChangeActivityToLogin();
    }

    private void checkForFirebaseValue() {

        Log.d(TAG, "checkForFirebaseValue: called");
        databaseReference = FirebaseDatabase.getInstance().getReference("UserData");
        databaseReference = databaseReference.child(uID);
        databaseReference.addValueEventListener(listener);

        //databaseReference.removeEventListener(listener);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        // Call the function callInstamojo to start payment here

        writerView = findViewById(R.id.typeWriterEffect);
        //Setting each character animation delay
        writerView.setDelay(10);

        //Setting music effect On/Off
        writerView.setWithMusic(false);

        //Animating Text
        writerView.animateText("Rajasthan Pustak\n" +
                " Sadan Stationers");


//
//      writerView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
//          @Override
//          public void onViewAttachedToWindow(View view) {
//
//          }
//
//          @Override
//          public void onViewDetachedFromWindow(View view) {
//
//          }
//      });


    }

    private void changeActivity() {

        Log.d(TAG, "changeActivity: " + new SettingMemoryData(SplashScreenActivity.this).getSharedPrefString(String.valueOf(R.string.USERNAME_KEY)));

        if ( new SettingMemoryData(SplashScreenActivity.this).getSharedPrefString(String.valueOf(R.string.USERNAME_KEY)) == null)
        {
            FirebaseAuth.getInstance().signOut();
            finish();
            startActivity(new Intent(SplashScreenActivity.this , MobileAuthenticationActivity.class));
        }
        else {

            Log.d(TAG, "changeActivity: called");

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    finish();
                    startActivity(new Intent(SplashScreenActivity.this, DashBoardMainUI.class));
                }
            }, 2000);
        }
    }

    public void  ChangeActivityToLogin()
    {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
                startActivity(new Intent(SplashScreenActivity.this , MobileAuthenticationActivity.class));
            }
        },2000);
    }

    @Override
    public void onTypingStart(String text) {

    }

    @Override
    public void onTypingEnd(String text) {
        writerView.removeAnimation();
    }

    @Override
    public void onCharacterTyped(String text, int position) {

    }

    @Override
    public void onTypingRemoved(String text) {

    }
}
