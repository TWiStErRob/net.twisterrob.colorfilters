package net.twisterrob.colorfilters.android.keyboard;

import android.inputmethodservice.*;
import android.view.*;
import android.widget.EditText;

public class FloatNavKeyboardHandler extends FloatKeyboardHandler {
	public FloatNavKeyboardHandler(Window window, android.inputmethodservice.KeyboardView keyboardView) {
		super(window, keyboardView);

		keyboardView.setKeyboard(new Keyboard(keyboardView.getContext(), R.xml.keyboard_floatnav));
		keyboardView.setOnKeyboardActionListener(new FloatNavKeyboardActionListener());
	}

	protected class FloatNavKeyboardActionListener extends FloatKeyboardActionListener {
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
					return super.onKey(editor, primaryCode, keyCodes);
			}
		}

		private void navigate(int direction, View view) {
			int[] focusKeys = FOCUS_SEARCH[direction];
			View focus = view;
			if (focus != null && focusKeys[0] != FOCUS_NO) {
				focus = focus.focusSearch(focusKeys[0]); // View.findUserSetNextFocus
			}
			if (focus != null && focusKeys[1] != FOCUS_NO) {
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
