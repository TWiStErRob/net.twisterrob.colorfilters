package net.twisterrob.android.view;

import android.widget.CompoundButton;

import java.util.LinkedList;
import java.util.List;

public class CheckableButtonManager implements CompoundButton.OnCheckedChangeListener {
    private final List<CompoundButton> buttons = new LinkedList<CompoundButton>();
    private CompoundButton checkedButton = null;
    private static final boolean safeMode = false;
    private CompoundButton.OnCheckedChangeListener listener;

    public void addButton(CompoundButton rb) {
        if (rb.isChecked()) {
            if (checkedButton == null) {
                checkedButton = rb;
            } else {
                rb.setChecked(false);
            }
        }
        rb.setOnCheckedChangeListener(this);
        buttons.add(rb);
    }

    public void onCheckedChanged(CompoundButton newlyCheckedButton, boolean isChecked) {
        if (isChecked) {
            disable(newlyCheckedButton);
            checkedButton = newlyCheckedButton;
            fireCheckedChange();
        }
    }

    public void setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener listener) {
        this.listener = listener;
    }

    private void fireCheckedChange() {
        if (listener != null) {
            listener.onCheckedChanged(checkedButton, true);
        }
    }

    private void disable(CompoundButton newlyCheckedButton) {
        if (safeMode) {
            for (CompoundButton cb : buttons) {
                if (!newlyCheckedButton.equals(cb)) {
                    cb.setChecked(false);
                }
            }
        } else {
            checkedButton.setChecked(false);
        }
    }

    public CompoundButton getChecked() {
        return checkedButton;
    }
}
