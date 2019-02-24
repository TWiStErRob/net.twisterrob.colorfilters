package net.twisterrob.android.view.color.swatches.pixel.color

import androidx.annotation.ColorInt
import androidx.annotation.IntRange
import net.twisterrob.android.view.color.FastMath
import net.twisterrob.android.view.color.PI
import net.twisterrob.android.view.color.fromHsb

class TriangularRadialHueGradient : PixelColor {

	@IntRange(from = 0)
	private var w: Int = 0

	@IntRange(from = 0)
	private var h: Int = 0

	override fun initializeInvariants(@IntRange(from = 0) w: Int, @IntRange(from = 0) h: Int) {
		this.w = w
		this.h = h
	}

	@ColorInt
	override fun getPixelColorAt(@IntRange(from = 0/*, to = w*/) x: Int, @IntRange(from = 0/*, to = h*/) y: Int): Int {
		val angle = FastMath.Atan2Faster.atan2((y - w / 2).toFloat(), (x - h / 2).toFloat()) + PI
		val hue = angle / (PI * 2)
		return fromHsb(hue, 1.0f, 1.0f, 1.0f)
	}
}
