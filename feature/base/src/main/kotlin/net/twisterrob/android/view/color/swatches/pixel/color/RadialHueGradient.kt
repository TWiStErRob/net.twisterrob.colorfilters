package net.twisterrob.android.view.color.swatches.pixel.color

import net.twisterrob.android.view.color.FastMath
import net.twisterrob.android.view.color.PI
import net.twisterrob.android.view.color.fromHsb

class RadialHueGradient : PixelColor {

	private var w: Int = 0
	private var h: Int = 0

	override fun initializeInvariants(w: Int, h: Int) {
		this.w = w
		this.h = h
	}

	override fun getPixelColorAt(x: Int, y: Int): Int {
		val angle = FastMath.Atan2Faster.atan2((y - w / 2).toFloat(), (x - h / 2).toFloat()) + PI
		val hue = angle / (PI * 2)
		return fromHsb(hue, 1f, 1f, 1f)
	}
}
