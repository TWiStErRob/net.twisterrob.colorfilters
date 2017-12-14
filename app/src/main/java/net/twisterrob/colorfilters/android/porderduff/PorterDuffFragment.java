package net.twisterrob.colorfilters.android.porderduff;

import java.util.*;

import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.graphics.*;
import android.os.*;
import android.support.annotation.NonNull;
import android.view.*;
import android.widget.*;

import net.twisterrob.android.view.*;
import net.twisterrob.android.view.color.ColorPickerView;
import net.twisterrob.android.view.listeners.*;
import net.twisterrob.colorfilters.android.*;
import net.twisterrob.colorfilters.android.keyboard.KeyboardMode;

public class PorterDuffFragment extends ColorFilterFragment {
	private static final String PREF_PORTERDUFF_COLOR = "PorterDuffColorFilter.color";
	private static final String PREF_PORTERDUFF_MODE = "PorterDuffColorFilter.mode";
	private static final String PREF_PORTERDUFF_SWATCH = "PorterDuffColorFilter.colorSwatch";

	private static final Map<Integer, PorterDuff.Mode> MODES = createModes();

	private static final int DEFAULT_COLOR = Color.argb(0xff, 0x00, 0x00, 0x00);
	private static final PorterDuff.Mode DEFAULT_MODE = getDefaultMode();
	private static final int KEEP_SWATCH = 0;

	private ColorPickerView colorView;
	private EditText editor;
	private TextView rgbLabel;
	private SeekBar alphaSlider;
	private View colorPreview;
	private int currentColor;
	private final CheckableButtonManager modes = new CheckableButtonManager();
	private boolean pendingUpdate = false;

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private static PorterDuff.Mode getDefaultMode() {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
			return PorterDuff.Mode.SCREEN;
		} else {
			return PorterDuff.Mode.OVERLAY;
		}
	}

	public static PorterDuffFragment newInstance() {
		return new PorterDuffFragment();
	}

	@Override
	protected void displayHelp() {
		displayHelp(R.string.cf_porterduff_info_title, R.string.cf_porterduff_info);
	}

	@Override
	protected @NonNull ColorFilter createFilter() {
		PorterDuff.Mode mode = MODES.get(modes.getChecked().getId());
		return new PorterDuffColorFilter(currentColor, mode);
	}

	@Override
	protected KeyboardMode getPreferredKeyboardMode() {
		return KeyboardMode.Hex;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_porterduff, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		rgbLabel = (TextView)view.findViewById(R.id.colorRGBLabel);
		colorView = (ColorPickerView)view.findViewById(R.id.color);
		colorView.setColorChangedListener(new ColorPickerView.OnColorChangedListener() {
			@Override
			public void colorChanged(int color) {
				color = Color.argb(Color.alpha(currentColor), Color.red(color), Color.green(color), Color.blue(color));
				updateColor(color, UpdateOrigin.Picker);
			}
		});
		colorPreview = view.findViewById(R.id.colorPreview);
		colorPreview.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				updateColor(DEFAULT_COLOR, null);
			}
		});

		editor = (EditText)view.findViewById(R.id.colorEditor);
		editor.addTextChangedListener(new TextWatcherAdapter() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				try {
					int color = Color.parseColor("#" + s.toString());
					updateColor(color, UpdateOrigin.Editor);
					editor.setError(null);
				} catch (RuntimeException ex) {
					//Log.w(TAG, "Cannot parse color: " + s, ex);
					editor.setError(ex.getMessage() + " " + s);
				}
			}
		});

		alphaSlider = (SeekBar)view.findViewById(R.id.colorAlpha);
		alphaSlider.setOnSeekBarChangeListener(new OnSeekBarChangeAdapter() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				int color = Color.argb(progress,
						Color.red(currentColor), Color.green(currentColor), Color.blue(currentColor));
				updateColor(color, UpdateOrigin.Alpha);
			}
		});
		getKeyboard().registerEditText(editor);
		final View modesContainer = view.findViewById(R.id.modes);
		getKeyboard().setCustomKeyboardListner(new KeyboardHandler.CustomKeyboardListener() {
			@Override
			public void customKeyboardShown() {
				modesContainer.setVisibility(View.GONE);
			}

			@Override
			public void customKeyboardHidden() {
				modesContainer.setVisibility(View.VISIBLE);
			}
		});

		((CompoundButton)view.findViewById(findView(DEFAULT_MODE))).setChecked(true);
		for (int id : MODES.keySet()) {
			modes.addButton((RadioButton)view.findViewById(id));
		}
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
			view.findViewById(R.id.mode_add).setEnabled(false);
			view.findViewById(R.id.mode_overlay).setEnabled(false);
		}
		modes.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					updateFilter();
				}
			}
		});
	}

	public void updateColor(int color, UpdateOrigin origin) {
		if (pendingUpdate) {
			return;
		}
		try {
			pendingUpdate = true;
			currentColor = color;

			if (origin != UpdateOrigin.Editor) {
				editor.setText(colorToARGBHexString("", color));
			}
			if (origin != UpdateOrigin.Alpha) {
				alphaSlider.setProgress(Color.alpha(color));
			}
			if (origin != UpdateOrigin.Picker) {
				colorView.setColor(color);
			}

			rgbLabel.setText(colorToARGBString(color));
			colorPreview.setBackgroundColor(color);
		} finally {
			pendingUpdate = false;
		}
		updateFilter();
	}

	@Override public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt(PREF_PORTERDUFF_SWATCH, colorView.getSwatches().indexOf(colorView.getSwatch()));
	}

	@Override
	public void onViewStateRestored(Bundle savedInstanceState) {
		super.onViewStateRestored(savedInstanceState);
		if (savedInstanceState == null) {
			SharedPreferences prefs = getPrefs();
			int color = prefs.getInt(PREF_PORTERDUFF_COLOR, DEFAULT_COLOR);
			String mode = prefs.getString(PREF_PORTERDUFF_MODE, DEFAULT_MODE.name());
			int swatchIndex = prefs.getInt(PREF_PORTERDUFF_SWATCH, KEEP_SWATCH);
			setValues(color, PorterDuff.Mode.valueOf(mode), swatchIndex);
		} else {
			colorView.setSwatch(savedInstanceState.getInt(PREF_PORTERDUFF_SWATCH, KEEP_SWATCH));
		}
	}

	@Override
	public void onStop() {
		super.onStop();
		getPrefs().edit()
		          .putInt(PREF_PORTERDUFF_COLOR, currentColor)
		          .putString(PREF_PORTERDUFF_MODE, MODES.get(modes.getChecked().getId()).name())
		          .putInt(PREF_PORTERDUFF_SWATCH, colorView.getSwatches().indexOf(colorView.getSwatch()))
		          .apply()
		;
	}

	@Override
	public void onDestroyView() {
		getKeyboard().unregisterEditText(editor);
		super.onDestroyView();
	}

	@Override
	public void reset() {
		setValues(DEFAULT_COLOR, DEFAULT_MODE, KEEP_SWATCH);
	}

	private void setValues(int color, PorterDuff.Mode mode, int swatchIndex) {
		int modeId = findView(mode);
		((CompoundButton)getView().findViewById(modeId)).setChecked(true);
		colorView.setSwatch(swatchIndex);
		updateColor(color, null);
	}

	private int findView(PorterDuff.Mode mode) {
		for (Map.Entry<Integer, PorterDuff.Mode> entry : MODES.entrySet()) {
			if (entry.getValue() == mode) {
				return entry.getKey();
			}
		}
		return View.NO_ID;
	}

	@Override
	protected @NonNull String generateCode() {
		int color = currentColor;
		PorterDuff.Mode mode = MODES.get(modes.getChecked().getId());
		String zeroX = colorToARGBHexString("0x", color) + ", PorterDuff.Mode." + mode.name(); //NON-NLS
		return "new PorterDuffColorFilter(" + zeroX + ");"; //NON-NLS
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private static Map<Integer, PorterDuff.Mode> createModes() {
		Map<Integer, PorterDuff.Mode> modes = new TreeMap<>();
		modes.put(R.id.mode_clear, PorterDuff.Mode.CLEAR);
		modes.put(R.id.mode_src, PorterDuff.Mode.SRC);
		modes.put(R.id.mode_dst, PorterDuff.Mode.DST);
		modes.put(R.id.mode_src_over, PorterDuff.Mode.SRC_OVER);
		modes.put(R.id.mode_dst_over, PorterDuff.Mode.DST_OVER);
		modes.put(R.id.mode_src_in, PorterDuff.Mode.SRC_IN);
		modes.put(R.id.mode_dst_in, PorterDuff.Mode.DST_IN);
		modes.put(R.id.mode_src_out, PorterDuff.Mode.SRC_OUT);
		modes.put(R.id.mode_dst_out, PorterDuff.Mode.DST_OUT);
		modes.put(R.id.mode_src_atop, PorterDuff.Mode.SRC_ATOP);
		modes.put(R.id.mode_dst_atop, PorterDuff.Mode.DST_ATOP);
		modes.put(R.id.mode_xor, PorterDuff.Mode.XOR);
		modes.put(R.id.mode_darken, PorterDuff.Mode.DARKEN);
		modes.put(R.id.mode_lighten, PorterDuff.Mode.LIGHTEN);
		modes.put(R.id.mode_multiply, PorterDuff.Mode.MULTIPLY);
		modes.put(R.id.mode_screen, PorterDuff.Mode.SCREEN);
		if (Build.VERSION_CODES.HONEYCOMB <= Build.VERSION.SDK_INT) {
			modes.put(R.id.mode_add, PorterDuff.Mode.ADD);
			modes.put(R.id.mode_overlay, PorterDuff.Mode.OVERLAY);
		}
		return modes;
	}

	private enum UpdateOrigin {
		Editor,
		Picker,
		Alpha
	}
}
