package net.twisterrob.colorfilters.android

import java.util.Locale

@Suppress("NOTHING_TO_INLINE")
inline fun String.formatRoot(vararg args: Any?) =
	this.format(Locale.ROOT, *args)
