package net.twisterrob.android.core

import android.content.Context
import androidx.core.content.getSystemService

inline fun <reified T> Context.requireSystemService(): T =
	getSystemService()
		?: error("System service ${T::class.java.name} is not available.")
