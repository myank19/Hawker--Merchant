package io.goolean.tech.hawker.merchant.Model;

public class PaymentPlanModel {
    String PlanType;
    String Price;
    String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }


    public String getPlanType() {
        return PlanType;
    }

    public void setPlanType(String planType) {
        PlanType = planType;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }
}
