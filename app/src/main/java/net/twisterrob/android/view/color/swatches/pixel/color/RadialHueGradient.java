package net.twisterrob.android.view.color.swatches.pixel.color;

import net.twisterrob.android.view.color.*;

public class RadialHueGradient implements PixelColor {
	private int w, h;

	@Override
	public void initializeInvariants(int w, int h) {
		this.w = w;
		this.h = h;
	}

	@Override
	public int getPixelColorAt(int x, int y) {
		float angle = FastMath.Atan2Faster.atan2(y - w / 2, x - h / 2) + ColorMath.PI;
		float hue = angle / (ColorMath.PI * 2);
		return ColorMath.fromHsb(hue, 1, 1, 1);
	}
}
