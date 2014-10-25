package net.twisterrob.android.view.color.swatches;

import android.graphics.Canvas;

public class EmptySwatch extends Swatch {
	private int color;

	@Override
	public int getCurrentColor() {
		return color;
	}

	@Override
	public void setCurrentColor(int color) {
		this.color = color;
	}

	@Override
	public int findColor(int trackedArea, float x, float y) {
		return color;
	}

	@Override
	public void setCurrentArea(int areaCode) {
	}

	@Override
	public void draw(Canvas canvas) {
		canvas.drawColor(color);
	}
}
