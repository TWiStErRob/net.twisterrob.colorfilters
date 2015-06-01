package net.twisterrob.colorfilters.android.palette;

import java.util.*;

import android.content.*;
import android.graphics.*;
import android.os.Bundle;
import android.support.v7.graphics.Palette;
import android.support.v7.graphics.Palette.Swatch;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import android.widget.AdapterView.*;

import net.twisterrob.android.view.listeners.*;
import net.twisterrob.colorfilters.android.*;
import net.twisterrob.colorfilters.android.keyboard.KeyboardMode;

public class PaletteFragment extends ColorFilterFragment {
	private static final String PREF_PALETTE_NUMCOLORS = "Palette.numColors";
	private static final String PREF_PALETTE_RESIZEDIMEN = "Palette.resizeDimen";
	private static final String PREF_PALETTE_DISPLAY = "Palette.display";
	private static final int DEFAULT_NUMCOLORS = 16;
	private static final int DEFAULT_RESIZEDIMEN = 192;
	private static final PaletteAdapter.Display DEFAULT_DISPLAY = PaletteAdapter.Display.get___Swatch;

	private int currentNumColors;
	private int currentResizeDimen;
	private Bitmap lastImage;

	private SeekBar numColorSlider;
	private EditText numColorEditor;
	private TextView numSwatches;
	private SeekBar resizeDimenSlider;
	private EditText resizeDimenEditor;

	private Spinner swatchDisplay;
	private ListView swatchList;
	private PaletteAdapter swatchAdapter = new PaletteAdapter();

	private boolean pendingUpdate;

	public static PaletteFragment newInstance() {
		return new PaletteFragment();
	}

	@Override
	protected void displayHelp() {
		displayHelp(R.string.cf_palette_info_title, R.string.cf_palette_info);
	}

	@Override
	protected ColorFilter createFilter() {
		int pos = swatchList.getCheckedItemPosition();
		//Log.d("Palette", "createFilter, pos = " + pos);
		if (pos < 0) {
			return null;
		}
		int color = (int)swatchList.getAdapter().getItemId(pos);
		color = 0x00FFFFFF & color | 0x60000000; // replace alpha alpha
		return new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_OVER);
	}

	@Override protected void updateFilter() {
		generatePalette();
		super.updateFilter();
	}

	@Override protected void imageChanged() {
		lastImage = getCurrentBitmap();
		if (lastImage != null) {
			boolean wasAtMax = resizeDimenSlider.getMax() == resizeDimenSlider.getProgress();
			resizeDimenSlider.setMax(Math.max(lastImage.getWidth(), lastImage.getHeight()) - 1);
			if (wasAtMax) {
				updateResizeDimen(resizeDimenSlider.getMax() + 1, null);
			}
		}
		resizeDimenSlider.setEnabled(lastImage != null);
		resizeDimenEditor.setEnabled(lastImage != null);
		generatePalette();
		super.updateFilter();
	}

	private void generatePalette() {
		if (lastImage != null) {
			Palette palette = Palette
					.from(lastImage)
					.maximumColorCount(currentNumColors)
					.resizeBitmapSize(currentResizeDimen)
					.generate();
			numSwatches.setText(Integer.toString(palette.getSwatches().size()));
			swatchAdapter.update(palette, getCurrentSort());
		} else {
			numSwatches.setText("?");
			swatchAdapter.notifyDataSetInvalidated();
		}
	}

	@Override
	protected KeyboardMode getPreferredKeyboardMode() {
		return KeyboardMode.NATIVE;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_palette, container, false);
		ListView list = (ListView)view.findViewById(android.R.id.list);
		list.addHeaderView(inflater.inflate(R.layout.inc_palette_header, list, false));
		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		numSwatches = (TextView)view.findViewById(R.id.numPalette);

		numColorSlider = (SeekBar)view.findViewById(R.id.numColors);
		numColorSlider.setOnSeekBarChangeListener(new OnSeekBarChangeAdapter() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				updateNumColors(progress + 1, UpdateOrigin.Slider);
			}
		});
		numColorEditor = (EditText)view.findViewById(R.id.numEditor);
		numColorEditor.addTextChangedListener(new TextWatcherAdapter() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				try {
					int numColors = Integer.parseInt(s.toString());
					updateNumColors(numColors, UpdateOrigin.Editor);
					numColorEditor.setError(null);
				} catch (RuntimeException ex) {
					//Log.w(TAG, "Cannot parse color: " + s, ex);
					numColorEditor.setError(ex.getMessage() + " " + s);
				}
			}
		});

		resizeDimenSlider = (SeekBar)view.findViewById(R.id.resizeDimen);
		resizeDimenSlider.setOnSeekBarChangeListener(new OnSeekBarChangeAdapter() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				updateResizeDimen(progress + 1, UpdateOrigin.Slider);
			}
		});
		resizeDimenEditor = (EditText)view.findViewById(R.id.resizeDimenEditor);
		resizeDimenEditor.addTextChangedListener(new TextWatcherAdapter() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				try {
					int resizeDimen = Integer.parseInt(s.toString());
					updateResizeDimen(resizeDimen, UpdateOrigin.Editor);
					resizeDimenEditor.setError(null);
				} catch (RuntimeException ex) {
					//Log.w(TAG, "Cannot parse color: " + s, ex);
					resizeDimenEditor.setError(ex.getMessage() + " " + s);
				}
			}
		});

		swatchList = (ListView)view.findViewById(android.R.id.list);
		swatchList.setOnItemClickListener(new OnItemClickListener() {
			@Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				swatchList.setItemChecked(position, true);
				PaletteFragment.super.updateFilter();
			}
		});
		swatchList.setAdapter(swatchAdapter);

		swatchDisplay = (Spinner)view.findViewById(R.id.swatchSort);
		PaletteAdapter.Display[] values = PaletteAdapter.Display.values();
		String[] displays = new String[values.length];
		for (int i = 0; i < values.length; i++) {
			displays[i] = values[i].getTitle();
		}
		swatchDisplay.setAdapter(new ArrayAdapter<>(getActivity(),
				android.R.layout.simple_spinner_dropdown_item, android.R.id.text1, displays));
		swatchDisplay.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				updateFilter();
			}
			@Override public void onNothingSelected(AdapterView<?> parent) {
				throw new IllegalStateException("This should happen.");
			}
		});
	}

	public void updateNumColors(int numColors, UpdateOrigin origin) {
		if (pendingUpdate) {
			return;
		}
		if (numColors < 1) {
			throw new IllegalArgumentException("numColors must be 1 of greater");
		}

		try {
			pendingUpdate = true;
			currentNumColors = numColors;

			if (origin != UpdateOrigin.Editor) {
				numColorEditor.setText(Integer.toString(numColors));
			}
			if (origin != UpdateOrigin.Slider) {
				numColorSlider.setProgress(numColors - 1);
			}
		} finally {
			pendingUpdate = false;
		}
		updateFilter();
	}

	public void updateResizeDimen(int resizeDimen, UpdateOrigin origin) {
		if (pendingUpdate) {
			return;
		}
		if (resizeDimen < 1) {
			throw new IllegalArgumentException("resizeDimen must be 1 of greater");
		}

		try {
			pendingUpdate = true;
			currentResizeDimen = resizeDimen;

			if (origin != UpdateOrigin.Editor) {
				resizeDimenEditor.setText(Integer.toString(resizeDimen));
			}
			if (origin != UpdateOrigin.Slider) {
				resizeDimenSlider.setProgress(resizeDimen - 1);
			}
		} finally {
			pendingUpdate = false;
		}
		updateFilter();
	}

	@Override public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt(PREF_PALETTE_DISPLAY, swatchDisplay.getSelectedItemPosition());
	}
	@Override
	public void onViewStateRestored(Bundle savedInstanceState) {
		super.onViewStateRestored(savedInstanceState);
		if (savedInstanceState == null) {
			SharedPreferences prefs = getPrefs();
			int numColors = prefs.getInt(PREF_PALETTE_NUMCOLORS, DEFAULT_NUMCOLORS);
			int resizeDimen = prefs.getInt(PREF_PALETTE_RESIZEDIMEN, DEFAULT_RESIZEDIMEN);
			String displayString = prefs.getString(PREF_PALETTE_DISPLAY, DEFAULT_DISPLAY.name());
			PaletteAdapter.Display display = PaletteAdapter.Display.valueOf(displayString);
			setValues(numColors, resizeDimen, display);
		} else {
			swatchDisplay.setSelection(savedInstanceState.getInt(PREF_PALETTE_DISPLAY, DEFAULT_DISPLAY.ordinal()));
		}
	}

	@Override
	public void onStop() {
		super.onStop();
		getPrefs().edit()
		          .putInt(PREF_PALETTE_NUMCOLORS, currentNumColors)
		          .putInt(PREF_PALETTE_RESIZEDIMEN, currentResizeDimen)
		          .putString(PREF_PALETTE_DISPLAY, getCurrentSort().name())
		          .apply()
		;
	}

	@Override
	public void onDestroyView() {
		getKeyboard().unregisterEditText(numColorEditor);
		super.onDestroyView();
	}

	@Override
	public void reset() {
		setValues(DEFAULT_NUMCOLORS, DEFAULT_RESIZEDIMEN, DEFAULT_DISPLAY);
	}

	private void setValues(int numColors, int resizeDimen, PaletteAdapter.Display display) {
		swatchDisplay.setSelection(display.ordinal());
		updateNumColors(numColors, null);
		updateResizeDimen(resizeDimen, null);
	}

	@Override
	protected String generateCode() {
		StringBuilder sb = new StringBuilder();
		sb.append("Palette palette = Palette\n"); //NON-NLS
		sb.append("\t\t.from(bitmap)\n"); //NON-NLS
		if (currentNumColors != DEFAULT_NUMCOLORS) {
			sb.append("\t\t.maximumColorCount(").append(currentNumColors).append(")\n"); //NON-NLS
		}
		if (currentResizeDimen != DEFAULT_RESIZEDIMEN) {
			sb.append("\t\t.resizeBitmapSize(").append(currentResizeDimen).append(")\n"); //NON-NLS
		}
		sb.append("\t\t.generate()\n");
		sb.append(";"); //NON-NLS
		return sb.toString();
	}
	public PaletteAdapter.Display getCurrentSort() {
		return PaletteAdapter.Display.values()[(int)swatchDisplay.getSelectedItemId()];
	}

	private enum UpdateOrigin {
		Editor,
		Slider
	}

	private static class PaletteAdapter extends BaseAdapter {
		private Palette palette;
		private List<Swatch> swatches = Collections.emptyList();

		private static class ViewHolder {
			private final View view;
			private final ListView list;

			ViewHolder(View view, ListView list) {
				this.view = view;
				this.list = list;
				colorText = (TextView)view.findViewById(R.id.color);
				population = (TextView)view.findViewById(R.id.population);
				titleText = (TextView)view.findViewById(R.id.titleText);
				bodyText = (TextView)view.findViewById(R.id.bodyText);

				colorText.setOnClickListener(new OnClickListener() {
					@Override public void onClick(View v) {
						clickMe();
					}
				});
				colorText.setOnLongClickListener(new OnLongClickListener() {
					@Override public boolean onLongClick(View v) {
						copy(v.getContext(), "Palette color", color);
						return true;
					}
				});
				titleText.setOnClickListener(new OnClickListener() {
					@Override public void onClick(View v) {
						clickMe();
					}
				});
				titleText.setOnLongClickListener(new OnLongClickListener() {
					@Override public boolean onLongClick(View v) {
						copy(v.getContext(), "Palette title color", title);
						return true;
					}
				});
				bodyText.setOnClickListener(new OnClickListener() {
					@Override public void onClick(View v) {
						clickMe();
					}
				});
				bodyText.setOnLongClickListener(new OnLongClickListener() {
					@Override public boolean onLongClick(View v) {
						copy(v.getContext(), "Palette body color", body);
						return true;
					}
				});
			}
			void clickMe() {
				list.performItemClick(view, position, color);
			}

			void copy(Context context, String title, int color) {
				copyToClipboard(context, title, colorToRGBHexString("", color));
				String copiedAlert = context.getString(R.string.cf_info_copy_toast_arg, title);
				Toast.makeText(context, copiedAlert, Toast.LENGTH_SHORT).show();
			}

			int position;
			int color;
			int title;
			int body;
			TextView colorText;
			TextView titleText;
			TextView bodyText;
			TextView population;
		}

		@Override public int getCount() {
			return swatches.size();
		}
		@Override public Swatch getItem(int position) {
			return swatches.get(position);
		}
		@Override public boolean hasStableIds() {
			return false;
		}
		@Override public long getItemId(int position) {
			Swatch item = getItem(position);
			return item != null? item.getRgb() : 0;
		}
		@Override public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				LayoutInflater inflater = LayoutInflater.from(parent.getContext());
				convertView = inflater.inflate(R.layout.inc_palette_swatch, parent, false);
				holder = new ViewHolder(convertView, (ListView)parent);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder)convertView.getTag();
			}
			bindView(position, holder);
			return convertView;
		}

		private void bindView(int position, ViewHolder holder) {
			holder.position = position;
			Swatch swatch = getItem(position);
			if (swatch != null) {
				holder.color = swatch.getRgb();
				holder.title = swatch.getTitleTextColor();
				holder.body = swatch.getBodyTextColor();
				float[] hsl = swatch.getHsl();
				String type = getType(swatch);
				holder.colorText.setText(colorToRGBHexString("#", holder.color)
						+ "\n" + String.format(Locale.ROOT, "%.0f°, %.0f%%, %.0f%%", hsl[0], hsl[1] * 100, hsl[2] * 100)
						+ (type != null? "\n" + type : ""));
				holder.titleText.setBackgroundColor(holder.color);
				holder.titleText.setTextColor(holder.title);
				holder.titleText.setText("Title: " + colorToRGBHexString("#", holder.title));
				holder.bodyText.setBackgroundColor(holder.color);
				holder.bodyText.setTextColor(holder.body);
				holder.bodyText.setText("Body: " + colorToRGBHexString("#", holder.body));
				holder.population.setText(Integer.toString(swatch.getPopulation()));
			} else {
				holder.color = 0;
				holder.title = 0;
				holder.body = 0;
				holder.colorText.setText("missing");
				holder.titleText.setBackgroundColor(Color.TRANSPARENT);
				holder.titleText.setTextColor(Color.TRANSPARENT);
				holder.titleText.setText(null);
				holder.bodyText.setBackgroundColor(Color.TRANSPARENT);
				holder.bodyText.setTextColor(Color.TRANSPARENT);
				holder.bodyText.setText(null);
				holder.population.setText("N/A");
			}
		}

		private String getType(Swatch swatch) {
			if (swatch == palette.getVibrantSwatch()) {
				return "Vibrant";
			} else if (swatch == palette.getMutedSwatch()) {
				return "Muted";
			} else if (swatch == palette.getLightVibrantSwatch()) {
				return "Light Vibrant";
			} else if (swatch == palette.getDarkVibrantSwatch()) {
				return "Dark Vibrant";
			} else if (swatch == palette.getLightMutedSwatch()) {
				return "Light Muted";
			} else if (swatch == palette.getDarkMutedSwatch()) {
				return "Dark Muted";
			} else {
				return null;
			}
		}

		public void update(Palette palette, Display display) {
			//Log.d("Adapter", display + ": " + palette);
			this.palette = palette;
			this.swatches = display.getSwatches(palette);

			this.notifyDataSetChanged();
		}

		enum Display {
			get___Swatch("get???Swatch()") {
				@Override public List<Swatch> getSwatches(Palette palette) {
					return Arrays.asList(
							palette.getVibrantSwatch(), palette.getLightVibrantSwatch(), palette.getDarkVibrantSwatch(),
							palette.getMutedSwatch(), palette.getLightMutedSwatch(), palette.getDarkMutedSwatch()
					);
				}
			},
			getSwatches("getSwatches()") {
				@Override public List<Swatch> getSwatches(Palette palette) {
					return palette.getSwatches();
				}
			},
			getSwatchesByHSL("getSwatches() by HSL") {
				@Override public List<Swatch> getSwatches(Palette palette) {
					ArrayList<Swatch> swatches = new ArrayList<>(palette.getSwatches());
					Collections.sort(swatches, new Comparator<Swatch>() {
						@Override public int compare(Swatch lhs, Swatch rhs) {
							int compHue = Float.compare(lhs.getHsl()[0], rhs.getHsl()[0]);
							if (compHue != 0) {
								return compHue;
							}
							int compSat = Float.compare(lhs.getHsl()[1], rhs.getHsl()[1]);
							if (compSat != 0) {
								return compSat;
							}
							int compVal = Float.compare(lhs.getHsl()[2], rhs.getHsl()[2]);
							if (compVal != 0) {
								return compVal;
							}
							return 0;
						}
					});
					return swatches;
				}
			},
			getSwatchesByPopulationAsc("getSwatches() by population ASC") {
				@Override public List<Swatch> getSwatches(Palette palette) {
					ArrayList<Swatch> swatches = new ArrayList<>(palette.getSwatches());
					Collections.sort(swatches, new Comparator<Swatch>() {
						@Override public int compare(Swatch lhs, Swatch rhs) {
							return (int)((long)lhs.getPopulation() - (long)rhs.getPopulation());
						}
					});
					return swatches;
				}
			},
			getSwatchesByPopulationDesc("getSwatches() by population DESC") {
				@Override public List<Swatch> getSwatches(Palette palette) {
					ArrayList<Swatch> swatches = new ArrayList<>(palette.getSwatches());
					Collections.sort(swatches, new Comparator<Swatch>() {
						@Override public int compare(Swatch lhs, Swatch rhs) {
							return (int)((long)rhs.getPopulation() - (long)lhs.getPopulation());
						}
					});
					return swatches;
				}
			};

			private final String title;
			Display(String title) {
				this.title = title;
			}
			public String getTitle() {
				return title;
			}

			public abstract List<Swatch> getSwatches(Palette palette);
		}
	}
}
