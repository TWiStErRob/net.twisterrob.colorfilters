package net.twisterrob.android.view.color.swatches;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;

public abstract class Swatch extends Drawable {
    public static final int AREA_INVALID = -1;
    public static final int AREA_DEFAULT = 0;

    public abstract int getCurrentColor();

    public abstract void setCurrentColor(int color);

    public abstract int findColor(int area, float x, float y);

    protected RuntimeException invalidArea(int area, float x, float y) {
        return new IllegalStateException("Cannot find color for area " + area + " at " + x + ", " + y);
    }

    public abstract void draw(Canvas canvas);

    public int getAreaCode(float x, float y) {
        return AREA_DEFAULT;
    }

    public void setCurrentArea(int areaCode) {
    }

    @Override
    public void setAlpha(int alpha) {
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
    }

    @Override
    public int getOpacity() {
        return 0;
    }

    public boolean triggersColorChange(int trackedArea) {
        return true;
    }
}
