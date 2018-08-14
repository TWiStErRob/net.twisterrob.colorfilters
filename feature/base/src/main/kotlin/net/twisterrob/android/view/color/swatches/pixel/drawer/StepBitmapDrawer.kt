package net.twisterrob.android.view.color.swatches.pixel.drawer

import net.twisterrob.android.view.color.swatches.pixel.color.PixelColor

class StepBitmapDrawer(
	bitmap: IntArray, w: Int, h: Int, pixel: PixelColor
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
