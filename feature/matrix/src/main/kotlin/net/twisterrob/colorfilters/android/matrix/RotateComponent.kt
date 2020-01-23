package net.twisterrob.colorfilters.android.matrix

import android.content.SharedPreferences
import android.graphics.ColorMatrix
import android.view.View
import android.widget.SeekBar
import android.widget.TextView
import net.twisterrob.android.view.listeners.OnSeekBarChangeAdapter
import net.twisterrob.colorfilters.android.formatRoot
import kotlin.math.roundToInt

internal class RotateComponent(
	view: View,
	listener: Component.RefreshListener,
	private val compRGB: Int
) : Component(view, listener) {

	companion object {

		private const val PREF_ROTATE_ROT = "Rotate.rot/"
		private const val NO_ROT = 0
		private const val FULL_ROT = 360
		private val seekIDs = intArrayOf(R.id.seek_rR, R.id.seek_rG, R.id.seek_rB)
		private val editIDs = intArrayOf(R.id.edit_rR, R.id.edit_rG, R.id.edit_rB)

		private fun getDisplay(value: Int): String = "% 3d".formatRoot(value)
	}

	private val rotateMatrix = ColorMatrix()
	private val valueView: TextView = tv(editIDs[compRGB])
	private val slider: SeekBar = sb(seekIDs[compRGB])

	override fun setupUI() {
		slider.setOnSeekBarChangeListener(object : OnSeekBarChangeAdapter() {
			override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
				valueView.text = getDisplay(value)
				refreshModel()
				dispatchRefresh(true)
			}
		})
	}

	override fun saveToPreferences(editor: SharedPreferences.Editor) {
		editor.putInt(PREF_ROTATE_ROT + compRGB, value)
	}

	override fun restoreFromPreferences(prefs: SharedPreferences) {
		value = FULL_ROT // force onProgressChanged
		value = prefs.getInt(PREF_ROTATE_ROT + compRGB, NO_ROT)
	}

	override fun reset() {
		value = FULL_ROT // force onProgressChanged
		value = NO_ROT
	}

	override fun refreshModel() {
		val rot = value
		rotateMatrix.setRotate(compRGB, rot.toFloat())
	}

	override fun combineInto(colorMatrix: ColorMatrix) {
		colorMatrix.postConcat(rotateMatrix)
	}

	override fun appendTo(sb: StringBuilder): Boolean {
		val value = value
		if (NO_ROT != value && FULL_ROT != value) {
			val rot = getDisplay(value).trim()
			sb.append("\ntemp.setRotate(").append(compRGB).append(", ").append(rot).append(");")
			sb.append("\nmatrix.postConcat(temp);")
			return true
		}
		return false
	}

	private var value: Int
		get() = get(slider, FULL_ROT.toFloat(), 0f).roundToInt()
		set(value) = set(slider, FULL_ROT.toFloat(), 0f, value.toFloat())
}
