package net.twisterrob.colorfilters.android.matrix;

import java.util.Locale;

import android.content.SharedPreferences;
import android.graphics.ColorMatrix;
import android.view.View;
import android.widget.*;

import net.twisterrob.android.view.listeners.OnSeekBarChangeAdapter;
import net.twisterrob.colorfilters.android.R;

class SaturationComponent extends Component {
	private static final float NO_SCALE = 1;
	private static final String PREF_SATURATION_SAT = "Saturation.sat";
	private final ColorMatrix satMatrix = new ColorMatrix();
	private final TextView saturationLabel;
	private final SeekBar saturation;

	public SaturationComponent(View view, RefreshListener listener) {
		super(view, listener);
		saturation = sb(R.id.seek_sat);
		saturationLabel = tv(R.id.edit_sat);
	}

	@Override
	public void setupUI() {
		saturation.setOnSeekBarChangeListener(new OnSeekBarChangeAdapter() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				saturationLabel.setText(getDisplay(getValue()));
				refreshModel();
				dispatchRefresh(true);
			}
		});
	}

	@Override
	public void saveToPreferences(SharedPreferences.Editor editor) {
		editor.putFloat(PREF_SATURATION_SAT, getValue());
	}

	@Override
	public void restoreFromPreferences(SharedPreferences prefs) {
		setValue(prefs.getFloat(PREF_SATURATION_SAT, NO_SCALE));
	}

	@Override
	public void reset() {
		setValue(NO_SCALE);
	}

	@Override
	public void refreshModel() {
		float sat = getValue();
		satMatrix.setSaturation(sat);
	}

	@Override
	public void combineInto(ColorMatrix colorMatrix) {
		colorMatrix.postConcat(satMatrix);
	}

	@Override
	public boolean appendTo(StringBuilder sb) {
		float value = getValue();
		if (value != NO_SCALE) {
			String sat = getDisplay(value);
			sb.append("\ntemp.setSaturation(").append(sat).append(");"); //NON-NLS
			sb.append("\nmatrix.postConcat(temp);"); //NON-NLS
			return true;
		}
		return false;
	}

	private static String getDisplay(float value) {
		return String.format(Locale.getDefault(), "%.2f", value); //NON-NLS
	}

	private float getValue() {
		return get(saturation, 1, 0);
	}

	private void setValue(float value) {
		set(saturation, 1, 0, value);
	}
}
