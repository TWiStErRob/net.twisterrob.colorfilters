package net.twisterrob.android.view.color.swatches.pixel.drawer

import net.twisterrob.android.view.color.swatches.pixel.color.PixelColor

class LineByLineBitmapDrawer(
	bitmap: IntArray, w: Int, h: Int, pixel: PixelColor
) : BitmapDrawer(bitmap, w, h, pixel) {

	override fun fillPixels() {
		val w = this.w
		val h = this.h
		for (y in 0 until h) {
			for (x in 0 until w) {
				bitmap[y * w + x] = pixel.getPixelColorAt(x, y)
			}
			reportProgress()
		}
	}

	companion object {

		@JvmStatic
		fun factory() = object : BitmapDrawer.Factory {
			override fun create(bitmap: IntArray, w: Int, h: Int, pixel: PixelColor) =
				LineByLineBitmapDrawer(bitmap, w, h, pixel)
		}
	}
}
