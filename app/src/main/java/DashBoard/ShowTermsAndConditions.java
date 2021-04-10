package DashBoard;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.selflearn.rpsstationary.R;


import DashBoard.Adapter.TAndCAdapter;

public class ShowTermsAndConditions extends AppCompatActivity {

    RecyclerView recyclerView;
    TextView textView;
    CheckBox checkBox;
    Button submit;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.terms_and_condition_page);

        submit = findViewById(R.id.submitBtn);
        submit.setClickable(false);
        submit.setEnabled(false);
        submit.setAlpha(0.4f);

        checkBox = findViewById(R.id.checkBox);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                {
                    submit.setClickable(true);
                    submit.setEnabled(true);
                    submit.setAlpha(1.0f);
                }
                else {
                    submit.setClickable(false);
                    submit.setEnabled(false);
                    submit.setAlpha(0.4f);
                }
            }
        });

        textView = findViewById(R.id.applyTC);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (recyclerView.getVisibility() == View.VISIBLE)
                recyclerView.setVisibility(View.INVISIBLE);
                else recyclerView.setVisibility(View.VISIBLE);
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(ShowTermsAndConditions.this , DashBoardMainUI.class));
            }
        });

        recyclerView = findViewById(R.id.showTAndC);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this , RecyclerView.VERTICAL , false));

        String[] termsAndConditions = getResources().getStringArray(R.array.tc);

        TAndCAdapter adapter = new TAndCAdapter(termsAndConditions);
        recyclerView.setAdapter(adapter);

    }
}
