package net.twisterrob.colorfilters.android

import android.graphics.Color

@Suppress("NOTHING_TO_INLINE")
inline fun Int.red() = Color.red(this)

@Suppress("NOTHING_TO_INLINE")
inline fun Int.green() = Color.green(this)

@Suppress("NOTHING_TO_INLINE")
inline fun Int.blue() = Color.blue(this)

@Suppress("NOTHING_TO_INLINE")
inline fun Int.alpha() = Color.alpha(this)

@Suppress("NOTHING_TO_INLINE")
inline fun Int.replaceAlphaFrom(color: Int) =
	this.replaceAlpha(color.alpha())

@Suppress("NOTHING_TO_INLINE")
inline fun Int.replaceAlpha(alpha: Int) =
	Color.argb(alpha, this.red(), this.green(), this.blue())
