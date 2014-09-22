package net.twisterrob.android.view.color.swatches.pixel.color;

import net.twisterrob.android.view.color.ColorMath;

public class LinearHueGradient implements PixelColor {
    private int w;

    @Override
    public void initializeInvariants(int w, int h) {
        this.w = w;
    }

    @Override
    public int getPixelColorAt(int x, int y) {
        return ColorMath.fromHsb((float) x / w, 1, 1, 1);
    }
}
