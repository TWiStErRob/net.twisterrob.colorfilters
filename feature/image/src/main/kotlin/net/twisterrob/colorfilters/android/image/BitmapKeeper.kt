package net.twisterrob.colorfilters.android.image

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target

private val FRAGMENT_TAG = BitmapKeeper::class.java.simpleName

/**
 * @see [Android app losing data during orientation change](http://stackoverflow.com/questions/15043222.15043471)
 */
class BitmapKeeper : Fragment() {

	private var bitmap: Bitmap? = null
	private var uri: Uri? = null

	interface Listener {
		fun loadComplete()
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		retainInstance = true
	}

	companion object {

		fun into(fragmentManager: FragmentManager, imageView: ImageView, listener: Listener): Boolean {
			getCurrent(fragmentManager)?.let { fragment ->
				fragment.bitmap?.let { bitmap ->
					imageView.setImageBitmap(bitmap)
					listener.loadComplete()
					return true
				}
				fragment.uri?.let { uri ->
					Glide
						.with(imageView.context)
						.load(uri)
						.apply(
							RequestOptions()
								.dontTransform()
						)
						.listener(GlideRequestListener(listener))
						.into(imageView)
					return true
				}
			}
			return false
		}

		fun clear(fragmentManager: FragmentManager) = getOrCreate(fragmentManager).run {
			this.bitmap = null
			this.uri = null
		}

		fun save(fragmentManager: FragmentManager, bitmap: Bitmap) = getOrCreate(fragmentManager).run {
			this.uri = null
			this.bitmap = bitmap
		}

		fun save(fragmentManager: FragmentManager, uri: Uri) = getOrCreate(fragmentManager).run {
			this.bitmap = null
			this.uri = uri
		}

		private fun getOrCreate(fragmentManager: FragmentManager): BitmapKeeper =
			getCurrent(fragmentManager)
				?: BitmapKeeper().also {
					fragmentManager
						.beginTransaction()
						.add(it, FRAGMENT_TAG)
						.commitAllowingStateLoss()
				}

		private fun getCurrent(fragmentManager: FragmentManager): BitmapKeeper? =
			fragmentManager.findFragmentByTag(FRAGMENT_TAG) as BitmapKeeper?

		fun getUri(fragmentManager: FragmentManager): Uri? =
			getOrCreate(fragmentManager).uri
	}

	private class GlideRequestListener(
		private val listener: Listener
	) : RequestListener<Drawable> {

		private val ui = Handler(Looper.getMainLooper())
		private val notifyLoadComplete = Runnable { listener.loadComplete() }

		override fun onLoadFailed(
			e: GlideException?, model: Any?, target: Target<Drawable>, isFirstResource: Boolean
		): Boolean = false

		override fun onResourceReady(
			resource: Drawable, model: Any?, target: Target<Drawable>, dataSource: DataSource, isFirstResource: Boolean
		): Boolean = false.also {
			ui.post(notifyLoadComplete) // async to give Glide time to set a drawable just after returning
		}
	}
}

internal fun Drawable.asBitmap(): Bitmap? = when (this) {
	is BitmapDrawable -> this.bitmap
	is GifDrawable -> this.firstFrame
	else -> Bitmap.createBitmap(this.intrinsicWidth, this.intrinsicHeight, Bitmap.Config.ARGB_8888).apply {
		val canvas = Canvas(this)
		draw(canvas)
	}
}
