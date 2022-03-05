@file:Suppress("DEPRECATION")

package net.twisterrob.colorfilters.android.keyboard

import android.content.Context
import android.inputmethodservice.Keyboard
import android.util.AttributeSet

/**
 * ```
 * <net.twisterrob.colorfilters.android.keyboard.KeyboardView
 *     android:layout_width="*"
 *     android:layout_height="*"
 *     android:background="@android:color/transparent"
 *     android:keyBackground="@drawable/keyboard_key"
 *     />
 * ```
 * Defaults for backgrounds can't be set in code, because the fields have no accessors.
 */
class KeyboardView : android.inputmethodservice.KeyboardView {

	constructor(context: Context, attrs: AttributeSet) :
		super(context, attrs)

	constructor(context: Context, attrs: AttributeSet, defStyle: Int) :
		super(context, attrs, defStyle)

	/**
	 * [Key.gap][Keyboard.Key.gap] is handled differently in [Key&#39;s constructor][Keyboard.Key.constructor]:
	 * `gap` is before (to the left of) the key; whereas in [Keyboard.resize]:
	 * `gap` is after (to the right of) the key.
	 * `resize` is called from [onSizeChanged][android.inputmethodservice.KeyboardView.onSizeChanged].
	 *
	 * This means that we need to "call" resize to be consistent before using the keyboard.
	 * By calling `resize` [horizontalGap][android.R.attr.horizontalGap] always means "after".
	 *
	 * *Note*: also it is required to have a wider keyboard than 100% for this to work,
	 * see condition (`totalGap + totalWidth > newWidth`} in `resize`.
	 * The extra wideness can be achieved by having a perfectly laid out Keyboard
	 * (`keyWidth` and `horizontalGap` add up to 100%),
	 * but at the same time adding `android:horizontalGap="1px"` to `<Keyboard>` in the xml resource.
	 *
	 * @attr ref android.R.styleable#Keyboard_horizontalGap
	 * @see com.android.internal.R.styleable.Keyboard_horizontalGap
	 */
	@Suppress("KDocUnresolvedReference")
	private var keyboardChanged = false

	override fun setKeyboard(keyboard: Keyboard) {
		super.setKeyboard(keyboard)
		keyboardChanged = true // force a resize call for this Keyboard
	}

	override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
		super.onSizeChanged(w, h, oldw, oldh)
		keyboardChanged = false // it was called, thank you.
	}

	override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
		if (keyboardChanged) {
			onSizeChanged(right - left, bottom - top, 0, 0)
		}
	}
}
