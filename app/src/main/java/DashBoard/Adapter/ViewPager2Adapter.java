package DashBoard.Adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.makeramen.roundedimageview.RoundedImageView;
import com.selflearn.rpsstationary.R;

import java.util.List;

import ModelClasses.SliderItem;

public class ViewPager2Adapter extends RecyclerView.Adapter<ViewPager2Adapter.SlideViewHolder> {

    private ViewPager2 viewPager;
    List<SliderItem> sliderItemList;
    private static final String TAG = "ViewPager2Adapter";

    public ViewPager2Adapter(ViewPager2 viewPager, List<SliderItem> sliderItemList) {

        Log.d(TAG, "ViewPager2Adapter: called");
        this.viewPager = viewPager;
        this.sliderItemList = sliderItemList;
    }


    @NonNull
    @Override
    public SlideViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        Log.d(TAG, "onCreateViewHolder: called");
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_adapter_view , parent , false);
        view.setLayoutParams(new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        return new SlideViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SlideViewHolder holder, int position) {
        try {
            Log.d(TAG, "onBindViewHolder: called");
            holder.setImage(sliderItemList.get(position));
        }catch (Exception e)
        {
            Log.d(TAG, "onBindViewHolder: error :  " + e.getMessage() + " " +e.getClass().getName());
        }
    }

    @Override
    public int getItemCount() {
        return sliderItemList.size();
    }

    public class SlideViewHolder  extends  RecyclerView.ViewHolder{

        private RoundedImageView imageView;

        public SlideViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image);
        }

        void setImage(SliderItem sliderItem)
        {
            imageView.setImageResource(sliderItem.getImage());
        }


    }
}
