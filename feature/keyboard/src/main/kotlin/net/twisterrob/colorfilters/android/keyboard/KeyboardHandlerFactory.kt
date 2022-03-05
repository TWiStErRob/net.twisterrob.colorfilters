package net.twisterrob.colorfilters.android.keyboard

import android.view.Window
import net.twisterrob.colorfilters.android.keyboard.KeyboardMode.Float
import net.twisterrob.colorfilters.android.keyboard.KeyboardMode.FloatNav
import net.twisterrob.colorfilters.android.keyboard.KeyboardMode.Hex
import net.twisterrob.colorfilters.android.keyboard.KeyboardMode.NATIVE

class KeyboardHandlerFactory {

	fun create(
		mode: KeyboardMode,
		window: Window,
		@Suppress("DEPRECATION")
		keyboardView: android.inputmethodservice.KeyboardView
	): KeyboardHandler =
		when (mode) {
			Hex -> HexKeyboardHandler(window, keyboardView)
			FloatNav -> FloatNavKeyboardHandler(window, keyboardView)
			Float -> FloatKeyboardHandler(window, keyboardView)
			NATIVE -> NullKeyboardHandler(window, keyboardView)
		}
}
