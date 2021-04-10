package AddressAndMap;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.model.LatLng;
import com.selflearn.rpsstationary.R;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class
TakingAddressActivity extends AppCompatActivity
{

    private static final int ERROR_DIALOG_REQUEST = 9001;
    private static final String TAG = "TakingAddressActivity";
    Button  saveBtn;
    Button getCurrentLocationBtn;
    private static final int request_code = 1;
    EditText pinCodeEditText, house_info , road_or_area_info , city_info , state_info , landmark_info ;
    String pinCode , houseInfo , roadOrAreaInfo , cityInfo , stateInfo , landInfo;
    float latitude,  longitude;
    private LatLng latLng_data ;
    List<Address> addresses;
    EditText name , number;
    String Name , Number;


    private View.OnClickListener listener_for_getLocationBtn = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if (isServicesOK())
            {
                Intent intent = new Intent(TakingAddressActivity.this , MapActivity.class);
                startActivityForResult(intent , request_code);
            }else {
                Log.d(TAG, "onClick: services are not good");
            }

        }
    };

    private View.OnClickListener listener_for_save_details = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            Intent data = new Intent();

            if (isFormValidated())
            {
                if (isUserDataAvailable()) {

                    data.putExtra("address", houseInfo + " " + roadOrAreaInfo + " " + cityInfo + " " + stateInfo + " " + (landInfo != null ? landInfo : "") + " " + pinCode);
                    data.putExtra("name" , Name);
                    data.putExtra("number", Number);
                    //data.putExtra("address" , addresses.get(0));
                    setResult(RESULT_OK, data);
                    finish();

                }
            }else {
                if (addresses != null) {
                    if (addresses.get(0) != null) {
                        if (addresses.get(0).getAddressLine(0) != null) {
                            if (isUserDataAvailable()) {

                                //data.putExtra("address", houseInfo + " " + roadOrAreaInfo + " " + cityInfo + " " + stateInfo + " " + (landInfo != null ? landInfo : "") + " " + pinCode);
                                data.putExtra("address", addresses.get(0).getAddressLine(0));
                                data.putExtra("name" , Name);
                                data.putExtra("number", Number);
                                setResult(RESULT_OK, data);
                                finish();

                            }
                            else {
                                Toast.makeText(getApplicationContext() , " please fill the address first" , Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(getApplicationContext() , " please fill the address first" , Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext() , "please fill the address first" , Toast.LENGTH_LONG).show();
                    }
                }
                else {
                    Toast.makeText(getApplicationContext() , "please fill the address first" , Toast.LENGTH_LONG).show();
                }
            }
        }
    };

    private boolean isUserDataAvailable() {
        boolean result = false;

        Name = name.getText().toString();
        Number = number.getText().toString();

        if (!Name.isEmpty() && !Number.isEmpty())
        {
            if (Number.length() == 10)
            {
                Log.d(TAG, "isUserDataAvailable: " + Number.length());
                result = true;
            }
            else {
                number.setError("please enter 10 digit phone number");
                number.requestFocus();
            }
        }
        return  result;
    }

    private boolean isFormValidated() {
        boolean result  = false;

        pinCode = pinCodeEditText.getText().toString();
        houseInfo =  house_info.getText().toString();
        roadOrAreaInfo = road_or_area_info.getText().toString();
        cityInfo = city_info.getText().toString();
        stateInfo = state_info.getText().toString();
        name = findViewById(R.id.name);
        number = findViewById(R.id.phone);

        if (!pinCode.isEmpty() && !houseInfo.isEmpty() && !roadOrAreaInfo.isEmpty() && !cityInfo.isEmpty() && stateInfo.isEmpty())
        {
            result = true;
        }

        return  result;
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_address_or_crrenent_address);

        findId();
        setListeners();

//        address_by_user.setPostalCode(pinCode);
//        address_by_user.setCountryCode("IN");
//        address_by_user.setFeatureName(landInfo);
//        address_by_user.setAddressLine(0,houseInfo);
//        address_by_user.setAddressLine(1, roadOrAreaInfo);
//        address_by_user.setAddressLine(2 , cityInfo);
//        address_by_user.setAddressLine(4 , stateInfo);
//

    }

    private void setListeners() {
        saveBtn.setOnClickListener(listener_for_save_details);
        getCurrentLocationBtn.setOnClickListener(listener_for_getLocationBtn);
    }

    private void findId() {
        getCurrentLocationBtn = findViewById(R.id.get_current_location_btn1);
        saveBtn = findViewById(R.id.saveBtn_location1);
        pinCodeEditText = findViewById(R.id.pin_code);
        house_info = findViewById(R.id.building_info);
        road_or_area_info = findViewById(R.id.road_name_area_colony);
        city_info = findViewById(R.id.city);
        state_info = findViewById(R.id.state);
        landmark_info = findViewById(R.id.landmark);
    }

    @SuppressLint("LongLogTag")
    public boolean isServicesOK() {
        Log.d(TAG , "checking google services version");
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);

        if (available == ConnectionResult.SUCCESS)
        {
            // everything is fine and user can make map request
            Log.d(TAG , "google play services are working");
            return  true;
        }
        else if (GoogleApiAvailability.getInstance().isUserResolvableError(available))
        {
            // error can be resolved

            Log.d(TAG, "isServicesOK: we can resolve the error");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(TakingAddressActivity.this , available , ERROR_DIALOG_REQUEST);
            dialog.show();
        }
        else
        {
            Toast.makeText(getApplicationContext() , "we cannot make a map request ", Toast.LENGTH_LONG).show();
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == request_code)
        {
            if(resultCode == RESULT_OK)
            {
                 latitude = Float.parseFloat(Objects.requireNonNull(Objects.requireNonNull(data).getStringExtra("lat")));
                 longitude = Float.parseFloat(Objects.requireNonNull(data.getStringExtra("long")));

                Log.d(TAG, "onActivityResult: " + latitude + " " + longitude);

                latLng_data = new LatLng(latitude, longitude);

                setValueInForm();

            }
        }
                super.onActivityResult(requestCode, resultCode, data);

    }

    @SuppressLint("SetTextI18n")
    private void setValueInForm() {
        if(latLng_data != null)
        {
            Geocoder geocoder = new Geocoder(TakingAddressActivity.this, Locale.getDefault());
            try {
                addresses= geocoder.getFromLocation(latLng_data.latitude, latLng_data.longitude, 3);
                if (addresses.size() > 0)
                {
//                    marker =map.addMarker(new MarkerOptions().position(latLng_data).title(addresses.get(0).getAddressLine(0)));
//                    markkedAddress = addresses.get(0).getAddressLine(0);

                    pinCode = addresses.get(0).getPostalCode();
                    pinCodeEditText.setText(pinCode);

                    String str  = addresses.get(0).getAddressLine(0);
                    List<String> list = Arrays.asList(str.split(","));
                    houseInfo  = list.get(0);
                    if (!houseInfo.equals("Unnamed Road"))
                    house_info.setText("house number "+ houseInfo);
                    else house_info.setText(addresses.get(0).getLocality());

                    roadOrAreaInfo = list.get(1);

                    if (roadOrAreaInfo!=null && addresses.get(0).getSubLocality() != null) {
                        Log.d(TAG, "setValueInForm: data is non null");
                        if (!addresses.get(0).getSubLocality().trim().equals(roadOrAreaInfo))
                            road_or_area_info.setText(roadOrAreaInfo + addresses.get(0).getSubLocality());
                        else
                            road_or_area_info.setText(roadOrAreaInfo);

                    }else road_or_area_info.setText(addresses.get(1).getAddressLine(0));

                    cityInfo = addresses.get(0).getLocality();
                    city_info.setText(cityInfo);

                    stateInfo = addresses.get(0).getAdminArea();
                    state_info.setText(stateInfo);


                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
