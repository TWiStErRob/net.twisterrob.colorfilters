package net.twisterrob.android.view.color.swatches.pixel.color

import net.twisterrob.android.view.color.fromHsb

class LinearHueGradient : PixelColor {

	private var w: Int = 0

	override fun initializeInvariants(w: Int, h: Int) {
		this.w = w
	}

	override fun getPixelColorAt(x: Int, y: Int): Int {
		return fromHsb(x.toFloat() / w.toFloat(), 1.0f, 1.0f, 1.0f)
	}
}
