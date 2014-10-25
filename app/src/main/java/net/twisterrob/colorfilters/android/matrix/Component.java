package net.twisterrob.colorfilters.android.matrix;

import android.content.SharedPreferences;
import android.graphics.ColorMatrix;
import android.view.*;
import android.widget.*;

// TODO refactor to subfragments and/or custom View/ViewGroups
abstract class Component {
	interface RefreshListener {
		void refresh(boolean recombine);
	}

	protected static final int COMP_R = 0;
	protected static final int COMP_G = 1;
	protected static final int COMP_B = 2;
	protected static final int COMP_A = 3;

	protected final View view;
	private final RefreshListener listener;

	public Component(View view, RefreshListener listener) {
		this.view = view;
		this.listener = listener;
	}

	public abstract void setupUI();

	public void unWire() {
	}

	protected EditText et(int viewID) {
		return (EditText)view.findViewById(viewID);
	}

	protected TextView tv(int viewID) {
		return (TextView)view.findViewById(viewID);
	}

	protected View v(int viewID) {
		return view.findViewById(viewID);
	}

	protected ViewGroup vg(int viewID) {
		return (ViewGroup)view.findViewById(viewID);
	}

	protected SeekBar sb(int viewID) {
		return (SeekBar)view.findViewById(viewID);
	}

	public abstract void reset();

	public abstract void refreshModel();

	public abstract void combineInto(ColorMatrix colorMatrix);

	public abstract boolean appendTo(StringBuilder sb);

	protected static float get(SeekBar seekBar, float scale, float offset) {
		return (float)seekBar.getProgress() / (float)seekBar.getMax() * scale + offset;
	}

	protected static void set(SeekBar seekBar, float scale, float offset, float value) {
		seekBar.setProgress((int)((value - offset) / scale * seekBar.getMax()));
	}

	protected void dispatchRefresh(boolean recombine) {
		listener.refresh(recombine);
	}

	public abstract void saveToPreferences(SharedPreferences.Editor editor);

	public abstract void restoreFromPreferences(SharedPreferences prefs);
}
