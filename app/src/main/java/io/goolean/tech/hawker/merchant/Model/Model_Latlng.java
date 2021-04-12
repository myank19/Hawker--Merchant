package io.goolean.tech.hawker.merchant.Model;

import android.content.Context;

public class Model_Latlng {


    double mcurr_lati,mcurr_long;
    Context mcontext;

    public Model_Latlng(Context context) {
       this.mcontext=context;
    }

    public void setLat(double curr_lati) {
        this.mcurr_lati = curr_lati;
    }

    public void setLon(double curr_long) {
        mcurr_long = curr_long;
    }

    public double getMcurr_lati() {
        return mcurr_lati;
    }

    public double getMcurr_long() {
        return mcurr_long;
    }
}
