package net.twisterrob.android.view.color.swatches.pixel.drawer;

import net.twisterrob.android.view.color.swatches.pixel.color.PixelColor;

public class CenterBitmapDrawer extends BitmapDrawer {
    public CenterBitmapDrawer(int[] bitmap, int w, int h, PixelColor pixel) {
        super(bitmap, w, h, pixel);
    }

    @Override
    protected void fillPixels() {
        int w = this.w;
        int cx = w / 2;
        int cy = this.h / 2;
        for (int cd = 0; cd < cy; ++cd) {
            int startX = cx - cd;
            int endX = cx + cd;
            int startY = cy - cd;
            int endY = cy + cd;
            for (int x = startX; x <= endX; ++x) {
                bitmap[startY * w + x] = pixel.getPixelColorAt(x, startY);
            }
            for (int x = startX; x <= endX; ++x) {
                bitmap[endY * w + x] = pixel.getPixelColorAt(x, endY);
            }
            startY++;
            endY--;
            for (int y = startY; y <= endY; ++y) {
                bitmap[y * w + startX] = pixel.getPixelColorAt(startX, y);
            }
            for (int y = startY; y <= endY; ++y) {
                bitmap[y * w + endX] = pixel.getPixelColorAt(endX, y);
            }
            reportProgress();
        }
        // DrawerUtils.drawBorder(this, ColorMath.randomColor(), 10);
    }

    public static Factory factory() {
        return new Factory() {
            @Override
            public BitmapDrawer create(int[] bitmap, int w, int h, PixelColor color) {
                return new CenterBitmapDrawer(bitmap, w, h, color);
            }
        };
    }
}
