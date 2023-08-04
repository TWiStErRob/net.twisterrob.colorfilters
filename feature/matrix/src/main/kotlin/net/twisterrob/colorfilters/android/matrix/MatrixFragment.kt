package net.twisterrob.colorfilters.android.matrix

import android.content.SharedPreferences
import android.graphics.ColorFilter
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import net.twisterrob.colorfilters.android.ColorFilterFragment
import net.twisterrob.colorfilters.android.keyboard.KeyboardHandler
import net.twisterrob.colorfilters.android.keyboard.KeyboardMode
import net.twisterrob.colorfilters.android.matrix.Component.Companion.COMP_B
import net.twisterrob.colorfilters.android.matrix.Component.Companion.COMP_G
import net.twisterrob.colorfilters.android.matrix.Component.Companion.COMP_R

class MatrixFragment : ColorFilterFragment() {

	companion object {

		private val SAVE_ORDER_MAP = OrderComponent::class.java.simpleName
		private val SAVE_EDITOR_DIRTY = MatrixComponent::class.java.simpleName
	}

	private val colorMatrix = ColorMatrix()
	private lateinit var saturation: Component
	private lateinit var rotates: RotatesComponent
	private lateinit var scale: Component
	private lateinit var order: OrderComponent

	private val components: Array<Component> by lazy {
		arrayOf(order, saturation, rotates, scale, editor)
	}

	/**
	 * If the matrix has been tampered with, it's better to generate the float[] constructor.
	 */
	private var dirty: Boolean = false
	private lateinit var editor: MatrixComponent

	override fun displayHelp() {
		displayHelp(R.string.cf_matrix_info_title, R.string.cf_matrix_info)
	}

	override fun createFilter(): ColorFilter = ColorMatrixColorFilter(colorMatrix)

	override val preferredKeyboardMode = KeyboardMode.FloatNav

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
		inflater.inflate(R.layout.fragment_matrix, container, false)

	private val listener = CentralRefreshListener()

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		listener.disable()
		view.findViewById<View>(R.id.matrix_reset).setOnClickListener { editor.reset() }
		order = OrderComponent(view, listener)
		saturation = SaturationComponent(view, listener)
		rotates = RotatesComponent(view, listener)
		scale = ScaleComponent(view, listener)
		editor = MatrixComponent(view, keyboard, listener)
		setupUI()

		val controlsGroup: View = view.findViewById(R.id.controls)
		val orderGroup: View = view.findViewById(R.id.order)
		keyboard.customKeyboardListener = object : KeyboardHandler.CustomKeyboardListener {
			override fun customKeyboardShown() {
				if (isPortrait) {
					controlsGroup.visibility = View.GONE
				}
				orderGroup.visibility = View.GONE
			}

			override fun customKeyboardHidden() {
				if (isPortrait) {
					controlsGroup.visibility = View.VISIBLE
				}
				orderGroup.visibility = View.VISIBLE
			}
		}
	}

	override fun onViewStateRestored(savedInstanceState: Bundle?) {
		super.onViewStateRestored(savedInstanceState)
		val combine: Boolean
		if (savedInstanceState != null) {
			dirty = savedInstanceState.getBoolean(SAVE_EDITOR_DIRTY)
			savedInstanceState.getIntArray(SAVE_ORDER_MAP)?.let { order.map = it}
			combine = !dirty
		} else {
			restoreFromPreferences(prefs)
			combine = false
		}
		listener.enable()
		listener.refresh(combine)
	}

	private fun setupUI() {
		components.forEach { it.setupUI() }
	}

	override fun onSaveInstanceState(outState: Bundle) {
		super.onSaveInstanceState(outState)
		outState.apply {
			putBoolean(SAVE_EDITOR_DIRTY, dirty)
			putIntArray(SAVE_ORDER_MAP, order.map)
		}
	}

	override fun onStop() {
		super.onStop()
		saveToPreferences(prefs)
	}

	override fun onDestroyView() {
		editor.unWire()
		super.onDestroyView()
	}

	override fun reset() {
		colorMatrix.reset()
		components.forEach { it.reset() }
		listener.refresh(true)
	}

	private fun saveToPreferences(prefs: SharedPreferences) {
		val editor = prefs.edit()
		components.forEach { it.saveToPreferences(editor) }
		editor.apply()
	}

	private fun restoreFromPreferences(prefs: SharedPreferences) {
		colorMatrix.reset()
		components.forEach { it.restoreFromPreferences(prefs) }
	}

	private fun combineMatrices() {
		colorMatrix.reset()
		ordered.forEach { it.combineInto(colorMatrix) }
		editor.setMatrix(colorMatrix)
	}

	private val ordered: Array<Component>
		get() = order.order(rotates[COMP_R], rotates[COMP_G], rotates[COMP_B], scale, saturation)

	override fun generateCode(): String =
		buildString {
			if (dirty) {
				editor.appendTo(this)
			} else {
				append("ColorMatrix matrix = new ColorMatrix();\n")
				append("ColorMatrix temp = new ColorMatrix();\n")
				ordered.forEach { if (it.appendTo(this)) append('\n') }
				append("\nreturn matrix;")
			}
		}

	private inner class CentralRefreshListener : Component.RefreshListener {

		private var enabled: Boolean = true

		override fun refresh(recombine: Boolean) {
			if (!enabled) {
				return
			}
			dirty = !recombine
			if (recombine) {
				combineMatrices()
			}
			editor.refreshModel()
			editor.combineInto(colorMatrix)
			updateFilter()
		}

		fun disable() {
			enabled = false
		}

		fun enable() {
			enabled = true
		}
	}
}
