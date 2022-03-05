package net.twisterrob.android.view.color.swatches.pixel.drawer

import androidx.annotation.ColorInt
import androidx.annotation.IntRange
import net.twisterrob.android.view.color.swatches.pixel.color.PixelColor

class StepBitmapDrawer(
	@ColorInt
	bitmap: IntArray,
	@IntRange(from = 0)
	w: Int,
	@IntRange(from = 0)
	h: Int,
	pixel: PixelColor
) : BitmapDrawer(bitmap, w, h, pixel) {

	override fun fillPixels() {
		val w = this.w
		val h = this.h
		val iterations = 3
		val iterationStep = 1 // must be relative prime to iterations (e.g.: 5,3)
		for (stepY in 0 until iterations) {
			for (stepX in 0 until iterations) {
				val offsetX = (stepX * iterationStep) % iterations
				val offsetY = (stepY * iterationStep) % iterations
				// for (y in offsetY until h step iterations)?
				var y = offsetY
				while (y < h) {
					// for (x in offsetX until w step iterations)?
					var x = offsetX
					while (x < w) {
						bitmap[y * w + x] = pixel.getPixelColorAt(x, y)
						x += iterations
					}
					y += iterations
				}
				reportProgress()
			}
		}
	}
}
