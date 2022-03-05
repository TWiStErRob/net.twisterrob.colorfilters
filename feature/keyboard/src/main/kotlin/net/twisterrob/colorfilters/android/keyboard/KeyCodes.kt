package net.twisterrob.colorfilters.android.keyboard

/**
 * @see `res/xml/keyboard_*.xml`
 */
object KeyCodes {

	const val KEY_MOVE_LEFT: Int = '\u2190'.code // ←
	const val KEY_MOVE_RIGHT: Int = '\u2192'.code // →
	const val KEY_MOVE_START: Int = '\u21E4'.code // ⇤
	const val KEY_MOVE_END: Int = '\u21E5'.code // ⇥

	const val KEY_DONE: Int = '\u0017'.code //  (ETB / End of Transmission Block)
	const val KEY_CLEAR: Int = '\u0018'.code //  (CAN / Cancel)
	const val KEY_BACKSPACE: Int = '\u0008'.code //  (BS / Backspace)

	const val KEY_DECIMAL: Int = '\u2396'.code // ⎖
	const val KEY_CHANGE_SIGN: Int = '\u00B1'.code // ±
	const val KEY_NEGATE: Int = '\u00AC'.code // ¬

	const val KEY_NAV_W: Int = '\u21D0'.code // ⇐
	const val KEY_NAV_N: Int = '\u21D1'.code // ⇑
	const val KEY_NAV_E: Int = '\u21D2'.code // ⇒
	const val KEY_NAV_S: Int = '\u21D3'.code // ⇓
	const val KEY_NAV_NW: Int = '\u21D6'.code // ⇖
	const val KEY_NAV_NE: Int = '\u21D7'.code // ⇗
	const val KEY_NAV_SE: Int = '\u21D8'.code // ⇘
	const val KEY_NAV_SW: Int = '\u21D9'.code // ⇙
}
