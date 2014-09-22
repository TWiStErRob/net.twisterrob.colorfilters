package net.twisterrob.android.view.color.swatches.pixel.color;

import net.twisterrob.android.view.color.swatches.pixel.drawer.BitmapDrawer;

public class ColorReplacer implements PixelColor {
    private final PixelColor pixelColor;
    private final int findColor;
    private final int replaceColor;

    public ColorReplacer(PixelColor pixelColor, int findColor, int replaceColor) {
        this.pixelColor = pixelColor;
        this.findColor = findColor;
        this.replaceColor = replaceColor;
    }

    @Override
    public void initializeInvariants(int w, int h) {
        pixelColor.initializeInvariants(w, h);
    }

    @Override
    public int getPixelColorAt(int x, int y) {
        int color = pixelColor.getPixelColorAt(x, y);
        return color != findColor ? color : replaceColor;
    }

    public static BitmapDrawer.Factory wrap(final BitmapDrawer.Factory factory, final int findColor, final int replaceColor) {
        return new BitmapDrawer.Factory() {
            @Override
            public BitmapDrawer create(int[] bitmap, int w, int h, PixelColor color) {
                return factory.create(bitmap, w, h, new ColorReplacer(color, findColor, replaceColor));
            }
        };
    }
}
