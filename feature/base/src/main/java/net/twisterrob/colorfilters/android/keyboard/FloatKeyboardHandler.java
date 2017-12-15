package net.twisterrob.colorfilters.android.keyboard;

import android.inputmethodservice.*;
import android.text.Editable;
import android.view.Window;
import android.widget.EditText;

import net.twisterrob.android.view.KeyboardHandler;
import net.twisterrob.colorfilters.base.R;

public class FloatKeyboardHandler extends KeyboardHandler {
	public FloatKeyboardHandler(Window window, KeyboardView keyboardView) {
		super(window, keyboardView);

		keyboardView.setKeyboard(new Keyboard(keyboardView.getContext(), R.xml.keyboard_float));
		keyboardView.setOnKeyboardActionListener(new FloatKeyboardActionListener());
	}

	protected class FloatKeyboardActionListener extends BaseOnKeyboardActionListener {
		private static final int KEY_DECIMAL = '\u2396';
		private static final int KEY_CHANGE_SIGN = '\u00B1';

		@Override
		protected boolean onKey(EditText editor, int primaryCode, int[] keyCodes) {
			switch (primaryCode) {
				case KEY_DECIMAL:
					super.onKey('.', keyCodes);
					return true;
				case KEY_CHANGE_SIGN:
					negate(editor.getEditableText());
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
	}
}
