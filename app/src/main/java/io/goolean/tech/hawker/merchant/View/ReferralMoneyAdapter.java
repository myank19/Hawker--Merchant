package io.goolean.tech.hawker.merchant.View;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import io.goolean.tech.hawker.merchant.Model.ReferralMoneyModel;
import io.goolean.tech.hawker.merchant.R;


public class ReferralMoneyAdapter extends RecyclerView.Adapter<ReferralMoneyAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<ReferralMoneyModel> referralMoneyModelArrayList;
    private ReferralMoneyModel referralMoneyModel;
    ArrayList<ReferralMoneyModel> detailModelArrayList;

    public ReferralMoneyAdapter(Context applicationContext, ArrayList<ReferralMoneyModel> hawkerDetailModels) {
        context=applicationContext;
        this.detailModelArrayList = hawkerDetailModels;
        this.referralMoneyModelArrayList= detailModelArrayList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.listitem_referral_detail, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        referralMoneyModel = referralMoneyModelArrayList.get(position);
        holder.tv_date_time.setText(referralMoneyModel.getDate_time());
        holder.tvAmountDetail.setText(referralMoneyModel.getMoney());


    }

    @Override
    public int getItemCount() { return (null != referralMoneyModelArrayList ? referralMoneyModelArrayList.size() : 0); }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvAmountDetail,tv_date_time;
        public MyViewHolder(View itemView) {
            super(itemView);
            tvAmountDetail = itemView.findViewById(R.id.tvAmountDetail);
            tv_date_time = itemView.findViewById(R.id.tv_date_time);
        }
    }


}
