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
			BitmapDrawer.Factory { bitmap, w, h, color ->
				factory.create(
					bitmap,
					w,
					h,
					ColorReplacer(color, findColor, replaceColor)
				)
			}
	}
}
