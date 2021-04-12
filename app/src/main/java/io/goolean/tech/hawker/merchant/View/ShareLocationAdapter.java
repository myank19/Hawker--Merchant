package io.goolean.tech.hawker.merchant.View;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import io.goolean.tech.hawker.merchant.Model.ShareLocationModel;
import io.goolean.tech.hawker.merchant.R;

public class ShareLocationAdapter extends RecyclerView.Adapter<ShareLocationAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<ShareLocationModel> arrayList;
    private ShareLocationModel shareLocationModel;
    CallClick callClick ;
    NavigateClick navigateClick;
    CancelClick cancelClick;
    CompleteClick completeClick;



    public interface  CallClick { void onCallClickListener(int position, ShareLocationModel helper);}
    public void OnCallClickMethod(CallClick callClick ) { this.callClick = callClick ; }

    public interface  NavigateClick { void onNavigateClickListener(int position, ShareLocationModel helper);}
    public void OnNavigateClickMethod(NavigateClick navigateClick ) { this.navigateClick = navigateClick ; }

    public interface CompleteClick { void onCompleteClickListener(int position, ShareLocationModel helper);}
    public void OnCompleteClickMethod(CompleteClick completeClick ) { this.completeClick = completeClick ; }

    public interface  CancelClick { void onCancelClickListener(int position, ShareLocationModel helper);}
    public void OnCancelClickMethod(CancelClick cancelClick ) { this.cancelClick = cancelClick ; }



    public ShareLocationAdapter(Context applicationContext, ArrayList<ShareLocationModel> arrayListNotificaiton) {
        context=applicationContext;
        arrayList= arrayListNotificaiton;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.listitem_share_location_layout, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        shareLocationModel = arrayList.get(position);
        holder.tv_date_time.setText(shareLocationModel.getDate_time());
        holder.tv_name_customer.setText(shareLocationModel.getCustomer_mobile_no()+"("+shareLocationModel.getCustomer_name()+")");
        holder.tv_location.setText(shareLocationModel.getLocation_name());
        holder.phone_call_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(callClick!=null) {
                    callClick.onCallClickListener(position,shareLocationModel);
                }
            }
        });
        holder.navigate_location_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(navigateClick!=null) {
                    navigateClick.onNavigateClickListener(position,shareLocationModel);
                }
            }
        });
        holder.cancel_trip_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cancelClick!=null) {
                    cancelClick.onCancelClickListener(position,shareLocationModel);
                }
            }
        });

        holder.complete_trip_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(completeClick!=null) {
                    completeClick.onCompleteClickListener(position,shareLocationModel);
                }
            }
        });
    }

    @Override
    public int getItemCount() { return (null != arrayList ? arrayList.size() : 0); }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_date_time, tv_name_customer,tv_location;// init the item view's
        RelativeLayout phone_call_id,navigate_location_id,complete_trip_id,cancel_trip_id;

        public MyViewHolder(View itemView) {
            super(itemView);
            tv_date_time = (TextView) itemView.findViewById(R.id.tv_date_time);
            tv_name_customer = (TextView) itemView.findViewById(R.id.tv_name_customer);
            tv_location = (TextView) itemView.findViewById(R.id.tv_location);

            phone_call_id = itemView.findViewById(R.id.phone_call_id);
            navigate_location_id = itemView.findViewById(R.id.navigate_location_id);
            complete_trip_id = itemView.findViewById(R.id.complete_trip_id);
            cancel_trip_id = itemView.findViewById(R.id.cancel_trip_id);




        }
    }

}
