package FirebaseConnectivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthSettings;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import AuthenticationAndLogin.AccountCreationForm;
import AuthenticationAndLogin.MobileAuthenticationActivity;
import DashBoard.DashBoardMainUI;
import Utils.CustomProgressDialog;

public class MobileAuthenticationHelper {

    private Context context;
    CustomProgressDialog customProgressDialog;

    public MobileAuthenticationHelper(Context context){
        this.context = context;
        customProgressDialog = new CustomProgressDialog((Activity)context);
    }

    public  void loginWithCredentials(PhoneAuthCredential credential) {

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful() && task.getResult() != null){

                    customProgressDialog.dismissDialog();

                    if(task.getResult().getAdditionalUserInfo().isNewUser()){

                        Toast.makeText(((Activity)(context)).getApplicationContext(),"Congratulation new user",Toast.LENGTH_SHORT).show();

                        ((Activity)(context)).finish();
                        ((Activity)(context)).startActivity(new Intent(((Activity)(context)), AccountCreationForm.class));
                    }
                    else
                    {
                        Toast.makeText(((Activity)(context)).getApplicationContext(),"welcome back user",Toast.LENGTH_SHORT).show();

                        ((Activity)(context)).finish();
                        ((Activity)(context)).startActivity(new Intent(((Activity)(context)), DashBoardMainUI.class));
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("error", Objects.requireNonNull(e.getMessage()));
            }
        });
    }

    public void getOTP(String number) {

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        FirebaseAuthSettings firebaseAuthSettings = firebaseAuth.getFirebaseAuthSettings();

        firebaseAuthSettings.setAutoRetrievedSmsCodeForPhoneNumber("+919982917736","917736");

        PhoneAuthProvider.OnVerificationStateChangedCallbacks m_call_backs;

        m_call_backs = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);

                MobileAuthenticationActivity.varID = s;
                Log.d("code sent ","Code Sent Success"+MobileAuthenticationActivity.varID);

            }

            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

                MobileAuthenticationActivity.code = phoneAuthCredential.getSmsCode();

                MobileAuthenticationActivity.credential = phoneAuthCredential;
                Log.d("progress","credential created");

                assert MobileAuthenticationActivity.code != null;
                Log.d("OTP",MobileAuthenticationActivity.code);
                MobileAuthenticationActivity.pinView.setText(MobileAuthenticationActivity.code);

                if(  (phoneAuthCredential!= null) ) {

//                    CustomProgressDialog customProgressDialog = new CustomProgressDialog((Activity) context);
                    customProgressDialog.startLoadingDialog();
                    loginWithCredentials(phoneAuthCredential);

                }
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {

                Log.d("error", Objects.requireNonNull(e.getMessage()));
            }
        };

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                number,
                30,
                TimeUnit.SECONDS,
                ((Activity)(context)),
                m_call_backs
        );


    }

    public void sendDataToDataBase( String phone_number){

    }

}
