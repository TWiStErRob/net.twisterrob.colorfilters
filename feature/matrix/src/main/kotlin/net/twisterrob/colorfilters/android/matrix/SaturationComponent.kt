package net.twisterrob.colorfilters.android.matrix

import android.content.SharedPreferences
import android.graphics.ColorMatrix
import android.view.View
import android.widget.SeekBar
import android.widget.TextView
import net.twisterrob.android.view.listeners.OnSeekBarChangeAdapter
import net.twisterrob.colorfilters.android.formatRoot

private const val NO_SCALE: Float = 1f
private const val PREF_SATURATION_SAT = "Saturation.sat"

internal class SaturationComponent(
	view: View,
	listener: Component.RefreshListener
) : Component(view, listener) {

	private val satMatrix = ColorMatrix()
	private val saturationLabel: TextView = tv(R.id.edit_sat)
	private val saturation: SeekBar = sb(R.id.seek_sat)

	private var value: Float
		get() = get(saturation, 1f, 0f)
		set(value) = set(saturation, 1f, 0f, value)

	override fun setupUI() {
		saturation.setOnSeekBarChangeListener(object : OnSeekBarChangeAdapter() {
			override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
				saturationLabel.text = getDisplay(value)
				refreshModel()
				dispatchRefresh(true)
			}
		})
	}

	override fun saveToPreferences(editor: SharedPreferences.Editor) {
		editor.putFloat(PREF_SATURATION_SAT, value)
	}

	override fun restoreFromPreferences(prefs: SharedPreferences) {
		value = prefs.getFloat(PREF_SATURATION_SAT, NO_SCALE)
	}

	override fun reset() {
		value = NO_SCALE
	}

	override fun refreshModel() {
		val sat = value
		satMatrix.setSaturation(sat)
	}

	override fun combineInto(colorMatrix: ColorMatrix) {
		colorMatrix.postConcat(satMatrix)
	}

	override fun appendTo(sb: StringBuilder): Boolean {
		val value = value
		if (value != NO_SCALE) {
			val sat = getDisplay(value)
			sb.append("\ntemp.setSaturation(").append(sat).append(");")
			sb.append("\nmatrix.postConcat(temp);")
			return true
		}
		return false
	}
}

private fun getDisplay(value: Float): String = "%.2f".formatRoot(value)
