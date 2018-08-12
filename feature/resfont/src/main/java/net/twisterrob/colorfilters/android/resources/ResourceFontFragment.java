package net.twisterrob.colorfilters.android.resources;

import android.graphics.ColorFilter;
import android.os.Build.*;
import android.os.Bundle;
import android.support.annotation.*;
import android.text.*;
import android.text.style.ForegroundColorSpan;
import android.view.*;
import android.widget.*;

import net.twisterrob.colorfilters.android.ColorFilterFragment;
import net.twisterrob.colorfilters.android.keyboard.KeyboardMode;
import net.twisterrob.colorfilters.android.resfont.R;

public class ResourceFontFragment extends ColorFilterFragment {

	private static final @StringRes int[] TESTS = new int[] {
			R.string.cf_resfont_fg_argb,
			R.string.cf_resfont_fg_rgb,
			R.string.cf_resfont_fg_named,
			R.string.cf_resfont_fg_color,
			R.string.cf_resfont_fg_neg,
			R.string.cf_resfont_col_argb,
			R.string.cf_resfont_col_rgb,
			R.string.cf_resfont_col_named,
			R.string.cf_resfont_col_color,
			R.string.cf_resfont_col_neg,
			R.string.cf_resfont_col_all_named,
			R.string.cf_resfont_col_all_semi,
			R.string.cf_resfont_col_old,
	};

	@Override public @NonNull KeyboardMode getPreferredKeyboardMode() {
		return KeyboardMode.NATIVE;
	}
	@Override protected void displayHelp() {
		displayHelp(R.string.cf_resfont_info_title, R.string.cf_resfont_info);
	}
	@Override protected @NonNull CharSequence generateCode() {
		return "";
	}
	@Override protected @Nullable ColorFilter createFilter() {
		return null;
	}
	@Override public void reset() {

	}
	@Override public boolean needsImages() {
		return false;
	}

	@Override
	public @Nullable View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
			@Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_resfont, container, false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		TableLayout table = view.findViewById(R.id.table);
		table.removeViews(1, table.getChildCount() - 1); // keep header
		for (@StringRes int test : TESTS) {
			createRow(table, test);
		}
		addHtmlRow(table);
	}

	private TableRow addHtmlRow(TableLayout table) {
		LayoutInflater inflater = LayoutInflater.from(table.getContext());
		TableRow row = (TableRow)inflater.inflate(R.layout.inc_resfont_row, table, false);
		table.addView(row);

		TextView source = row.findViewById(R.id.source);
		TextView resultBlack = row.findViewById(R.id.resultBlack);
		TextView resultWhite = row.findViewById(R.id.resultWhite);
		TextView resolved = row.findViewById(R.id.resolved);

		source.setText(getString(R.string.cf_resfont_html_code));
		try {
			CharSequence resultText = fromHtml(R.string.cf_resfont_html);
			resultBlack.setText(resultText);
			resultWhite.setText(resultText);
			resolved.setText(getColor(resultText));
		} catch (Exception ex) {
			setError(row, ex);
		}
		return row;
	}

	@SuppressWarnings("deprecation")
	private CharSequence fromHtml(@StringRes int htmlRes) {
		if (VERSION.SDK_INT < VERSION_CODES.N) {
			return Html.fromHtml(getString(htmlRes));
		} else {
			return Html.fromHtml(getString(htmlRes), Html.FROM_HTML_MODE_LEGACY);
		}
	}

	private TableRow createRow(TableLayout table, @StringRes int test) {
		LayoutInflater inflater = LayoutInflater.from(table.getContext());
		TableRow row = (TableRow)inflater.inflate(R.layout.inc_resfont_row, table, false);
		table.addView(row);

		TextView source = row.findViewById(R.id.source);
		TextView resultBlack = row.findViewById(R.id.resultBlack);
		TextView resultWhite = row.findViewById(R.id.resultWhite);
		TextView resolved = row.findViewById(R.id.resolved);

		String codeRes = getResources().getResourceEntryName(test) + "_code";
		source.setText(getResources().getIdentifier(codeRes, "string", requireContext().getPackageName()));
		try {
			CharSequence resultText = getText(test);
			resultBlack.setText(resultText);
			resultWhite.setText(resultText);
			resolved.setText(getColor(resultText));
		} catch (Exception ex) {
			setError(row, ex);
		}
		return row;
	}

	private void setError(TableRow row, Exception ex) {
		TextView resolved = row.findViewById(R.id.resolved);
		TableRow.LayoutParams params = (TableRow.LayoutParams)resolved.getLayoutParams();
		int resolvedAt = row.indexOfChild(resolved) + 1;
		int removeCount = row.getChildCount() - resolvedAt;
		row.removeViews(resolvedAt, removeCount);
		params.span += removeCount; // two rendered + two divider
		resolved.setText(String.format("%s\n%s", ex.getClass().getSimpleName(), ex.getMessage()));
	}

	private String getColor(CharSequence text) {
		StringBuilder color = new StringBuilder();
		if (text instanceof Spanned) {
			ForegroundColorSpan[] spans = ((Spanned)text).getSpans(0, text.length(), ForegroundColorSpan.class);
			if (spans != null) {
				for (ForegroundColorSpan span : spans) {
					if (color.length() > 0) {
						color.append(",\n");
					}
					color.append(colorToARGBHexString("", span.getForegroundColor()));
				}
			}
		}
		if (color.length() != 0) {
			return color.toString();
		} else {
			return null;
		}
	}
}
