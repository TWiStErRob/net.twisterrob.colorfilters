package net.twisterrob.android.view.color.swatches.pixel.drawer;

import net.twisterrob.android.view.color.swatches.pixel.color.PixelColor;

public class StepBitmapDrawer extends BitmapDrawer {
    public StepBitmapDrawer(int[] bitmap, int w, int h, PixelColor pixel) {
        super(bitmap, w, h, pixel);
    }

    @Override
    protected void fillPixels() {
        final int w = this.w;
        final int h = this.h;
        final int iterations = 3;
        final int iterationStep = 1; // must be relative prime to iterations (e.g.: 5,3)
        for (int stepY = 0; stepY < iterations; ++stepY) {
            for (int stepX = 0; stepX < iterations; ++stepX) {
                final int offsetX = (stepX * iterationStep) % iterations;
                final int offsetY = (stepY * iterationStep) % iterations;
                for (int y = offsetY; y < h; y += iterations) {
                    for (int x = offsetX; x < w; x += iterations) {
                        bitmap[y * w + x] = pixel.getPixelColorAt(x, y);
                    }
                }
                reportProgress();
            }
        }
    }
}
