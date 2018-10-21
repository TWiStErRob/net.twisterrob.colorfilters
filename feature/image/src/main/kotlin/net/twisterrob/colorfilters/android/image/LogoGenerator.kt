package net.twisterrob.colorfilters.android.image

import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.graphics.Bitmap.Config
import android.graphics.Color
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
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import kotlin.math.sqrt

object LogoGenerator {

	fun write(file: File, vararg sizes: Int) {
		ZipOutputStream(file.outputStream()).use { zip ->
			sizes.forEach { size ->
				zip.putNextEntry(ZipEntry("logo_$size.png"))
				zip.write(draw(size).toByteArray(CompressFormat.PNG))
				zip.closeEntry()
			}
		}
	}

	private fun Bitmap.toByteArray(format: CompressFormat): ByteArray {
		val memory = ByteArrayOutputStream()
		// no .use {} necessary because it's a memory stream
		this.compress(format, 100, memory)
		return memory.toByteArray()
	}

	private fun draw(size: Int): Bitmap {
		val pixels = IntArray(size * size)
		LineByLineBitmapDrawer(pixels, size, size, RadialHSBGradientLogo()).draw()
		return Bitmap.createBitmap(pixels, size, size, Config.ARGB_8888)
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
