package net.twisterrob.colorfilters.android.matrix;

import android.content.SharedPreferences;
import android.graphics.*;
import android.os.Bundle;
import android.view.*;

import net.twisterrob.android.view.KeyboardHandler;
import net.twisterrob.colorfilters.android.*;
import net.twisterrob.colorfilters.android.keyboard.KeyboardMode;

import static net.twisterrob.colorfilters.android.matrix.Component.*;

public class MatrixFragment extends ColorFilterFragment {
	private static final String SAVE_ORDER_MAP = OrderComponent.class.getSimpleName();
	private static final String SAVE_EDITOR_DIRTY = MatrixComponent.class.getSimpleName();

	private final ColorMatrix colorMatrix = new ColorMatrix();
	private Component saturation;
	private RotatesComponent rotates;
	private Component scale;
	private OrderComponent order;
	private Component[] components;

	/**
	 * If the matrix has been tampered with, it's better to generate the float[] constructor.
	 */
	private boolean dirty;
	private MatrixComponent editor;

	public static MatrixFragment newInstance() {
		return new MatrixFragment();
	}

	@Override
	protected void displayHelp() {
		displayHelp(R.string.cf_matrix_info_title, R.string.cf_matrix_info);
	}

	@Override
	protected ColorFilter createFilter() {
		return new ColorMatrixColorFilter(colorMatrix);
	}

	@Override
	protected KeyboardMode getPreferredKeyboardMode() {
		return KeyboardMode.FloatNav;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_matrix, container, false);
	}

	private final CentralRefreshListener listener = new CentralRefreshListener();

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		listener.disable();
		view.findViewById(R.id.matrix_reset).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				editor.reset();
			}
		});
		order = new OrderComponent(view, listener);
		saturation = new SaturationComponent(view, listener);
		rotates = new RotatesComponent(view, listener);
		scale = new ScaleComponent(view, listener);
		editor = new MatrixComponent(view, getKeyboard(), listener);
		components = new Component[] {order, saturation, rotates, scale, editor};
		setupUI();

		final View controlsGroup = view.findViewById(R.id.controls);
		final View orderGroup = view.findViewById(R.id.order);
		getKeyboard().setCustomKeyboardListner(new KeyboardHandler.CustomKeyboardListener() {
			@Override
			public void customKeyboardShown() {
				if (isPortrait()) {
					controlsGroup.setVisibility(View.GONE);
				}
				orderGroup.setVisibility(View.GONE);
			}

			@Override
			public void customKeyboardHidden() {
				if (isPortrait()) {
					controlsGroup.setVisibility(View.VISIBLE);
				}
				orderGroup.setVisibility(View.VISIBLE);
			}
		});
	}

	@Override
	public void onViewStateRestored(Bundle savedInstanceState) {
		super.onViewStateRestored(savedInstanceState);
		boolean combine;
		if (savedInstanceState == null) {
			restoreFromPreferences(getPrefs());
			combine = false;
		} else {
			dirty = savedInstanceState.getBoolean(SAVE_EDITOR_DIRTY);
			order.setMap(savedInstanceState.getIntArray(SAVE_ORDER_MAP));
			combine = !dirty;
		}
		listener.enable();
		listener.refresh(combine);
	}

	private void setupUI() {
		for (Component component : components) {
			component.setupUI();
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putBoolean(SAVE_EDITOR_DIRTY, dirty);
		outState.putIntArray(SAVE_ORDER_MAP, order.getMap());
	}

	@Override
	public void onStop() {
		super.onStop();
		saveToPreferences(getPrefs());
	}

	@Override
	public void onDestroyView() {
		editor.unWire();
		super.onDestroyView();
	}

	@Override
	public void reset() {
		colorMatrix.reset();
		for (Component component : components) {
			component.reset();
		}
		listener.refresh(true);
	}

	private void saveToPreferences(SharedPreferences prefs) {
		SharedPreferences.Editor editor = prefs.edit();
		for (Component component : components) {
			component.saveToPreferences(editor);
		}
		editor.apply();
	}

	private void restoreFromPreferences(SharedPreferences prefs) {
		colorMatrix.reset();
		for (Component component : components) {
			component.restoreFromPreferences(prefs);
		}
	}

	private void combineMatrices() {
		colorMatrix.reset();

		Component[] components = getOrdered();
		for (Component component : components) {
			component.combineInto(colorMatrix);
		}

		editor.setMatrix(colorMatrix);
	}

	private Component[] getOrdered() {
		return order.order(rotates.get(COMP_R), rotates.get(COMP_G), rotates.get(COMP_B), scale, saturation);
	}

	@Override
	protected String generateCode() {
		StringBuilder colorCode = new StringBuilder();
		if (dirty) {
			editor.appendTo(colorCode);
		} else {
			colorCode.append("ColorMatrix matrix = new ColorMatrix();\n"); //NON-NLS
			colorCode.append("ColorMatrix temp = new ColorMatrix();\n"); //NON-NLS
			Component[] components = getOrdered();
			for (Component component : components) {
				if (component.appendTo(colorCode)) {
					colorCode.append('\n');
				}
			}
			colorCode.append("\nreturn matrix;"); //NON-NLS
		}
		return colorCode.toString();
	}

	private class CentralRefreshListener implements RefreshListener {
		private boolean enabled = true;

		@Override
		public void refresh(boolean recombine) {
			if (!enabled) {
				return;
			}
			dirty = !recombine;
			if (recombine) {
				combineMatrices();
			}
			editor.refreshModel();
			editor.combineInto(colorMatrix);
			updateFilter();
		}

		public void disable() {
			enabled = false;
		}

		public void enable() {
			enabled = true;
		}
	}
}
