package net.twisterrob.android.view.color.swatches.pixel.color

import net.twisterrob.android.view.color.swatches.pixel.drawer.BitmapDrawerFactory

class ColorReplacer(
	private val pixelColor: PixelColor,
	private val findColor: Int,
	private val replaceColor: Int
) : PixelColor by pixelColor {

	override fun getPixelColorAt(x: Int, y: Int): Int {
		val color = pixelColor.getPixelColorAt(x, y)
		return if (color == findColor) replaceColor else color
	}

	companion object {

		@JvmStatic
		fun wrap(factory: BitmapDrawerFactory, findColor: Int, replaceColor: Int): BitmapDrawerFactory =
			{ bitmap, w, h, pixel ->
				factory(bitmap, w, h, ColorReplacer(pixel, findColor, replaceColor))
			}
	}
}
