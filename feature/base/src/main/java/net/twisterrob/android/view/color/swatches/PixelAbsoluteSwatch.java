package net.twisterrob.android.view.color.swatches;

import android.graphics.*;
import android.support.annotation.NonNull;

import net.twisterrob.android.view.color.swatches.pixel.color.PixelColor;
import net.twisterrob.android.view.color.swatches.pixel.drawer.BitmapDrawer;

public class PixelAbsoluteSwatch extends Swatch {
	private final BitmapDrawer.Factory originalFactory;
	private BitmapDrawer.Factory drawerFactory;
	private final PixelColor pixels;
	private int[] bitmap;
	private int color;

	public PixelAbsoluteSwatch(BitmapDrawer.Factory drawerFactory, PixelColor pixels) {
		this.originalFactory = drawerFactory;
		this.drawerFactory = drawerFactory;
		this.pixels = pixels;
	}

	private final BitmapDrawer.Callback invalidate = new BitmapDrawer.Callback() {
		@Override
		public void drawStared() {
			// ignore (empty bitmap, nothing to draw)
		}

		@Override
		public void drawProgress() {
			invalidateSelf();
		}

		@Override
		public void drawFinished() {
			invalidateSelf();
		}
	};

	@Override
	public void draw(@NonNull Canvas canvas) {
		if (bitmap == null) {
			return;
		}
		Rect bounds = getBounds();
		canvas.drawBitmap(bitmap, 0, bounds.width(), 0, 0, bounds.width(), bounds.height(), true, null);
	}

	@Override
	protected void onBoundsChange(Rect bounds) {
		super.onBoundsChange(bounds);
		int w = bounds.width();
		int h = bounds.height();
		this.bitmap = new int[w * h];
		BitmapDrawer drawer = drawerFactory.create(bitmap, w, h, pixels);
		drawer.setCallback(invalidate);
		drawer.draw();
	}

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
		return pixels.getPixelColorAt((int)x, (int)y);
	}

	public void forceAsync() {
		drawerFactory = BitmapDrawer.Factory.Async.wrap(originalFactory);
	}

	public void forceSync() {
		drawerFactory = BitmapDrawer.Factory.Sync.wrap(originalFactory);
	}

	public void resetAsync() {
		drawerFactory = originalFactory;
	}
}
