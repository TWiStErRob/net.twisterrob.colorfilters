package net.twisterrob.android.view.color.swatches.pixel.color

import net.twisterrob.android.view.color.swatches.pixel.drawer.BitmapDrawer
import net.twisterrob.android.view.color.swatches.pixel.drawer.BitmapDrawer.Factory

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
		fun wrap(factory: Factory, findColor: Int, replaceColor: Int) =
			object : BitmapDrawer.Factory {
				override fun create(bitmap: IntArray, w: Int, h: Int, pixel: PixelColor) =
					factory.create(bitmap, w, h, ColorReplacer(pixel, findColor, replaceColor))
			}
	}
}
