package net.twisterrob.colorfilters.android.matrix

import android.content.SharedPreferences
import android.graphics.ColorMatrix
import android.view.View

internal class RotatesComponent(
	view: View,
	listener: Component.RefreshListener
) : Component(view, listener) {

	private val rotates = arrayOf(
		RotateComponent(view, listener, COMP_R),
		RotateComponent(view, listener, COMP_G),
		RotateComponent(view, listener, COMP_B)
	)

	override fun setupUI() {
		rotates.forEach { it.setupUI() }
	}

	override fun saveToPreferences(editor: SharedPreferences.Editor) {
		rotates.forEach { it.saveToPreferences(editor) }
	}

	override fun restoreFromPreferences(prefs: SharedPreferences) {
		rotates.forEach { it.restoreFromPreferences(prefs) }
	}

	override fun reset() {
		rotates.forEach { it.reset() }
	}

	override fun refreshModel() {
		rotates.forEach { it.refreshModel() }
	}

	override fun combineInto(colorMatrix: ColorMatrix) {
		rotates.forEach { it.combineInto(colorMatrix) }
	}

	override fun appendTo(sb: StringBuilder): Boolean =
		rotates.fold(false) { written, component ->
			written or component.appendTo(sb)
		}

	operator fun get(compRGB: Int): Component =
		rotates[compRGB]
}
