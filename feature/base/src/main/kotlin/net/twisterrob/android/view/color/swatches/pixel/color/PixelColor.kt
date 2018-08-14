package net.twisterrob.android.view.color.swatches.pixel.color

interface PixelColor {

	fun initializeInvariants(w: Int, h: Int)

	fun getPixelColorAt(x: Int, y: Int): Int
}
