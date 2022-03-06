package net.twisterrob.android.view.listeners

import android.text.Editable
import android.text.TextWatcher

open class TextWatcherAdapter : TextWatcher {

	override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
		// Default empty implementation, optional override.
	}

	override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
		// Default empty implementation, optional override.
	}

	override fun afterTextChanged(s: Editable) {
		// Default empty implementation, optional override.
	}
}
