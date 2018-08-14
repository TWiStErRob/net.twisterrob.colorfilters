package net.twisterrob.android.view.color.swatches

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Rect
import net.twisterrob.android.view.color.swatches.pixel.color.PixelColor
import net.twisterrob.android.view.color.swatches.pixel.drawer.BitmapDrawer

class PixelAbsoluteSwatch(
	private val originalFactory: BitmapDrawer.Factory,
	private val pixels: PixelColor
) : Swatch() {

	private var drawerFactory = originalFactory
	private var bitmap: IntArray? = null
	override var currentColor: Int = Color.TRANSPARENT

	private val invalidate = object : BitmapDrawer.Callback {
		override fun drawStared() {
			// ignore (empty bitmap, nothing to draw)
		}

		override fun drawProgress() {
			invalidateSelf()
		}

		override fun drawFinished() {
			invalidateSelf()
		}
	}

	override fun draw(canvas: Canvas) {
		if (bitmap == null) {
			return
		}
		with(bounds) {
			@Suppress("DEPRECATION")
			canvas.drawBitmap(bitmap, 0, width(), 0, 0, width(), height(), true, null)
		}
	}

	override fun onBoundsChange(bounds: Rect) {
		super.onBoundsChange(bounds)
		val w = bounds.width()
		val h = bounds.height()
		val bitmap = IntArray(w * h).also { this.bitmap = it }
		this.bitmap = bitmap
		val drawer = drawerFactory.create(bitmap, w, h, pixels).apply {
			callback = this@PixelAbsoluteSwatch.invalidate
		}
		drawer.draw()
	}

	override fun findColor(area: Int, x: Float, y: Float) = pixels.getPixelColorAt(x.toInt(), y.toInt())

	fun forceAsync() {
		drawerFactory = BitmapDrawer.Factory.Async.wrap(originalFactory)
	}

	fun forceSync() {
		drawerFactory = BitmapDrawer.Factory.Sync.wrap(originalFactory)
	}

	fun resetAsync() {
		drawerFactory = originalFactory
	}
}
