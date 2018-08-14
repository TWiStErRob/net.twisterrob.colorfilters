package net.twisterrob.colorfilters.android.matrix

import android.annotation.TargetApi
import android.content.SharedPreferences
import android.graphics.ColorMatrix
import android.os.Build
import android.view.View
import android.widget.EditText
import net.twisterrob.android.view.listeners.TextWatcherAdapter
import net.twisterrob.colorfilters.android.formatRoot
import net.twisterrob.colorfilters.android.keyboard.KeyboardHandler

internal class MatrixComponent(
	view: View,
	private val kbd: KeyboardHandler,
	listener: Component.RefreshListener
) : Component(view, listener) {

	private val editMatrix = ColorMatrix()
	private var automaticRefreshInProgress = false
	private val editors = arrayOf(
		et(R.id.matrix_a), et(R.id.matrix_b), et(R.id.matrix_c), et(R.id.matrix_d), et(R.id.matrix_e),
		et(R.id.matrix_f), et(R.id.matrix_g), et(R.id.matrix_h), et(R.id.matrix_i), et(R.id.matrix_j),
		et(R.id.matrix_k), et(R.id.matrix_l), et(R.id.matrix_m), et(R.id.matrix_n), et(R.id.matrix_o),
		et(R.id.matrix_p), et(R.id.matrix_q), et(R.id.matrix_r), et(R.id.matrix_s), et(R.id.matrix_t)
	)

	override fun setupUI() {
		for (et in editors) {
			kbd.registerEditText(et)
			et.addTextChangedListener(object : TextWatcherAdapter() {
				override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
					if (!automaticRefreshInProgress) {
						try {
							dispatchRefresh(false)
							et.error = null
						} catch (ex: RuntimeException) {
							et.error = ex.message
						}
					}
				}
			})
		}
		setupFocus()
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private fun setupFocus() {
		for (y in 0 until HEIGHT) {
			for (x in 0 until WIDTH) {
				val i = y * WIDTH + x
				editors[i].nextFocusLeftId = getIDAtIndex(y * WIDTH + (x - 1 + WIDTH) % WIDTH)
				editors[i].nextFocusRightId = getIDAtIndex(y * WIDTH + (x + 1 + WIDTH) % WIDTH)
				editors[i].nextFocusUpId = getIDAtIndex((y - 1 + HEIGHT) % HEIGHT * WIDTH + x)
				editors[i].nextFocusDownId = getIDAtIndex((y + 1 + HEIGHT) % HEIGHT * WIDTH + x)
				if (Build.VERSION_CODES.HONEYCOMB <= Build.VERSION.SDK_INT) {
					editors[i].nextFocusForwardId = getIDAtIndex(i + 1)
				}
			}
		}
	}

	private fun getIDAtIndex(index: Int): Int =
		if (0 <= index && index < editors.size)
			editors[index].id
		else
			View.NO_ID

	override fun unWire() {
		for (et in editors) {
			kbd.unregisterEditText(et)
		}
	}

	override fun saveToPreferences(editor: SharedPreferences.Editor) {
		for (y in 0 until HEIGHT) {
			for (x in 0 until WIDTH) {
				editor.putFloat("${PREF_MATRIX_ITEM}${x},${y}", getValue(editors[y * WIDTH + x]))
			}
		}
	}

	override fun restoreFromPreferences(prefs: SharedPreferences) {
		val savedMatrix = ColorMatrix()
		val c = savedMatrix.array
		for (y in 0 until HEIGHT) {
			for (x in 0 until WIDTH) {
				c[y * WIDTH + x] = prefs.getFloat("$PREF_MATRIX_ITEM$x,$y", defaultValue(x, y))
			}
		}
		setMatrix(savedMatrix)
	}

	override fun reset() {
		editMatrix.reset()
		setMatrix(editMatrix)
	}

	override fun refreshModel() {
		val c = editMatrix.array
		for (i in c.indices) {
			c[i] = getValue(editors[i])
		}
	}

	override fun combineInto(colorMatrix: ColorMatrix) {
		colorMatrix.set(editMatrix)
	}

	override fun appendTo(sb: StringBuilder): Boolean {
		sb.append("\nnew ColorMatrix(new float[] {")
		for (i in editors.indices) {
			if (i % 5 == 0) {
				sb.append("\n    ")
			}
			sb.append(getCode(editors[i]))
			sb.append(if (i < editors.size - 1) ", " else "\n")
		}
		sb.append("});")
		return true
	}

	fun setMatrix(colorMatrix: ColorMatrix) {
		try {
			automaticRefreshInProgress = true
			val c = colorMatrix.array
			val e = editMatrix.array
			for (i in c.indices) {
				editors[i].error = null
				setValue(editors[i], c[i])
				e[i] = c[i]
			}
		} finally {
			automaticRefreshInProgress = false
		}
	}

	companion object {
		private const val PREF_MATRIX_ITEM = "Matrix/"
		private const val HEIGHT = 4
		private const val WIDTH = 5

		private fun getDisplay(value: Float): String =
			if (value == 0f /* 0.0, -0.0 */) "0" else "%.3f".formatRoot(value)

		private fun getCode(edit: EditText): String =
			try {
				getDisplay(getValue(edit)) + "f"
			} catch (ex: RuntimeException) {
				"0 /* " + edit.text + ": " + ex.message + " */"
			}

		private fun defaultValue(x: Int, y: Int): Float =
			if (x == y) 1f else 0f

		private fun setValue(ed: EditText, value: Float) {
			ed.setText(getDisplay(value))
		}

		private fun getValue(ed: EditText): Float {
			val text = ed.text.toString()
			return if (text.isEmpty()) 0f else text.toFloat()
		}
	}
}
