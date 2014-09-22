package net.twisterrob.colorfilters.android.matrix;

import android.content.SharedPreferences;
import android.graphics.ColorMatrix;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import net.twisterrob.android.view.listeners.OnSeekBarChangeAdapter;
import net.twisterrob.colorfilters.android.R;

import java.util.Locale;

class RotateComponent extends Component {
    private static final String PREF_ROTATE_ROT = "Rotate.rot/";
    private static final int NO_ROT = 0;
    private static final int FULL_ROT = 360;
    private static final int[] seekIDs = {R.id.seek_rR, R.id.seek_rG, R.id.seek_rB};
    private static final int[] editIDs = {R.id.edit_rR, R.id.edit_rG, R.id.edit_rB};

    private final ColorMatrix rotateMatrix = new ColorMatrix();
    private final TextView valueView;
    private final SeekBar slider;
    private final int compRGB;

    public RotateComponent(View view, RefreshListener listener, int compRGB) {
        super(view, listener);
        this.slider = sb(seekIDs[compRGB]);
        this.valueView = tv(editIDs[compRGB]);
        this.compRGB = compRGB;

    }

    @Override
    public void setupUI() {
        slider.setOnSeekBarChangeListener(new OnSeekBarChangeAdapter() {
            @Override
            public void onProgressChanged(SeekBar slider, int progress, boolean fromUser) {
                valueView.setText(getDisplay(getValue()));
                refreshModel();
                dispatchRefresh(true);
            }
        });
    }

    @Override
    public void saveToPreferences(SharedPreferences.Editor editor) {
        editor.putInt(PREF_ROTATE_ROT + compRGB, getValue());
    }

    @Override
    public void restoreFromPreferences(SharedPreferences prefs) {
        setValue(FULL_ROT); // force onProgressChanged
        setValue(prefs.getInt(PREF_ROTATE_ROT + compRGB, NO_ROT));
    }

    @Override
    public void reset() {
        setValue(FULL_ROT); // force onProgressChanged
        setValue(NO_ROT);
    }

    @Override
    public void refreshModel() {
        int rot = getValue();
        rotateMatrix.setRotate(compRGB, rot);
    }

    @Override
    public void combineInto(ColorMatrix colorMatrix) {
        colorMatrix.postConcat(rotateMatrix);
    }

    @Override
    public boolean appendTo(StringBuilder sb) {
        int value = getValue();
        if (NO_ROT != value && FULL_ROT != value) {
            String rot = getDisplay(value).trim();
            sb.append("\ntemp.setRotate(").append(compRGB).append(", ").append(rot).append(");"); //NON-NLS
            sb.append("\nmatrix.postConcat(temp);"); //NON-NLS
            return true;
        }
        return false;
    }

    private static String getDisplay(int value) {
        return String.format(Locale.getDefault(), "% 3d", value); //NON-NLS
    }

    private int getValue() {
        return Math.round(get(slider, FULL_ROT, 0));
    }

    private void setValue(int value) {
        set(slider, FULL_ROT, 0, value);
    }
}
