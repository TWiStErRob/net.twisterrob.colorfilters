package net.twisterrob.android.view.color.swatches.pixel.color

import net.twisterrob.android.view.color.FastMath
import net.twisterrob.android.view.color.PI
import net.twisterrob.android.view.color.fromHsb
import net.twisterrob.android.view.color.ofMap
import kotlin.math.sqrt

class RadialHSBGradient : PixelColor {

	private var w: Int = 0
	private var cx: Int = 0
	private var cy: Int = 0

	override fun initializeInvariants(w: Int, h: Int) {
		this.w = w
		this.cx = w / 2
		this.cy = w / 2
	}

	override fun getPixelColorAt(x: Int, y: Int): Int {
		val angle = FastMath.Atan2Faster.atan2((y - cy).toFloat(), (x - cx).toFloat()) + PI
		val hue = angle / (PI * 2)
		val dist = sqrt(((x - cx) * (x - cx) + (y - cy) * (y - cy)).toDouble()).toFloat()
		val sat = ofMap(dist, 0, w / 4, 0, 1)
		val bri = ofMap(dist, w / 4, w / 2, 1, 0)
		return fromHsb(hue, sat, bri, 1.0f)
	}
}
