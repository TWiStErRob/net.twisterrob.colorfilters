package net.twisterrob.android.view.color.swatches.pixel.color

import androidx.annotation.ColorInt
import androidx.annotation.IntRange

interface PixelColor {

	fun initializeInvariants(@IntRange(from = 0) w: Int, @IntRange(from = 0) h: Int)

	@ColorInt
	fun getPixelColorAt(@IntRange(from = 0/*, to = w*/) x: Int, @IntRange(from = 0/*, to = h*/) y: Int): Int
}
