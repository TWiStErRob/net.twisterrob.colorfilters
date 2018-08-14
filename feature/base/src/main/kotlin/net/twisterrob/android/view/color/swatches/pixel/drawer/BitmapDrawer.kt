package net.twisterrob.android.view.color.swatches.pixel.drawer

import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import net.twisterrob.android.view.color.swatches.pixel.color.PixelColor

abstract class BitmapDrawer(
	val bitmap: IntArray,
	val w: Int,
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
	}

	var callback: Callback? = null
	private var async: Boolean = false

	protected abstract fun fillPixels()

	fun draw() {
		doStart()
		if (async) {
			asyncHandler!!.post(::doDraw)
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
			uiHandler!!.post { callback?.drawProgress() }
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

	interface Factory { // TODO function type
		fun create(bitmap: IntArray, w: Int, h: Int, pixel: PixelColor): BitmapDrawer

		class Async private constructor(private val factory: Factory) : Factory by factory {

			override fun create(bitmap: IntArray, w: Int, h: Int, pixel: PixelColor) =
				factory.create(bitmap, w, h, pixel).apply { enableAsync() }

			companion object {

				fun wrap(factory: Factory): Factory =
					factory as? Async ?: Async(factory)
			}
		}

		class Sync private constructor(private val factory: Factory) : Factory by factory {

			override fun create(bitmap: IntArray, w: Int, h: Int, pixel: PixelColor) =
				factory.create(bitmap, w, h, pixel).apply { disableAsync() }

			companion object {

				fun wrap(factory: Factory): Factory =
					factory as? Sync ?: Sync(factory)
			}
		}
	}
}
