package net.twisterrob.colorfilters.android.matrix;

import java.util.Locale;

import android.content.SharedPreferences;
import android.graphics.ColorMatrix;
import android.view.View;
import android.widget.*;

import net.twisterrob.android.view.listeners.OnSeekBarChangeAdapter;

class ScaleComponent extends Component {
	private static final int NO_SCALE = 1;
	private static final String PREF_SCALE_R = "Scale.scaleR";
	private static final String PREF_SCALE_G = "Scale.scaleG";
	private static final String PREF_SCALE_B = "Scale.scaleB";
	private static final String PREF_SCALE_A = "Scale.scaleA";
	private final ColorMatrix scaleMatrix = new ColorMatrix();
	private final TextView[] scaleRGBALabel;
	private final SeekBar[] scaleRGBA;

	public ScaleComponent(View view, RefreshListener listener) {
		super(view, listener);
		scaleRGBA = new SeekBar[] {sb(R.id.seek_sR), sb(R.id.seek_sG), sb(R.id.seek_sB), sb(R.id.seek_sA)};
		scaleRGBALabel = new TextView[] {tv(R.id.edit_sR), tv(R.id.edit_sG), tv(R.id.edit_sB), tv(R.id.edit_sA)};
	}

	@Override
	public void setupUI() {
		for (int i = 0; i < scaleRGBA.length; ++i) {
			final TextView tv = scaleRGBALabel[i];
			scaleRGBA[i].setOnSeekBarChangeListener(new OnSeekBarChangeAdapter() {
				@Override
				public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
					tv.setText(getDisplay(getValue(seekBar)));
					refreshModel();
					dispatchRefresh(true);
				}
			});
		}
	}

	@Override
	public void saveToPreferences(SharedPreferences.Editor editor) {
		editor.putFloat(PREF_SCALE_R, getValue(COMP_R));
		editor.putFloat(PREF_SCALE_G, getValue(COMP_G));
		editor.putFloat(PREF_SCALE_B, getValue(COMP_B));
		editor.putFloat(PREF_SCALE_A, getValue(COMP_A));
	}

	@Override
	public void restoreFromPreferences(SharedPreferences prefs) {
		setValue(COMP_R, prefs.getFloat(PREF_SCALE_R, NO_SCALE));
		setValue(COMP_G, prefs.getFloat(PREF_SCALE_G, NO_SCALE));
		setValue(COMP_B, prefs.getFloat(PREF_SCALE_B, NO_SCALE));
		setValue(COMP_A, prefs.getFloat(PREF_SCALE_A, NO_SCALE));
	}

	@Override
	public void reset() {
		for (SeekBar scaleBar : scaleRGBA) {
			setValue(scaleBar, NO_SCALE);
		}
	}

	@Override
	public void refreshModel() {
		float rScale = getValue(COMP_R);
		float gScale = getValue(COMP_G);
		float bScale = getValue(COMP_B);
		float aScale = getValue(COMP_A);
		scaleMatrix.setScale(rScale, gScale, bScale, aScale);
	}

	@Override
	public void combineInto(ColorMatrix colorMatrix) {
		colorMatrix.postConcat(scaleMatrix);
	}

	@Override
	public boolean appendTo(StringBuilder sb) {
		float rScale = getValue(COMP_R);
		float gScale = getValue(COMP_G);
		float bScale = getValue(COMP_B);
		float aScale = getValue(COMP_A);
		if (rScale != NO_SCALE || gScale != NO_SCALE || bScale != NO_SCALE || aScale != NO_SCALE) {
			sb.append("\ntemp.setScale(") //NON-NLS
					.append(getDisplay(rScale)).append(", ")
					.append(getDisplay(gScale)).append(", ")
					.append(getDisplay(bScale)).append(", ")
					.append(getDisplay(aScale)).append(");")
			;
			sb.append("\nmatrix.postConcat(temp);"); //NON-NLS
			return true;
		}
		return false;
	}

	private static String getDisplay(float value) {
		return String.format(Locale.ROOT, "%.2f", value); //NON-NLS
	}

	private float getValue(int component) {
		return get(scaleRGBA[component], 3, -1);
	}

	private static float getValue(SeekBar scaleBar) {
		return get(scaleBar, 3, -1);
	}

	private void setValue(int component, float value) {
		set(scaleRGBA[component], 3, -1, value);
	}

	private static void setValue(SeekBar scaleBar, float value) {
		set(scaleBar, 3, -1, value);
	}
}
