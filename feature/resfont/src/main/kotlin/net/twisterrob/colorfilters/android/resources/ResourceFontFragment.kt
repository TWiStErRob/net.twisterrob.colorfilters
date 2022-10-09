package net.twisterrob.colorfilters.android.resources

import android.annotation.SuppressLint
import android.graphics.ColorFilter
import android.os.Bundle
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.core.text.HtmlCompat
import androidx.core.text.getSpans
import net.twisterrob.colorfilters.android.ColorFilterFragment
import net.twisterrob.colorfilters.android.keyboard.KeyboardMode
import net.twisterrob.colorfilters.android.resfont.R
import net.twisterrob.colorfilters.android.toARGBHexString

@StringRes
private val TESTS = mapOf(
	R.string.cf_resfont_fg_argb to R.string.cf_resfont_fg_argb_code,
	R.string.cf_resfont_fg_rgb to R.string.cf_resfont_fg_rgb_code,
	R.string.cf_resfont_fg_named to R.string.cf_resfont_fg_named_code,
	R.string.cf_resfont_fg_color to R.string.cf_resfont_fg_color_code,
	R.string.cf_resfont_fg_neg to R.string.cf_resfont_fg_neg_code,
	R.string.cf_resfont_col_argb to R.string.cf_resfont_col_argb_code,
	R.string.cf_resfont_col_rgb to R.string.cf_resfont_col_rgb_code,
	R.string.cf_resfont_col_named to R.string.cf_resfont_col_named_code,
	R.string.cf_resfont_col_color to R.string.cf_resfont_col_color_code,
	R.string.cf_resfont_col_neg to R.string.cf_resfont_col_neg_code,
	R.string.cf_resfont_col_all_named to R.string.cf_resfont_col_all_named_code,
	R.string.cf_resfont_col_all_semi to R.string.cf_resfont_col_all_semi_code,
	R.string.cf_resfont_col_old to R.string.cf_resfont_col_old_code,
)

class ResourceFontFragment : ColorFilterFragment() {

	override val preferredKeyboardMode = KeyboardMode.NATIVE

	override fun displayHelp() {
		displayHelp(R.string.cf_resfont_info_title, R.string.cf_resfont_info)
	}

	override fun generateCode(): CharSequence = ""

	override fun createFilter(): ColorFilter? = null

	override fun reset() {
		// There are no interactions on this screen, cannot reset state.
	}

	override val needsImages = false

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
		inflater.inflate(R.layout.fragment_resfont, container, false)

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		val table: TableLayout = view.findViewById(R.id.table)
		table.removeViews(1, table.childCount - 1) // keep header
		for ((@StringRes test, @StringRes code) in TESTS) {
			createRow(table, test, code)
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
			val resultText = HtmlCompat.fromHtml(
				getString(R.string.cf_resfont_html),
				HtmlCompat.FROM_HTML_MODE_LEGACY
			)
			resultBlack.text = resultText
			resultWhite.text = resultText
			resolved.text = getColor(resultText)
		} catch (@Suppress("TooGenericExceptionCaught") ex: Exception) {
			// Anything can happen, make sure we don't crash, 
			// but rather handle it nicely by showing the error to the user (developer).
			setError(row, ex)
		}

		return row.also { table.addView(row) }
	}

	private fun createRow(
		table: TableLayout,
		@StringRes test: Int,
		@StringRes code: Int,
	): TableRow {
		val inflater = LayoutInflater.from(table.context)
		val row = inflater.inflate(R.layout.inc_resfont_row, table, false) as TableRow
		table.addView(row)

		val source: TextView = row.findViewById(R.id.source)
		val resultBlack: TextView = row.findViewById(R.id.resultBlack)
		val resultWhite: TextView = row.findViewById(R.id.resultWhite)
		val resolved: TextView = row.findViewById(R.id.resolved)

		source.setText(code)
		try {
			val resultText = getText(test)
			resultBlack.text = resultText
			resultWhite.text = resultText
			resolved.text = getColor(resultText)
		} catch (@Suppress("TooGenericExceptionCaught") ex: Exception) {
			// Anything can happen, make sure we don't crash, 
			// but rather handle it nicely by showing the error to the user (developer).
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
		if (text is Spanned) {
			val spans = text.getSpans<ForegroundColorSpan>(0, text.length)
			if (spans.isNotEmpty()) {
				return spans.joinToString(",\n") { span ->
					span.foregroundColor.toARGBHexString()
				}
			}
		}
		return null
	}
}
