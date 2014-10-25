package net.twisterrob.colorfilters.android.keyboard;

import android.inputmethodservice.*;
import android.text.Editable;
import android.view.*;
import android.widget.EditText;

import net.twisterrob.android.view.KeyboardHandler;
import net.twisterrob.colorfilters.android.R;

public class FloatNavKeyboardHandler extends KeyboardHandler {
	public FloatNavKeyboardHandler(Window window, KeyboardView keyboardView) {
		super(window, keyboardView);

		keyboardView.setKeyboard(new Keyboard(keyboardView.getContext(), R.xml.keyboard_floatnav));
		keyboardView.setOnKeyboardActionListener(new MyOnKeyboardActionListener());
	}

	private class MyOnKeyboardActionListener extends BaseOnKeyboardActionListener {
		private static final int KEY_DECIMAL = '\u2396';
		private static final int KEY_CHANGE_SIGN = '\u00B1';

		private static final int KEY_NAV_W = '\u21D0';
		private static final int KEY_NAV_N = '\u21D1';
		private static final int KEY_NAV_E = '\u21D2';
		private static final int KEY_NAV_S = '\u21D3';
		private static final int KEY_NAV_NW = '\u21D6';
		private static final int KEY_NAV_NE = '\u21D7';
		private static final int KEY_NAV_SE = '\u21D8';
		private static final int KEY_NAV_SW = '\u21D9';

		private static final int FOCUS_FIRST = KEY_NAV_W;
		private static final int FOCUS_NO = -1;
		private /*static*/ final int[][] FOCUS_SEARCH = {
				{View.FOCUS_LEFT, FOCUS_NO}, // KEY_NAV_W
				{View.FOCUS_UP, FOCUS_NO}, // KEY_NAV_N
				{View.FOCUS_RIGHT, FOCUS_NO}, // KEY_NAV_E
				{View.FOCUS_DOWN, FOCUS_NO}, // KEY_NAV_S
				{FOCUS_NO, FOCUS_NO},
				{FOCUS_NO, FOCUS_NO},
				{View.FOCUS_UP, View.FOCUS_LEFT}, // KEY_NAV_NW
				{View.FOCUS_UP, View.FOCUS_RIGHT}, // KEY_NAV_NE
				{View.FOCUS_DOWN, View.FOCUS_RIGHT}, // KEY_NAV_SE
				{View.FOCUS_DOWN, View.FOCUS_LEFT}, // KEY_NAV_SW

		};

		@Override
		protected boolean onKey(EditText editor, int primaryCode, int[] keyCodes) {
			switch (primaryCode) {
				case KEY_DECIMAL:
					super.onKey('.', keyCodes);
					return true;
				case KEY_CHANGE_SIGN:
					negate(editor.getEditableText());
					return true;
				case KEY_NAV_W:
				case KEY_NAV_N:
				case KEY_NAV_E:
				case KEY_NAV_S:
				case KEY_NAV_NW:
				case KEY_NAV_NE:
				case KEY_NAV_SE:
				case KEY_NAV_SW:
					navigate(primaryCode - FOCUS_FIRST, editor);
					return true;
				default:
					return false;
			}
		}

		private void negate(Editable text) {
			if (text == null) {
				return;
			}
			char firstChar = text.length() > 0? text.charAt(0) : '+';
			if (firstChar == '-') { // -x -> x
				text.delete(0, 1);
			} else { // x -> -x
				text.insert(0, "-");
			}
		}

		private void navigate(int direction, View view) {
			int[] focusKeys = FOCUS_SEARCH[direction];
			View focus = view;
			if (focus != null && focusKeys[0] != FOCUS_NO) {
				//noinspection ResourceType
				focus = focus.focusSearch(focusKeys[0]); // View.findUserSetNextFocus
			}
			if (focus != null && focusKeys[1] != FOCUS_NO) {
				//noinspection ResourceType
				focus = focus.focusSearch(focusKeys[1]); // View.findUserSetNextFocus
			}
			if (focus != null) {
				focus.requestFocus();
			} else {
				hideCustomKeyboard();
			}
		}
	}
}
