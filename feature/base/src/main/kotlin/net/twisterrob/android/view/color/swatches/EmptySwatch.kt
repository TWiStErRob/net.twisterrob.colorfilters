package net.twisterrob.android.view.color.swatches

import android.graphics.Canvas
import android.graphics.Color

class EmptySwatch : Swatch() {

	override var currentColor: Int = Color.TRANSPARENT

	override fun findColor(area: Int, x: Float, y: Float): Int {
		return currentColor
	}

	override fun draw(canvas: Canvas) {
		canvas.drawColor(currentColor)
	}
}
