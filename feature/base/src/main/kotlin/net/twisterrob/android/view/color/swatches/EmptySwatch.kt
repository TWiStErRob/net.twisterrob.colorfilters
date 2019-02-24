package net.twisterrob.android.view.color.swatches

import android.graphics.Canvas
import android.graphics.Color
import androidx.annotation.ColorInt

class EmptySwatch : Swatch() {

	@ColorInt
	override var currentColor: Int = Color.TRANSPARENT

	@ColorInt
	override fun findColor(area: AreaCode, x: Float, y: Float): Int {
		return currentColor
	}

	override fun draw(canvas: Canvas) {
		canvas.drawColor(currentColor)
	}
}
