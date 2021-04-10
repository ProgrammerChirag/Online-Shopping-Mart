package DashBoard.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.selflearn.rpsstationary.R;

import DashBoard.ActivityChooseCategory;
import DashBoard.DashBoardMainUI;

public class ChooseCategoryAdapter extends RecyclerView.Adapter<ChooseCategoryAdapter.ChooseCategoryViewHolder> {

    Context context;
    private static final String TAG = "ChooseCategoryAdapter";

    int[] categories = {
            R.drawable.school_product_logo,
            R.drawable.office_product_logo,
            R.drawable.both_product_logo

    };

     int[] categories_selected = {
            R.drawable.school_product2,
            R.drawable.office_product2,
            R.drawable.both_product2
    };

     int[] data =
             {
                     R.string.school_product,
                     R.string.office_product,
                     R.string.both
             };


    public ChooseCategoryAdapter(Context context)
    {this.context =context;}


    @NonNull
    @Override
    public ChooseCategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_slider_recycler_view_choose_category, parent, false);
        return new ChooseCategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ChooseCategoryViewHolder holder, final int position) {


        holder.imageView.setImageResource(categories[position]);

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                holder.imageView.setImageResource(categories_selected[position]);

                ActivityChooseCategory.SELECTED_ITEM = v.getResources().getString(data[position]);

                ((Activity)(context)).finish();
                Intent intent = new Intent(context.getApplicationContext(), DashBoardMainUI.class);
                intent.putExtra("selected", ActivityChooseCategory.SELECTED_ITEM);
                Log.d(TAG, "onClick: "+ActivityChooseCategory.SELECTED_ITEM);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return categories.length;
    }

    public static class ChooseCategoryViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        public ChooseCategoryViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.image);

        }
    }
}
