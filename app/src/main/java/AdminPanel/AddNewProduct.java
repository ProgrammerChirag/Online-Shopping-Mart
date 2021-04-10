package AdminPanel;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.selflearn.rpsstationary.R;

import java.util.Arrays;
import java.util.List;

public class AddNewProduct extends AppCompatActivity {

    protected InputFilter filter = new InputFilter() {
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

            Log.d(TAG, "filter: called");
            String str= String.valueOf(source);
            List<String> list;

            if (str.contains("/"))
            {
                Log.d(TAG, "filter: matched");
                list = Arrays.asList(str.split("/"));
                StringBuilder stringBuilder = new StringBuilder();
                for (String string : list)
                {
                    stringBuilder.append(string);
                }
                str = String.valueOf(stringBuilder);

                Log.d(TAG, "filter: "+str);
                return str;
            }
            return  str;
        }
    };

    EditText ProductName , Category , Type , Size , Color , QuantityInOnePack , Brand , MRP , SellingPrice , Discount , OrderQuantity;

    String   productName , category , type , size , color , quantityInOnePack , brand , mrp , sellingPrice , discount , orderQuantity;

    Button next;

    boolean result;

    private static final String TAG = "AddNewProduct";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_product);

        findID();

    }

    private void findID() {


        ProductName = findViewById(R.id.productName);
        ProductName.setFilters(new InputFilter[]{filter});

        Category = findViewById(R.id.productCategory);
        Category.setFilters(new InputFilter[]{filter});

        Type = findViewById(R.id.productType);
        Type.setFilters(new InputFilter[]{filter});

        Size  = findViewById(R.id.productSizeText);
        Size.setFilters(new InputFilter[]{filter});

        Color = findViewById(R.id.productColor);
        Color.setFilters(new InputFilter[]{filter});

        QuantityInOnePack = findViewById(R.id.quantityInOnePack);
        QuantityInOnePack.setFilters(new InputFilter[]{filter});

        Brand = findViewById(R.id.Brand);
        Brand.setFilters(new InputFilter[]{filter});

        MRP = findViewById(R.id.MRP);
        MRP.setFilters(new InputFilter[]{filter});

        SellingPrice = findViewById(R.id.SellingPrice);
        SellingPrice.setFilters(new InputFilter[]{filter});

        Discount = findViewById(R.id.productDiscount);
        Discount.setFilters(new InputFilter[]{filter});

        OrderQuantity = findViewById(R.id.orderQuantity);
        OrderQuantity.setFilters(new InputFilter[]{filter});

        next = findViewById(R.id.next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (formValidated()) {


                        // move to next activity.

                        Log.d(TAG, "onClick: form validated now moving to the next  page..");

                        Intent intent = new Intent(AddNewProduct.this , UploadProductImages.class);

                        intent.putExtra("ProductName" , productName);
                        intent.putExtra("Category" , category);
                        intent.putExtra("Type" , type);
                        intent.putExtra("Size" , size);
                        intent.putExtra("Color" , color);
                        intent.putExtra("QuantityInOnePack" , quantityInOnePack);
                        intent.putExtra("Brand" , brand);
                        intent.putExtra("MRP" , mrp);
                        intent.putExtra("SellingPrice" , sellingPrice);
                        intent.putExtra("Discount" , discount);
                        intent.putExtra("OrderQuantity" , orderQuantity);

                        finish();

                        startActivity(intent);


                    }

                }catch (Exception e)
                {
                    Log.d(TAG, "onClick: " + e.getClass().getName() + " " +e.getMessage() );
                    Toast.makeText(getApplicationContext() , "please fill all the details correctly" , Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    private boolean formValidated() {

        productName = ProductName.getText().toString().trim();
        category = Category.getText().toString().trim();
        type = Type.getText().toString().trim();
        size = Size.getText().toString().trim();
        color = Color.getText().toString().trim();
        quantityInOnePack = QuantityInOnePack.getText().toString().trim();
        brand = Brand.getText().toString().trim();
        mrp = MRP.getText().toString().trim();
        sellingPrice = SellingPrice.getText().toString().trim();
        discount = Discount.getText().toString().trim();
        orderQuantity = OrderQuantity.getText().toString().trim();

        result = !productName.isEmpty() && !category.isEmpty() && !type.isEmpty() && !color.isEmpty() && !quantityInOnePack.isEmpty() && !brand.isEmpty() && !mrp.isEmpty() && !sellingPrice.isEmpty() && !discount.isEmpty() && !orderQuantity.isEmpty();

        return  result;
    }
}