package com.ss.profilepercentageview;

import android.graphics.Canvas;

public interface Painter {

    void draw(Canvas canvas);

    void setBackColor(int color);

    void setDashWidth(float value);

    void setArcWidth(float value);

    void setDashSpace(float value);

    void setDashEnable(boolean value);

    void setRounded(boolean value);

    int getBackColor();

    void onSizeChanged(int height, int width);
}
