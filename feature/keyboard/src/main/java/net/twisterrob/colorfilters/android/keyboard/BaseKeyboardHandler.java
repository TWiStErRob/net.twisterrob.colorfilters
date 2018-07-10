package net.twisterrob.colorfilters.android.keyboard;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.inputmethodservice.KeyboardView;
import android.os.Build;
import android.support.annotation.NonNull;
import android.text.*;
import android.util.Log;
import android.view.*;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

// http://www.fampennings.nl/maarten/android/09keyboard/index.htm
// http://forum.xda-developers.com/showthread.php?t=2497237
public abstract class BaseKeyboardHandler implements KeyboardHandler {

	private boolean haptic;
	protected final Context context;
	protected final Window window;
	protected final KeyboardView keyboardView;
	private CustomKeyboardListener listener;

	public BaseKeyboardHandler(Window window, KeyboardView keyboardView) {
		this.window = window;
		this.context = keyboardView.getContext().getApplicationContext();
		this.keyboardView = keyboardView;

		hideCustomKeyboard();
		window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		keyboardView.setPreviewEnabled(false);
		setHapticFeedback(true);
	}

	public void hideCustomKeyboard() {
		keyboardView.setVisibility(View.GONE);
		keyboardView.setEnabled(false);
		clearFocus();
		if (listener != null) {
			listener.customKeyboardHidden();
		}
	}

	protected void clearFocus() {
		View focused = window.getCurrentFocus();
		if (focused != null) {
			focused.clearFocus();
		}
	}

	public void showCustomKeyboard(@NonNull View v) {
		InputMethodManager imm = (InputMethodManager)context.getSystemService(Activity.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
		keyboardView.setEnabled(true);
		keyboardView.setVisibility(View.VISIBLE);
		if (listener != null) {
			listener.customKeyboardShown();
		}
	}

	public void registerEditText(@NonNull EditText editText) {
		// alternative to this is editText.setOnTouchListener(new OnTouchWrapper());
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
			editText.setInputType(InputType.TYPE_NULL);
		} else {
			try {
				editText.getClass().getMethod("setShowSoftInputOnFocus", boolean.class)
				        .invoke(editText, false); //NON-NLS
			} catch (Exception ex) {
				Log.w("HACK", "Could turn of input focus for EditText: " + editText, ex); //NON-NLS
			}
		}
		editText.setFocusable(true);
		editText.setFocusableInTouchMode(true);
		editText.setOnFocusChangeListener(new MyOnFocusChangeListener());
		editText.setOnClickListener(new MyOnClickListener());
	}

	public void unregisterEditText(@NonNull EditText editText) {
		editText.setOnFocusChangeListener(null);
		editText.setOnClickListener(null);
		editText.clearFocus();
	}

	public void setHapticFeedback(boolean haptic) {
		keyboardView.setHapticFeedbackEnabled(haptic);
		this.haptic = haptic;
	}

	public boolean handleBack() {
		if (keyboardView.handleBack()) {
			return true;
		}
		if (keyboardView.getVisibility() == View.VISIBLE) {
			hideCustomKeyboard();
			return true;
		}
		return false;
	}

	public void setCustomKeyboardListener(@NonNull CustomKeyboardListener listener) {
		this.listener = listener;
	}

	@SuppressWarnings("unused")
	@SuppressLint("ClickableViewAccessibility")
	private static class OnTouchWrapper implements View.OnTouchListener {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			EditText editText = (EditText)v;
			int backup = editText.getInputType();
			editText.setInputType(InputType.TYPE_NULL); // Disable standard keyboard
			try {
				return editText.onTouchEvent(event); // Call native handler
			} finally {
				editText.setInputType(backup);
				editText.setCursorVisible(true);
			}
		}
	}

	private class MyOnClickListener implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			showCustomKeyboard(v);
		}
	}

	private class MyOnFocusChangeListener implements View.OnFocusChangeListener {
		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			if (hasFocus) {
				showCustomKeyboard(v);
			} else {
				hideCustomKeyboard();
			}
		}
	}

	protected abstract class BaseOnKeyboardActionListener implements KeyboardView.OnKeyboardActionListener {
		protected static final int KEY_MOVE_LEFT = '\u2190';
		protected static final int KEY_MOVE_RIGHT = '\u2192';
		protected static final int KEY_MOVE_START = '\u21E4';
		protected static final int KEY_MOVE_END = '\u21E5';

		protected static final int KEY_DONE = '\u0017';
		protected static final int KEY_CLEAR = '\u0018';
		protected static final int KEY_BACKSPACE = '\u0008';

		protected View findView() {
			return window.getCurrentFocus();
		}

		protected EditText findEdit() {
			View focusCurrent = findView();
			if (focusCurrent instanceof EditText) {
				return (EditText)focusCurrent;
			}
			return null;
		}

		@Override
		public void onKey(int primaryCode, int[] keyCodes) {
			EditText editor = findEdit();
			if (editor == null) {
				hideCustomKeyboard();
				return;
			}

			Editable editable = editor.getText();

			// if there's a selection, clear it to make any edits replace actions
			int start = editor.getSelectionStart();
			int end = editor.getSelectionEnd();
			if (start < end) {
				editable.delete(start, end);
			}

			switch (primaryCode) {
				case KEY_CLEAR:
					if (editable != null) {
						editable.clear();
					}
					break;
				case KEY_BACKSPACE: {
					if (editable != null && 0 < start) {
						editable.delete(start - 1, start);
					}
					break;
				}
				case KEY_DONE:
					hideCustomKeyboard();
					break;
				case KEY_MOVE_START:
					editor.setSelection(0);
					break;
				case KEY_MOVE_END:
					editor.setSelection(editor.length());
					break;
				case KEY_MOVE_LEFT: {
					if (start > 0) {
						editor.setSelection(start - 1);
					}
					break;
				}
				case KEY_MOVE_RIGHT: {
					if (start < editor.length()) {
						editor.setSelection(start + 1);
					}
					break;
				}
				default: {
					if (!this.onKey(editor, primaryCode, keyCodes)) {
						if (editable != null) {
							editable.insert(editor.getSelectionStart(), Character.toString((char)primaryCode));
						}
					}
					// TODO consider the following:
					//long eventTime = SystemClock.uptimeMillis();
					//KeyEvent event = new KeyEvent(eventTime, eventTime, KeyEvent.ACTION_DOWN, primaryCode, 0, 0, 0, 0, KeyEvent.FLAG_SOFT_KEYBOARD | KeyEvent.FLAG_KEEP_TOUCH_MODE);
					//mTargetActivity.dispatchKeyEvent(event);
					break;
				}
			}
		}

		protected abstract boolean onKey(EditText editor, int primaryCode, int[] keyCodes);

		@Override
		public void onPress(int primaryCode) {
			if (haptic && primaryCode != 0) {
				keyboardView.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
			}
			//findEdit().onKeyDown(primaryCode, new KeyEvent(KeyEvent.ACTION_DOWN, primaryCode));
		}

		@Override
		public void onRelease(int primaryCode) {
			//findEdit().onKeyUp(primaryCode, new KeyEvent(KeyEvent.ACTION_UP, primaryCode));
		}

		@Override
		public void onText(CharSequence text) {
			EditText editor = findEdit();
			if (editor == null) {
				//? hideCustomKeyboard();
				return;
			}
			int start = editor.getSelectionStart();
			int end = editor.getSelectionEnd();
			editor.setText(text);
			editor.setSelection(start, end);
		}

		@Override
		public void swipeDown() {
		}

		@Override
		public void swipeLeft() {
		}

		@Override
		public void swipeRight() {
		}

		@Override
		public void swipeUp() {
		}
	}
}
