package net.twisterrob.colorfilters.android

import java.util.Locale

@Suppress("NOTHING_TO_INLINE")
inline fun String.formatRoot(vararg args: Any?): String =
	this.format(Locale.ROOT, *args)
