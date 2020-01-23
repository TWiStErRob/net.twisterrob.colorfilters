package net.twisterrob.colorfilters.android.porterduff

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.EditText
import android.widget.RadioButton
import android.widget.SeekBar
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.core.graphics.alpha
import androidx.core.graphics.toColorInt
import net.twisterrob.android.view.CheckableButtonManager
import net.twisterrob.android.view.color.ColorPickerView
import net.twisterrob.android.view.listeners.OnSeekBarChangeAdapter
import net.twisterrob.android.view.listeners.TextWatcherAdapter
import net.twisterrob.colorfilters.android.ColorFilterFragment
import net.twisterrob.colorfilters.android.keyboard.KeyboardHandler
import net.twisterrob.colorfilters.android.keyboard.KeyboardMode
import net.twisterrob.colorfilters.android.replaceAlpha
import net.twisterrob.colorfilters.android.replaceAlphaFrom
import net.twisterrob.colorfilters.android.toARGBDecString
import net.twisterrob.colorfilters.android.toARGBHexString

private const val PREF_PORTERDUFF_COLOR = "PorterDuffColorFilter.color"
private const val PREF_PORTERDUFF_MODE = "PorterDuffColorFilter.mode"
private const val PREF_PORTERDUFF_SWATCH = "PorterDuffColorFilter.colorSwatch"

@TargetApi(VERSION_CODES.HONEYCOMB)
private val MODES = (mapOf(
	R.id.mode_clear to PorterDuff.Mode.CLEAR,
	R.id.mode_src to PorterDuff.Mode.SRC,
	R.id.mode_dst to PorterDuff.Mode.DST,
	R.id.mode_src_over to PorterDuff.Mode.SRC_OVER,
	R.id.mode_dst_over to PorterDuff.Mode.DST_OVER,
	R.id.mode_src_in to PorterDuff.Mode.SRC_IN,
	R.id.mode_dst_in to PorterDuff.Mode.DST_IN,
	R.id.mode_src_out to PorterDuff.Mode.SRC_OUT,
	R.id.mode_dst_out to PorterDuff.Mode.DST_OUT,
	R.id.mode_src_atop to PorterDuff.Mode.SRC_ATOP,
	R.id.mode_dst_atop to PorterDuff.Mode.DST_ATOP,
	R.id.mode_xor to PorterDuff.Mode.XOR,
	R.id.mode_darken to PorterDuff.Mode.DARKEN,
	R.id.mode_lighten to PorterDuff.Mode.LIGHTEN,
	R.id.mode_multiply to PorterDuff.Mode.MULTIPLY,
	R.id.mode_screen to PorterDuff.Mode.SCREEN
) + if (VERSION_CODES.HONEYCOMB <= VERSION.SDK_INT) mapOf(
	R.id.mode_add to PorterDuff.Mode.ADD,
	R.id.mode_overlay to PorterDuff.Mode.OVERLAY
) else emptyMap()).toSortedMap()

private val DEFAULT_COLOR = Color.argb(0xff, 0x00, 0x00, 0x00)
@SuppressLint("ObsoleteSdkInt")
@TargetApi(VERSION_CODES.HONEYCOMB)
private val DEFAULT_MODE =
	if (VERSION.SDK_INT < VERSION_CODES.HONEYCOMB) {
		PorterDuff.Mode.SCREEN
	} else {
		PorterDuff.Mode.OVERLAY
	}
private const val KEEP_SWATCH = 0

class PorterDuffFragment : ColorFilterFragment() {

	private lateinit var colorView: ColorPickerView
	private lateinit var editor: EditText
	private lateinit var rgbLabel: TextView
	private lateinit var alphaSlider: SeekBar
	private lateinit var colorPreview: View
	@ColorInt
	private var currentColor: Int = 0
	private val currentMode: PorterDuff.Mode get() = MODES.getValue(modes.checked!!.id)
	private val modes = CheckableButtonManager()
	private var pendingUpdate = false

	override val preferredKeyboardMode = KeyboardMode.Hex

	override fun displayHelp() {
		displayHelp(R.string.cf_porterduff_info_title, R.string.cf_porterduff_info)
	}

	override fun createFilter(): ColorFilter = PorterDuffColorFilter(currentColor, currentMode)

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
		inflater.inflate(R.layout.fragment_porterduff, container, false)

	@SuppressLint("ObsoleteSdkInt")
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		rgbLabel = view.findViewById(R.id.colorRGBLabel)
		colorView = view.findViewById(R.id.color)
		colorView.colorChangedListener = object : ColorPickerView.OnColorChangedListener {
			override fun colorChanged(@ColorInt color: Int) {
				updateColor(color.replaceAlphaFrom(currentColor), UpdateOrigin.Picker)
			}
		}
		colorPreview = view.findViewById(R.id.colorPreview)
		colorPreview.setOnClickListener { updateColor(DEFAULT_COLOR, null) }

		editor = view.findViewById(R.id.colorEditor)
		editor.addTextChangedListener(object : TextWatcherAdapter() {
			override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
				try {
					val color = "#${s}".toColorInt()
					updateColor(color, UpdateOrigin.Editor)
					editor.error = null
				} catch (ex: RuntimeException) {
					//Log.w(TAG, "Cannot parse color: " + s, ex);
					editor.error = ex.message + " " + s
				}
			}
		})

		alphaSlider = view.findViewById(R.id.colorAlpha)
		alphaSlider.setOnSeekBarChangeListener(object : OnSeekBarChangeAdapter() {
			override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
				updateColor(currentColor.replaceAlpha(progress), UpdateOrigin.Alpha)
			}
		})
		keyboard.registerEditText(editor)
		val modesContainer = view.findViewById<View>(R.id.modes)
		keyboard.customKeyboardListener = object : KeyboardHandler.CustomKeyboardListener {
			override fun customKeyboardShown() {
				modesContainer.visibility = View.GONE
			}

			override fun customKeyboardHidden() {
				modesContainer.visibility = View.VISIBLE
			}
		}

		(view.findViewById<View>(findView(DEFAULT_MODE)) as CompoundButton).isChecked = true
		for (id in MODES.keys) {
			modes.addButton(view.findViewById<RadioButton>(id))
		}
		if (VERSION.SDK_INT < VERSION_CODES.HONEYCOMB) {
			view.findViewById<View>(R.id.mode_add).isEnabled = false
			view.findViewById<View>(R.id.mode_overlay).isEnabled = false
		}
		modes.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { _, isChecked ->
			if (isChecked) {
				updateFilter()
			}
		})
	}

	private fun updateColor(@ColorInt color: Int, origin: UpdateOrigin?) {
		if (pendingUpdate) {
			return
		}
		try {
			pendingUpdate = true
			currentColor = color

			if (origin != UpdateOrigin.Editor) {
				editor.setText(color.toARGBHexString())
			}
			if (origin != UpdateOrigin.Alpha) {
				alphaSlider.progress = color.alpha
			}
			if (origin != UpdateOrigin.Picker) {
				colorView.color = color
			}

			rgbLabel.text = color.toARGBDecString()
			colorPreview.setBackgroundColor(color)
		} finally {
			pendingUpdate = false
		}
		updateFilter()
	}

	override fun onSaveInstanceState(outState: Bundle) {
		super.onSaveInstanceState(outState)
		outState.putInt(PREF_PORTERDUFF_SWATCH, colorView.swatchIndex)
	}

	override fun onViewStateRestored(savedInstanceState: Bundle?) {
		super.onViewStateRestored(savedInstanceState)
		if (savedInstanceState == null) {
			prefs.run {
				val color = getInt(PREF_PORTERDUFF_COLOR, DEFAULT_COLOR)
				val mode = getString(PREF_PORTERDUFF_MODE, DEFAULT_MODE.name)!!
				val swatchIndex = getInt(PREF_PORTERDUFF_SWATCH, KEEP_SWATCH)
				setValues(color, PorterDuff.Mode.valueOf(mode), swatchIndex)
			}
		} else {
			colorView.setSwatch(savedInstanceState.getInt(PREF_PORTERDUFF_SWATCH, KEEP_SWATCH))
		}
	}

	override fun onStop() {
		super.onStop()
		prefs.edit().apply {
			putInt(PREF_PORTERDUFF_COLOR, currentColor)
			putString(PREF_PORTERDUFF_MODE, currentMode.name)
			putInt(PREF_PORTERDUFF_SWATCH, colorView.swatchIndex)
		}.apply()
	}

	override fun onDestroyView() {
		keyboard.unregisterEditText(editor)
		super.onDestroyView()
	}

	override fun reset() {
		setValues(DEFAULT_COLOR, DEFAULT_MODE, KEEP_SWATCH)
	}

	private fun setValues(color: Int, mode: PorterDuff.Mode, swatchIndex: Int) {
		val modeId = findView(mode)
		view!!.findViewById<CompoundButton>(modeId).isChecked = true
		colorView.setSwatch(swatchIndex)
		updateColor(color, null)
	}

	private fun findView(mode: PorterDuff.Mode): Int =
		MODES.entries.singleOrNull { it.value == mode }?.key
			?: View.NO_ID

	override fun generateCode(): String {
		return "new PorterDuffColorFilter(" +
				"${currentColor.toARGBHexString("0x")}," +
				" PorterDuff.Mode.${currentMode.name}" +
				");"
	}

	private enum class UpdateOrigin {
		Editor,
		Picker,
		Alpha
	}
}
