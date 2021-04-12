package io.goolean.tech.hawker.merchant.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import io.goolean.tech.hawker.merchant.Model.PaymentPlanModel;
import io.goolean.tech.hawker.merchant.R;
import io.goolean.tech.hawker.merchant.View.PaymentActivity;

public class PlanAdapter extends RecyclerView.Adapter<PlanAdapter.ViewHolder> {
    Context context;
    List<PaymentPlanModel> planList;
    PaymentPlanModel planModel;
    List<CardView> cardViewList = new ArrayList<>();

    public PlanAdapter(Context context, List<PaymentPlanModel> planList) {
        this.context = context;
        this.planList = planList;
    }

    @NonNull
    @Override
    public PlanAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.payment_plan, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final PlanAdapter.ViewHolder holder, int position) {
        planModel = planList.get(position);
        holder.tvMoney.setText("₹ " + planModel.getPrice());
        holder.tvTimePlan.setText(planModel.getPlanType());
        holder.tvDays.setText(planModel.getValue());
    }

    @Override
    public int getItemCount() {
        return planList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTimePlan, tvMoney, tvDays;
        CardView cdDayPlan;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTimePlan = itemView.findViewById(R.id.tvTimePlan);
            tvMoney = itemView.findViewById(R.id.tvMoney);
            tvDays = itemView.findViewById(R.id.tvDays);
            cdDayPlan = itemView.findViewById(R.id.cdDayPlan);

            if (!cardViewList.contains(cdDayPlan)) {
                cardViewList.add(cdDayPlan);
            }
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((PaymentActivity) context).SelectPlan(planList.get(getLayoutPosition()));

                    for (CardView cardView : cardViewList) {
                        cardView.setCardBackgroundColor(context.getResources().getColor(R.color.white));
                    }
                    cdDayPlan.setCardBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
                }
            });
        }
    }
}
