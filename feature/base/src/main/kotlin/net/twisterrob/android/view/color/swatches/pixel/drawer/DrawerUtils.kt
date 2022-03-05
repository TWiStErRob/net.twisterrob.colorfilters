@file:JvmName("DrawerUtils")

package net.twisterrob.android.view.color.swatches.pixel.drawer

fun drawBorder(drawer: BitmapDrawer, color: Int, width: Int) {
	val h = drawer.h
	val w = drawer.w
	val bitmap = drawer.bitmap
	for (y in 0 until h) {
		for (x in 0 until width) {
			bitmap[y * w + x] = color
			bitmap[y * w + (w - x - 1)] = color
		}
	}
	for (x in 0 until w) {
		for (y in 0 until width) {
			bitmap[y * w + x] = color
			bitmap[(h - y - 1) * w + x] = color
		}
	}
}
