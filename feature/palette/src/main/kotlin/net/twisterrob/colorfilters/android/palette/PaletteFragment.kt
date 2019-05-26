package net.twisterrob.colorfilters.android.palette

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.AdapterView.OnItemLongClickListener
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.BaseAdapter
import android.widget.EditText
import android.widget.ListView
import android.widget.SeekBar
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.palette.graphics.Palette
import androidx.palette.graphics.Palette.Swatch
import net.twisterrob.android.view.listeners.OnSeekBarChangeAdapter
import net.twisterrob.android.view.listeners.TextWatcherAdapter
import net.twisterrob.colorfilters.android.ColorFilterFragment
import net.twisterrob.colorfilters.android.formatRoot
import net.twisterrob.colorfilters.android.keyboard.KeyboardHandler
import net.twisterrob.colorfilters.android.keyboard.KeyboardMode
import net.twisterrob.colorfilters.android.palette.PaletteFragment.PaletteAdapter.Display
import net.twisterrob.colorfilters.android.toRGBHexString
import net.twisterrob.colorfilters.base.R as baseR

private const val PREF_PALETTE_NUM_COLORS = "Palette.numColors"
private const val PREF_PALETTE_RESIZE_DIMEN = "Palette.resizeDimen"
private const val PREF_PALETTE_DISPLAY = "Palette.display"
private const val DEFAULT_NUM_COLORS = 16
private const val DEFAULT_RESIZE_DIMEN = 192

class PaletteFragment : ColorFilterFragment() {

	override val preferredKeyboardMode: KeyboardMode = KeyboardMode.Float

	private var currentNumColors: Int = 0
	private var currentResizeDimen: Int = 0
	private var lastImage: Bitmap? = null

	private lateinit var numColorSlider: SeekBar
	private lateinit var numColorEditor: EditText
	private lateinit var numSwatches: TextView
	private lateinit var resizeDimenSlider: SeekBar
	private lateinit var resizeDimenEditor: EditText

	private lateinit var swatchDisplay: Spinner
	private lateinit var swatchList: ListView
	private val swatchAdapter = PaletteAdapter()

	private var pendingUpdate: Boolean = false

	override fun displayHelp() {
		displayHelp(R.string.cf_palette_info_title, R.string.cf_palette_info)
	}

	override fun createFilter(): ColorFilter? {
		val pos = swatchList.checkedItemPosition
		//Log.d("Palette", "createFilter, pos = ${pos}")
		if (pos < 0) {
			return null
		}
		var color = swatchList.adapter.getItemId(pos).toInt()
		color = (0x00FFFFFF and color) or 0x60000000 // replace alpha
		return PorterDuffColorFilter(color, PorterDuff.Mode.SRC_OVER)
	}

	override fun updateFilter() {
		generatePalette()
		super.updateFilter()
	}

	override fun imageChanged() {
		val bitmap = currentBitmap
		lastImage = bitmap
		if (bitmap != null) {
			val wasAtMax = resizeDimenSlider.max == resizeDimenSlider.progress
			resizeDimenSlider.max = Math.max(bitmap.width, bitmap.height) - 1
			if (wasAtMax) {
				updateResizeDimen(resizeDimenSlider.max + 1, null)
			}
		}
		resizeDimenSlider.isEnabled = bitmap != null
		resizeDimenEditor.isEnabled = bitmap != null
		generatePalette()
		super.updateFilter()
	}

	private fun generatePalette() {
		val image = lastImage
		if (image != null) {
			@Suppress("deprecation")
			val palette = Palette
				.from(image)
				.maximumColorCount(currentNumColors)
				.resizeBitmapSize(currentResizeDimen)
				.generate()
			numSwatches.text = palette.swatches.size.asString()
			swatchAdapter.update(palette, currentSort)
		} else {
			numSwatches.text = "?"
			swatchAdapter.notifyDataSetInvalidated()
		}
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
		val view: View = inflater.inflate(R.layout.fragment_palette, container, false)
		val list: ListView = view.findViewById(android.R.id.list)
		list.addHeaderView(inflater.inflate(R.layout.inc_palette_header, list, false))
		return view
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		numSwatches = view.findViewById(R.id.numPalette)

		numColorSlider = view.findViewById(R.id.numColors)
		numColorSlider.setOnSeekBarChangeListener(object : OnSeekBarChangeAdapter() {
			override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
				updateNumColors(progress + 1, UpdateOrigin.Slider)
			}
		})
		numColorEditor = view.findViewById(R.id.numEditor)
		keyboard.registerEditText(numColorEditor)
		numColorEditor.addTextChangedListener(object : TextWatcherAdapter() {
			override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
				try {
					val numColors = s.toString().fromString()
					updateNumColors(numColors, UpdateOrigin.Editor)
					numColorEditor.error = null
				} catch (ex: RuntimeException) {
					//Log.w(TAG, "Cannot parse color: " + s, ex);
					numColorEditor.error = ex.message + " " + s
				}
			}
		})

		resizeDimenSlider = view.findViewById(R.id.resizeDimen)
		resizeDimenSlider.setOnSeekBarChangeListener(object : OnSeekBarChangeAdapter() {
			override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
				updateResizeDimen(progress + 1, UpdateOrigin.Slider)
			}
		})
		resizeDimenEditor = view.findViewById(R.id.resizeDimenEditor)
		keyboard.registerEditText(resizeDimenEditor)
		resizeDimenEditor.addTextChangedListener(object : TextWatcherAdapter() {
			override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
				try {
					val resizeDimen = s.toString().fromString()
					updateResizeDimen(resizeDimen, UpdateOrigin.Editor)
					resizeDimenEditor.error = null
				} catch (ex: RuntimeException) {
					//Log.w(TAG, "Cannot parse color: ${s}", ex)
					resizeDimenEditor.error = ex.message + " " + s
				}
			}
		})

		swatchList = view.findViewById(android.R.id.list)
		swatchList.onItemClickListener = OnItemClickListener { _, _, position, _ ->
			swatchList.setItemChecked(position, true)
			super@PaletteFragment.updateFilter()
		}
		swatchList.onItemLongClickListener = object : OnItemLongClickListener {
			override fun onItemLongClick(parent: AdapterView<*>, view: View, position: Int, id: Long): Boolean {
				val context = parent.context
				val swatch = parent.adapter.getItem(position) as Swatch?
				if (swatch == null) {
					Toast.makeText(context, "No swatch", Toast.LENGTH_SHORT).show()
					return true
				}
				val titles = arrayOf<CharSequence>(
					"Color",
					"Title Text Color",
					"Body Text Color",
					"Summary",
					"Android Color Resources"
				)
				val values = arrayOf<CharSequence>(
					swatch.rgb.toRGBHexString(),
					swatch.titleTextColor.toRGBHexString(),
					swatch.bodyTextColor.toRGBHexString(),
					"""
						Color: #${swatch.rgb.toRGBHexString()}
						Title: #${swatch.titleTextColor.toRGBHexString()}
						Body: #${swatch.bodyTextColor.toRGBHexString()}
						Population: ${swatch.population}
					""".trimIndent(),
					res("myColor", swatch)
				)

				AlertDialog.Builder(context)
					.setTitle("Copy swatch to clipboard")
					.setItems(titles) { _, which ->
						copyToClipboard(context, titles[which], values[which])
						val copiedAlert = context.getString(baseR.string.cf_info_copy_toast_arg, titles[which])
						Toast.makeText(context, copiedAlert, Toast.LENGTH_SHORT).show()
					}
					.setNeutralButton("Copy all named swatches") { _, _ ->
						val palette = swatchAdapter.palette!!
						copyToClipboard(
							context, "Color Resources", TextUtils.concat(
								res("vibrant", palette.vibrantSwatch),
								res("vibrantLight", palette.lightVibrantSwatch),
								res("vibrantDark", palette.darkVibrantSwatch),
								res("muted", palette.mutedSwatch),
								res("mutedLight", palette.lightMutedSwatch),
								res("mutedDark", palette.darkMutedSwatch)
							)
						)
						val copiedAlert = context.getString(baseR.string.cf_info_copy_toast)
						Toast.makeText(context, copiedAlert, Toast.LENGTH_SHORT).show()
					}
					.show()
				return true
			}

			private fun res(name: String, swatch: Swatch?) =
				if (swatch == null)
					"<!-- ${name} not available -->\n"
				else
					"""
					<!-- ${name} -->
					<color name="${name}">${swatch.rgb.toRGBHexString("#")}</color>
					<color name="${name}_title">${swatch.titleTextColor.toRGBHexString("#")}</color>
					<color name="${name}_body">${swatch.bodyTextColor.toRGBHexString("#")}</color>
					""".trimIndent()
		}
		swatchList.adapter = swatchAdapter

		swatchDisplay = view.findViewById(R.id.swatchSort)
		swatchDisplay.adapter = ArrayAdapter(
			requireContext(),
			android.R.layout.simple_spinner_dropdown_item,
			android.R.id.text1,
			enumValues<Display>().map { it.title }.toTypedArray()
		)
		swatchDisplay.onItemSelectedListener = object : OnItemSelectedListener {
			override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
				updateFilter()
			}

			override fun onNothingSelected(parent: AdapterView<*>) {
				throw IllegalStateException("This should not happen.")
			}
		}

		keyboard.customKeyboardListener = object : KeyboardHandler.CustomKeyboardListener {
			override fun customKeyboardShown() {
				if (isPortrait) {
					swatchList.visibility = View.GONE
				}
			}

			override fun customKeyboardHidden() {
				if (isPortrait) {
					swatchList.visibility = View.VISIBLE
				}
			}
		}
	}

	private fun updateNumColors(numColors: Int, origin: UpdateOrigin?) {
		if (pendingUpdate) {
			return
		}
		if (numColors < 1) {
			throw IllegalArgumentException("numColors must be 1 of greater")
		}

		try {
			pendingUpdate = true
			currentNumColors = numColors

			if (origin != UpdateOrigin.Editor) {
				numColorEditor.setText(numColors.asString())
			}
			if (origin != UpdateOrigin.Slider) {
				numColorSlider.progress = numColors - 1
			}
		} finally {
			pendingUpdate = false
		}
		updateFilter()
	}

	private fun updateResizeDimen(resizeDimen: Int, origin: UpdateOrigin?) {
		if (pendingUpdate) {
			return
		}
		if (resizeDimen < 1) {
			throw IllegalArgumentException("resizeDimen must be 1 of greater")
		}

		try {
			pendingUpdate = true
			currentResizeDimen = resizeDimen

			if (origin != UpdateOrigin.Editor) {
				resizeDimenEditor.setText(resizeDimen.asString())
			}
			if (origin != UpdateOrigin.Slider) {
				resizeDimenSlider.progress = resizeDimen - 1
			}
		} finally {
			pendingUpdate = false
		}
		updateFilter()
	}

	override fun onSaveInstanceState(outState: Bundle) = super.onSaveInstanceState(outState).also {
		outState.apply {
			putInt(PREF_PALETTE_DISPLAY, swatchDisplay.selectedItemPosition)
		}
	}

	override fun onViewStateRestored(savedInstanceState: Bundle?) {
		super.onViewStateRestored(savedInstanceState)
		if (savedInstanceState == null) {
			val prefs = prefs
			val numColors = prefs.getInt(PREF_PALETTE_NUM_COLORS, DEFAULT_NUM_COLORS)
			val resizeDimen = prefs.getInt(PREF_PALETTE_RESIZE_DIMEN, DEFAULT_RESIZE_DIMEN)
			val displayString = prefs.getString(PREF_PALETTE_DISPLAY, Display.DEFAULT.name)!!
			val display = PaletteAdapter.Display.valueOf(displayString)
			setValues(numColors, resizeDimen, display)
		} else {
			swatchDisplay.setSelection(savedInstanceState.getInt(PREF_PALETTE_DISPLAY, Display.DEFAULT.ordinal))
		}
	}

	override fun onStop() {
		super.onStop()
		prefs.edit().apply {
			putInt(PREF_PALETTE_NUM_COLORS, currentNumColors)
			putInt(PREF_PALETTE_RESIZE_DIMEN, currentResizeDimen)
			putString(PREF_PALETTE_DISPLAY, currentSort.name)
		}.apply()
	}

	override fun onDestroyView() {
		keyboard.unregisterEditText(numColorEditor)
		keyboard.unregisterEditText(resizeDimenEditor)
		super.onDestroyView()
	}

	override fun reset() {
		setValues(DEFAULT_NUM_COLORS, DEFAULT_RESIZE_DIMEN, Display.DEFAULT)
	}

	private fun setValues(numColors: Int, resizeDimen: Int, display: PaletteAdapter.Display) {
		swatchDisplay.setSelection(display.ordinal)
		updateNumColors(numColors, null)
		updateResizeDimen(resizeDimen, null)
	}

	override fun generateCode() =
		StringBuilder().apply {
			append("Palette palette = Palette\n")
			append("\t\t.from(bitmap)\n")
			if (currentNumColors != DEFAULT_NUM_COLORS) {
				append("\t\t.maximumColorCount(").append(currentNumColors).append(")\n")
			}
			if (currentResizeDimen != DEFAULT_RESIZE_DIMEN) {
				append("\t\t.resizeBitmapSize(").append(currentResizeDimen).append(")\n")
			}
			append("\t\t.generate()\n")
			append(";")
		}.toString()

	private val currentSort: PaletteAdapter.Display
		get() = PaletteAdapter.Display.values()[swatchDisplay.selectedItemId.toInt()]

	companion object {
		// REPORT report this not being flagged for i18n,
		// it should be because all usages are setText()
		// TODO return String.format(Locale.getDefault(), "%d", number);
		private fun Int.asString(): CharSequence = Integer.toString(this)

		// TODO return NumberFormat.getInstance().parse(s.toString()).intValue();
		private fun String.fromString(): Int = Integer.parseInt(this)
	}

	private enum class UpdateOrigin {
		Editor,
		Slider
	}

	private class PaletteAdapter : BaseAdapter() {
		var palette: Palette? = null
			private set
		private var swatches: List<Swatch?> = emptyList()

		private class ViewHolder(view: View) {
			val colorText: TextView = view.findViewById(R.id.color)
			val titleText: TextView = view.findViewById(R.id.titleText)
			val bodyText: TextView = view.findViewById(R.id.bodyText)
			val population: TextView = view.findViewById(R.id.population)
		}

		override fun getCount() = swatches.size

		override fun getItem(position: Int): Swatch? = swatches[position]

		override fun hasStableIds() = false

		override fun getItemId(position: Int): Long {
			val item = getItem(position)
			return item?.rgb?.toLong() ?: 0
		}

		override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
			@Suppress("NAME_SHADOWING") var convertView = convertView
			val holder: ViewHolder
			if (convertView == null) {
				val inflater = LayoutInflater.from(parent.context)
				convertView = inflater.inflate(R.layout.inc_palette_swatch, parent, false)
				holder = ViewHolder(convertView)
				convertView.tag = holder
			} else {
				holder = convertView.tag as ViewHolder
			}
			bindView(position, holder)
			return convertView!!
		}

		@SuppressLint("SetTextI18n")
		private fun bindView(position: Int, holder: ViewHolder) {
			val swatch = getItem(position)
			if (swatch != null) {
				val hsl = swatch.hsl
				val type = getType(swatch)
				holder.colorText.text = "%s\n%.0f°, %.0f%%, %.0f%%%s".formatRoot(
					swatch.rgb.toRGBHexString("#"),
					hsl[0], hsl[1] * 100, hsl[2] * 100,
					if (type != null) "\n" + type else ""
				)
				holder.titleText.setBackgroundColor(swatch.rgb)
				holder.titleText.setTextColor(swatch.titleTextColor)
				holder.titleText.text = "Title: %s".format(swatch.titleTextColor.toRGBHexString("#"))
				holder.bodyText.setBackgroundColor(swatch.rgb)
				holder.bodyText.setTextColor(swatch.bodyTextColor)
				holder.bodyText.text = "Body: %s".format(swatch.bodyTextColor.toRGBHexString("#"))
				holder.population.text = swatch.population.asString()
			} else {
				holder.colorText.setText(R.string.cf_palette_missing)
				holder.titleText.setBackgroundColor(Color.TRANSPARENT)
				holder.titleText.setTextColor(Color.TRANSPARENT)
				holder.titleText.text = null
				holder.bodyText.setBackgroundColor(Color.TRANSPARENT)
				holder.bodyText.setTextColor(Color.TRANSPARENT)
				holder.bodyText.text = null
				// REPORT why is this not a lint?
				holder.population.text = "N/A"
			}
		}

		private fun getType(swatch: Swatch) = when (swatch) {
			palette!!.vibrantSwatch -> "Vibrant"
			palette!!.mutedSwatch -> "Muted"
			palette!!.lightVibrantSwatch -> "Light Vibrant"
			palette!!.darkVibrantSwatch -> "Dark Vibrant"
			palette!!.lightMutedSwatch -> "Light Muted"
			palette!!.darkMutedSwatch -> "Dark Muted"
			else -> null
		}

		fun update(palette: Palette, display: Display) {
			//Log.d("Adapter", display + ": " + palette);
			this.palette = palette
			this.swatches = display.getSwatches(palette)

			this.notifyDataSetChanged()
		}

		@Suppress("EnumEntryName")
		enum class Display(
			val title: String
		) {

			get___Swatch("get???Swatch()") {
				override fun getSwatches(palette: Palette): List<Swatch?> = listOf(
					palette.vibrantSwatch, palette.lightVibrantSwatch, palette.darkVibrantSwatch,
					palette.mutedSwatch, palette.lightMutedSwatch, palette.darkMutedSwatch
				)
			},
			getSwatches("getSwatches()") {
				override fun getSwatches(palette: Palette): List<Swatch> =
					palette.swatches
			},
			getSwatchesByHSL("getSwatches() by HSL") {
				override fun getSwatches(palette: Palette): List<Swatch> =
					palette.swatches.sortedWith(compareBy({ it.hsl[0] }, { it.hsl[1] }, { it.hsl[2] }))
			},
			getSwatchesByPopulationAsc("getSwatches() by population ASC") {
				override fun getSwatches(palette: Palette): List<Swatch> =
					palette.swatches.sortedBy { it.population }
			},
			getSwatchesByPopulationDesc("getSwatches() by population DESC") {
				override fun getSwatches(palette: Palette): List<Swatch> =
					palette.swatches.sortedByDescending { it.population }
			};

			abstract fun getSwatches(palette: Palette): List<Swatch?>

			companion object {
				val DEFAULT = get___Swatch
			}
		}
	}
}
