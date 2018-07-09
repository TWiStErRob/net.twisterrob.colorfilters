package net.twisterrob.colorfilters.android.image;

import android.graphics.*;
import android.graphics.drawable.*;
import android.net.Uri;
import android.os.*;
import android.support.annotation.*;
import android.support.v4.app.*;
import android.widget.ImageView;

import com.bumptech.glide.*;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.*;
import com.bumptech.glide.request.target.Target;

/**
 * @see <a href="http://stackoverflow.com/questions/15043222#15043471">Android app losing data during orientation change</a>
 */
public class BitmapKeeper extends Fragment {
	interface Listener {
		void loadComplete();
	}

	private static final String FRAGMENT_TAG = BitmapKeeper.class.getSimpleName();
	private Bitmap bitmap;
	private Uri uri;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
	}

	public static boolean into(FragmentManager fragmentManager, ImageView imageView, @Nullable Listener listener) {
		BitmapKeeper fragment = getCurrent(fragmentManager);
		if (fragment != null) {
			if (fragment.bitmap != null) {
				imageView.setImageBitmap(fragment.bitmap);
				if (listener != null) {
					listener.loadComplete();
				}
				return true;
			}
			if (fragment.uri != null) {
				RequestBuilder<Drawable> builder = Glide
						.with(imageView.getContext())
						.load(fragment.uri)
						.apply(new RequestOptions()
								.dontTransform()
						);
				if (listener != null) {
					builder = builder.listener(new GlideRequestListener(listener));
				}
				builder.into(imageView);
				return true;
			}
		}
		return false;
	}

	public static void clear(FragmentManager fragmentManager) {
		BitmapKeeper fragment = getOrCreate(fragmentManager);
		fragment.bitmap = null;
		fragment.uri = null;
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
			fragmentManager.executePendingTransactions(); // to make .save then .into work immediately after
		}
		return fragment;
	}

	public static BitmapKeeper getCurrent(FragmentManager fragmentManager) {
		return (BitmapKeeper)fragmentManager.findFragmentByTag(FRAGMENT_TAG);
	}

	public static Uri getUri(FragmentManager fragmentManager) {
		BitmapKeeper fragment = getOrCreate(fragmentManager);
		return fragment != null? fragment.uri : null;
	}

	static @Nullable Bitmap getBitmap(@Nullable Drawable d) {
		if (d == null) {
			return null;
		}
		if (d instanceof BitmapDrawable) {
			return ((BitmapDrawable)d).getBitmap();
		}
		if (d instanceof GifDrawable) {
			return ((GifDrawable)d).getFirstFrame();
		}
		Bitmap image = Bitmap.createBitmap(d.getIntrinsicWidth(), d.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(image);
		d.draw(canvas);
		return image;
	}

	private static class GlideRequestListener implements RequestListener<Drawable> {
		private final Listener listener;
		private final Handler ui = new Handler(Looper.getMainLooper());
		private final Runnable notifyLoadComplete = new Runnable() {
			@Override public void run() {
				listener.loadComplete();
			}
		};

		public GlideRequestListener(@NonNull Listener listener) {
			this.listener = listener;
		}

		@Override public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target,
				boolean isFirstResource) {
			return false;
		}
		@Override public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target,
				DataSource dataSource, boolean isFirstResource) {
			ui.post(notifyLoadComplete); // async to give Glide time to set a drawable just after returning
			return false;
		}
	}
}
