package net.twisterrob.android.view.color.swatches

import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.PixelFormat
import android.graphics.drawable.Drawable
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

	open fun setCurrentArea(areaCode: AreaCode) {}

	override fun setAlpha(@IntRange(from = 0, to = 255) alpha: Int) {}

	override fun setColorFilter(cf: ColorFilter?) {}

	override fun getOpacity(): Int = PixelFormat.UNKNOWN

	open fun triggersColorChange(trackedArea: AreaCode): Boolean = true

	companion object {

		const val AREA_INVALID: AreaCode = -1
		const val AREA_DEFAULT: AreaCode = 0
	}
}
