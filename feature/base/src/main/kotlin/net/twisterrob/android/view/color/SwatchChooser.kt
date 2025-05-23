package net.twisterrob.android.view.color

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.PixelFormat
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.MotionEvent
import android.view.View
import androidx.core.graphics.applyCanvas
import androidx.core.graphics.createBitmap
import net.twisterrob.android.view.color.swatches.PixelAbsoluteSwatch
import net.twisterrob.android.view.color.swatches.Swatch
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.sqrt

class SwatchChooser(swatches: Collection<Swatch>) : Drawable(), View.OnTouchListener {

	interface OnSwatchChangeListener {

		fun swatchSelected(swatch: Swatch)
	}

	private val swatches: Array<Swatch> = swatches.toTypedArray()
	private val locations: Array<Rect?> = arrayOfNulls(this.swatches.size)
	var onSwatchChangeListener: OnSwatchChangeListener? = null
	private var margin: Int = 0
	private var image: Bitmap? = null /* by lazy { build }, but with with setter */
		get() {
			field = field ?: build(bounds.width(), bounds.height())
			return field
		}

	fun setTileMargin(margin: Int) {
		this.margin = margin
		layout()
	}

	override fun draw(canvas: Canvas) {
		canvas.drawBitmap(requireNotNull(image) { "Missing image!" }, 0f, 0f, null)
	}

	override fun onBoundsChange(bounds: Rect) {
		super.onBoundsChange(bounds)
		layout()
		image = null
	}

	@Suppress("NestedBlockDepth") // Needs a rethink.
	private fun layout() {
		val bounds = bounds
		val w = bounds.width()
		val h = bounds.height()
		if (w == 0 || h == 0) {
			return
		}
		var s = side2(w, h, swatches.size).toInt()
		if (s <= margin * 2) {
			return
		}
		val nx = w / s
		val ny = h / s
		val left = (w - nx * s) / 2
		val realNY = ceil(swatches.size.toFloat() / nx.toFloat()).toInt()
		val top = (h - realNY * s) / 2
		s -= margin * 2 // reserve size for margin
		val swatchBounds = Rect(left, top, left + s, top + s)
		for (y in 0 until ny) {
			for (x in 0 until nx) {
				val i = y * ny + x
				if (i < swatches.size) { // there may be a gap: say 3x3, but we only have 8 swatches
					locations[i] = Rect(swatchBounds).apply {
						val dx = x * (s + 2 * margin) + margin
						val dy = y * (s + 2 * margin) + margin
						offset(dx, dy)
					}
				}
			}
		}
	}

	private fun build(w: Int, h: Int): Bitmap =
		createBitmap(w, h).applyCanvas {
			val swatchBounds = Rect()
			for (i in swatches.indices) {
				val beforeDraw = this.save()
				try {
					val location = locations[i] ?: continue
					swatchBounds.set(location) // copy location
					this.translate(
						swatchBounds.left.toFloat(),
						swatchBounds.top.toFloat()
					) // offset canvas
					swatchBounds.offsetTo(0, 0) // unoffset rect

					val sw = swatches[i]
					(sw as? PixelAbsoluteSwatch)?.forceSync()
					sw.bounds = swatchBounds
					sw.draw(this)
				} finally {
					this.restoreToCount(beforeDraw)
				}
			}
		}

	fun at(x: Int, y: Int): Swatch? {
		for (i in locations.indices) {
			val rect = locations[i]
			if (rect != null && rect.contains(x, y)) {
				return swatches[i]
			}
		}
		return null
	}

	/**
	 * https://math.stackexchange.com/questions/466198/algorithm-to-get-the-maximum-size-of-n-squares-that-fit-into-a-rectangle-with-a/
	 */
	private fun side2(w: Int, h: Int, n: Int): Double {
		val px = ceil(sqrt((n * w / h).toDouble()))
		val sx =
			if (floor(px * h / w) * px < n) { // does not fit, h / (w/px) = px * h/w
				h / ceil(px * h / w)
			} else {
				w / px
			}
		val py = ceil(sqrt((n * h / w).toDouble()))
		val sy =
			if (floor(py * w / h) * py < n) { // does not fit
				w / ceil(w * py / h)
			} else {
				h / py
			}
		return max(sx, sy)
	}

	override fun setAlpha(alpha: Int) {
		// Alpha not supported.
	}

	override fun setColorFilter(cf: ColorFilter?) {
		// Color filters are not supported, will always render custom draw.
	}

	// https://stackoverflow.com/a/78595315/253468
	@Suppress("OVERRIDE_DEPRECATION") // Still used in API <29.
	@SuppressLint("UseRequiresApi")
	@TargetApi(Build.VERSION_CODES.Q) // This is a lie, but ObsoleteSdkInt will flag this method when minSdk goes above.
	override fun getOpacity() = PixelFormat.UNKNOWN

	override fun onTouch(v: View, event: MotionEvent): Boolean {
		when (event.action) {
			MotionEvent.ACTION_DOWN -> {
				val x = (event.x - v.paddingLeft).toInt()
				val y = (event.y - v.paddingTop).toInt()
				onSwatchChangeListener?.apply {
					val swatch = at(x, y)
					swatch?.let { swatchSelected(it) }
				}
				v.performClick()
				return true
			}
		}
		return false
	}
}
