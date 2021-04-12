package io.goolean.tech.hawker.merchant.Utility;


import android.content.Context;
import android.graphics.Typeface;


public class TypefaceClass {

        public static Typeface getLightTypeface(Context ctx)
              {
                   Typeface light = Typeface.createFromAsset(ctx.getAssets(), "fonts/LIONELLO-Light.otf");
                   return light ;
             }

    public static Typeface getBoldTypeface(Context ctx)
    {
        Typeface bold = Typeface.createFromAsset(ctx.getAssets(), "fonts/LIONELLO.otf");
        return bold ;
    }



    public static Typeface getRegularTypeface(Context ctx)
    {
        Typeface regular = Typeface.createFromAsset(ctx.getAssets(), "fonts/OpenSans-Regular.ttf");
        return regular ;
    }



    public static Typeface getSemiBoldTypeface(Context ctx)
    {
        Typeface semibold = Typeface.createFromAsset(ctx.getAssets(), "fonts/OpenSans-Semibold.ttf");
        return semibold ;
    }



    public static Typeface getNormalTypeface(Context ctx)
    {
        Typeface normal = Typeface.createFromAsset(ctx.getAssets(), "fonts/normal.otf");
        return normal ;
    }

}
