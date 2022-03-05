package net.twisterrob.colorfilters.android

@Suppress("NOTHING_TO_INLINE")
inline fun String.formatRoot(vararg args: Any?) = this.format(java.util.Locale.ROOT, *args)
