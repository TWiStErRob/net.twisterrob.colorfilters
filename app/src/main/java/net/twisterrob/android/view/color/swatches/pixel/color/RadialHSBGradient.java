package net.twisterrob.android.view.color.swatches.pixel.color;

import net.twisterrob.android.view.color.ColorMath;
import net.twisterrob.android.view.color.FastMath;

public class RadialHSBGradient implements PixelColor {
    private int w;
    private int cx, cy;

    @Override
    public void initializeInvariants(int w, int h) {
        this.w = w;
        this.cx = w / 2;
        this.cy = w / 2;
    }

    @Override
    public int getPixelColorAt(int x, int y) {
        float angle = FastMath.Atan2Faster.atan2(y - cy, x - cx) + ColorMath.PI;
        float hue = angle / (ColorMath.PI * 2);
        float dist = (float) Math.sqrt((x - cx) * (x - cx) + (y - cy) * (y - cy));
        float sat = ColorMath.ofMap(dist, 0, w / 4, 0, 1);
        float bri = ColorMath.ofMap(dist, w / 4, w / 2, 1, 0);
        return ColorMath.fromHsb(hue, sat, bri, 1);
    }
}
