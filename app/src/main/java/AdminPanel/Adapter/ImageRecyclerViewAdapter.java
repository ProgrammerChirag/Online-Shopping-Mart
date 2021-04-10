package AdminPanel.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.selflearn.rpsstationary.R;

import java.util.List;

public class ImageRecyclerViewAdapter extends RecyclerView.Adapter<ImageRecyclerViewAdapter.MyViewHolder> {

    Context context;
    List<Bitmap> bitmapList ;

    public ImageRecyclerViewAdapter(Context context , List<Bitmap> bitmapList)
    {
        this.context = context;
        this.bitmapList = bitmapList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_image_view , parent , false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        holder.imageView.setImageBitmap(bitmapList.get(position));

        holder.delete_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bitmapList.size() > 0)
                {
                    bitmapList.remove(position);
                    notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return bitmapList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView ;
        ImageView delete_image;

        public MyViewHolder(@NonNull View itemView) {

            super(itemView);
            imageView = itemView.findViewById(R.id.imgViewRecycle);
            delete_image = itemView.findViewById(R.id.delete_image);
        }
    }
}

