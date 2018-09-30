@file:JvmName("ColorMath")

package net.twisterrob.android.view.color

import android.graphics.Color

const val PI: Float = kotlin.math.PI.toFloat()

fun ofMap(value: Int, inputMin: Int, inputMax: Int, outputMin: Int, outputMax: Int): Float =
	ofMap(value.toFloat(), inputMin.toFloat(), inputMax.toFloat(), outputMin.toFloat(), outputMax.toFloat())

fun ofMap(value: Float, inputMin: Int, inputMax: Int, outputMin: Int, outputMax: Int): Float =
	ofMap(value, inputMin.toFloat(), inputMax.toFloat(), outputMin.toFloat(), outputMax.toFloat())

fun ofMap(value: Float, inputMin: Float, inputMax: Float, outputMin: Float, outputMax: Float): Float {
	var outVal = (value - inputMin) / (inputMax - inputMin) * (outputMax - outputMin) + outputMin

	if (outputMax < outputMin) {
		if (outVal < outputMax) {
			outVal = outputMax
		} else if (outVal > outputMin) {
			outVal = outputMin
		}
	} else {
		if (outVal > outputMax) {
			outVal = outputMax
		} else if (outVal < outputMin) {
			outVal = outputMin
		}
	}
	return outVal
}

/**
 * Same as [Color.HSVToColor], but without the weird interface and much faster.
 * Values are not clamped, anything out of range may explode!
 *
 * @param hue        `[0, 1]`
 * @param saturation `[0, 1]`
 * @param brightness `[0, 1]`
 * @param alpha      `[0, 1]`
 * @return RGBA color
 */
fun fromHsb(hue: Float, saturation: Float, brightness: Float, alpha: Float): Int {
	var r = 0f
	var g = 0f
	var b = 0f
	if (brightness == 0f) { // black
		b = 0f
		g = 0f
		r = 0f
	} else if (saturation == 0f) { // grays
		b = brightness
		g = brightness
		r = brightness
	} else {
		val hueSix = hue * 6.0f
		val hueSixCategory = hueSix.toInt()
		val hueSixRemainder = hueSix - hueSixCategory
		val pv = (1.0f - saturation) * brightness
		val qv = (1.0f - saturation * hueSixRemainder) * brightness
		val tv = (1.0f - saturation * (1.0f - hueSixRemainder)) * brightness
		when (hueSixCategory) {
			0, 6 -> { // r
				r = brightness
				g = tv
				b = pv
			}

			1 -> { // g
				r = qv
				g = brightness
				b = pv
			}

			2 -> {
				r = pv
				g = brightness
				b = tv
			}

			3 -> { // b
				r = pv
				g = qv
				b = brightness
			}

			4 -> {
				r = tv
				g = pv
				b = brightness
			}

			5 -> { // back to r
				r = brightness
				g = pv
				b = qv
			}
		}
	}
	return (alpha * 255).toInt() shl 24 or
			((r * 255).toInt() shl 16) or
			((g * 255).toInt() shl 8) or
			((b * 255).toInt() shl 0)
}

fun randomColor(): Int = fromHsb(Math.random().toFloat(), 1.0f, 1.0f, 1.0f)
