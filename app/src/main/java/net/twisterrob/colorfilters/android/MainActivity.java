package net.twisterrob.colorfilters.android;

import java.io.File;

import android.annotation.SuppressLint;
import android.content.*;
import android.graphics.*;
import android.inputmethodservice.KeyboardView;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.*;
import android.support.v7.app.*;
import android.util.Log;
import android.view.*;
import android.widget.ArrayAdapter;

import static android.provider.MediaStore.*;

import net.twisterrob.colorfilters.android.about.AboutActivity;
import net.twisterrob.colorfilters.android.image.*;
import net.twisterrob.colorfilters.android.keyboard.*;
import net.twisterrob.colorfilters.android.lighting.LightingFragment;
import net.twisterrob.colorfilters.android.matrix.MatrixFragment;
import net.twisterrob.colorfilters.android.palette.PaletteFragment;
import net.twisterrob.colorfilters.android.porterduff.PorterDuffFragment;
import net.twisterrob.colorfilters.android.resources.ResourceFontFragment;

public class MainActivity extends AppCompatActivity implements ColorFilterFragment.Listener, ImageFragment.Listener {
	private static final String PREF_COLORFILTER_SELECTED = "ColorFilter.selected";
	private static final String PREF_COLORFILTER_PREVIEW = "ColorFilter.images";
	private static final int COLORFILTER_DEFAULT = 0;

	private ImageFragment images;
	private KeyboardHandler kbd;
	private MenuItem imageToggleItem;

	@Override protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_color_filter);

		images = (ImageFragment)getSupportFragmentManager().findFragmentById(R.id.images);

		final ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setHomeButtonEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeAsUpIndicator(R.drawable.ic_launcher);
		setupNavigation(actionBar);
	}

	@SuppressWarnings("deprecation")
	private void setupNavigation(ActionBar actionBar) {
		// TODO the java compiler ignores deprecation suppression if there's a o.m(R.X.name); in here
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		ArrayAdapter<CharSequence> adapter = new ArrayAdapter<>(
				actionBar.getThemedContext(),
				android.R.layout.simple_list_item_1,
				android.R.id.text1,
				new CharSequence[] {
						getText(R.string.cf_lighting_title),
						getText(R.string.cf_porterduff_title),
						getText(R.string.cf_matrix_title),
						getText(R.string.cf_palette_title)
						//,getText(R.string.cf_resfont_title)
				});
		actionBar.setListNavigationCallbacks(adapter, new ActionBar.OnNavigationListener() {
			@Override
			public boolean onNavigationItemSelected(int position, long id) {
				try {
					// keep the existing instance on rotation
					if (getPosition(getCurrentFragment()) != position) {
						ColorFilterFragment fragment = createFragment(position);
						getSupportFragmentManager().beginTransaction()
						                           .replace(R.id.container, fragment)
						                           .commit();
					}
					return true;
				} catch (RuntimeException ex) {
					return false;
				}
			}
		});
	}

	@SuppressWarnings("deprecation")
	@Override protected void onResume() {
		super.onResume();
		int position = getPrefs().getInt(PREF_COLORFILTER_SELECTED, COLORFILTER_DEFAULT);
		getSupportActionBar().setSelectedNavigationItem(position);
	}

	@Override
	protected void onPause() {
		super.onPause();
		int position = getPosition(getCurrentFragment());
		if (0 <= position) {
			getPrefs().edit().putInt(PREF_COLORFILTER_SELECTED, position).apply();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.activity_color_filter, menu);
		imageToggleItem = menu.findItem(R.id.action_image_toggle);
		imageToggleItem.setChecked(getPrefs().getBoolean(PREF_COLORFILTER_PREVIEW, imageToggleItem.isChecked()));
		updateImagesVisibility();
		return true;
	}

	@SuppressLint("LogConditional")
	@Override public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				startActivity(new Intent(getApplicationContext(), AboutActivity.class));
				return true;
			case R.id.action_settings:
				// no actual result, just want a notification of return from preferences
				startActivityForResult(new Intent(getApplicationContext(), PreferencesActivity.class),
						RESULT_FIRST_USER);
				return true;
			case R.id.action_logo:
				File logo = LogoWriter.write(36, 48, 72, 96, 144, 192, 512);
				images.load(Uri.fromFile(logo));
				Log.i("LOGO", "Written to: " + logo);
				return true;
			case R.id.action_image_toggle:
				imageToggleItem.setChecked(!imageToggleItem.isChecked());
				getPrefs().edit().putBoolean(PREF_COLORFILTER_PREVIEW, imageToggleItem.isChecked()).apply();
				updateImagesVisibility();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	private void updateImagesVisibility() {
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		ColorFilterFragment fragment = getCurrentFragment();
		if (imageToggleItem.isChecked() && fragment != null && fragment.needsImages()) {
			ft.show(images);
		} else {
			ft.hide(images);
		}
		ft.commitAllowingStateLoss();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == RESULT_FIRST_USER) {
			kbd = null;
			Fragment fragment = getCurrentFragment();
			if (fragment != null) {
				// force recreating the fragment to pick up the new keyboard (in case it changed in settings)
				getSupportFragmentManager().beginTransaction()
				                           .detach(fragment)
				                           .attach(fragment)
				                           .commitAllowingStateLoss()
				                        /* .commit won't work because onResume is called after onActivityResult
				                            and at this state we're "after onSaveInstanceState" */
				;
			}
			return;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void reset() {
		ColorFilterFragment fragment = getCurrentFragment();
		fragment.reset();
	}

	@Override
	public void onBackPressed() {
		if (kbd != null && kbd.handleBack()) {
			return;
		}
		super.onBackPressed();
	}

	public ColorFilterFragment getCurrentFragment() {
		return (ColorFilterFragment)getSupportFragmentManager().findFragmentById(R.id.container);
	}

	private ColorFilterFragment createFragment(int position) {
		switch (position) {
			case 0:
				return LightingFragment.newInstance();
			case 1:
				return PorterDuffFragment.newInstance();
			case 2:
				return MatrixFragment.newInstance();
			case 3:
				return PaletteFragment.newInstance();
			case 4:
				return ResourceFontFragment.newInstance();
		}
		throw new IllegalStateException("Unknown position " + position);
	}

	private int getPosition(Fragment fragment) {
		if (fragment instanceof LightingFragment) {
			return 0;
		} else if (fragment instanceof PorterDuffFragment) {
			return 1;
		} else if (fragment instanceof MatrixFragment) {
			return 2;
		} else if (fragment instanceof PaletteFragment) {
			return 3;
		} else if (fragment instanceof ResourceFontFragment) {
			return 4;
		}
		return -1;
	}

	@Override
	public void colorFilterChanged(ColorFilter colorFilter) {
		images.setColorFilter(colorFilter);
	}

	@Override
	public void imageChanged() {
		ColorFilterFragment fragment = getCurrentFragment();
		if (fragment != null && fragment.isResumed()) {
			fragment.imageChanged();
		}
	}

	@Override
	public Bitmap getCurrentBitmap() {
		return images.getCurrent();
	}

	@Override
	public Uri renderCurrentView(CharSequence title, CharSequence desc) {
		Bitmap shareContent = images.renderPreview();
		String uri = Images.Media.insertImage(getContentResolver(), shareContent, title.toString(), desc.toString());
		return Uri.parse(uri);
	}

	@Override
	public void onAttachFragment(Fragment fragment) {
		super.onAttachFragment(fragment);
		kbd = null;
	}

	@Override public void fragmentOnResume() {
		imageChanged();
	}

	@Override
	public KeyboardHandler getKeyboard() {
		if (kbd == null) {
			ColorFilterFragment fragment = getCurrentFragment();
			if (fragment != null) {
				KeyboardMode mode = fragment.getPreferredKeyboardMode();
				if (!getPrefs().getBoolean(getString(R.string.cf_pref_keyboard), false)) {
					mode = KeyboardMode.NATIVE;
				}
				kbd = new KeyboardHandlerFactory().create(mode, getWindow(), (KeyboardView)findViewById(R.id.keyboard));
			}
		}
		return kbd;
	}

	private SharedPreferences getPrefs() {
		return PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
	}
}
