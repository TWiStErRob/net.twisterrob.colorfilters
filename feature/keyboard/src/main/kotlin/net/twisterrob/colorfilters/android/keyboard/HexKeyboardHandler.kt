@file:Suppress("DEPRECATION")

package net.twisterrob.colorfilters.android.keyboard

import android.inputmethodservice.Keyboard
import android.inputmethodservice.KeyboardView
import android.view.Window
import android.widget.EditText
import net.twisterrob.colorfilters.android.keyboard.KeyCodes.KEY_NEGATE

class HexKeyboardHandler(
	window: Window, keyboardView: KeyboardView
) : BaseKeyboardHandler(window, keyboardView) {

	init {
		keyboardView.keyboard = Keyboard(keyboardView.context, R.xml.keyboard_hex)
		keyboardView.setOnKeyboardActionListener(MyOnKeyboardActionListener())
	}

	@Suppress("detekt.UnnecessaryInnerClass") // The inner superclass requires this handler as its outer receiver.
	private inner class MyOnKeyboardActionListener : BaseOnKeyboardActionListener() {

		override fun onKey(editor: EditText, primaryCode: Int, keyCodes: IntArray): Boolean {
			@Suppress("detekt.UseIfInsteadOfWhen")
			when (primaryCode) {
				KEY_NEGATE -> super.onText(editor.editableText?.negate())
				else -> return false
			}
			return true
		}
	}
}

private inline fun String.map(transform: (Char) -> Char): String {
	val arr = this.toCharArray()
	for (i in arr.indices) {
		arr[i] = transform(arr[i])
	}
	return String(arr)
}

private val NEGATE = charArrayOf(
	/*0x0?*/ '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0',
	/*0x1?*/ '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0',
	/*0x2?*/ '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0',
	/*0x3?*/ 'F', 'E', 'D', 'C', 'B', 'A', '9', '8', '7', '6', '0', '0', '0', '0', '0', '0',
	/*0x4?*/ '0', '5', '4', '3', '2', '1', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0',
	/*0x5?*/ '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0',
	/*0x6?*/ '0', '5', '4', '3', '2', '1', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0',
	/*0x7?*/ '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0'
)

private fun CharSequence.negate(): String =
	this.replace("""[0-9A-Fa-f]+""".toRegex()) { match ->
		match.value.map { NEGATE[it.toInt()] }
	}
