package DashBoard;

import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.selflearn.rpsstationary.R;

import java.util.ArrayList;
import java.util.List;

import DashBoard.Adapter.ShowProductAdapter;
import ModelClasses.ProductData;

public class ShowProducts extends AppCompatActivity{

    Toolbar toolbar;
    RecyclerView products;
    ArrayList<ProductData> productDataList;
    ArrayList<List<String>> imageData;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_show_products);

        productDataList = (ArrayList<ProductData>) getIntent().getSerializableExtra("productDataList");
        imageData = (ArrayList<List<String>>) getIntent().getSerializableExtra("image");

        toolbar = findViewById(R.id.toolbar11);
        products = findViewById(R.id.recycler_view_products);
        products.setHasFixedSize(true);
        GridLayoutManager layoutManager;
        layoutManager = new GridLayoutManager(this , 2 , RecyclerView.VERTICAL, false);
        products.setLayoutManager(layoutManager);

        products.addItemDecoration(new DividerItemDecoration(ShowProducts.this,
                DividerItemDecoration.VERTICAL));

        products.setHasFixedSize(true);
        products.setAdapter(new ShowProductAdapter(imageData, productDataList, ShowProducts.this));

    }
}
