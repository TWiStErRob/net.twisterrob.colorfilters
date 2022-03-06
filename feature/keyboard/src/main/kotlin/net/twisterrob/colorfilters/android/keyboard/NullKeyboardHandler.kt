@file:Suppress("DEPRECATION")

package net.twisterrob.colorfilters.android.keyboard

import android.inputmethodservice.Keyboard
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.EditText

class NullKeyboardHandler(
	window: Window,
	keyboardView: android.inputmethodservice.KeyboardView
) : BaseKeyboardHandler(saveInputMode(window), keyboardView) {

	init {
		window.setSoftInputMode(originalInputMode)

		keyboardView.keyboard = Keyboard(keyboardView.context, R.xml.keyboard_empty)
		keyboardView.setOnKeyboardActionListener(null)
	}

	override fun registerEditText(editText: EditText) {
		// No op, stubbed out for "null" keyboard.
	}

	override fun unregisterEditText(editText: EditText) {
		// No op, stubbed out for "null" keyboard.
	}

	override fun handleBack(): Boolean =
		// No op, stubbed out for "null" keyboard, the keyboard doesn't need to react to "back".
		false

	override fun showCustomKeyboard(v: View) {
		// No op, stubbed out for "null" keyboard.
	}

	override fun hideCustomKeyboard() {
		// No op, stubbed out for "null" keyboard.
	}

	companion object {

		private var originalInputMode: Int = WindowManager.LayoutParams.SOFT_INPUT_STATE_UNSPECIFIED

		private fun saveInputMode(window: Window) = window
			.also { originalInputMode = window.attributes.softInputMode }
	}
}
