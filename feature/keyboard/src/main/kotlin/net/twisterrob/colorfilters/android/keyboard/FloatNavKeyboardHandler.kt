package net.twisterrob.colorfilters.android.keyboard

import android.inputmethodservice.Keyboard
import android.inputmethodservice.KeyboardView
import android.view.View
import android.view.Window
import android.widget.EditText
import net.twisterrob.colorfilters.android.keyboard.KeyCodes.KEY_NAV_E
import net.twisterrob.colorfilters.android.keyboard.KeyCodes.KEY_NAV_N
import net.twisterrob.colorfilters.android.keyboard.KeyCodes.KEY_NAV_NE
import net.twisterrob.colorfilters.android.keyboard.KeyCodes.KEY_NAV_NW
import net.twisterrob.colorfilters.android.keyboard.KeyCodes.KEY_NAV_S
import net.twisterrob.colorfilters.android.keyboard.KeyCodes.KEY_NAV_SE
import net.twisterrob.colorfilters.android.keyboard.KeyCodes.KEY_NAV_SW
import net.twisterrob.colorfilters.android.keyboard.KeyCodes.KEY_NAV_W

class FloatNavKeyboardHandler(
	window: Window, keyboardView: KeyboardView
) : FloatKeyboardHandler(window, keyboardView) {

	init {
		keyboardView.keyboard = Keyboard(keyboardView.context, R.xml.keyboard_floatnav)
		keyboardView.setOnKeyboardActionListener(FloatNavKeyboardActionListener())
	}

	private inner class FloatNavKeyboardActionListener : FloatKeyboardHandler.FloatKeyboardActionListener() {
		override fun onKey(editor: EditText, primaryCode: Int, keyCodes: IntArray): Boolean {
			when (primaryCode) {
				KEY_NAV_W, KEY_NAV_N, KEY_NAV_E, KEY_NAV_S,
				KEY_NAV_NW, KEY_NAV_NE, KEY_NAV_SE, KEY_NAV_SW ->
					editor.navigate(primaryCode)?.requestFocus() ?: hideCustomKeyboard()
				else -> return super.onKey(editor, primaryCode, keyCodes)
			}
			return true
		}
	}
}

// region Focus
private const val FOCUS_FIRST = KEY_NAV_W
private const val FOCUS_NO = -1

private val FOCUS_SEARCH = arrayOf(
	intArrayOf(View.FOCUS_LEFT, FOCUS_NO), // KEY_NAV_W
	intArrayOf(View.FOCUS_UP, FOCUS_NO), // KEY_NAV_N
	intArrayOf(View.FOCUS_RIGHT, FOCUS_NO), // KEY_NAV_E
	intArrayOf(View.FOCUS_DOWN, FOCUS_NO), // KEY_NAV_S
	intArrayOf(FOCUS_NO, FOCUS_NO),
	intArrayOf(FOCUS_NO, FOCUS_NO),
	intArrayOf(View.FOCUS_UP, View.FOCUS_LEFT), // KEY_NAV_NW
	intArrayOf(View.FOCUS_UP, View.FOCUS_RIGHT), // KEY_NAV_NE
	intArrayOf(View.FOCUS_DOWN, View.FOCUS_RIGHT), // KEY_NAV_SE
	intArrayOf(View.FOCUS_DOWN, View.FOCUS_LEFT) // KEY_NAV_SW
)

/**
 * @param primaryCode Unicode character, see [KEY_NAV_*]
 */
private fun View.navigate(primaryCode: Int): View? {
	val focusKeys = FOCUS_SEARCH[primaryCode - FOCUS_FIRST]
	var focus: View? = this
	if (focus != null && focusKeys[0] != FOCUS_NO) {
		focus = focus.focusSearch(focusKeys[0]) // View.findUserSetNextFocus
	}
	if (focus != null && focusKeys[1] != FOCUS_NO) {
		focus = focus.focusSearch(focusKeys[1]) // View.findUserSetNextFocus
	}
	return focus
}
// endregion
