package net.twisterrob.android.view.color.swatches.pixel.color;

public interface PixelColor {
    void initializeInvariants(int w, int h);

    int getPixelColorAt(int x, int y);
}
