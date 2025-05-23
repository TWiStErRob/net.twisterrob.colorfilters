package net.twisterrob.android.view.color.swatches

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.PixelFormat
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.annotation.ColorInt
import androidx.annotation.IntRange

// TODO inline class AreaCode(val value: Int)
typealias AreaCode = Int

abstract class Swatch : Drawable() {

	@get:ColorInt abstract var currentColor: Int

	@ColorInt abstract fun findColor(area: AreaCode, x: Float, y: Float): Int

	protected fun invalidArea(area: AreaCode, x: Float, y: Float): RuntimeException =
		IllegalStateException("Cannot find color for area $area at $x, $y")

	abstract override fun draw(canvas: Canvas)

	open fun getAreaCode(x: Float, y: Float): AreaCode = AREA_DEFAULT

	open fun setCurrentArea(areaCode: AreaCode) {
		// Track area code, most swatches have only one area. Optional override.
	}

	override fun setAlpha(@IntRange(from = 0, to = 255) alpha: Int) {
		// Default implementation, alpha is not supported, will mostly render custom draw.
	}

	override fun setColorFilter(cf: ColorFilter?) {
		// Default implementation, color filters are not supported, will mostly render custom draw.
	}

	// See https://stackoverflow.com/a/78595315/253468
	@Suppress("OVERRIDE_DEPRECATION") // Still used in API <29.
	@SuppressLint("UseRequiresApi")
	@TargetApi(Build.VERSION_CODES.Q) // This is a lie, but ObsoleteSdkInt will flag this method when minSdk goes above.
	override fun getOpacity(): Int = PixelFormat.UNKNOWN

	open fun triggersColorChange(trackedArea: AreaCode): Boolean = true

	companion object {

		const val AREA_INVALID: AreaCode = -1
		const val AREA_DEFAULT: AreaCode = 0
	}
}
