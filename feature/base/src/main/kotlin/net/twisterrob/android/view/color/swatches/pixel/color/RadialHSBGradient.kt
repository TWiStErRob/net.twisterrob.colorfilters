package net.twisterrob.android.view.color.swatches.pixel.color

import androidx.annotation.ColorInt
import androidx.annotation.IntRange
import net.twisterrob.android.view.color.FastMath
import net.twisterrob.android.view.color.PI
import net.twisterrob.android.view.color.fromHsb
import net.twisterrob.android.view.color.ofMap
import kotlin.math.sqrt

class RadialHSBGradient : PixelColor {

	@IntRange(from = 0)
	private var w: Int = 0

	@IntRange(from = 0)
	private var cx: Int = 0

	@IntRange(from = 0)
	private var cy: Int = 0

	override fun initializeInvariants(@IntRange(from = 0) w: Int, @IntRange(from = 0) h: Int) {
		this.w = w
		this.cx = w / 2
		this.cy = w / 2
	}

	@ColorInt
	override fun getPixelColorAt(@IntRange(from = 0/*, to = w*/) x: Int, @IntRange(from = 0/*, to = h*/) y: Int): Int {
		val angle = FastMath.Atan2Faster.atan2((y - cy).toFloat(), (x - cx).toFloat()) + PI
		val hue = angle / (PI * 2)
		val dist = sqrt(((x - cx) * (x - cx) + (y - cy) * (y - cy)).toDouble()).toFloat()
		val sat = ofMap(dist, 0, w / 4, 0, 1)
		val bri = ofMap(dist, w / 4, w / 2, 1, 0)
		return fromHsb(hue, sat, bri, 1.0f)
	}
}
