package net.twisterrob.android.view.listeners

import android.widget.SeekBar

open class OnSeekBarChangeAdapter : SeekBar.OnSeekBarChangeListener {

	override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
		// Default empty implementation, optional override.
	}

	override fun onStartTrackingTouch(seekBar: SeekBar) {
		// Default empty implementation, optional override.
	}

	override fun onStopTrackingTouch(seekBar: SeekBar) {
		// Default empty implementation, optional override.
	}
}
