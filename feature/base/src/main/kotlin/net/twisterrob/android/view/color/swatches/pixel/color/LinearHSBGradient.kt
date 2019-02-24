package net.twisterrob.android.view.color.swatches.pixel.color

import androidx.annotation.ColorInt
import androidx.annotation.IntRange
import net.twisterrob.android.view.color.fromHsb
import net.twisterrob.android.view.color.ofMap

class LinearHSBGradient : PixelColor {

	private var w: Int = 0
	private var h: Int = 0

	override fun initializeInvariants(@IntRange(from = 0) w: Int, @IntRange(from = 0) h: Int) {
		this.w = w
		this.h = h
	}

	@ColorInt
	override fun getPixelColorAt(@IntRange(from = 0/*, to = w*/) x: Int, @IntRange(from = 0/*, to = h*/) y: Int): Int {
		val hue = x.toFloat() / w.toFloat()
		val sat = ofMap(y, 0, h / 2, 0, 1)
		val bri = ofMap(y, h / 2, h, 1, 0)
		return fromHsb(hue, sat, bri, 1f)
	}
}
