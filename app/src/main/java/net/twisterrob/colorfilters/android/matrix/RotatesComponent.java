package net.twisterrob.colorfilters.android.matrix;

import android.content.SharedPreferences;
import android.graphics.ColorMatrix;
import android.view.View;

class RotatesComponent extends Component {
    private final RotateComponent[] rotates;

    public RotatesComponent(View view, RefreshListener listener) {
        super(view, listener);
        rotates = new RotateComponent[]{
                new RotateComponent(view, listener, COMP_R),
                new RotateComponent(view, listener, COMP_G),
                new RotateComponent(view, listener, COMP_B)
        };
    }

    @Override
    public void setupUI() {
        for (Component rotate : rotates) {
            rotate.setupUI();
        }
    }


    @Override
    public void saveToPreferences(SharedPreferences.Editor editor) {
        for (Component rotate : rotates) {
            rotate.saveToPreferences(editor);
        }
    }

    @Override
    public void restoreFromPreferences(SharedPreferences prefs) {
        for (Component rotate : rotates) {
            rotate.restoreFromPreferences(prefs);
        }
    }

    @Override
    public void reset() {
        for (Component rotate : rotates) {
            rotate.reset();
        }
    }

    @Override
    public void refreshModel() {
        for (Component rotate : rotates) {
            rotate.refreshModel();
        }
    }

    @Override
    public void combineInto(ColorMatrix colorMatrix) {
        for (Component rotate : rotates) {
            rotate.combineInto(colorMatrix);
        }
    }

    @Override
    public boolean appendTo(StringBuilder sb) {
        boolean written = false;
        for (Component rotate : rotates) {
            written |= rotate.appendTo(sb);
        }
        return written;
    }

    public Component get(int compRGB) {
        return rotates[compRGB];
    }
}
