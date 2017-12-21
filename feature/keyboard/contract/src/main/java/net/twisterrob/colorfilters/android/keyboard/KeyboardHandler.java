package net.twisterrob.colorfilters.android.keyboard;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.EditText;

public interface KeyboardHandler {

	interface CustomKeyboardListener {
		void customKeyboardShown();

		void customKeyboardHidden();
	}

	void hideCustomKeyboard();

	void showCustomKeyboard(@NonNull View v);

	void registerEditText(@NonNull EditText editText);

	void unregisterEditText(@NonNull EditText editText);

	void setHapticFeedback(boolean haptic);

	boolean handleBack();

	void setCustomKeyboardListener(@NonNull CustomKeyboardListener listener);
}

