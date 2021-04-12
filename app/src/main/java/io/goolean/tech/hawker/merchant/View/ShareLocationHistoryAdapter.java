package io.goolean.tech.hawker.merchant.View;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import io.goolean.tech.hawker.merchant.Model.ShareLocationModelHistory;
import io.goolean.tech.hawker.merchant.R;

public class ShareLocationHistoryAdapter extends RecyclerView.Adapter<ShareLocationHistoryAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<ShareLocationModelHistory> arrayList;
    private ShareLocationModelHistory shareLocationModelHistory;


    public ShareLocationHistoryAdapter(Context applicationContext, ArrayList<ShareLocationModelHistory> arrayListNotificaiton) {
        context=applicationContext;
        arrayList= arrayListNotificaiton;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.listitem_share_location_history_layout, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        shareLocationModelHistory = arrayList.get(position);
        holder.tv_date_time.setText(shareLocationModelHistory.getDate_time());
        holder.tv_name_customer.setText(shareLocationModelHistory.getCustomer_mobile_no()+"("+shareLocationModelHistory.getCustomer_name()+")");
        holder.tv_location.setText(shareLocationModelHistory.getLocation_name());

        if(shareLocationModelHistory.getClose_status().equalsIgnoreCase("1")){
            holder.img_show.setBackgroundResource(R.drawable.ic_correct);
            holder.tv_showstatus.setText("पूर्ण किया"+"\n"+"Complete");
        }else if(shareLocationModelHistory.getClose_status().equalsIgnoreCase("2")){
            holder.img_show.setBackgroundResource(R.drawable.ic_cancel);
            holder.tv_showstatus.setText("रद्द किया\nCancel");
        }
    }

    @Override
    public int getItemCount() { return (null != arrayList ? arrayList.size() : 0); }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_date_time, tv_name_customer,tv_location,tv_showstatus;// init the item view's
        ImageView img_show;

        public MyViewHolder(View itemView) {
            super(itemView);
            tv_date_time = (TextView) itemView.findViewById(R.id.tv_date_time);
            tv_name_customer = (TextView) itemView.findViewById(R.id.tv_name_customer);
            tv_location = (TextView) itemView.findViewById(R.id.tv_location);
            tv_showstatus = (TextView) itemView.findViewById(R.id.tv_showstatus);

            img_show = itemView.findViewById(R.id.img_show);

        }
    }

}
