package DashBoard;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.selflearn.rpsstationary.R;

import java.util.ArrayList;
import java.util.List;

public class ApplyFilterActivity extends AppCompatActivity {

    Spinner spinner  , spinner2 , spinner3 , spinner4;
    List<String> categories ;
    List<String> productType;
    List<String> productSize;
    List<String> productColor;
    TextView applyBtn;

    private static final String TAG = "ApplyFilterActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.filter_activity);

        categories = getIntent().getStringArrayListExtra("category");
        productType = getIntent().getStringArrayListExtra("productType");
        productSize = getIntent().getStringArrayListExtra("productSize");
        productColor = getIntent().getStringArrayListExtra("productColor");
        applyBtn = findViewById(R.id.apply_btn);

        applyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cat = spinner.getSelectedItem().toString();
                String type = spinner2.getSelectedItem().toString();
                String size = spinner3.getSelectedItem().toString();
                String color = spinner4.getSelectedItem().toString();


                if (!cat.isEmpty() && !type.isEmpty() && !size.isEmpty() && !color.isEmpty()){
                    Log.d(TAG, "onClick: " + cat + " "+ type +" " + size +" "+ color);

                    Intent intent = new Intent();

                    intent.putExtra("category" , cat);
                    intent.putExtra("productType" , type);
                    intent.putExtra("productSize" , size);
                    intent.putExtra("productColor" , color);

                    setResult(RESULT_OK,intent);
                    finish();
                }
                else {
                    Toast.makeText(getApplicationContext() , "unable to apply filter" ,Toast.LENGTH_LONG).show();
                    finish();
                }

            }
        });

        spinner = findViewById(R.id.spinner);
        spinner2 = findViewById(R.id.spinner2);
        spinner3 = findViewById(R.id.spinner3);
        spinner4 = findViewById(R.id.spinner4);


        spinner.setPrompt("choose product Category");

        ArrayAdapter<CharSequence> category  = new ArrayAdapter<>(
                this , android.R.layout.simple_spinner_item , new ArrayList<CharSequence>(categories)
        );
        spinner.setAdapter(category);

        ArrayAdapter<CharSequence> productTypes = new ArrayAdapter<>(this ,
                android.R.layout.simple_spinner_item , new ArrayList<CharSequence>(productType)
                );

      spinner2.setPrompt("choose product Type");
      spinner2.setAdapter(productTypes);

      ArrayAdapter<CharSequence> productSizes = new ArrayAdapter<>(this ,
              android.R.layout.simple_spinner_item , new ArrayList<CharSequence>(productSize)
              );

      spinner3.setPrompt("choose product Size");
      spinner3.setAdapter(productSizes);

      ArrayAdapter<CharSequence> productColors = new ArrayAdapter<>(this ,
              android.R.layout.simple_spinner_item , new ArrayList<CharSequence>(productColor)
              );

      spinner4.setPrompt("choose product Color");
      spinner4.setAdapter(productColors);


    }
}
