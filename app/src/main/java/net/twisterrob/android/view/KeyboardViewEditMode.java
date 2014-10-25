package net.twisterrob.android.view;

import android.content.Context;
import android.util.AttributeSet;

/**
 * It is not possible to do <code>super(isInEditMode()? new ContextWrapper(context) : context);</code>
 * and also the layout editor doesn't support {@code <view tools:class="..." />},
 * so resorting to manually changing the class name in the editor
 */
public class KeyboardViewEditMode extends KeyboardView {
	public KeyboardViewEditMode(Context context, AttributeSet attrs) {
		super(new KeyboardViewEditModeContext(context), attrs);
	}

	public KeyboardViewEditMode(Context context, AttributeSet attrs, int defStyle) {
		super(new KeyboardViewEditModeContext(context), attrs, defStyle);
	}
}
