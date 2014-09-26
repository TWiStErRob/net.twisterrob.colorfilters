package net.twisterrob.colorfilters.android.image;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import net.twisterrob.colorfilters.android.R;

import java.util.ArrayList;
import java.util.List;

public class ImageFragment extends Fragment {
    private static final String PREF_IMAGE_URL = "Image.url";

    public interface Listener {
        void reset();
    }

    private static final int REQUEST_CODE_GET_PICTURE = Activity.RESULT_FIRST_USER;
    private ImageView original;
    private ImageView preview;

    private Listener listener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        listener = (Listener) activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_image, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        original = (ImageView) view.findViewById(R.id.original);
        original.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startLoadImage();
            }
        });
        original.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                loadDefaults();
                return true;
            }
        });

        preview = (ImageView) view.findViewById(R.id.preview);
        preview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.reset();
            }
        });
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState == null) {
            SharedPreferences prefs = getPrefs();
            String imageUrl = prefs.getString(PREF_IMAGE_URL, null);
            if (imageUrl != null) {
                load(Uri.parse(imageUrl));
                return;
            }
        } else {
            boolean originalLoaded = BitmapKeeper.into(getFragmentManager(), original);
            boolean previewLoaded = BitmapKeeper.into(getFragmentManager(), preview);
            if (originalLoaded && previewLoaded) {
                return;
            }
        }
        loadDefaults();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_image, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_image:
                startLoadImage();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStop() {
        super.onStop();
        Uri uri = BitmapKeeper.getUri(getFragmentManager());
        SharedPreferences.Editor editor = getPrefs().edit();
        if (uri != null) {
            editor.putString(PREF_IMAGE_URL, uri.toString());
        } else {
            editor.remove(PREF_IMAGE_URL);
        }
        editor.apply();
    }

    private void startLoadImage() {
        // Camera
        List<Intent> cameraIntents = new ArrayList<Intent>();
        Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        PackageManager packageManager = getActivity().getPackageManager();
        List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for (ResolveInfo res : listCam) {
            Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            cameraIntents.add(intent);
        }

        // Filesystem
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*"); //NON-NLS

        // Chooser of filesystem options
        Intent chooserIntent = Intent.createChooser(galleryIntent, getText(R.string.cf_image_action));
        // Add the camera options
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[0]));

        startActivityForResult(chooserIntent, REQUEST_CODE_GET_PICTURE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_GET_PICTURE: {
                if (resultCode == Activity.RESULT_OK) {
                    if (data.getData() != null) {
                        load(data.getData());
                        return;
                    }
                    if (data.getExtras() != null) {
                        Object extraData = data.getExtras().get("data"); //NON-NLS
                        if (extraData instanceof Bitmap) {
                            load((Bitmap) extraData);
                            return;
                        }
                    }
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void loadDefaults() {
        original.setImageResource(R.drawable.default_image);
        preview.setImageResource(R.drawable.default_image);
    }


    public void load(Uri uri) {
        BitmapKeeper.save(getFragmentManager(), uri);
        Glide.with(this).load(uri).dontTransform().into(original);
        Glide.with(this).load(uri).dontTransform().into(preview);
    }

    private void load(Bitmap bitmap) {
        BitmapKeeper.save(getFragmentManager(), bitmap);
        original.setImageBitmap(bitmap);
        preview.setImageBitmap(bitmap);
    }

    private SharedPreferences getPrefs() {
        return PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
    }


    public void setColorFilter(ColorFilter colorFilter) {
        preview.setColorFilter(colorFilter);
    }

    public Bitmap renderToBitmap() {
        Drawable od = original.getDrawable(), pd = preview.getDrawable();
        int ow = od.getIntrinsicWidth(), pw = pd.getIntrinsicWidth();
        int oh = od.getIntrinsicHeight(), ph = pd.getIntrinsicHeight();
        boolean portrait = Math.max(ow, pw) <= Math.max(oh, ph);

        int w, h;
        if (portrait) {
            w = ow + pw;
            h = Math.max(ph, oh);
        } else {
            w = Math.max(ow, pw);
            h = oh + ph;
        }

        Bitmap image = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(image);
        od.draw(canvas);
        canvas.translate(portrait ? ow : 0, portrait ? 0 : oh);
        pd.draw(canvas);
        return image;
    }
}
