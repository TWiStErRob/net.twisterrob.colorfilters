package net.twisterrob.android.view.color.swatches.pixel.color

import net.twisterrob.android.view.color.fromHsb
import net.twisterrob.android.view.color.ofMap

class LinearHSBGradient : PixelColor {

	private var w: Int = 0
	private var h: Int = 0

	override fun initializeInvariants(w: Int, h: Int) {
		this.w = w
		this.h = h
	}

	override fun getPixelColorAt(x: Int, y: Int): Int {
		val hue = x.toFloat() / w.toFloat()
		val sat = ofMap(y, 0, h / 2, 0, 1)
		val bri = ofMap(y, h / 2, h, 1, 0)
		return fromHsb(hue, sat, bri, 1f)
	}
}
