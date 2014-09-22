package net.twisterrob.colorfilters.android.keyboard;

import android.inputmethodservice.KeyboardView;
import android.view.Window;

import net.twisterrob.android.view.KeyboardHandler;

public enum KeyboardMode {
    Hex {
        @Override
        public KeyboardHandler create(Window window, KeyboardView keyboardView) {
            return new HexKeyboardHandler(window, keyboardView);
        }
    },
    Float {
        @Override
        public KeyboardHandler create(Window window, KeyboardView keyboardView) {
            return new FloatNavKeyboardHandler(window, keyboardView);
        }
    },
    NATIVE {
        @Override
        public KeyboardHandler create(Window window, KeyboardView keyboardView) {
            return new NullKeyboardHandler(window, keyboardView);
        }
    };

    public abstract KeyboardHandler create(Window window, KeyboardView keyboardView);
}
