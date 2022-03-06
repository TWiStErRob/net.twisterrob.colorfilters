package net.twisterrob.colorfilters.android.lighting

import android.graphics.Color
import android.graphics.LightingColorFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.core.graphics.toColorInt
import net.twisterrob.android.view.color.ColorPickerView
import net.twisterrob.android.view.listeners.TextWatcherAdapter
import net.twisterrob.colorfilters.android.ColorFilterFragment
import net.twisterrob.colorfilters.android.keyboard.KeyboardHandler
import net.twisterrob.colorfilters.android.keyboard.KeyboardMode
import net.twisterrob.colorfilters.android.toRGBDecString
import net.twisterrob.colorfilters.android.toRGBHexString

private const val PREF_LIGHTING_MUL = "LightingColorFilter.mul"
private const val PREF_LIGHTING_MUL_SWATCH = "LightingColorFilter.mulSwatch"
private const val PREF_LIGHTING_ADD = "LightingColorFilter.add"
private const val PREF_LIGHTING_ADD_SWATCH = "LightingColorFilter.addSwatch"
private val DEFAULT_MUL = Color.argb(0xff, 0xff, 0xff, 0xff)
private val DEFAULT_ADD = Color.argb(0xff, 0x00, 0x00, 0x00)
private const val KEEP_SWATCH = -1

class LightingFragment : ColorFilterFragment() {

	private lateinit var mulWiring: Wiring
	private lateinit var addWiring: Wiring

	override val preferredKeyboardMode = KeyboardMode.Hex

	private lateinit var mulColor: ColorPickerView
	private lateinit var addColor: ColorPickerView

	override fun displayHelp() {
		displayHelp(R.string.cf_lighting_info_title, R.string.cf_lighting_info)
	}

	override fun createFilter() =
		if (::mulColor.isInitialized && ::addColor.isInitialized) {
			LightingColorFilter(mulColor.color, addColor.color)
		} else {
			null
		}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
		inflater.inflate(R.layout.fragment_lighting, container, false)

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		mulColor = view.findViewById(R.id.mulColor)
		val mulColorDesc: EditText = view.findViewById(R.id.mulEditor)
		val mulColorRGB: TextView = view.findViewById(R.id.mulRGBLabel)
		val mulPreview: View = view.findViewById(R.id.mulPreview)
		mulWiring = Wiring(mulColor, mulColorDesc, mulColorRGB, mulPreview, DEFAULT_MUL)

		addColor = view.findViewById(R.id.addColor)
		val addColorDesc: EditText = view.findViewById(R.id.addEditor)
		val addColorRGB: TextView = view.findViewById(R.id.addRGBLabel)
		val addPreview: View = view.findViewById(R.id.addPreview)
		addWiring = Wiring(addColor, addColorDesc, addColorRGB, addPreview, DEFAULT_ADD)

		keyboard.customKeyboardListener = object : KeyboardHandler.CustomKeyboardListener {
			override fun customKeyboardShown() {
				if (isPortrait) {
					mulColor.visibility = View.GONE
					addColor.visibility = View.GONE
				}
			}

			override fun customKeyboardHidden() {
				if (isPortrait) {
					mulColor.visibility = View.VISIBLE
					addColor.visibility = View.VISIBLE
				}
			}
		}
	}

	override fun onSaveInstanceState(outState: Bundle) = super.onSaveInstanceState(outState).also {
		outState.putInt(PREF_LIGHTING_MUL_SWATCH, mulColor.swatchIndex)
		outState.putInt(PREF_LIGHTING_ADD_SWATCH, addColor.swatchIndex)
	}

	override fun onViewStateRestored(savedInstanceState: Bundle?) {
		super.onViewStateRestored(savedInstanceState)
		if (savedInstanceState == null) {
			prefs.run {
				val mul = getInt(PREF_LIGHTING_MUL, DEFAULT_MUL)
				val mulSwatch = getInt(PREF_LIGHTING_MUL_SWATCH, KEEP_SWATCH)
				val add = getInt(PREF_LIGHTING_ADD, DEFAULT_ADD)
				val addSwatch = getInt(PREF_LIGHTING_ADD_SWATCH, KEEP_SWATCH)
				setValues(mul, mulSwatch, add, addSwatch)
			}
		} else {
			mulColor.setSwatch(savedInstanceState.getInt(PREF_LIGHTING_MUL_SWATCH, KEEP_SWATCH))
			addColor.setSwatch(savedInstanceState.getInt(PREF_LIGHTING_ADD_SWATCH, KEEP_SWATCH))
		}
	}

	override fun onStop() = super.onStop().also {
		prefs.edit().apply {
			putInt(PREF_LIGHTING_MUL, mulColor.color)
			putInt(PREF_LIGHTING_MUL_SWATCH, mulColor.swatchIndex)
			putInt(PREF_LIGHTING_ADD, addColor.color)
			putInt(PREF_LIGHTING_ADD_SWATCH, addColor.swatchIndex)
		}.apply()
	}

	override fun onDestroyView() {
		keyboard.unregisterEditText(requireView().findViewById(R.id.mulEditor))
		keyboard.unregisterEditText(requireView().findViewById(R.id.addEditor))
		super.onDestroyView()
	}

	override fun reset() {
		setValues(DEFAULT_MUL, KEEP_SWATCH, DEFAULT_ADD, KEEP_SWATCH)
	}

	private fun setValues(mul: Int, mulSwatch: Int, add: Int, addSwatch: Int) {
		mulColor.setSwatch(mulSwatch)
		mulWiring.updateColor(mul, null)

		addColor.setSwatch(addSwatch)
		addWiring.updateColor(add, null)
	}

	override fun generateCode(): String {
		if (::mulColor.isInitialized && ::addColor.isInitialized) {
			val mul = mulColor.color.toRGBHexString("0x")
			val add = addColor.color.toRGBHexString("0x")
			return "new LightingColorFilter(${mul}, ${add});"
		} else {
			throw IllegalStateException("No colors available for code generation")
		}
	}

	private inner class Wiring(
		private val colorView: ColorPickerView,
		private val editor: EditText,
		private val rgbLabel: TextView,
		private val preview: View,
		private val defaultColor: Int
	) {

		private var pendingUpdate: Boolean = false

		init {
			colorView.isContinuousMode = true
			preview.setOnClickListener {
				updateColor(defaultColor, null)
			}
			editor.addTextChangedListener(object : TextWatcherAdapter() {
				override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
					try {
						val color = "#${s}".toColorInt()
						updateColor(color, UpdateOrigin.Editor)
						editor.error = null
					} catch (ex: RuntimeException) {
						editor.error = ex.message
					}
				}
			})
			colorView.colorChangedListener = object : ColorPickerView.OnColorChangedListener {
				override fun colorChanged(@ColorInt color: Int) {
					updateColor(color, UpdateOrigin.Picker)
				}
			}
			keyboard.registerEditText(editor)
		}

		fun updateColor(@ColorInt color: Int, origin: UpdateOrigin?) {
			if (pendingUpdate) {
				return
			}
			try {
				pendingUpdate = true

				if (origin != UpdateOrigin.Editor) {
					editor.setText(color.toRGBHexString())
				}
				if (origin != UpdateOrigin.Picker) {
					colorView.color = color
				}

				rgbLabel.text = color.toRGBDecString()
				preview.setBackgroundColor(color)
			} finally {
				pendingUpdate = false
			}
			updateFilter()
		}
	}

	private enum class UpdateOrigin {
		Editor,
		Picker
	}
}
