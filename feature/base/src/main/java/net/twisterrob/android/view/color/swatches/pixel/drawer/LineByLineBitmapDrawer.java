package net.twisterrob.android.view.color.swatches.pixel.drawer;

import net.twisterrob.android.view.color.swatches.pixel.color.PixelColor;

public class LineByLineBitmapDrawer extends BitmapDrawer {
	public LineByLineBitmapDrawer(int[] bitmap, int w, int h, PixelColor pixel) {
		super(bitmap, w, h, pixel);
	}

	@Override
	protected void fillPixels() {
		int w = this.w;
		int h = this.h;
		for (int y = 0; y < h; ++y) {
			for (int x = 0; x < w; ++x) {
				bitmap[y * w + x] = pixel.getPixelColorAt(x, y);
			}
			reportProgress();
		}
	}

	public static Factory factory() {
		return new Factory() {
			@Override
			public BitmapDrawer create(int[] bitmap, int w, int h, PixelColor color) {
				return new LineByLineBitmapDrawer(bitmap, w, h, color);
			}
		};
	}
}
