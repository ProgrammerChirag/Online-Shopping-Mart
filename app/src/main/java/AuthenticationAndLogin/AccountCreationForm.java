package AuthenticationAndLogin;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.selflearn.rpsstationary.R;

import java.util.Calendar;

import DashBoard.DashBoardMainUI;
import MemoryManagement.SettingMemoryData;
import ModelClasses.UserData;
import Utils.CustomProgressDialog;

public class AccountCreationForm extends AppCompatActivity {



    Spinner spinner;
    private static final String TAG = "AccountCreationForm";
    EditText name , email , address;
    TextView dob;
    Button login_button;
    DatabaseReference databaseReference ;
    String user_name  , user_email , user_address , Username , phone_number , profession , birthDate;

    CustomProgressDialog customProgressDialog;



    @Override
    protected void onStart() {

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null)
        {
         phone_number = firebaseUser.getPhoneNumber();
         if (phone_number != null)
         {
             Log.d(TAG, "onStart: "+phone_number);
         }
        }
        super.onStart();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_after_mobile_auth);

        findID();
        setSpinner();

        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateForm())
                {
                    // showing to user that form is validated
                    Toast.makeText(getApplicationContext() , "form is fully filled",Toast.LENGTH_LONG).show();
                    // starting loading dialog.
                    customProgressDialog.startLoadingDialog();
                    // sending data to database server.
                    sendDataToDataBase();
                    // completion of login process.
//                    loginWithData();
                    //openDashBoard();

                }
                else
                    Log.d(TAG, "onClick:  form is not validated");
            }
        });

        dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                int date = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);

                DatePickerDialog pickerDialog = new DatePickerDialog(AccountCreationForm.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                dob.setText(String.format("%s/%s/%s", dayOfMonth, (month + 1), year));
                            }
                        }, year , month , date
                );
                pickerDialog.show();
            }
        });

    }

//    private void openDashBoard() {
//        customProgressDialog.dismissDialog();
//        finish();
//        startActivity(new Intent(AccountCreationForm.this , DashBoardMainUI.class));
//    }

    private void sendDataToDataBase() {

        profession = spinner.getSelectedItem().toString();
        user_email = email.getText().toString().trim();
        user_name = name.getText().toString().trim();
        user_address = address.getText().toString().trim();
        Username = "user" + "@" + phone_number;
        birthDate = dob.getText().toString().trim();

        UserData userData = new UserData(user_address, user_name , phone_number , profession , Username , birthDate , user_email);

        databaseReference = FirebaseDatabase.getInstance().getReference("UserData");
        Log.d(TAG, "sendDataToDataBase: "+userData.getUserName());
        databaseReference.child(userData.getUserName() ).setValue(userData).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "onSuccess: "+"data uploaded on database ");
                saveMemoryData();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: "+e.getMessage() + " " +e.getClass().getName());
                customProgressDialog.dismissDialog();
            }
        });

    }

    private void saveMemoryData() {

        SettingMemoryData settingMemoryData = new SettingMemoryData(AccountCreationForm.this);

        settingMemoryData.setSharedPrefString(String.valueOf(R.string.ADDRESS_KEY) , user_address);
        settingMemoryData.setSharedPrefString(String.valueOf(R.string.NAME_KEY) , user_name);
        settingMemoryData.setSharedPrefString(String.valueOf(R.string.PHONE_KEY) , phone_number);
        settingMemoryData.setSharedPrefString(String.valueOf(R.string.USERNAME_KEY) , Username);

        changeActivity();

    }

    private void changeActivity() {
        // changing activity to choose category type.
        customProgressDialog.dismissDialog();

        finish();
        startActivity(new Intent(AccountCreationForm.this , DashBoardMainUI.class));
    }

//    private void loginWithData() {
//
//    }

    private boolean validateForm() {

        if(name.getText().toString().isEmpty()) {

            name.setError("please fill the name");
            name.requestFocus();
            return  false;
        }
        else{
            if(dob.getText().toString().isEmpty()) {

                dob.setError("please fill the birth date");
                dob.requestFocus();
                return  false;}
            else{
                if(email.getText().toString().isEmpty()) {

                    email.setError("please fill the email");
                    email.requestFocus();
                    return  false;}
                else{
                    if(address.getText().toString().isEmpty()){
                        address.setError("please fill your address");
                        address.requestFocus();
                        return false;}
                    else{
                       return true;
                    }
                }
            }
        }

    }

    private void setSpinner() {

        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(this, R.array.spinner_elements , android.R.layout.simple_spinner_item);

        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);
        spinner.setSelection(0);


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(position == 0){
                    TextView errorText = (TextView)spinner.getSelectedView();
                    if (errorText != null) {
                        errorText.setError("");
                        errorText.setTextColor(Color.RED);//just to highlight that this is an error
                        errorText.setText("please choose your profession first");
                    }else {
                        spinner.setSelection(1);
                    }
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void findID() {

        spinner = findViewById(R.id.spinner);
        name = findViewById(R.id.name);
        dob = findViewById(R.id.dob);
        email = findViewById(R.id.email);
        address = findViewById(R.id.delivery_address);
        login_button=findViewById(R.id.login_btn);
        customProgressDialog = new CustomProgressDialog(AccountCreationForm.this);
    }

    @Override
    protected void onDestroy() {
        customProgressDialog.dismissDialog();
        customProgressDialog = null;
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {

        customProgressDialog.dismissDialog();
        customProgressDialog = null;
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            firebaseUser.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    finish();
                    startActivity(new Intent(AccountCreationForm.this , MobileAuthenticationActivity.class));
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "onFailure: "+e.getMessage() + " " + e.getClass().getName());
                }
            });
        }

    }
}
