package net.twisterrob.colorfilters.android.keyboard;

import android.inputmethodservice.KeyboardView;
import android.view.Window;

public enum KeyboardMode implements KeyboardHandlerFactory {
	Hex {
		@Override
		public KeyboardHandler create(Window window, KeyboardView keyboardView) {
			return new HexKeyboardHandler(window, keyboardView);
		}
	},
	FloatNav {
		@Override
		public KeyboardHandler create(Window window, KeyboardView keyboardView) {
			return new FloatNavKeyboardHandler(window, keyboardView);
		}
	},
	Float {
		@Override
		public KeyboardHandler create(Window window, KeyboardView keyboardView) {
			return new FloatKeyboardHandler(window, keyboardView);
		}
	},
	NATIVE {
		@Override
		public KeyboardHandler create(Window window, KeyboardView keyboardView) {
			return new NullKeyboardHandler(window, keyboardView);
		}
	}
}
