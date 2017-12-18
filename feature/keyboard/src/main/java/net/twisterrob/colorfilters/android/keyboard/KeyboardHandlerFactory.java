package net.twisterrob.colorfilters.android.keyboard;

import android.inputmethodservice.KeyboardView;
import android.support.annotation.NonNull;
import android.view.Window;

public class KeyboardHandlerFactory {

	public KeyboardHandler create(
			@NonNull KeyboardMode mode, @NonNull Window window, @NonNull KeyboardView keyboardView) {
		switch (mode) {
			case Hex:
				return new HexKeyboardHandler(window, keyboardView);
			case FloatNav:
				return new FloatNavKeyboardHandler(window, keyboardView);
			case Float:
				return new FloatKeyboardHandler(window, keyboardView);
			case NATIVE:
				return new NullKeyboardHandler(window, keyboardView);
			default:
				throw new UnsupportedOperationException("Cannot create keyboard for " + mode);
		}
	}
}
