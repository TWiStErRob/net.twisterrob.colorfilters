package net.twisterrob.android.view.color.swatches;

import android.graphics.*;

import androidx.annotation.ColorInt;
import androidx.annotation.FloatRange;
import androidx.annotation.NonNull;

import net.twisterrob.android.view.color.ColorMath;

// Based on https://github.com/android/platform_development/blob/android-sdk-adt_r20/samples/ApiDemos/src/com/example/android/apis/graphics/ColorPickerDialog.java
public class APIDemoSwatch extends Swatch {
	private static final int /*AreaCode*/ AREA_CENTER = 1;

	@ColorInt
	private static final int[] COLORS =
			new int[] {0xFFFF0000, 0xFFFF00FF, 0xFF0000FF, 0xFF00FFFF, 0xFF00FF00, 0xFFFFFF00, 0xFFFF0000};

	private static final Shader SHADER = new SweepGradient(0, 0, COLORS, null);

	protected final @NonNull Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	protected final @NonNull Paint mCenterPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	private /*AreaCode*/ int currentArea;

	public APIDemoSwatch() {
		mPaint.setShader(SHADER);
		mPaint.setStyle(Paint.Style.STROKE);
	}

	@Override
	public @ColorInt int getCurrentColor() {
		return mCenterPaint.getColor();
	}

	@Override
	public void setCurrentColor(@ColorInt int color) {
		mCenterPaint.setColor(color);
	}

	@Override
	public void setCurrentArea(/*AreaCode*/ int areaCode) {
		currentArea = areaCode;
	}

	@Override
	public void draw(@NonNull Canvas canvas) {
		Rect bounds = getBounds();

		mPaint.setStrokeWidth(bounds.width() * 0.2f);
		mCenterPaint.setStrokeWidth(bounds.width() * 0.05f);

		float r = bounds.exactCenterX() - mPaint.getStrokeWidth() * 0.5f;
		float cr = getCenterRadius();

		canvas.translate(bounds.exactCenterX(), bounds.exactCenterY());

		canvas.drawCircle(0, 0, r, mPaint);
		canvas.drawCircle(0, 0, cr, mCenterPaint);

		if (currentArea == AREA_CENTER) {
			int c = mCenterPaint.getColor();
			mCenterPaint.setStyle(Paint.Style.STROKE);
			mCenterPaint.setAlpha(0xFF);

			canvas.drawCircle(0, 0, cr + mCenterPaint.getStrokeWidth(), mCenterPaint);

			mCenterPaint.setStyle(Paint.Style.FILL);
			mCenterPaint.setColor(c);
		}
	}

	@Override
	public /*AreaCode*/ int getAreaCode(float x, float y) {
		x -= getBounds().exactCenterX();
		y -= getBounds().exactCenterY();
		return Math.sqrt(x * x + y * y) <= getCenterRadius()? AREA_CENTER : AREA_DEFAULT;
	}

	@Override
	public int findColor(/*AreaCode*/ int area, float x, float y) {
		switch (area) {
			case AREA_DEFAULT:
				x -= getBounds().exactCenterX();
				y -= getBounds().exactCenterY();
				float unit = APIMath.angleAsUnit(x, y);
				return APIMath.interpColor(COLORS, unit);
			case AREA_CENTER:
				return getCurrentColor();
			default:
				throw invalidArea(area, x, y);
		}
	}

	@Override
	public boolean triggersColorChange(/*AreaCode*/ int area) {
		return area == AREA_CENTER;
	}

	protected float getCenterRadius() {
		return getRadius() * .25f;
	}

	protected int getRadius() {
		return Math.min(getBounds().width(), getBounds().height()) / 2;
	}

	private static class APIMath {
		/**
		 * Calculates the interpolated color from the equidistant array of colors based on the value given.
		 *
		 * @param colors equidistant array of colors
		 * @param unit   which color to pick in {@code [0, 1)} range
		 * @return interpolated color
		 */
		@ColorInt
		public static int interpColor(
				@ColorInt int colors[],
				@FloatRange(from = 0.0, to = 1.0, toInclusive = false) float unit
		) {
			if (unit <= 0) { // (-∞, 0] = 0
				return colors[0];
			}
			if (1 <= unit) { // [1, ∞) = 1
				return colors[colors.length - 1];
			}
			// unit in (0, 1)

			float p = unit * (colors.length - 1); // (0.0, n - 1)
			int i = (int)p; // [0, n - 1]
			p -= i; // [0.0, 1.0)

			int c0 = colors[i];
			int c1 = colors[i + 1];
			int a = lerp(Color.alpha(c0), Color.alpha(c1), p); // a0 <= ..p.. a ..(1-p).. <= a1
			int r = lerp(Color.red(c0), Color.red(c1), p);     // r0 <= ..p.. r ..(1-p).. <= r1
			int g = lerp(Color.green(c0), Color.green(c1), p); // g0 <= ..p.. g ..(1-p).. <= g1
			int b = lerp(Color.blue(c0), Color.blue(c1), p);   // b0 <= ..p.. b ..(1-p).. <= g1

			return Color.argb(a, r, g, b);
		}

		private static int lerp(int s, int d, float p) {
			return s + Math.round(p * (d - s));
		}

		/**
		 * Calculates the angular angle of {@code x, y} point relative to {@code 0, 0} and scales the result to the unit range {@code [0, 1)}.
		 * <p>
		 * The result resembles that of {@link Math#atan2(double, double) atan2} (i.e. +x axis = 0 and counter-clockwise), resulting in the following ranges:
		 * <table>
		 * <tr><th>sgn(x)</th><th>sgn(y)</th><th>atan2 range in radians</th><th>range in degrees</th><th>resulting range</th></tr>
		 * <tr><th>+</th><th>+</th><td>[0, pi/2)</td><td>[0°, 90°)</td><td>[0, 0.25)</td></tr>
		 * <tr><th>-</th><th>+</th><td>[0, +3pi/4)</td><td> [90°, 180°)</td><td>[0.25, 0.5)</td></tr>
		 * <tr><th>-</th><th>-</th><td>[0, -3pi/4)</td><td>[-180°, -90°)<br/>[180°, 270°)</td><td>[0.5, 0.75)</td></tr>
		 * <tr><th>+</th><th>-</th><td>[0, -pi/2)</td><td>[-90°, 0°)<br/>[270°, 0°)</td><td> [0.75, 1)</td></tr>
		 * </table>
		 * Note: since {@code -pi = +pi} ({@code -180° = +180°}) the mapping will map both to {@code 0.5} which results in never returning {@code 1} from this method.
		 * </p>
		 *
		 * @param x x coordinate
		 * @param y y coordinate
		 * @return angle mapped to {@code [0, 1)} (see table)
		 */
		@FloatRange(from = 0.0, to = 1.0, toInclusive = false)
		public static float angleAsUnit(float x, float y) {
			@FloatRange(from = -ColorMath.PI, to = ColorMath.PI)
			float angle = (float)Math.atan2(y, x);

			float unit = angle / (2 * ColorMath.PI); // [-0.5, +0.5]
			if (unit < 0) {
				unit += 1; // [-0.5, 0) -> [0.5, 1)
			}
			return unit; // [0, 1)
		}
	}
}
