package net.twisterrob.android.view;

import java.util.*;

import android.widget.CompoundButton;

public class CheckableButtonManager implements CompoundButton.OnCheckedChangeListener {
	private final List<CompoundButton> buttons = new LinkedList<>();
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
			if (checkedButton != null) {
				checkedButton.setChecked(false);
			}
		}
	}

	public CompoundButton getChecked() {
		return checkedButton;
	}
}
