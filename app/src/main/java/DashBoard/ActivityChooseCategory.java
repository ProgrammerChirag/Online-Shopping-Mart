package DashBoard;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.ramotion.cardslider.CardSliderLayoutManager;
import com.selflearn.rpsstationary.R;

import DashBoard.Adapter.ChooseCategoryAdapter;

public class ActivityChooseCategory extends AppCompatActivity {

    RecyclerView recyclerViewChooseCategory;

    public static String SELECTED_ITEM;
    private static final String TAG = "ActivityChooseCategory";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_choose_category);

        recyclerViewChooseCategory = findViewById(R.id.recyclerViewChooseCategory);
        recyclerViewChooseCategory.setHasFixedSize(true);
        CardSliderLayoutManager manager = new CardSliderLayoutManager(5 , 940 , 0);
        recyclerViewChooseCategory.setLayoutManager(manager);

        final int pos = manager.getActiveCardPosition();
        Log.d(TAG, "onCreate: "+pos);
        recyclerViewChooseCategory.setAdapter(new ChooseCategoryAdapter(ActivityChooseCategory.this));


    }


}
