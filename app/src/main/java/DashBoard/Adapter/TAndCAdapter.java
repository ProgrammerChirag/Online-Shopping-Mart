package DashBoard.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.selflearn.rpsstationary.R;


public class TAndCAdapter extends RecyclerView.Adapter<TAndCAdapter.termsViewHolder> {

    String[] data ;

    public TAndCAdapter(String[] data)
    {
        this.data =data;
    }


    @NonNull
    @Override
    public termsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return  new termsViewHolder(LayoutInflater.from(parent.getContext()).inflate(
                R.layout.textview, parent , false
        ));
    }

    @Override
    public void onBindViewHolder(@NonNull termsViewHolder holder, int position) {
        holder.textView.setText(data[position]);
    }

    @Override
    public int getItemCount() {
        return data.length;
    }

    public static class termsViewHolder extends RecyclerView.ViewHolder {

        TextView textView;


        public termsViewHolder(@NonNull View itemView) {
            super(itemView);

            textView = itemView.findViewById(R.id.textview);
        }
    }
}
