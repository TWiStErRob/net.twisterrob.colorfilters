package net.twisterrob.colorfilters.android;

import java.util.Locale;

import android.annotation.TargetApi;
import android.app.*;
import android.content.*;
import android.content.res.Configuration;
import android.graphics.*;
import android.net.Uri;
import android.os.*;
import android.preference.PreferenceManager;
import android.support.annotation.*;
import android.support.v4.app.Fragment;
import android.text.*;
import android.text.style.*;
import android.view.*;
import android.widget.Toast;

import net.twisterrob.colorfilters.android.keyboard.*;
import net.twisterrob.colorfilters.base.R;

public abstract class ColorFilterFragment extends Fragment {
	public interface Listener {
		void colorFilterChanged(@Nullable ColorFilter colorFilter);

		@NonNull KeyboardHandler getKeyboard();

		@NonNull Uri renderCurrentView(@NonNull CharSequence title, @NonNull CharSequence description);

		void fragmentOnResume();

		@Nullable Bitmap getCurrentBitmap();
	}

	private Listener listener;

	protected @NonNull KeyboardHandler getKeyboard() {
		return listener.getKeyboard();
	}

	protected abstract @NonNull KeyboardMode getPreferredKeyboardMode();

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		listener = (Listener)context;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override public void onResume() {
		super.onResume();
		listener.fragmentOnResume();
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.fragment_color_filter, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int itemId = item.getItemId();
		if (itemId == R.id.action_info) {
			displayHelp();
			return true;
		} else if (itemId == R.id.action_share) {
			Uri image = listener.renderCurrentView(getString(R.string.cf_share_subject), generateCode());

			Intent intent = new Intent(Intent.ACTION_SEND);
			intent.setType("image/jpeg"); //NON-NLS
			intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.cf_share_subject));
			intent.putExtra(Intent.EXTRA_TEXT, getSharedText());
			intent.putExtra(Intent.EXTRA_STREAM, image);
			startActivity(Intent.createChooser(intent, getText(R.string.cf_share_picker_title)));
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private CharSequence getSharedText() {
		SpannableStringBuilder builder = new SpannableStringBuilder();
		builder.append(getText(R.string.cf_share_text));
		builder.append("\n\n");
		builder.append(generateFormattedCode());
		return builder;
	}

	/** @see #displayHelp(int, int) */
	protected abstract void displayHelp();

	protected void displayHelp(final int titleResourceId, int descriptionResourceId) {
		AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
		dialog.setTitle(getText(titleResourceId));
		dialog.setMessage(getInfoMessage(descriptionResourceId));
		dialog.setPositiveButton(R.string.cf_info_ok, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.dismiss();
			}
		});
		dialog.setNeutralButton(R.string.cf_info_copy, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				copyToClipboard(getActivity(), getText(titleResourceId), generateCode());
				Toast.makeText(getActivity(), R.string.cf_info_copy_toast, Toast.LENGTH_SHORT).show();
			}
		});
		dialog.show();
	}

	private CharSequence getInfoMessage(int descriptionResourceId) {
		SpannableStringBuilder builder = new SpannableStringBuilder();
		builder.append(getText(descriptionResourceId));
		builder.append("\n\n"); //NON-NLS
		builder.append(getText(R.string.cf_info_code));
		builder.append("\n"); //NON-NLS
		builder.append(generateFormattedCode());
		return builder;
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@SuppressWarnings("deprecation")
	public static void copyToClipboard(Context context, CharSequence title, CharSequence content) {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
			android.text.ClipboardManager clipboard =
					(android.text.ClipboardManager)context.getSystemService(Context.CLIPBOARD_SERVICE);
			clipboard.setText(content);
		} else {
			android.content.ClipboardManager clipboard =
					(android.content.ClipboardManager)context.getSystemService(Context.CLIPBOARD_SERVICE);
			clipboard.setPrimaryClip(android.content.ClipData.newPlainText(title, content));
		}
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		getKeyboard().hideCustomKeyboard();
	}

	protected void imageChanged() {
		// noop by default, use getCurrentBitmap to query Bitmap if needed
	}

	protected void updateFilter() {
		ColorFilter filter = createFilter();
		listener.colorFilterChanged(filter);
	}

	protected Bitmap getCurrentBitmap() {
		return listener.getCurrentBitmap();
	}

	protected boolean isPortrait() {
		return getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
	}

	protected abstract @NonNull CharSequence generateCode();

	protected CharSequence generateFormattedCode() {
		Spannable code = new SpannableString(generateCode());
		code.setSpan(new TypefaceSpan("monospace"), 0, code.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE); //NON-NLS
		code.setSpan(new RelativeSizeSpan(0.6f), 0, code.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
		code.setSpan(new BackgroundColorSpan(0x33999999), 0, code.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
		return code;
	}

	protected abstract @Nullable ColorFilter createFilter();

	public abstract void reset();
	
	public boolean needsImages() {
		return true;
	}

	protected static String colorToRGBString(@ColorInt int color) {
		return String.format(Locale.ROOT, "%d, %d, %d", //NON-NLS
				Color.red(color), Color.green(color), Color.blue(color));
	}

	protected static String colorToARGBString(@ColorInt int color) {
		return String.format(Locale.ROOT, "%d, %d, %d, %d", //NON-NLS
				Color.alpha(color), Color.red(color), Color.green(color), Color.blue(color));
	}

	protected static String colorToRGBHexString(@NonNull String prefix, @ColorInt int color) {
		return String.format(Locale.ROOT, "%s%06X", prefix, 0xFFFFFF & color); //NON-NLS
	}

	protected static String colorToARGBHexString(@NonNull String prefix, @ColorInt int color) {
		return String.format(Locale.ROOT, "%s%08X", prefix, color); //NON-NLS
	}

	protected SharedPreferences getPrefs() {
		return PreferenceManager.getDefaultSharedPreferences(requireContext().getApplicationContext());
	}
}
