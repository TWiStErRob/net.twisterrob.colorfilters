package net.twisterrob.colorfilters.android.image;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

// http://stackoverflow.com/questions/15043222/android-app-losing-data-during-orientation-change#15043471
public class BitmapKeeper extends Fragment {
    private static final String FRAGMENT_TAG = BitmapKeeper.class.getSimpleName();
    private Bitmap bitmap;
    private Uri uri;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public static boolean into(FragmentManager fragmentManager, ImageView imageView) {
        BitmapKeeper fragment = getCurrent(fragmentManager);
        if (fragment != null) {
            if (fragment.bitmap != null) {
                imageView.setImageBitmap(fragment.bitmap);
                return true;
            }
            if (fragment.uri != null) {
                Glide.with(imageView.getContext()).load(fragment.uri).into(imageView);
                return true;
            }
        }
        return false;
    }

    public static void save(FragmentManager fragmentManager, Bitmap bitmap) {
        BitmapKeeper fragment = getOrCreate(fragmentManager);
        fragment.uri = null;
        fragment.bitmap = bitmap;
    }

    public static void save(FragmentManager fragmentManager, Uri uri) {
        BitmapKeeper fragment = getOrCreate(fragmentManager);
        fragment.bitmap = null;
        fragment.uri = uri;
    }

    public static BitmapKeeper getOrCreate(FragmentManager fragmentManager) {
        BitmapKeeper fragment = getCurrent(fragmentManager);
        if (fragment == null) {
            fragment = new BitmapKeeper();
            fragmentManager.beginTransaction().add(fragment, FRAGMENT_TAG).commitAllowingStateLoss();
        }
        return fragment;
    }

    public static BitmapKeeper getCurrent(FragmentManager fragmentManager) {
        return (BitmapKeeper) fragmentManager.findFragmentByTag(FRAGMENT_TAG);
    }

    public static Uri getUri(FragmentManager fragmentManager) {
        BitmapKeeper fragment = getOrCreate(fragmentManager);
        return fragment != null ? fragment.uri : null;
    }
}
