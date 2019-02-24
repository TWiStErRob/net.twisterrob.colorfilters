package net.twisterrob.android.view.color.swatches.pixel.color

import androidx.annotation.ColorInt
import androidx.annotation.IntRange
import net.twisterrob.android.view.color.fromHsb

class LinearHueGradient : PixelColor {

	@IntRange(from = 0)
	private var w: Int = 0

	override fun initializeInvariants(@IntRange(from = 0) w: Int, @IntRange(from = 0) h: Int) {
		this.w = w
	}

	@ColorInt
	override fun getPixelColorAt(@IntRange(from = 0/*, to = w*/) x: Int, @IntRange(from = 0/*, to = h*/) y: Int): Int {
		return fromHsb(x.toFloat() / w.toFloat(), 1.0f, 1.0f, 1.0f)
	}
}
