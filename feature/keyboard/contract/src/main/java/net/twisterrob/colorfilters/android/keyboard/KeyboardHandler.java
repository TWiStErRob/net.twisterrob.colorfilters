package net.twisterrob.colorfilters.android.keyboard;

import android.view.View;
import android.widget.EditText;

public interface KeyboardHandler {

	interface CustomKeyboardListener {
		void customKeyboardShown();

		void customKeyboardHidden();
	}

	void hideCustomKeyboard();

	void showCustomKeyboard(View v);

	void registerEditText(EditText editText);

	void unregisterEditText(EditText editText);

	void setHapticFeedback(boolean haptic);

	boolean handleBack();

	void setCustomKeyboardListner(CustomKeyboardListener listener);
}

