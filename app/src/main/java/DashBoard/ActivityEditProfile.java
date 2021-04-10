package DashBoard;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.selflearn.rpsstationary.R;

import java.util.Calendar;

import FirebaseConnectivity.StorageDevice;
import MemoryManagement.SettingMemoryData;
import ModelClasses.UserData;
import Utils.CustomProgressDialog;
import de.hdodenhof.circleimageview.CircleImageView;

public class ActivityEditProfile extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1234;
    EditText name , email , address;
    TextView dob;
    Spinner spinner;
    private static final String TAG = "ActivityEditProfile";
    DatabaseReference databaseReference ;
    String user_name  , user_email , user_address , Username , phone_number , profession , birthDate;
    CustomProgressDialog customProgressDialog;
    ImageButton done;
    CircleImageView circleImageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        findID();
        setSpinner();

    }

    private void findID() {

        done =findViewById(R.id.done);
        spinner = findViewById(R.id.spinner);
        name = findViewById(R.id.name);
        dob = findViewById(R.id.dob);
        email = findViewById(R.id.email);
        address = findViewById(R.id.delivery_address);
        customProgressDialog = new CustomProgressDialog(ActivityEditProfile.this);
        circleImageView = findViewById(R.id.profile_image);

        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get image from gallery.
                selectImage();

            }
        });

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (validateForm())
                {
                    sendDataToDatabse();
                }
            }
        });

        dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                int date = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);

                DatePickerDialog pickerDialog = new DatePickerDialog(ActivityEditProfile.this,
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

    private void sendDataToDatabse() {

        phone_number = new SettingMemoryData(ActivityEditProfile.this).getSharedPrefString(String.valueOf(R.string.PHONE_KEY));
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

    @Override
    protected void onDestroy() {
        customProgressDialog.dismissDialog();
        customProgressDialog = null;
        super.onDestroy();
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
//                    errorText.setError("");
//                    errorText.setTextColor(Color.RED);//just to highlight that this is an error
                    errorText.setText("please choose your profession first");
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private boolean validateForm() {

//        user_name = name.getText().toString().trim();
//        user_address = address.getText().toString();
//        phone_number = new SettingMemoryData(ActivityEditProfile.this).getSharedPrefString(String.valueOf(R.string.PHONE_KEY));


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

    private void saveMemoryData() {

        SettingMemoryData settingMemoryData = new SettingMemoryData(ActivityEditProfile.this);

        settingMemoryData.setSharedPrefString(String.valueOf(R.string.ADDRESS_KEY) , user_address);
        settingMemoryData.setSharedPrefString(String.valueOf(R.string.NAME_KEY) , user_name);
        settingMemoryData.setSharedPrefString(String.valueOf(R.string.PHONE_KEY) , phone_number);
        settingMemoryData.setSharedPrefString(String.valueOf(R.string.USERNAME_KEY) , Username);

        changeActivity();

    }

    private void changeActivity() {
        finish();
        onBackPressed();
    }

    private void selectImage() {
        // Defining Implicit Intent to mobile gallery
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(
                Intent.createChooser(
                        intent,
                        "Select Image from here..."),
                PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {


        if (requestCode == PICK_IMAGE_REQUEST
                && resultCode == RESULT_OK
                && data != null
                && data.getData() != null)
        {
            Uri filePath = data.getData();

            try {

                // Setting image on image view using Bitmap
                Bitmap bitmap = MediaStore
                        .Images
                        .Media
                        .getBitmap(
                                getContentResolver(),
                                filePath);
                circleImageView.setImageBitmap(bitmap);


               StorageDevice.UploadUserProfilePicture(
                       new SettingMemoryData(ActivityEditProfile.this).getSharedPrefString(String.valueOf(R.string.USERNAME_KEY)),
                               ActivityEditProfile.this ,filePath
               );

//                customProgressDialog.dismissDialog();

            }catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}