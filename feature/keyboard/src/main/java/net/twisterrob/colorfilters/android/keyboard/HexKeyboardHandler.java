package net.twisterrob.colorfilters.android.keyboard;

import java.util.regex.*;

import android.inputmethodservice.*;
import android.view.Window;
import android.widget.EditText;

import net.twisterrob.android.view.KeyboardHandler;

public class HexKeyboardHandler extends KeyboardHandler {
	public HexKeyboardHandler(Window window, KeyboardView keyboardView) {
		super(window, keyboardView);

		keyboardView.setKeyboard(new Keyboard(keyboardView.getContext(), R.xml.keyboard_hex));
		keyboardView.setOnKeyboardActionListener(new MyOnKeyboardActionListener());
	}

	private class MyOnKeyboardActionListener extends BaseOnKeyboardActionListener {
		private static final int KEY_NEGATE = '\u00AC';

		@Override
		protected boolean onKey(EditText editor, int primaryCode, int[] keyCodes) {
			switch (primaryCode) {
				case KEY_NEGATE:
					super.onText(negate(editor.getEditableText()));
					return true;
				default:
					return false;
			}
		}

		private CharSequence negate(CharSequence editable) {
			if (editable == null) {
				return null;
			}
			Matcher m = Pattern.compile("[0-9A-Fa-f]+").matcher(editable); //NON-NLS
			StringBuffer sb = new StringBuffer(editable.length());
			while (m.find()) {
				m.appendReplacement(sb, negate(m.group()));
			}
			m.appendTail(sb);
			return sb;
		}

		private String negate(String s) {
			char[] arr = s.toCharArray();
			for (int i = 0; i < arr.length; ++i) {
				arr[i] = NEGATE[arr[i]];
			}
			return new String(arr);
		}

		private /*static*/ final char[] NEGATE = {
		            /*0x0?*/ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
		            /*0x1?*/ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
		            /*0x2?*/ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    /*0x3?*/ 'F', 'E', 'D', 'C', 'B', 'A', '9', '8', '7', '6', 0, 0, 0, 0, 0, 0,
                    /*0x4?*/ 0, '5', '4', '3', '2', '1', '0', 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    /*0x5?*/ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    /*0x6?*/ 0, '5', '4', '3', '2', '1', '0', 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    /*0x7?*/ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
		};
	}
}
