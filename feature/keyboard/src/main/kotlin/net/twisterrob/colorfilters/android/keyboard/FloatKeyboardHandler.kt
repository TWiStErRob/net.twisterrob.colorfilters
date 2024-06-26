package net.twisterrob.colorfilters.android.keyboard

import android.text.Editable
import android.view.Window
import android.widget.EditText
import net.twisterrob.colorfilters.android.keyboard.KeyCodes.KEY_CHANGE_SIGN
import net.twisterrob.colorfilters.android.keyboard.KeyCodes.KEY_DECIMAL

open class FloatKeyboardHandler(
	window: Window,
	@Suppress("DEPRECATION")
	keyboardView: android.inputmethodservice.KeyboardView,
) : BaseKeyboardHandler(window, keyboardView) {

	init {
		@Suppress("DEPRECATION")
		keyboardView.keyboard = android.inputmethodservice.Keyboard(keyboardView.context, R.xml.keyboard_float)
		@Suppress("DEPRECATION")
		keyboardView.setOnKeyboardActionListener(FloatKeyboardActionListener())
	}

	protected open inner class FloatKeyboardActionListener : BaseOnKeyboardActionListener() {

		override fun onKey(editor: EditText, primaryCode: Int, keyCodes: IntArray): Boolean {
			when (primaryCode) {
				KEY_DECIMAL -> super.onKey('.'.code, keyCodes)
				KEY_CHANGE_SIGN -> editor.editableText?.negate()
				else -> return false
			}
			return true
		}
	}
}

private fun Editable.negate() {
	val firstChar = if (isNotEmpty()) this[0] else '+'
	if (firstChar == '-') { // -x -> x
		delete(0, 1)
	} else { // x -> -x
		insert(0, "-")
	}
}
