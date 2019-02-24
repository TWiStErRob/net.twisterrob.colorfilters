package net.twisterrob.android.view.color.swatches.pixel.drawer

import androidx.annotation.ColorInt
import androidx.annotation.IntRange
import net.twisterrob.android.view.color.swatches.pixel.color.PixelColor

class ColumnByColumnBitmapDrawer(
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
		for (x in 0 until w) {
			for (y in 0 until h) {
				bitmap[y * w + x] = pixel.getPixelColorAt(x, y)
			}
			reportProgress()
		}
	}
}
