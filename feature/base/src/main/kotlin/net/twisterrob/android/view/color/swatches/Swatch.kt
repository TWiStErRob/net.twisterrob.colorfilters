package net.twisterrob.android.view.color.swatches

import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.PixelFormat
import android.graphics.drawable.Drawable
import androidx.annotation.ColorInt
import androidx.annotation.IntRange

abstract class Swatch : Drawable() {

	@get:ColorInt abstract var currentColor: Int

	@ColorInt abstract fun findColor(area: Int, x: Float, y: Float): Int

	protected fun invalidArea(area: Int, x: Float, y: Float): RuntimeException =
		IllegalStateException("Cannot find color for area $area at $x, $y")

	abstract override fun draw(canvas: Canvas)

	open fun getAreaCode(x: Float, y: Float): Int = AREA_DEFAULT

	open fun setCurrentArea(areaCode: Int) {}

	override fun setAlpha(@IntRange(from = 0, to = 255) alpha: Int) {}

	override fun setColorFilter(cf: ColorFilter?) {}

	override fun getOpacity(): Int = PixelFormat.UNKNOWN

	open fun triggersColorChange(trackedArea: Int): Boolean = true

	companion object {
		const val AREA_INVALID = -1
		const val AREA_DEFAULT = 0
	}
}
