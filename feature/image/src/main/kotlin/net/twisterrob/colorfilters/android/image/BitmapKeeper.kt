package net.twisterrob.colorfilters.android.image

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
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
		@Suppress("DEPRECATION") // TODO group: ViewModel
		retainInstance = true
	}

	companion object {

		fun into(fragmentManager: FragmentManager, imageView: ImageView, listener: Listener): Boolean {
			val fragment = getCurrent(fragmentManager) ?: return false
			val bitmap = fragment.bitmap
			if (bitmap != null) {
				imageView.setImageBitmap(bitmap)
				listener.loadComplete()
				return true
			}
			val uri = fragment.uri ?: return false
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

		fun clear(fragmentManager: FragmentManager) {
			getOrCreate(fragmentManager).run {
				this.bitmap = null
				this.uri = null
			}
		}

		fun save(fragmentManager: FragmentManager, bitmap: Bitmap) {
			getOrCreate(fragmentManager).run {
				this.uri = null
				this.bitmap = bitmap
			}
		}

		fun save(fragmentManager: FragmentManager, uri: Uri) {
			getOrCreate(fragmentManager).run {
				this.bitmap = null
				this.uri = uri
			}
		}

		private fun getOrCreate(fragmentManager: FragmentManager): BitmapKeeper =
			getCurrent(fragmentManager)
				?: BitmapKeeper().also { keeper ->
					fragmentManager
						.beginTransaction()
						.add(keeper, FRAGMENT_TAG)
						.commitAllowingStateLoss()
				}

		@Suppress("detekt.CastToNullableType")
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
			resource: Drawable, model: Any, target: Target<Drawable>, dataSource: DataSource, isFirstResource: Boolean
		): Boolean = false.also {
			ui.post(notifyLoadComplete) // async to give Glide time to set a drawable just after returning
		}
	}
}

@Suppress("detekt.UseIfInsteadOfWhen")
internal fun Drawable.asBitmap(): Bitmap? = when (this) {
	is GifDrawable -> this.firstFrame
	else -> this.toBitmap()
}
