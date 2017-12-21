package net.twisterrob.colorfilters.android.keyboard;

import android.inputmethodservice.*;
import android.support.annotation.NonNull;
import android.view.*;
import android.widget.EditText;

public class NullKeyboardHandler extends BaseKeyboardHandler {
	private static int originalInputMode;

	public NullKeyboardHandler(Window window, android.inputmethodservice.KeyboardView keyboardView) {
		super(saveInputMode(window), keyboardView);
		window.setSoftInputMode(originalInputMode);

		keyboardView.setKeyboard(new Keyboard(keyboardView.getContext(), R.xml.keyboard_empty));
		keyboardView.setOnKeyboardActionListener(null);
	}

	@Override
	public void registerEditText(@NonNull EditText editText) {
	}

	@Override
	public void unregisterEditText(@NonNull EditText editText) {
	}

	@Override
	public boolean handleBack() {
		return false;
	}

	@Override
	public void showCustomKeyboard(@NonNull View v) {
	}

	@Override
	public void hideCustomKeyboard() {
	}

	private static Window saveInputMode(Window window) {
		originalInputMode = window.getAttributes().softInputMode;
		return window;
	}
}
