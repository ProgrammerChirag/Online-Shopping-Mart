package DashBoard.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.makeramen.roundedimageview.RoundedImageView;
import com.selflearn.rpsstationary.R;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;

import java.util.List;

public class ImageSliderAdapter extends RecyclerView.Adapter<ImageSliderAdapter.ImageSliderHolder> {

    Context context;
    List<String> list;
    private static final String TAG = "ImageSliderAdapter";

    public ImageSliderAdapter(Context context , List<String> list)
    {
        this.list = list;
        this.context = context;
        Log.d(TAG, "ImageSliderAdapter: " +list.size());
    }

    @NonNull
    @Override
    public ImageSliderHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        Log.d(TAG, "onCreateViewHolder: called");
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_adapter_view , parent , false);
        view.setLayoutParams(new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        return new ImageSliderHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ImageSliderHolder holder, int position) {

        Glide.with(holder.imageView)
                .load(list.get(position))
//                .centerCrop()
                .override(Target.SIZE_ORIGINAL , Target.SIZE_ORIGINAL)
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public class ImageSliderHolder extends RecyclerView.ViewHolder
    {

        private RoundedImageView imageView;


        public ImageSliderHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image);
        }


    }
}
