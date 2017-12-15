package net.twisterrob.colorfilters.android.keyboard;

import android.inputmethodservice.KeyboardView;
import android.view.Window;

public interface KeyboardHandlerFactory {

	KeyboardHandler create(Window window, KeyboardView keyboardView);
}
