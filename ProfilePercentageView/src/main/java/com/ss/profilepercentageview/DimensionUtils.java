package com.ss.profilepercentageview;

import android.content.Context;
import android.util.DisplayMetrics;

public class DimensionUtils {

  private DimensionUtils() {
    //No instances allowed    
  }

  public static int getSizeInPixels(float dp, Context context) {
    DisplayMetrics metrics = context.getResources().getDisplayMetrics();
    float pixels = metrics.density * dp;
    return (int) (pixels + 0.5f);
  }

  public static float pixelsToSp(Context context, float sp) {
    float scaledDensity = context.getResources().getDisplayMetrics().scaledDensity;
    return sp * scaledDensity;
  }
}
