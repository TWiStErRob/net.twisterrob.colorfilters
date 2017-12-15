package net.twisterrob.colorfilters.android.keyboard;

import android.inputmethodservice.*;
import android.view.*;
import android.widget.EditText;

import net.twisterrob.android.view.KeyboardHandler;

public class NullKeyboardHandler extends KeyboardHandler {
	private static int originalInputMode;

	public NullKeyboardHandler(Window window, KeyboardView keyboardView) {
		super(saveInputMode(window), keyboardView);
		window.setSoftInputMode(originalInputMode);

		keyboardView.setKeyboard(new Keyboard(keyboardView.getContext(), R.xml.keyboard_empty));
		keyboardView.setOnKeyboardActionListener(null);
	}

	@Override
	public void registerEditText(EditText editText) {
	}

	@Override
	public void unregisterEditText(EditText editText) {
	}

	@Override
	public boolean handleBack() {
		return false;
	}

	@Override
	public void showCustomKeyboard(View v) {
	}

	@Override
	public void hideCustomKeyboard() {
	}

	private static Window saveInputMode(Window window) {
		originalInputMode = window.getAttributes().softInputMode;
		return window;
	}
}
