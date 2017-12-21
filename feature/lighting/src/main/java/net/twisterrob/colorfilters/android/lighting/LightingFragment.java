package net.twisterrob.colorfilters.android.lighting;

import android.content.SharedPreferences;
import android.graphics.*;
import android.os.Bundle;
import android.support.annotation.*;
import android.view.*;
import android.widget.*;

import net.twisterrob.android.view.color.ColorPickerView;
import net.twisterrob.android.view.listeners.TextWatcherAdapter;
import net.twisterrob.colorfilters.android.ColorFilterFragment;
import net.twisterrob.colorfilters.android.keyboard.*;

public class LightingFragment extends ColorFilterFragment {
	private static final String PREF_LIGHTING_MUL = "LightingColorFilter.mul";
	private static final String PREF_LIGHTING_MUL_SWATCH = "LightingColorFilter.mulSwatch";
	private static final String PREF_LIGHTING_ADD = "LightingColorFilter.add";
	private static final String PREF_LIGHTING_ADD_SWATCH = "LightingColorFilter.addSwatch";
	private static final int DEFAULT_MUL = Color.argb(0xff, 0xff, 0xff, 0xff);
	private static final int DEFAULT_ADD = Color.argb(0xff, 0x00, 0x00, 0x00);
	private static final int KEEP_SWATCH = -1;

	private Wiring mulWiring;
	private Wiring addWiring;

	public static LightingFragment newInstance() {
		return new LightingFragment();
	}

	@Override
	protected void displayHelp() {
		displayHelp(R.string.cf_lighting_info_title, R.string.cf_lighting_info);
	}

	@Override
	protected @Nullable ColorFilter createFilter() {
		if (mulColor != null && addColor != null) {
			return new LightingColorFilter(mulColor.getColor(), addColor.getColor());
		} else {
			return null;
		}
	}

	@Override
	protected @NonNull KeyboardMode getPreferredKeyboardMode() {
		return KeyboardMode.Hex;
	}

	private ColorPickerView mulColor;
	private ColorPickerView addColor;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_lighting, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		mulColor = (ColorPickerView)view.findViewById(R.id.mulColor);
		EditText mulColorDesc = (EditText)view.findViewById(R.id.mulEditor);
		TextView mulColorRGB = (TextView)view.findViewById(R.id.mulRGBLabel);
		View mulPreview = view.findViewById(R.id.mulPreview);
		mulWiring = new Wiring(mulColor, mulColorDesc, mulColorRGB, mulPreview, DEFAULT_MUL);

		addColor = (ColorPickerView)view.findViewById(R.id.addColor);
		EditText addColorDesc = (EditText)view.findViewById(R.id.addEditor);
		TextView addColorRGB = (TextView)view.findViewById(R.id.addRGBLabel);
		View addPreview = view.findViewById(R.id.addPreview);
		addWiring = new Wiring(addColor, addColorDesc, addColorRGB, addPreview, DEFAULT_ADD);

		getKeyboard().setCustomKeyboardListener(new KeyboardHandler.CustomKeyboardListener() {
			@Override
			public void customKeyboardShown() {
				if (isPortrait()) {
					mulColor.setVisibility(View.GONE);
					addColor.setVisibility(View.GONE);
				}
			}

			@Override
			public void customKeyboardHidden() {
				if (isPortrait()) {
					mulColor.setVisibility(View.VISIBLE);
					addColor.setVisibility(View.VISIBLE);
				}
			}
		});
	}

	@Override public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt(PREF_LIGHTING_MUL_SWATCH, mulColor.getSwatches().indexOf(mulColor.getSwatch()));
		outState.putInt(PREF_LIGHTING_ADD_SWATCH, addColor.getSwatches().indexOf(addColor.getSwatch()));
	}

	@Override
	public void onViewStateRestored(Bundle savedInstanceState) {
		super.onViewStateRestored(savedInstanceState);
		if (savedInstanceState == null) {
			SharedPreferences prefs = getPrefs();
			int mul = prefs.getInt(PREF_LIGHTING_MUL, DEFAULT_MUL);
			int mulSwatch = prefs.getInt(PREF_LIGHTING_MUL_SWATCH, KEEP_SWATCH);
			int add = prefs.getInt(PREF_LIGHTING_ADD, DEFAULT_ADD);
			int addSwatch = prefs.getInt(PREF_LIGHTING_ADD_SWATCH, KEEP_SWATCH);
			setValues(mul, mulSwatch, add, addSwatch);
		} else {
			mulColor.setSwatch(savedInstanceState.getInt(PREF_LIGHTING_MUL_SWATCH, KEEP_SWATCH));
			addColor.setSwatch(savedInstanceState.getInt(PREF_LIGHTING_ADD_SWATCH, KEEP_SWATCH));
		}
	}

	@Override
	public void onStop() {
		super.onStop();
		getPrefs().edit()
		          .putInt(PREF_LIGHTING_MUL, mulColor.getColor())
		          .putInt(PREF_LIGHTING_MUL_SWATCH, mulColor.getSwatches().indexOf(mulColor.getSwatch()))
		          .putInt(PREF_LIGHTING_ADD, addColor.getColor())
		          .putInt(PREF_LIGHTING_ADD_SWATCH, addColor.getSwatches().indexOf(addColor.getSwatch()))
		          .apply()
		;
	}

	@Override
	public void onDestroyView() {
		getKeyboard().unregisterEditText((EditText)getView().findViewById(R.id.mulEditor));
		getKeyboard().unregisterEditText((EditText)getView().findViewById(R.id.addEditor));
		super.onDestroyView();
	}

	@Override
	public void reset() {
		setValues(DEFAULT_MUL, KEEP_SWATCH, DEFAULT_ADD, KEEP_SWATCH);
	}

	private void setValues(int mul, int mulSwatch, int add, int addSwatch) {
		mulColor.setSwatch(mulSwatch);
		mulWiring.updateColor(mul, null);

		addColor.setSwatch(addSwatch);
		addWiring.updateColor(add, null);
	}

	@Override
	protected @NonNull String generateCode() {
		if (mulColor != null && addColor != null) {
			String mul = colorToRGBHexString("0x", mulColor.getColor()); //NON-NLS
			String add = colorToRGBHexString("0x", addColor.getColor()); //NON-NLS
			return "new LightingColorFilter(" + mul + ", " + add + ");"; //NON-NLS
		} else {
			throw new IllegalStateException("No colors available for code generation");
		}
	}

	private class Wiring
			extends TextWatcherAdapter implements ColorPickerView.OnColorChangedListener, View.OnClickListener {
		private final ColorPickerView colorView;
		private final EditText editor;
		private final TextView rgbLabel;
		private final View preview;
		private final int defaultColor;
		private boolean pendingUpdate;

		Wiring(ColorPickerView colorView, EditText editor, TextView rgbLabel, View preview, int defaultColor) {
			this.colorView = colorView;
			this.editor = editor;
			this.rgbLabel = rgbLabel;
			this.preview = preview;
			this.defaultColor = defaultColor;

			colorView.setContinuousMode(true);
			preview.setOnClickListener(this);
			editor.addTextChangedListener(this);
			colorView.setColorChangedListener(this);
			getKeyboard().registerEditText(editor);
		}

		@Override
		public void /*colorView.*/colorChanged(int color) {
			updateColor(color, UpdateOrigin.Picker);
		}

		@Override
		public void /*descView.*/onTextChanged(CharSequence s, int start, int before, int count) {
			try {
				int color = Color.parseColor("#" + s.toString());
				updateColor(color, UpdateOrigin.Editor);
				editor.setError(null);
			} catch (RuntimeException ex) {
				editor.setError(ex.getMessage());
			}
		}

		@Override
		public void /*preview.*/onClick(View v) {
			updateColor(defaultColor, null);
		}

		public void updateColor(int color, UpdateOrigin origin) {
			if (pendingUpdate) {
				return;
			}
			try {
				pendingUpdate = true;

				if (origin != UpdateOrigin.Editor) {
					editor.setText(colorToRGBHexString("", color));
				}
				if (origin != UpdateOrigin.Picker) {
					colorView.setColor(color);
				}

				rgbLabel.setText(colorToRGBString(color));
				preview.setBackgroundColor(color);
			} finally {
				pendingUpdate = false;
			}
			updateFilter();
		}
	}

	private enum UpdateOrigin {
		Editor,
		Picker
	}
}
