package net.twisterrob.android.view.color.swatches.pixel.drawer

import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import androidx.annotation.ColorInt
import androidx.annotation.IntRange
import net.twisterrob.android.view.color.swatches.pixel.color.PixelColor

typealias BitmapDrawerFactory = (bitmap: IntArray, w: Int, h: Int, pixel: PixelColor) -> BitmapDrawer

abstract class BitmapDrawer(
	@ColorInt
	val bitmap: IntArray,

	@IntRange(from = 0)
	val w: Int,

	@IntRange(from = 0)
	val h: Int,

	val pixel: PixelColor
) {

	interface Callback {

		fun drawStared()

		fun drawProgress()

		fun drawFinished()
	}

	companion object {

		private val uiHandler = Looper.getMainLooper()?.let { Handler(it) }
		private val asyncHandler = BitmapDrawerThread().mHandler

		fun sync(factory: BitmapDrawerFactory): BitmapDrawerFactory =
			factory as? Sync ?: Sync(factory)

		fun async(factory: BitmapDrawerFactory): BitmapDrawerFactory =
			factory as? Async ?: Async(factory)
	}

	var callback: Callback? = null
	private var async: Boolean = false

	protected abstract fun fillPixels()

	fun draw() {
		doStart()
		if (async) {
			requireNotNull(asyncHandler) { "Async drawing without asyncHandler" }
			asyncHandler.post(::doDraw)
		} else {
			doDraw()
			doFinish()
		}
	}

	private fun doStart() {
		callback?.drawStared()
	}

	private fun doDraw() {
		pixel.initializeInvariants(w, h)
		fillPixels()
		reportProgress()
	}

	protected fun reportProgress() {
		if (async) {
			requireNotNull(uiHandler) { "Async drawing without uiHandler" }
			uiHandler.post { callback?.drawProgress() }
		} else {
			callback?.drawProgress()
		}
	}

	private fun doFinish() {
		callback?.drawFinished()
	}

	private fun enableAsync() {
		if (!async) {
			async = true
		}
	}

	private fun disableAsync() {
		async = false
	}

	private class BitmapDrawerThread : HandlerThread(BitmapDrawerThread::class.java.simpleName) {

		internal val mHandler: Handler?

		init {
			start()
			mHandler = super.getLooper()?.let { Handler(it) }
		}

		internal fun stopThread() {
			super.getLooper().quit()
		}
	}

	private class Async(private val factory: BitmapDrawerFactory) : BitmapDrawerFactory by factory {

		override fun invoke(bitmap: IntArray, w: Int, h: Int, pixel: PixelColor) =
			factory(bitmap, w, h, pixel).apply { enableAsync() }
	}

	private class Sync(private val factory: BitmapDrawerFactory) : BitmapDrawerFactory by factory {

		override fun invoke(bitmap: IntArray, w: Int, h: Int, pixel: PixelColor) =
			factory(bitmap, w, h, pixel).apply { disableAsync() }
	}
}
