package net.twisterrob.android.view.color.swatches.pixel.drawer

import net.twisterrob.android.view.color.swatches.pixel.color.PixelColor

class CenterBitmapDrawer(
	bitmap: IntArray, w: Int, h: Int, pixel: PixelColor
) : BitmapDrawer(bitmap, w, h, pixel) {

	override fun fillPixels() {
		val w = this.w
		val cx = w / 2
		val cy = this.h / 2
		for (cd in 0 until cy) {
			val startX = cx - cd
			val endX = cx + cd
			var startY = cy - cd
			var endY = cy + cd
			for (x in startX..endX) {
				bitmap[startY * w + x] = pixel.getPixelColorAt(x, startY)
			}
			for (x in startX..endX) {
				bitmap[endY * w + x] = pixel.getPixelColorAt(x, endY)
			}
			startY++
			endY--
			for (y in startY..endY) {
				bitmap[y * w + startX] = pixel.getPixelColorAt(startX, y)
			}
			for (y in startY..endY) {
				bitmap[y * w + endX] = pixel.getPixelColorAt(endX, y)
			}
			reportProgress()
		}
		// drawBorder(this, randomColor(), 10)
	}

	companion object {

		@JvmStatic
		@Suppress("ConvertReferenceToLambda")
		fun factory() = BitmapDrawer.Factory(::CenterBitmapDrawer)
	}
}
