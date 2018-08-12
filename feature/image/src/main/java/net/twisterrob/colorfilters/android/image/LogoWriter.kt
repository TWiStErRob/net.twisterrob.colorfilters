package net.twisterrob.colorfilters.android.image

import android.graphics.Bitmap
import android.graphics.Color
import android.os.Environment
import android.util.Log
import net.twisterrob.android.view.color.FastMath
import net.twisterrob.android.view.color.PI
import net.twisterrob.android.view.color.fromHsb
import net.twisterrob.android.view.color.ofMap
import net.twisterrob.android.view.color.swatches.pixel.color.PixelColor
import net.twisterrob.android.view.color.swatches.pixel.drawer.LineByLineBitmapDrawer
import net.twisterrob.colorfilters.android.alpha
import net.twisterrob.colorfilters.android.blue
import net.twisterrob.colorfilters.android.green
import net.twisterrob.colorfilters.android.red
import java.io.File
import java.io.IOException
import kotlin.math.sqrt

object LogoWriter {

	fun write(vararg sizes: Int): File? =
		sizes.associate { size -> size to write(size) }.maxBy { it.key }?.value

	private fun write(size: Int): File? = try {
		val pixels = IntArray(size * size)
		LineByLineBitmapDrawer(pixels, size, size, RadialHSBGradientLogo()).draw()
		val bitmap = Bitmap.createBitmap(pixels, size, size, Bitmap.Config.ARGB_8888)!!
		val picturesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)!!
		File(picturesDir, "colorfilters_logo_$size.png").also { output ->
			output.outputStream().use { out ->
				bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
				out.flush()
			}
		}
	} catch (ex: IOException) {
		Log.e("LOGO", ex.message, ex)
		null
	}

	private class RadialHSBGradientLogo : PixelColor {

		private var w: Int = 0
		private var cx: Int = 0
		private var cy: Int = 0

		override fun initializeInvariants(w: Int, h: Int) {
			this.w = w / 2 / 6
			this.cx = w / 2
			this.cy = w / 2
		}

		override fun getPixelColorAt(x: Int, y: Int): Int {
			val angle = FastMath.Atan2Faster.atan2((y - cy).toFloat(), (x - cx).toFloat()) + PI // [0, 2pi]
			val dist = sqrt(((x - cx) * (x - cx) + (y - cy) * (y - cy)).toDouble()).toFloat()
			val bri = ofMap(dist, 4 * w, 6 * w, 1, 0)
			val alp = ofMap(dist, 5 * w, 6 * w, 1, 0)
			val sat = ofMap(dist, 0, 4 * w, 0, 1)
			var hue = angle / (PI * 2)
			if (cx <= x) {
				hue = 1 - hue // mirror on the right
			}
			hue = hue * 2 % 1 // draw 2 rounds in 1 circle
			var color = fromHsb(hue, sat, bri, alp)
			if (cx <= x) {
				color = desaturate(color)
			}
			return color
		}

		/**
		 * Twisted desaturation with off-ratios.
		 */
		private fun desaturate(color: Int): Int {
			val r = (0.4 * color.red() + 0.4 * color.green() + 0.2 * color.blue()).toInt()
			val g = (0.2 * color.red() + 0.6 * color.green() + 0.2 * color.blue()).toInt()
			val b = (0.1 * color.red() + 0.5 * color.green() + 0.4 * color.blue()).toInt()
			return Color.argb(color.alpha(), r, g, b)
		}
	}
}
