package net.twisterrob.colorfilters.android.keyboard

import android.annotation.SuppressLint
import android.content.Context
import android.text.Editable
import android.text.InputType
import android.util.Log
import android.view.HapticFeedbackConstants
import android.view.MotionEvent
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import net.twisterrob.android.core.requireSystemService
import net.twisterrob.colorfilters.android.keyboard.KeyCodes.KEY_BACKSPACE
import net.twisterrob.colorfilters.android.keyboard.KeyCodes.KEY_CLEAR
import net.twisterrob.colorfilters.android.keyboard.KeyCodes.KEY_DONE
import net.twisterrob.colorfilters.android.keyboard.KeyCodes.KEY_MOVE_END
import net.twisterrob.colorfilters.android.keyboard.KeyCodes.KEY_MOVE_LEFT
import net.twisterrob.colorfilters.android.keyboard.KeyCodes.KEY_MOVE_RIGHT
import net.twisterrob.colorfilters.android.keyboard.KeyCodes.KEY_MOVE_START
import net.twisterrob.colorfilters.android.keyboard.KeyboardHandler.CustomKeyboardListener

// http://www.fampennings.nl/maarten/android/09keyboard/index.htm
// http://forum.xda-developers.com/showthread.php?t=2497237
abstract class BaseKeyboardHandler(
	protected val window: Window,
	@Suppress("DEPRECATION")
	protected val keyboardView: android.inputmethodservice.KeyboardView,
) : KeyboardHandler {

	private val context: Context = keyboardView.context.applicationContext

	override var hapticFeedback: Boolean = false
		set(value) {
			keyboardView.isHapticFeedbackEnabled = value
			field = value
		}

	override var customKeyboardListener: CustomKeyboardListener? = null

	init {
		hideCustomKeyboard()
		window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
		@Suppress("DEPRECATION")
		keyboardView.isPreviewEnabled = false
		hapticFeedback = true
	}

	override fun hideCustomKeyboard() {
		keyboardView.visibility = View.GONE
		keyboardView.isEnabled = false
		clearFocus()
		customKeyboardListener?.customKeyboardHidden()
	}

	private fun clearFocus() {
		window.currentFocus?.clearFocus()
	}

	override fun showCustomKeyboard(v: View) {
		val imm: InputMethodManager = context.requireSystemService() 
		imm.hideSoftInputFromWindow(v.windowToken, 0)
		keyboardView.isEnabled = true
		keyboardView.visibility = View.VISIBLE
		customKeyboardListener?.customKeyboardShown()
	}

	override fun registerEditText(editText: EditText) {
		// alternative to this is editText.setOnTouchListener(new OnTouchWrapper());
		try {
			val setShowSoftInputOnFocus =
				editText::class.java.getMethod("setShowSoftInputOnFocus", Boolean::class.java)
			setShowSoftInputOnFocus(editText, false)
		} catch (@Suppress("TooGenericExceptionCaught") ex: Exception) {
			// It's a hack, anything can go wrong.
			Log.w("HACK", "Could not turn off input focus for EditText: $editText", ex)
		}
		editText.isFocusable = true
		editText.isFocusableInTouchMode = true
		editText.onFocusChangeListener = MyOnFocusChangeListener()
		editText.setOnClickListener(MyOnClickListener())
	}

	override fun unregisterEditText(editText: EditText) {
		editText.onFocusChangeListener = null
		editText.setOnClickListener(null)
		editText.clearFocus()
	}

	override fun handleBack(): Boolean {
		if (@Suppress("DEPRECATION") keyboardView.handleBack()) {
			return true
		}
		if (keyboardView.visibility == View.VISIBLE) {
			hideCustomKeyboard()
			return true
		}
		return false
	}

	@Suppress("unused")
	@SuppressLint("ClickableViewAccessibility")
	private class OnTouchWrapper : View.OnTouchListener {

		override fun onTouch(v: View, event: MotionEvent): Boolean {
			val editText = v as EditText
			val backup = editText.inputType
			editText.inputType = InputType.TYPE_NULL // Disable standard keyboard
			try {
				return editText.onTouchEvent(event) // Call native handler
			} finally {
				editText.inputType = backup
				editText.isCursorVisible = true
			}
		}
	}

	private inner class MyOnClickListener : View.OnClickListener {

		override fun onClick(v: View) {
			showCustomKeyboard(v)
		}
	}

	private inner class MyOnFocusChangeListener : View.OnFocusChangeListener {

		override fun onFocusChange(v: View, hasFocus: Boolean) {
			if (hasFocus) {
				showCustomKeyboard(v)
			} else {
				hideCustomKeyboard()
			}
		}
	}

	@Suppress("OVERRIDE_DEPRECATION") // KeyboardView is deprecated, but the listener methods need to be used.
	protected abstract inner class BaseOnKeyboardActionListener 
		: @Suppress("DEPRECATION") android.inputmethodservice.KeyboardView.OnKeyboardActionListener {

		private fun findView(): View? {
			return window.currentFocus
		}

		private fun findEdit(): EditText? {
			return findView() as? EditText
		}

		@Suppress("ComplexMethod") // Curious if this can be improved, but not now.
		override fun onKey(primaryCode: Int, keyCodes: IntArray) {
			val editor = findEdit()
			if (editor == null) {
				hideCustomKeyboard()
				return
			}

			val editable: Editable? = editor.text

			// if there's a selection, clear it to make any edits replace actions
			val start = editor.selectionStart
			val end = editor.selectionEnd
			if (start < end) {
				requireNotNull(editable) { "Selection can't exist without text" }
					.delete(start, end)
			}

			when (primaryCode) {
				KEY_CLEAR -> editable?.clear()
				KEY_BACKSPACE ->
					if (editable != null && 0 < start) {
						editable.delete(start - 1, start)
					}
				KEY_DONE -> hideCustomKeyboard()
				KEY_MOVE_START -> editor.setSelection(0)
				KEY_MOVE_END -> editor.setSelection(editor.length())
				KEY_MOVE_LEFT ->
					if (start > 0) {
						editor.setSelection(start - 1)
					}
				KEY_MOVE_RIGHT ->
					if (start < editor.length()) {
						editor.setSelection(start + 1)
					}

				else -> {
					if (!this.onKey(editor, primaryCode, keyCodes)) {
						editable?.insert(editor.selectionStart, primaryCode.toChar().toString())
					}
					// TODO consider the following:
					//val eventTime = SystemClock.uptimeMillis()
					//val event = KeyEvent(eventTime, eventTime, KeyEvent.ACTION_DOWN, primaryCode, 0, 0, 0, 0, KeyEvent.FLAG_SOFT_KEYBOARD or KeyEvent.FLAG_KEEP_TOUCH_MODE)
					//mTargetActivity.dispatchKeyEvent(event)
				}
			}
		}

		protected abstract fun onKey(editor: EditText, primaryCode: Int, keyCodes: IntArray): Boolean

		override fun onPress(primaryCode: Int) {
			if (hapticFeedback && primaryCode != 0) {
				keyboardView.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
			}
			//findEdit().onKeyDown(primaryCode, KeyEvent(KeyEvent.ACTION_DOWN, primaryCode))
		}

		override fun onRelease(primaryCode: Int) {
			//findEdit().onKeyUp(primaryCode, KeyEvent(KeyEvent.ACTION_UP, primaryCode))
		}

		override fun onText(text: CharSequence?) {
			val editor = findEdit()
			@Suppress("FoldInitializerAndIfToElvis")
			if (editor == null) {
				//? hideCustomKeyboard()
				return
			}
			val start = editor.selectionStart
			val end = editor.selectionEnd
			editor.setText(text)
			editor.setSelection(start, end)
		}

		override fun swipeDown() {
			// Swiping gestures are not handled on these keyboards.
		}

		override fun swipeLeft() {
			// Swiping gestures are not handled on these keyboards.
		}

		override fun swipeRight() {
			// Swiping gestures are not handled on these keyboards.
		}

		override fun swipeUp() {
			// Swiping gestures are not handled on these keyboards.
		}
	}
}
