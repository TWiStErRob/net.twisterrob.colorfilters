package net.twisterrob.colorfilters.android.matrix;

import java.util.Locale;

import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.graphics.ColorMatrix;
import android.os.Build;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;

import net.twisterrob.android.view.listeners.TextWatcherAdapter;
import net.twisterrob.colorfilters.android.keyboard.KeyboardHandler;

class MatrixComponent extends Component {
	private static final String PREF_MATRIX_ITEM = "Matrix/";
	private static final int HEIGHT = 4;
	private static final int WIDTH = 5;
	private final ColorMatrix editMatrix = new ColorMatrix();
	private final EditText[] editors;
	private final KeyboardHandler kbd;

	private boolean automaticRefreshInProgress = false;

	public MatrixComponent(View view, KeyboardHandler kbd, RefreshListener listener) {
		super(view, listener);
		this.kbd = kbd;
		editors = new EditText[] {
				et(R.id.matrix_a), et(R.id.matrix_b), et(R.id.matrix_c), et(R.id.matrix_d), et(R.id.matrix_e),
				et(R.id.matrix_f), et(R.id.matrix_g), et(R.id.matrix_h), et(R.id.matrix_i), et(R.id.matrix_j),
				et(R.id.matrix_k), et(R.id.matrix_l), et(R.id.matrix_m), et(R.id.matrix_n), et(R.id.matrix_o),
				et(R.id.matrix_p), et(R.id.matrix_q), et(R.id.matrix_r), et(R.id.matrix_s), et(R.id.matrix_t),
		};
	}

	@Override
	public void setupUI() {
		for (final EditText et : editors) {
			kbd.registerEditText(et);
			et.addTextChangedListener(new TextWatcherAdapter() {
				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {
					if (!automaticRefreshInProgress) {
						try {
							dispatchRefresh(false);
							et.setError(null);
						} catch (RuntimeException ex) {
							et.setError(ex.getMessage());
						}
					}
				}
			});
		}
		setupFocus();
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupFocus() {
		for (int y = 0; y < HEIGHT; ++y) {
			for (int x = 0; x < WIDTH; ++x) {
				int i = y * WIDTH + x;
				editors[i].setNextFocusLeftId(getIDAtIndex(y * WIDTH + (x - 1 + WIDTH) % WIDTH));
				editors[i].setNextFocusRightId(getIDAtIndex(y * WIDTH + (x + 1 + WIDTH) % WIDTH));
				editors[i].setNextFocusUpId(getIDAtIndex((y - 1 + HEIGHT) % HEIGHT * WIDTH + x));
				editors[i].setNextFocusDownId(getIDAtIndex((y + 1 + HEIGHT) % HEIGHT * WIDTH + x));
				if (Build.VERSION_CODES.HONEYCOMB <= Build.VERSION.SDK_INT) {
					editors[i].setNextFocusForwardId(getIDAtIndex(i + 1));
				}
			}
		}
	}

	private int getIDAtIndex(int index) {
		return 0 <= index && index < editors.length? editors[index].getId() : View.NO_ID;
	}

	@Override
	public void unWire() {
		for (EditText et : editors) {
			kbd.unregisterEditText(et);
		}
	}

	@Override
	public void saveToPreferences(SharedPreferences.Editor editor) {
		for (int y = 0; y < HEIGHT; ++y) {
			for (int x = 0; x < WIDTH; ++x) {
				editor.putFloat(PREF_MATRIX_ITEM + x + "," + y, getValue(editors[(y * WIDTH + x)]));
			}
		}
	}

	@Override
	public void restoreFromPreferences(SharedPreferences prefs) {
		ColorMatrix savedMatrix = new ColorMatrix();
		float[] c = savedMatrix.getArray();
		for (int y = 0; y < HEIGHT; ++y) {
			for (int x = 0; x < WIDTH; ++x) {
				c[(y * WIDTH + x)] = prefs.getFloat(PREF_MATRIX_ITEM + x + "," + y, defaultValue(x, y));
			}
		}
		setMatrix(savedMatrix);
	}

	@Override
	public void reset() {
		editMatrix.reset();
		setMatrix(editMatrix);
	}

	@Override
	public void refreshModel() {
		float[] c = editMatrix.getArray();
		for (int i = 0; i < c.length; ++i) {
			c[i] = getValue(editors[i]);
		}
	}

	@Override
	public void combineInto(ColorMatrix colorMatrix) {
		colorMatrix.set(editMatrix);
	}

	@Override
	public boolean appendTo(StringBuilder sb) {
		sb.append("\nnew ColorMatrix(new float[] {"); //NON-NLS
		for (int i = 0; i < editors.length; ++i) {
			if (i % 5 == 0) {
				sb.append("\n    ");
			}
			sb.append(getCode(editors[i]));
			sb.append(i < editors.length - 1? ", " : "\n");
		}
		sb.append("});");
		return true;
	}

	public void setMatrix(ColorMatrix colorMatrix) {
		try {
			automaticRefreshInProgress = true;
			float[] c = colorMatrix.getArray();
			float[] e = editMatrix.getArray();
			for (int i = 0; i < c.length; ++i) {
				editors[i].setError(null);
				setValue(editors[i], c[i]);
				e[i] = c[i];
			}
		} finally {
			automaticRefreshInProgress = false;
		}
	}

	private static String getDisplay(float value) {
		if (value == 0) { // 0.0, -0.0
			return "0";
		}
		return String.format(Locale.ROOT, "%.3f", value); //NON-NLS
	}

	private static String getCode(EditText edit) {
		String c;
		try {
			c = getDisplay(getValue(edit)) + "f"; //NON-NLS
		} catch (RuntimeException ex) {
			c = "0 /* " + edit.getText() + ": " + ex.getMessage() + " */"; //NON-NLS
		}
		return c;
	}

	private float defaultValue(int x, int y) {
		return x == y? 1 : 0;
	}

	private static void setValue(EditText ed, float value) {
		ed.setText(getDisplay(value));
	}

	private static float getValue(EditText ed) {
		Editable text = ed.getText();
		if (text.length() == 0) {
			return 0;
		}
		return Float.parseFloat(text.toString());
	}
}
