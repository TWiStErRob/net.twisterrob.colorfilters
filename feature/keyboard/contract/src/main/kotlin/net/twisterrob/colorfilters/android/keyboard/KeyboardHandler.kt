package net.twisterrob.colorfilters.android.keyboard

import android.view.View
import android.widget.EditText

interface KeyboardHandler {

	interface CustomKeyboardListener {

		fun customKeyboardShown()

		fun customKeyboardHidden()
	}

	fun hideCustomKeyboard()

	fun showCustomKeyboard(v: View)

	fun registerEditText(editText: EditText)

	fun unregisterEditText(editText: EditText)

	var hapticFeedback: Boolean

	fun handleBack(): Boolean

	var customKeyboardListener: CustomKeyboardListener?
}
