package io.goolean.tech.hawker.merchant.View;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

public class MyRecycler extends RecyclerView {


    private boolean verticleScrollingEnabled = false;

    public void enableVersticleScroll (boolean enabled) {
        verticleScrollingEnabled = enabled;
    }

    public boolean isVerticleScrollingEnabled() {
        return verticleScrollingEnabled;
    }

    @Override
    public int computeVerticalScrollRange() {

        if (isVerticleScrollingEnabled())
            return super.computeVerticalScrollRange();
        return 0;
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {

        if(isVerticleScrollingEnabled())
            return super.onInterceptTouchEvent(e);
        return false;

    }

    public MyRecycler(Context context) {
        super(context);
    }

    public MyRecycler(Context context, @Nullable AttributeSet attrs) {
        super(context);
    }

    public MyRecycler(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
}
