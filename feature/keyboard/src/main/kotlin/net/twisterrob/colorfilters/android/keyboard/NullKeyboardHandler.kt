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

	override fun registerEditText(editText: EditText) {}

	override fun unregisterEditText(editText: EditText) {}

	override fun handleBack(): Boolean = false

	override fun showCustomKeyboard(v: View) {}

	override fun hideCustomKeyboard() {}

	companion object {

		private var originalInputMode: Int = WindowManager.LayoutParams.SOFT_INPUT_STATE_UNSPECIFIED

		private fun saveInputMode(window: Window) = window
			.also { originalInputMode = window.attributes.softInputMode }
	}
}
