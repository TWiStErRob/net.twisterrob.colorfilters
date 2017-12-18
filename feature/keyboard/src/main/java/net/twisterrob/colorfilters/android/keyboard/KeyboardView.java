package net.twisterrob.colorfilters.android.keyboard;

import android.content.Context;
import android.graphics.Canvas;
import android.inputmethodservice.Keyboard;
import android.support.annotation.NonNull;
import android.util.AttributeSet;

/**
 * 	<pre><code>
 * 	&lt;net.twisterrob.colorfilters.android.keyboard.KeyboardView
 * 	    android:layout_width="*"
 * 	    android:layout_height="*"
 * 	    android:background="@android:color/transparent"
 * 	    android:keyBackground="@drawable/keyboard_key"
 * 	    /&gt;
 * 	</code></pre>
 * 	Defaults for backgrounds can't be set in code, because the fields have no accessors.
 */
public class KeyboardView extends android.inputmethodservice.KeyboardView {
	public KeyboardView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public KeyboardView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	/**
	 * <p>
	 * {@link Keyboard.Key#gap Key.gap} is handled differently in {@link Keyboard.Key#Key(android.content.res.Resources,
	 * android.inputmethodservice.Keyboard.Row, int, int, android.content.res.XmlResourceParser) Key's constructor}:
	 * {@code gap} is before (to the left of) the key; whereas in {@link Keyboard#resize(int, int) Keyboard.resize}:
	 * {@code gap} is after (to the right of) the key.
	 * {@code resize} is called from {@link android.inputmethodservice.KeyboardView#onSizeChanged(int, int, int, int) onSizeChanged}.
	 * </p>
	 * <p>
	 * This means that we need to "call" resize to be consistent before using the keyboard.
	 * By calling {@code resize} {@link android.R.attr#horizontalGap horizontalGap} always means "after".
	 * </p>
	 * <p>
	 * <em>Note</em>: also it is required to have a wider keyboard than 100% for this to work,
	 * see condition ({@code totalGap + totalWidth > newWidth}} in {@code resize}.
	 * The extra wideness can be achieved by having a perfectly laid out Keyboard
	 * ({@code keyWidth} and {@code horizontalGap} add up to 100%),
	 * but at the same time adding {@code android:horizontalGap="1px"} to {@code <Keyboard>} in the xml resource.
	 * </p>
	 *
	 * @attr ref android.R.styleable#Keyboard_horizontalGap
	 * @see com.android.internal.R.styleable#Keyboard_horizontalGap
	 */
	private boolean keyboardChanged = false;

	@Override
	public void setKeyboard(Keyboard keyboard) {
		super.setKeyboard(keyboard);
		keyboardChanged = true; // force a resize call for this Keyboard
	}

	@Override
	public void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		keyboardChanged = false; // it was called, thank you.
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		if (keyboardChanged) {
			onSizeChanged(right - left, bottom - top, 0, 0);
		}
	}

	@Override
	public void onDraw(@NonNull Canvas canvas) {
		super.onDraw(canvas);
	}
}
