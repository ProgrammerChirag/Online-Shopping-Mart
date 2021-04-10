package DashBoard.Adapter;

import android.content.Context;
import android.media.tv.TvView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.selflearn.rpsstationary.R;

import java.util.List;

import ModelClasses.DeliveryAddress;

public class ShowAddressAdapter extends RecyclerView.Adapter<ShowAddressAdapter.AddressViewHolder> {

    Context context ;
    List<DeliveryAddress> deliveryAddresses;

    public ShowAddressAdapter(){}

    public ShowAddressAdapter(Context context, List<DeliveryAddress> deliveryAddresses) {
        this.context = context;
        this.deliveryAddresses = deliveryAddresses;
    }

    @NonNull
    @Override
    public AddressViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       return  new AddressViewHolder(
               LayoutInflater.from(parent.getContext()).inflate(
                       R.layout.textview , parent , false
               )
       );
    }

    @Override
    public void onBindViewHolder(@NonNull AddressViewHolder holder, int position) {
        holder.textView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        holder.textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP , 22);

        holder.textView.setText(
                String.format("Address : %s\nName :%s\nPhone : %s",
                deliveryAddresses.get(position).getAddress(), deliveryAddresses.get(position).getName(), deliveryAddresses.get(position).getPhone())
        );
    }

    @Override
    public int getItemCount() {
        return deliveryAddresses.size();
    }

    public static class AddressViewHolder extends RecyclerView.ViewHolder {

        TextView textView ;

        public AddressViewHolder(@NonNull View itemView) {
            super(itemView);

            textView = itemView.findViewById(R.id.textview);

        }
    }
}
