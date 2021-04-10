package DashBoard.Algorithms;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.ArrayList;
import java.util.List;

import ModelClasses.ProductData;

public class SearchAlgorithm {

    String KEYWORD;
    List<ProductData> products;
    private static final String TAG = "SearchAlgorithm";
    ConstraintLayout constraintLayout;
    TextView exploringButton;

    // this is search Algorithm whenever user will enter text
    // for search then this algorithm will work.

    public SearchAlgorithm(String KeyWord , List<ProductData> products , ConstraintLayout constraintLayout , TextView exploringButton)
    {
        this.KEYWORD = KeyWord;
        this.products = products;
        this.constraintLayout = constraintLayout;
        this.exploringButton = exploringButton;
    }

    @SuppressLint("SetTextI18n")
    public List<ProductData> getData()
    {
        Log.d(TAG, "getData: called");

        List<ProductData> productDataList = new ArrayList<>();

        // process to get the perfect result.

        List<String> keywords = new ArrayList<>();

        for (int i=0 ; i <KEYWORD.length() ; i++)
        {
            for (int j= i +1 ; j < KEYWORD.length() ; j++)
            {
                keywords.add(KEYWORD.substring(i,j));
            }
            keywords.add(KEYWORD);
        }
        if (keywords.size() > 0) {
            if (products != null){
                for (ProductData productData : products) {
                    for (String keyword : keywords) {

                        String name = productData.getProductName();
                        Log.d(TAG, "getData: " + name);

                        if (check(name, keyword)) {
                            Log.d(TAG, "getData: new data added");
                            productDataList.add(productData);
                            break;
                        }

                    }
                }
            }
        }

        if (productDataList.size() > 0) {
            Log.d(TAG, "getData: " +productDataList.size());
            constraintLayout.setVisibility(View.GONE);
            exploringButton.setText("Show Categories");
            return productDataList;
        }
        else {
            Log.d(TAG, "getData: data size is " + productDataList.size());
            return null;
        }
    }


    // Function to match character
    static boolean check(String s1, String s2)
    {

        // here we need to match pattern so that we can check .......

        Log.d(TAG, "check: name :" + s1 +"keyword" + s2);

        if (s1 != null && s2 != null)
        {

            return s2.toLowerCase().equals(s1.toLowerCase());
        }
        else {
            Log.d(TAG, "check: " + s2 +" " +s1);
        }

        // else return 0
        return false;
    }

}
