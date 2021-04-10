package AdminPanel;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.selflearn.rpsstationary.R;

import Utils.CustomProgressDialogSpinKit;

public class AdminDashBoard extends AppCompatActivity {

    CardView AddData;
    ConstraintLayout constraintLayout;
    private static final String TAG = "AdminDashBoard";
    CardView showProductList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_dashboard);

        showProductList = findViewById(R.id.ShowProductList);
        showProductList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminDashBoard.this , ShowAllProduct.class);
                startActivity(intent);
            }
        });

        AddData = findViewById(R.id.AddData);
        constraintLayout = findViewById(R.id.constraint_layout2);
        AddData.setOnClickListener(new View.OnClickListener() {
                                       @Override
                                       public void onClick(View v) {

                                           try {

                                               final CustomProgressDialogSpinKit customProgressDialogSpinKit;
                                               constraintLayout.setAlpha(0.4f);

                                               customProgressDialogSpinKit = new CustomProgressDialogSpinKit(AdminDashBoard.this);
                                               customProgressDialogSpinKit.startLoadingDialog();

                                               new Handler().postDelayed(new Runnable() {
                                                   @Override
                                                   public void run() {

                                                       customProgressDialogSpinKit.dismissDialog();
                                                       // change Activity

                                                       finish();
                                                       startActivity(new Intent(AdminDashBoard.this, AddNewProduct.class));

                                                   }
                                               }, 2000);
                                           }catch (Exception e)
                                           {
                                               Log.d(TAG, "onClick: " +e.getMessage() + " " +e.getClass().getName());
                                               Toast.makeText(getApplicationContext() , e.getMessage() + " " +e.getClass().getName() , Toast.LENGTH_LONG).show();
                                           }
                                       }
                                       }
        );

    }


}





















































