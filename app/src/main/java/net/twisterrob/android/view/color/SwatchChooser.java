package net.twisterrob.android.view.color;

import java.util.Collection;

import android.graphics.*;
import android.graphics.drawable.Drawable;
import android.view.*;

import net.twisterrob.android.view.color.swatches.*;

public class SwatchChooser extends Drawable implements View.OnTouchListener {
	public interface OnSwatchChangeListener {
		void swatchSelected(Swatch swatch);
	}

	private final Swatch[] swatches;
	private final Rect[] locations;
	private OnSwatchChangeListener listener;
	private int margin;
	private Bitmap image;

	public SwatchChooser(Collection<? extends Swatch> swatches) {
		this.swatches = swatches.toArray(new Swatch[swatches.size()]);
		this.locations = new Rect[this.swatches.length];
	}

	public void setOnSwatchChangeListener(OnSwatchChangeListener onSwatchChangeListener) {
		this.listener = onSwatchChangeListener;
	}

	public void setTileMargin(int margin) {
		this.margin = margin;
		layout();
	}

	@Override
	public void draw(Canvas canvas) {
		if (image == null) {
			image = build(getBounds().width(), getBounds().height());
		}
		canvas.drawBitmap(image, 0, 0, null);
	}

	@Override
	protected void onBoundsChange(Rect bounds) {
		super.onBoundsChange(bounds);
		layout();
		image = null;
	}

	private void layout() {
		Rect bounds = getBounds();
		int w = bounds.width();
		int h = bounds.height();
		if (w == 0 || h == 0) {
			return;
		}
		int s = (int)side2(w, h, swatches.length);
		if (s <= margin * 2) {
			return;
		}
		int nx = w / s;
		int ny = h / s;
		int left = (w - nx * s) / 2;
		int realNY = (int)(swatches.length / (float)nx + 0.5f);
		int top = (h - realNY * s) / 2;
		s -= margin * 2; // reserve size for margin
		Rect swatchBounds = new Rect(left, top, left + s, top + s);
		for (int y = 0; y < ny; ++y) {
			for (int x = 0; x < nx; ++x) {
				int i = y * ny + x;
				if (i < swatches.length) { // there may be a gap: say 3x3, but we only have 8 swatches
					locations[i] = new Rect(swatchBounds);
					int dx = x * (s + 2 * margin) + margin;
					int dy = y * (s + 2 * margin) + margin;
					locations[i].offset(dx, dy);
				}
			}
		}
	}

	private Bitmap build(int w, int h) {
		Bitmap image = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(image);
		Rect swatchBounds = new Rect();
		for (int i = 0; i < swatches.length; ++i) {
			int beforeDraw = canvas.save();
			try {
				if (locations[i] == null) {
					continue;
				}
				swatchBounds.set(locations[i]); // copy location
				canvas.translate(swatchBounds.left, swatchBounds.top); // offset canvas
				swatchBounds.offsetTo(0, 0); // unoffset rect

				Swatch sw = swatches[i];
				if (sw instanceof PixelAbsoluteSwatch) {
					((PixelAbsoluteSwatch)sw).forceSync();
				}
				sw.setBounds(swatchBounds);
				sw.draw(canvas);
			} finally {
				canvas.restoreToCount(beforeDraw);
			}
		}
		return image;
	}

	public Swatch at(int x, int y) {
		for (int i = 0; i < locations.length; i++) {
			Rect rect = locations[i];
			if (rect != null && rect.contains(x, y)) {
				return swatches[i];
			}
		}
		return null;
	}

	/**
	 * https://math.stackexchange.com/questions/466198/algorithm-to-get-the-maximum-size-of-n-squares-that-fit-into-a-rectangle-with-a/
	 */
	private static double side2(int w, int h, int n) {
		double px = Math.ceil(Math.sqrt(n * w / h));
		double sx, sy;
		if (Math.floor(px * h / w) * px < n) { //does not fit, h / (w/px) = px * h/w
			sx = h / Math.ceil(px * h / w);
		} else {
			sx = w / px;
		}
		double py = Math.ceil(Math.sqrt(n * h / w));
		if (Math.floor(py * w / h) * py < n) { //does not fit
			sy = w / Math.ceil(w * py / h);
		} else {
			sy = h / py;
		}
		return Math.max(sx, sy);
	}

	@Override
	public void setAlpha(int alpha) {

	}

	@Override
	public void setColorFilter(ColorFilter cf) {

	}

	@Override
	public int getOpacity() {
		return 0;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				int x = (int)(event.getX() - v.getPaddingLeft());
				int y = (int)(event.getY() - v.getPaddingTop());
				if (listener != null) {
					listener.swatchSelected(at(x, y));
					v.performClick();
				}
				return true;
		}
		return false;
	}
}