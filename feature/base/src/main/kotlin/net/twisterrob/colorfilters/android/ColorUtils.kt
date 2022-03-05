package net.twisterrob.colorfilters.android

import android.graphics.Color
import androidx.annotation.ColorInt
import androidx.core.graphics.alpha
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red

@Suppress("NOTHING_TO_INLINE")
inline fun @receiver:ColorInt Int.replaceAlphaFrom(@ColorInt color: Int) =
	this.replaceAlpha(color.alpha)

@Suppress("NOTHING_TO_INLINE")
inline fun @receiver:ColorInt Int.replaceAlpha(alpha: Int) =
	Color.argb(alpha, red, green, blue)

fun @receiver:ColorInt Int.toRGBDecString(separator: String = ", "): String =
	"%d%s%d%s%d".formatRoot(red, separator, green, separator, blue)

fun @receiver:ColorInt Int.toARGBDecString(separator: String = ", "): String =
	"%d%s%d%s%d%s%d".formatRoot(alpha, separator, red, separator, green, separator, blue)

fun @receiver:ColorInt Int.toRGBHexString(prefix: String = ""): String =
	"%s%06X".formatRoot(prefix, 0xFFFFFF and this)

fun @receiver:ColorInt Int.toARGBHexString(prefix: String = ""): String =
	"%s%08X".formatRoot(prefix, this)
