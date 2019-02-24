package net.twisterrob.android.view.color.swatches.pixel.color

import androidx.annotation.ColorInt
import androidx.annotation.IntRange
import net.twisterrob.android.view.color.swatches.pixel.drawer.BitmapDrawerFactory

class ColorReplacer(
	private val pixelColor: PixelColor,
	@ColorInt
	private val findColor: Int,
	@ColorInt
	private val replaceColor: Int
) : PixelColor by pixelColor {

	@ColorInt
	override fun getPixelColorAt(@IntRange(from = 0/*, to = w*/) x: Int, @IntRange(from = 0/*, to = h*/) y: Int): Int {
		val color = pixelColor.getPixelColorAt(x, y)
		return if (color == findColor) replaceColor else color
	}

	companion object {

		@JvmStatic
		fun wrap(factory: BitmapDrawerFactory, @ColorInt findColor: Int, @ColorInt replaceColor: Int): BitmapDrawerFactory =
			{ bitmap, w, h, pixel ->
				factory(bitmap, w, h, ColorReplacer(pixel, findColor, replaceColor))
			}
	}
}
