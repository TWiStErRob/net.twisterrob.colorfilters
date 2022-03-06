package net.twisterrob.colorfilters.android.matrix

import android.content.SharedPreferences
import android.graphics.ColorMatrix
import android.view.View
import android.widget.SeekBar
import net.twisterrob.android.view.listeners.OnSeekBarChangeAdapter
import net.twisterrob.colorfilters.android.formatRoot

private const val NO_SCALE: Float = 1f
private const val SCALE_MIN: Float = -1f
private const val SCALE_MAX: Float = 2f
private const val PREF_SCALE_R = "Scale.scaleR"
private const val PREF_SCALE_G = "Scale.scaleG"
private const val PREF_SCALE_B = "Scale.scaleB"
private const val PREF_SCALE_A = "Scale.scaleA"

internal class ScaleComponent(
	view: View,
	listener: Component.RefreshListener
) : Component(view, listener) {

	private val scaleMatrix = ColorMatrix()

	private val scaleRGBA = arrayOf(
		sb(R.id.seek_sR), sb(R.id.seek_sG), sb(R.id.seek_sB), sb(R.id.seek_sA)
	)
	private val scaleRGBALabel = arrayOf(
		tv(R.id.edit_sR), tv(R.id.edit_sG), tv(R.id.edit_sB), tv(R.id.edit_sA)
	)

	override fun setupUI() {
		for (i in scaleRGBA.indices) {
			val tv = scaleRGBALabel[i]
			val sb = scaleRGBA[i]
			sb.setOnSeekBarChangeListener(object : OnSeekBarChangeAdapter() {
				override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
					tv.text = getDisplay(getValue(seekBar))
					refreshModel()
					dispatchRefresh(true)
				}
			})
		}
	}

	override fun saveToPreferences(editor: SharedPreferences.Editor) {
		editor.putFloat(PREF_SCALE_R, getValue(COMP_R))
		editor.putFloat(PREF_SCALE_G, getValue(COMP_G))
		editor.putFloat(PREF_SCALE_B, getValue(COMP_B))
		editor.putFloat(PREF_SCALE_A, getValue(COMP_A))
	}

	override fun restoreFromPreferences(prefs: SharedPreferences) {
		setValue(COMP_R, prefs.getFloat(PREF_SCALE_R, NO_SCALE))
		setValue(COMP_G, prefs.getFloat(PREF_SCALE_G, NO_SCALE))
		setValue(COMP_B, prefs.getFloat(PREF_SCALE_B, NO_SCALE))
		setValue(COMP_A, prefs.getFloat(PREF_SCALE_A, NO_SCALE))
	}

	override fun reset() {
		for (scaleBar in scaleRGBA) {
			setValue(scaleBar, NO_SCALE)
		}
	}

	override fun refreshModel() {
		val rScale = getValue(COMP_R)
		val gScale = getValue(COMP_G)
		val bScale = getValue(COMP_B)
		val aScale = getValue(COMP_A)
		scaleMatrix.setScale(rScale, gScale, bScale, aScale)
	}

	override fun combineInto(colorMatrix: ColorMatrix) {
		colorMatrix.postConcat(scaleMatrix)
	}

	override fun appendTo(sb: StringBuilder): Boolean {
		val rScale = getValue(COMP_R)
		val gScale = getValue(COMP_G)
		val bScale = getValue(COMP_B)
		val aScale = getValue(COMP_A)
		@Suppress("ComplexCondition")
		if (rScale != NO_SCALE || gScale != NO_SCALE || bScale != NO_SCALE || aScale != NO_SCALE) {
			sb.append("\ntemp.setScale(")
				.append(getDisplay(rScale)).append(", ")
				.append(getDisplay(gScale)).append(", ")
				.append(getDisplay(bScale)).append(", ")
				.append(getDisplay(aScale)).append(");")
			sb.append("\nmatrix.postConcat(temp);")
			return true
		}
		return false
	}

	private fun getDisplay(value: Float): String =
		"%.2f".formatRoot(value)

	private fun getValue(component: Int): Float =
		get(scaleRGBA[component], SCALE_MAX - SCALE_MIN, SCALE_MIN)

	private fun getValue(scaleBar: SeekBar): Float =
		get(scaleBar, SCALE_MAX - SCALE_MIN, SCALE_MIN)

	private fun setValue(component: Int, value: Float) =
		set(scaleRGBA[component], SCALE_MAX - SCALE_MIN, SCALE_MIN, value)

	private fun setValue(scaleBar: SeekBar, value: Float) =
		set(scaleBar, SCALE_MAX - SCALE_MIN, SCALE_MIN, value)
}
