package net.twisterrob.colorfilters.android.image

import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.graphics.Bitmap.Config
import android.graphics.Color
import androidx.annotation.ColorInt
import androidx.annotation.IntRange
import androidx.core.graphics.alpha
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red
import net.twisterrob.android.view.color.FastMath
import net.twisterrob.android.view.color.PI
import net.twisterrob.android.view.color.fromHsb
import net.twisterrob.android.view.color.ofMap
import net.twisterrob.android.view.color.swatches.pixel.color.PixelColor
import net.twisterrob.android.view.color.swatches.pixel.drawer.LineByLineBitmapDrawer
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import kotlin.math.sqrt

@Suppress("MagicNumber")
private val sizes = mapOf(
	"ldpi" to 36,
	"mdpi" to 48,
	"hdpi" to 72,
	"xhdpi" to 96,
	"xxhdpi" to 144,
	"xxxhdpi" to 192,
	null to 512
)

object LogoGenerator {

	fun write(file: File) {
		ZipOutputStream(file.outputStream()).use { zip ->
			sizes.forEach { (res, size) ->
				val name = if (res != null) "drawable-${res}/ic_launcher.png" else "Hi-res Icon (512x512).png"
				zip.putNextEntry(ZipEntry(name))
				zip.write(draw(size).toByteArray(CompressFormat.PNG))
				zip.closeEntry()
			}
		}
	}

	private fun Bitmap.toByteArray(format: CompressFormat): ByteArray {
		val memory = ByteArrayOutputStream()
		// no .use {} necessary because it's a memory stream
		this.compress(format, @Suppress("MagicNumber") 100, memory)
		return memory.toByteArray()
	}

	private fun draw(size: Int): Bitmap {
		val pixels = IntArray(size * size)
		LineByLineBitmapDrawer(pixels, size, size, RadialHSBGradientLogo()).draw()
		return Bitmap.createBitmap(pixels, size, size, Config.ARGB_8888)
	}

	private class RadialHSBGradientLogo : PixelColor {

		@IntRange(from = 0)
		private var w: Int = 0

		@IntRange(from = 0)
		private var cx: Int = 0

		@IntRange(from = 0)
		private var cy: Int = 0

		override fun initializeInvariants(@IntRange(from = 0) w: Int, @IntRange(from = 0) h: Int) {
			this.w = w / 2 / @Suppress("MagicNumber") 6
			this.cx = w / 2
			this.cy = w / 2
		}

		@Suppress("MagicNumber")
		@ColorInt
		override fun getPixelColorAt(@IntRange(from = 0/*, to = w*/) x: Int, @IntRange(from = 0/*, to = h*/) y: Int): Int {
			val angle = FastMath.Atan2Faster.atan2((y - cy).toFloat(), (x - cx).toFloat()) + PI // [0, 2pi]
			val dist = sqrt(((x - cx) * (x - cx) + (y - cy) * (y - cy)).toDouble()).toFloat()
			val bri = ofMap(dist, 4 * w, 6 * w, 1, 0)
			val alp = ofMap(dist, 5 * w, 6 * w, 1, 0)
			val sat = ofMap(dist, 0, 4 * w, 0, 1)
			val hue = angle / (PI * 2)
			val doubleHue = hue * 2 % 1 // draw 2 rounds in 1 circle
			val mirrorHue = if (cx <= x) 1 - doubleHue else doubleHue // mirror on the right
			val color = fromHsb(mirrorHue, sat, bri, alp)
			return if (cx <= x) desaturate(color) else color // desaturate on the right
		}

		/**
		 * Twisted desaturation with off-ratios.
		 * CONSIDER desaturating by setting sat before the color is calculated
		 */
		@Suppress("MagicNumber")
		@ColorInt
		private fun desaturate(@ColorInt color: Int): Int {
			val r = (0.4 * color.red + 0.4 * color.green + 0.2 * color.blue).toInt()
			val g = (0.2 * color.red + 0.6 * color.green + 0.2 * color.blue).toInt()
			val b = (0.1 * color.red + 0.5 * color.green + 0.4 * color.blue).toInt()
			return Color.argb(color.alpha, r, g, b)
		}
	}
}
