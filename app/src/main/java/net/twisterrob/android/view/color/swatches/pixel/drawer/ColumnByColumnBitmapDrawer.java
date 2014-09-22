package net.twisterrob.android.view.color.swatches.pixel.drawer;

import net.twisterrob.android.view.color.swatches.pixel.color.PixelColor;

public class ColumnByColumnBitmapDrawer extends BitmapDrawer {
    public ColumnByColumnBitmapDrawer(int[] bitmap, int w, int h, PixelColor pixel) {
        super(bitmap, w, h, pixel);
    }

    @Override
    protected void fillPixels() {
        int w = this.w;
        int h = this.h;
        for (int x = 0; x < w; ++x) {
            for (int y = 0; y < h; ++y) {
                bitmap[y * w + x] = pixel.getPixelColorAt(x, y);
            }
            reportProgress();
        }
    }

    public static Factory factory() {
        return new Factory() {
            @Override
            public BitmapDrawer create(int[] bitmap, int w, int h, PixelColor color) {
                return new ColumnByColumnBitmapDrawer(bitmap, w, h, color);
            }
        };
    }
}
