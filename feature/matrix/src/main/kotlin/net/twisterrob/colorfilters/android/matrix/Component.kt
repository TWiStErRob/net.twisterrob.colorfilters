package net.twisterrob.colorfilters.android.matrix

import android.content.SharedPreferences
import android.graphics.ColorMatrix
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.SeekBar
import android.widget.TextView

// TODO refactor to subfragments and/or custom View/ViewGroups
internal abstract class Component(
	protected val view: View,
	private val listener: RefreshListener
) {

	internal interface RefreshListener {
		fun refresh(recombine: Boolean)
	}

	abstract fun setupUI()

	open fun unWire() {}

	protected fun et(viewID: Int): EditText = view.findViewById(viewID)
	protected fun tv(viewID: Int): TextView = view.findViewById(viewID)
	protected fun v(viewID: Int): View = view.findViewById(viewID)
	protected fun vg(viewID: Int): ViewGroup = view.findViewById(viewID)
	protected fun sb(viewID: Int): SeekBar = view.findViewById(viewID)

	abstract fun reset()

	abstract fun refreshModel()

	abstract fun combineInto(colorMatrix: ColorMatrix)

	abstract fun appendTo(sb: StringBuilder): Boolean

	protected fun dispatchRefresh(recombine: Boolean) {
		listener.refresh(recombine)
	}

	abstract fun saveToPreferences(editor: SharedPreferences.Editor)

	abstract fun restoreFromPreferences(prefs: SharedPreferences)

	companion object {

		@JvmStatic val COMP_R = 0
		@JvmStatic val COMP_G = 1
		@JvmStatic val COMP_B = 2
		@JvmStatic val COMP_A = 3

		@JvmStatic
		protected fun get(seekBar: SeekBar, scale: Float, offset: Float): Float =
			seekBar.progress.toFloat() / seekBar.max.toFloat() * scale + offset

		@JvmStatic
		protected fun set(seekBar: SeekBar, scale: Float, offset: Float, value: Float) {
			seekBar.progress = ((value - offset) / scale * seekBar.max).toInt()
		}
	}
}
