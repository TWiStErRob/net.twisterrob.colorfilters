package net.twisterrob.colorfilters.android.resources

import android.annotation.SuppressLint
import android.graphics.ColorFilter
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.support.annotation.StringRes
import android.text.Html
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import net.twisterrob.colorfilters.android.ColorFilterFragment
import net.twisterrob.colorfilters.android.keyboard.KeyboardMode
import net.twisterrob.colorfilters.android.resfont.R
import net.twisterrob.colorfilters.android.toARGBHexString

@StringRes private val TESTS = intArrayOf(
	R.string.cf_resfont_fg_argb,
	R.string.cf_resfont_fg_rgb,
	R.string.cf_resfont_fg_named,
	R.string.cf_resfont_fg_color,
	R.string.cf_resfont_fg_neg,
	R.string.cf_resfont_col_argb,
	R.string.cf_resfont_col_rgb,
	R.string.cf_resfont_col_named,
	R.string.cf_resfont_col_color,
	R.string.cf_resfont_col_neg,
	R.string.cf_resfont_col_all_named,
	R.string.cf_resfont_col_all_semi,
	R.string.cf_resfont_col_old
)

class ResourceFontFragment : ColorFilterFragment() {

	override val preferredKeyboardMode = KeyboardMode.NATIVE

	override fun displayHelp() {
		displayHelp(R.string.cf_resfont_info_title, R.string.cf_resfont_info)
	}

	override fun generateCode(): CharSequence = ""

	override fun createFilter(): ColorFilter? = null

	override fun reset() {}

	override val needsImages = false

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
		inflater.inflate(R.layout.fragment_resfont, container, false)

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		val table: TableLayout = view.findViewById(R.id.table)
		table.removeViews(1, table.childCount - 1) // keep header
		for (@StringRes test in TESTS) {
			createRow(table, test)
		}
		addHtmlRow(table)
	}

	private fun addHtmlRow(table: TableLayout): TableRow {
		val inflater = LayoutInflater.from(table.context)!!
		val row = inflater.inflate(R.layout.inc_resfont_row, table, false) as TableRow

		val source: TextView = row.findViewById(R.id.source)
		val resultBlack: TextView = row.findViewById(R.id.resultBlack)
		val resultWhite: TextView = row.findViewById(R.id.resultWhite)
		val resolved: TextView = row.findViewById(R.id.resolved)

		source.text = getString(R.string.cf_resfont_html_code)
		try {
			val resultText = fromHtml(R.string.cf_resfont_html)
			resultBlack.text = resultText
			resultWhite.text = resultText
			resolved.text = getColor(resultText)
		} catch (ex: Exception) {
			setError(row, ex)
		}

		return row.also { table.addView(row) }
	}

	@Suppress("deprecation")
	private fun fromHtml(@StringRes htmlRes: Int): CharSequence {
		return if (VERSION.SDK_INT < VERSION_CODES.N) {
			Html.fromHtml(getString(htmlRes))
		} else {
			Html.fromHtml(getString(htmlRes), Html.FROM_HTML_MODE_LEGACY)
		}
	}

	private fun createRow(table: TableLayout, @StringRes test: Int): TableRow {
		val inflater = LayoutInflater.from(table.context)
		val row = inflater.inflate(R.layout.inc_resfont_row, table, false) as TableRow
		table.addView(row)

		val source: TextView = row.findViewById(R.id.source)
		val resultBlack: TextView = row.findViewById(R.id.resultBlack)
		val resultWhite: TextView = row.findViewById(R.id.resultWhite)
		val resolved: TextView = row.findViewById(R.id.resolved)

		val codeRes = resources.getResourceEntryName(test) + "_code"
		source.setText(resources.getIdentifier(codeRes, "string", requireContext().packageName))
		try {
			val resultText = getText(test)
			resultBlack.text = resultText
			resultWhite.text = resultText
			resolved.text = getColor(resultText)
		} catch (ex: Exception) {
			setError(row, ex)
		}

		return row
	}

	private fun setError(row: TableRow, ex: Exception) {
		val resolved: TextView = row.findViewById(R.id.resolved)
		val params = resolved.layoutParams as TableRow.LayoutParams
		val resolvedAt = row.indexOfChild(resolved) + 1
		val removeCount = row.childCount - resolvedAt
		row.removeViews(resolvedAt, removeCount)
		params.span += removeCount // two rendered + two divider
		@SuppressLint("SetTextI18n")
		resolved.text = "${ex.javaClass.simpleName}\n${ex.message}"
	}

	private fun getColor(text: CharSequence): String? {
		val color = StringBuilder()
		if (text is Spanned) {
			val spans = text.getSpans(0, text.length, ForegroundColorSpan::class.java)
			if (spans != null) {
				for (span in spans) { // TODO joinToString
					if (color.isNotEmpty()) {
						color.append(",\n")
					}
					color.append(span.foregroundColor.toARGBHexString())
				}
			}
		}
		return if (color.isNotEmpty()) color.toString() else null
	}
}
