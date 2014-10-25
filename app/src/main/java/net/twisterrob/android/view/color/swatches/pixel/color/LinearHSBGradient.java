package net.twisterrob.android.view.color.swatches.pixel.color;

import net.twisterrob.android.view.color.ColorMath;

public class LinearHSBGradient implements PixelColor {
	private int w, h;

	@Override
	public void initializeInvariants(int w, int h) {
		this.w = w;
		this.h = h;
	}

	@Override
	public int getPixelColorAt(int x, int y) {
		float hue = (float)x / w;
		float sat = ColorMath.ofMap(y, 0, h / 2, 0, 1);
		float bri = ColorMath.ofMap(y, h / 2, h, 1, 0);
		return ColorMath.fromHsb(hue, sat, bri, 1);
	}
}
