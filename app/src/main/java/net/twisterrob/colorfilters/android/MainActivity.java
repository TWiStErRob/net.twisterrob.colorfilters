package net.twisterrob.colorfilters.android;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.ColorFilter;
import android.inputmethodservice.KeyboardView;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;

import net.twisterrob.android.view.KeyboardHandler;
import net.twisterrob.colorfilters.android.image.ImageFragment;
import net.twisterrob.colorfilters.android.image.LogoWriter;
import net.twisterrob.colorfilters.android.keyboard.KeyboardMode;
import net.twisterrob.colorfilters.android.lighting.LightingFragment;
import net.twisterrob.colorfilters.android.matrix.MatrixFragment;
import net.twisterrob.colorfilters.android.porderduff.PorterDuffFragment;

import java.io.File;

import static android.provider.MediaStore.Images;


public class MainActivity extends ActionBarActivity implements ActionBar.OnNavigationListener, ColorFilterFragment.Listener, ImageFragment.Listener {
    private static final String PREF_COLORFILTER_SELECTED = "ColorFilter.selected";
    private static final int COLORFILTER_DEFAULT = 0;

    private ImageFragment images;
    private KeyboardHandler kbd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_color_filter);

        images = (ImageFragment) getSupportFragmentManager().findFragmentById(R.id.images);

        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        actionBar.setListNavigationCallbacks(new ArrayAdapter<CharSequence>(
                        actionBar.getThemedContext(),
                        android.R.layout.simple_list_item_1,
                        android.R.id.text1,
                        new CharSequence[]{
                                getText(R.string.cf_lighting_title),
                                getText(R.string.cf_porterduff_title),
                                getText(R.string.cf_matrix_title)
                        }),
                this);
    }

    @Override
    protected void onResume() {
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
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                startActivity(new Intent(getApplicationContext(), AboutActivity.class));
                return true;
            case R.id.action_settings:
                // no actual result, just want a notification of return from preferences
                startActivityForResult(new Intent(getApplicationContext(), PreferencesActivity.class), RESULT_FIRST_USER);
                return true;
            case R.id.action_logo:
                File logo = LogoWriter.write(36, 48, 72, 96, 144, 192, 512);
                images.load(Uri.fromFile(logo));
                Log.i("LOGO", "Written to: " + logo);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RESULT_FIRST_USER) {
            kbd = null;
            Fragment fragment = getCurrentFragment();
            if (fragment instanceof ColorFilterFragment) {
                // force recreating the fragment to pick up the new keyboard (in case it changed in settings)
                getSupportFragmentManager().beginTransaction()
                        .detach(fragment)
                        .attach(fragment)
                        .commitAllowingStateLoss() /* commit won't work because onResume is called after onActivityResult
                                                      and at this state we're "after onSaveInstanceState" */
                ;
            }
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void reset() {
        ColorFilterFragment fragment = (ColorFilterFragment) getCurrentFragment();
        fragment.reset();
    }

    @Override
    public void onBackPressed() {
        if (kbd != null && kbd.handleBack()) return;
        super.onBackPressed();
    }

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


    public Fragment getCurrentFragment() {
        return getSupportFragmentManager().findFragmentById(R.id.container);
    }

    private ColorFilterFragment createFragment(int position) {
        switch (position) {
            case 0:
                return LightingFragment.newInstance();
            case 1:
                return PorterDuffFragment.newInstance();
            case 2:
                return MatrixFragment.newInstance();
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
        }
        return -1;
    }

    @Override
    public void colorFilterChanged(ColorFilter colorFilter) {
        if (images != null) {
            images.setColorFilter(colorFilter);
        }
    }

    @Override
    public Uri renderCurrentView(CharSequence title, CharSequence desc) {
        if (images != null) {
            Bitmap image = images.renderToBitmap();
            String uri = Images.Media.insertImage(getContentResolver(), image, title.toString(), desc.toString());
            return Uri.parse(uri);
        }
        return null;
    }


    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);
        kbd = null;
    }

    @Override
    public KeyboardHandler getKeyboard() {
        if (kbd == null) {
            Fragment fragment = getCurrentFragment();
            if (fragment instanceof ColorFilterFragment) {
                KeyboardMode mode = ((ColorFilterFragment) fragment).getPreferredKeyboardMode();
                if (!getPrefs().getBoolean(getString(R.string.cf_pref_keyboard), false)) {
                    mode = KeyboardMode.NATIVE;
                }
                kbd = mode.create(getWindow(), (KeyboardView) findViewById(R.id.keyboard));
            }
        }
        return kbd;
    }

    private SharedPreferences getPrefs() {
        return PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
    }
}
