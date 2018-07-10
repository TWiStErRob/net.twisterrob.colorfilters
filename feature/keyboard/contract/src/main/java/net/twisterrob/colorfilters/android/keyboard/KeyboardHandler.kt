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

	fun setHapticFeedback(haptic: Boolean)

	fun handleBack(): Boolean

	fun setCustomKeyboardListener(listener: CustomKeyboardListener)
}
