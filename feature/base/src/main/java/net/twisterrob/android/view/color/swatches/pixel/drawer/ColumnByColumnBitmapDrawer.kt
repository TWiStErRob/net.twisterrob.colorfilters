package net.twisterrob.android.view.color.swatches.pixel.drawer

import net.twisterrob.android.view.color.swatches.pixel.color.PixelColor

class ColumnByColumnBitmapDrawer(
	bitmap: IntArray, w: Int, h: Int, pixel: PixelColor
) : BitmapDrawer(bitmap, w, h, pixel) {

	override fun fillPixels() {
		val w = this.w
		val h = this.h
		for (x in 0 until w) {
			for (y in 0 until h) {
				bitmap[y * w + x] = pixel.getPixelColorAt(x, y)
			}
			reportProgress()
		}
	}

	companion object {

		@JvmStatic
		@Suppress("ConvertReferenceToLambda")
		fun factory() = BitmapDrawer.Factory(::ColumnByColumnBitmapDrawer)
	}
}
