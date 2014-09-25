/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.twisterrob.android.view.color;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PointF;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.ImageView;

import net.twisterrob.android.view.color.swatches.APIDemoSwatch;
import net.twisterrob.android.view.color.swatches.PixelAbsoluteSwatch;
import net.twisterrob.android.view.color.swatches.Swatch;
import net.twisterrob.android.view.color.swatches.pixel.color.ColorReplacer;
import net.twisterrob.android.view.color.swatches.pixel.color.LinearHSBGradient;
import net.twisterrob.android.view.color.swatches.pixel.color.LinearHueGradient;
import net.twisterrob.android.view.color.swatches.pixel.color.RadialHSBGradient;
import net.twisterrob.android.view.color.swatches.pixel.color.RadialHueGradient;
import net.twisterrob.android.view.color.swatches.pixel.drawer.CenterBitmapDrawer;
import net.twisterrob.android.view.color.swatches.pixel.drawer.ColumnByColumnBitmapDrawer;
import net.twisterrob.android.view.color.swatches.pixel.drawer.LineByLineBitmapDrawer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ColorPickerView extends ImageView implements SwatchChooser.OnSwatchChangeListener {
    public interface OnColorChangedListener {
        void colorChanged(int color);
    }

    private OnColorChangedListener mListener;
    private final Touchy touch;
    private Swatch swatch;

    public ColorPickerView(Context context) {
        super(context);
        touch = new Touchy(context);
        init(context);
    }

    public ColorPickerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        touch = new Touchy(context);
        init(context);
    }

    public ColorPickerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        touch = new Touchy(context);
        init(context);
    }

    private void init(Context context) {
        setOnLongClickListener(touch);
        setOnTouchListener(touch);
        setFocusableInTouchMode(true);
        setupDefaultSwatch();
    }

    private void setupDefaultSwatch() {
        Swatch swatch = swatches.iterator().next();
        if (!isInEditMode()) {
            if (swatch instanceof PixelAbsoluteSwatch) {
                ((PixelAbsoluteSwatch) swatch).forceAsync();
            }
        }
        setSwatch(swatch);
    }

    public OnColorChangedListener getColorChangedListener() {
        return mListener;
    }

    public void setColorChangedListener(OnColorChangedListener listener) {
        this.mListener = listener;
    }

    public boolean isContinousMode() {
        return touch.isContinuousMode();
    }

    public void setContinousMode(boolean continousMode) {
        touch.setContinuousMode(continousMode);
    }


    public Swatch getSwatch() {
        return swatch;
    }

    public void setSwatch(Swatch swatch) {
        if (swatch == null) {
            throw new NullPointerException("Swatch cannot be null");
        }
        if (this.swatch != null) {
            swatch.setCurrentColor(this.swatch.getCurrentColor());
        }
        this.swatch = swatch;
        setImageDrawable(swatch);
    }

    public int getColor() {
        return swatch.getCurrentColor();
    }

    public void setColor(int color) {
        if (color == swatch.getCurrentColor()) return;
        swatch.setCurrentColor(color);
        invalidate();
        fireColorChanged();
    }

    /**
     * @see <a href="http://www.jayway.com/2012/12/12/creating-custom-android-views-part-4-measuring-and-how-to-force-a-view-to-be-square">Tutorial</a>
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        final float RATIO = 1f / 1f;

        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        int widthWithoutPadding = width - getPaddingLeft() - getPaddingRight();
        int heightWithoutPadding = height - getPaddingTop() - getPaddingBottom();

        int maxWidth = (int) (heightWithoutPadding * RATIO);
        int maxHeight = (int) (widthWithoutPadding / RATIO);

        if (maxWidth < maxHeight) {
            width = maxWidth + getPaddingLeft() + getPaddingRight();
        } else {
            height = maxHeight + getPaddingTop() + getPaddingBottom();
        }

        setMeasuredDimension(width, height);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public int getBaseline() {
        if (Build.VERSION_CODES.HONEYCOMB <= Build.VERSION.SDK_INT && getBaselineAlignBottom()) {
            return getMeasuredHeight();
        }
        return getPaddingTop() + (getMeasuredHeight() - getPaddingTop() - getPaddingBottom()) / 2;
    }

    protected void fireColorChanged() {
        if (mListener != null) {
            mListener.colorChanged(swatch.getCurrentColor());
        }
    }

    private final class Touchy implements OnTouchListener, OnLongClickListener {
        private final int mTouchSlop;
        private int trackedArea = Swatch.AREA_INVALID;
        private int highlightArea = Swatch.AREA_INVALID;
        private boolean continuousMode = true;

        public Touchy(Context context) {
            mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        }

        public boolean isContinuousMode() {
            return continuousMode;
        }

        public void setContinuousMode(boolean continuousMode) {
            this.continuousMode = continuousMode;
        }

        private PointF longTapStart = new PointF();

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (getDrawable() instanceof SwatchChooser) {
                return ((SwatchChooser) getDrawable()).onTouch(v, event);
            }
            invalidate();
            float x = event.getX() - getPaddingLeft();
            float y = event.getY() - getPaddingTop();
            if (!swatch.getBounds().contains((int) x, (int) y)) {
                return unhandled(event);
            }
            int inCenter = swatch.getAreaCode(x, y);

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    startLongTap(x, y);
                    //rebuild();
                    trackedArea = inCenter;
                    if (trackedArea != Swatch.AREA_INVALID && !continuousMode) {
                        swatch.setCurrentArea(highlightArea = inCenter);
                    }
                    return handled(event);
                case MotionEvent.ACTION_MOVE:
                    updateLongTap(x, y);
                    if (trackedArea != Swatch.AREA_INVALID) {
                        if (trackedArea == inCenter && !continuousMode) {
                            swatch.setCurrentArea(highlightArea = inCenter);
                        } else {
                            swatch.setCurrentArea(highlightArea = Swatch.AREA_INVALID);
                        }
                        int color = swatch.findColor(trackedArea, x, y);
                        if (continuousMode) {
                            setColor(color);
                        } else {
                            swatch.setCurrentColor(color);
                        }
                    }
                    return handled(event);
                case MotionEvent.ACTION_UP:
                    if (trackedArea != Swatch.AREA_INVALID) {
                        if (!continuousMode && trackedArea == highlightArea && swatch.triggersColorChange(trackedArea)) {
                            int color = swatch.findColor(trackedArea, x, y);
                            setColor(color);
                            View view = ColorPickerView.this; // ClickableViewAccessibility doesn't see the connection
                            view.performClick();
                        }
                        trackedArea = Swatch.AREA_INVALID;
                        swatch.setCurrentArea(highlightArea = Swatch.AREA_INVALID);
                    }
                    return handled(event);
                case MotionEvent.ACTION_CANCEL:
                    trackedArea = Swatch.AREA_INVALID;
                    swatch.setCurrentArea(highlightArea = Swatch.AREA_INVALID);
                    return handled(event);
            }
            return unhandled(event);
        }

        private boolean unhandled(MotionEvent event) {
            return false;
        }

        public boolean handled(MotionEvent event) {
            onTouchEvent(event);
            return true;
        }


        public void startLongTap(float x, float y) {
            longTapStart.set(x, y);
        }

        private void updateLongTap(float x, float y) {
            if (Math.sqrt((x - longTapStart.x) * (x - longTapStart.x) + (y - longTapStart.y) * (y - longTapStart.y)) >= mTouchSlop) {
                cancelLongPress();
            }
        }

        @Override
        public boolean onLongClick(View v) {
            showChooser();
            return true;
        }
    }

    @Override
    public boolean performClick() {
        boolean result = false;
        if (swatch != null) {
            if (swatch instanceof PixelAbsoluteSwatch) {
                ((PixelAbsoluteSwatch) swatch).forceAsync();
            }
            setSwatch(swatch);
        }
        return super.performClick() || result;
    }

    private final List<Swatch> swatches = new ArrayList<Swatch>(Arrays.asList(
            new PixelAbsoluteSwatch(ColorReplacer.wrap(CenterBitmapDrawer.factory(), Color.BLACK, Color.TRANSPARENT), new RadialHSBGradient()),
            new PixelAbsoluteSwatch(ColumnByColumnBitmapDrawer.factory(), new LinearHSBGradient()),
            new APIDemoSwatch(),
            new PixelAbsoluteSwatch(CenterBitmapDrawer.factory(), new RadialHueGradient()),
            new PixelAbsoluteSwatch(LineByLineBitmapDrawer.factory(), new LinearHueGradient())
    ));

    /**
     * Manipulate as you wish.
     */
    public List<Swatch> getSwatches() {
        return swatches;
    }

    /**
     * To hide chooser: call {@link #setSwatch}.
     */
    public void showChooser() {
        SwatchChooser chooser = new SwatchChooser(swatches);
        chooser.setOnSwatchChangeListener(this);
        chooser.setTileMargin((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics())); // 4dp
        setImageDrawable(chooser);
    }

    @Override
    public void swatchSelected(Swatch swatch) {
        this.swatch = swatch;
    }
}