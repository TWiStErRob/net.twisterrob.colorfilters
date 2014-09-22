package net.twisterrob.android.view.color.swatches.pixel.drawer;

public class DrawerUtils {
    public static void drawBorder(BitmapDrawer drawer, int color, int width) {
        int h = drawer.h;
        int w = drawer.w;
        int[] bitmap = drawer.bitmap;
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < width; x++) {
                bitmap[y * w + x] = color;
                bitmap[y * w + (w - x - 1)] = color;
            }
        }
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < width; y++) {
                bitmap[y * w + x] = color;
                bitmap[(h - y - 1) * w + x] = color;
            }
        }
    }
}
