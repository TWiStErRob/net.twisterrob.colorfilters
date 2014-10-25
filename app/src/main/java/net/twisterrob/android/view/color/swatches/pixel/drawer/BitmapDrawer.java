package net.twisterrob.android.view.color.swatches.pixel.drawer;

import android.os.*;

import net.twisterrob.android.view.color.swatches.pixel.color.PixelColor;

public abstract class BitmapDrawer {
	public interface Callback {
		void drawStared();

		void drawProgress();

		void drawFinished();
	}

	private static final Handler uiHandler = Looper.getMainLooper() != null? new Handler(Looper.getMainLooper()) : null;
	private static final Handler asyncHandler = new BitmapDrawerThread().mHandler;

	protected final int[] bitmap;
	protected final int w;
	protected final int h;

	protected PixelColor pixel;

	private Callback callback;
	private AsyncHandlers async;

	public BitmapDrawer(int[] bitmap, int w, int h, PixelColor pixel) {
		this.bitmap = bitmap;
		this.w = w;
		this.h = h;
		this.pixel = pixel;
	}

	protected abstract void fillPixels();

	public void draw() {
		doStart();
		if (async != null) {
			asyncHandler.post(async.doDraw);
		} else {
			doDraw();
			doFinish();
		}
	}

	private void doStart() {
		Callback callback = this.callback;
		if (callback != null) {
			callback.drawStared();
		}
	}

	private void doDraw() {
		pixel.initializeInvariants(w, h);
		fillPixels();
		reportProgress();
	}

	protected final void reportProgress() {
		if (async != null) {
			uiHandler.post(async.callbackProgress);
		} else {
			if (callback != null) {
				callback.drawProgress();
			}
		}
	}

	private void doFinish() {
		Callback callback = this.callback;
		if (callback != null) {
			callback.drawFinished();
		}
	}

	private void enableAsync() {
		if (async == null) {
			async = new AsyncHandlers();
		}
	}

	private void disableAsync() {
		async = null;
	}

	public void setCallback(Callback callback) {
		this.callback = callback;
	}

	private class AsyncHandlers {
		private Runnable doDraw = new Runnable() {
			@Override
			public void run() {
				doDraw();
			}
		};
		private Runnable callbackProgress = new Runnable() {
			@Override
			public void run() {
				if (callback != null) {
					callback.drawProgress();
				}
			}
		};
	}

	private static class BitmapDrawerThread extends HandlerThread {
		private Handler mHandler = null;

		BitmapDrawerThread() {
			super(BitmapDrawerThread.class.getSimpleName());
			start();
			Looper looper = getLooper();
			if (looper != null) {
				mHandler = new Handler(looper);
			}
		}

		void stopThread() {
			getLooper().quit();
		}
	}

	public interface Factory {
		BitmapDrawer create(int[] bitmap, int w, int h, PixelColor color);

		public static final class Async implements Factory {
			private final Factory factory;

			private Async(Factory factory) {
				this.factory = factory;
			}

			@Override
			public BitmapDrawer create(int[] bitmap, int w, int h, PixelColor color) {
				BitmapDrawer drawer = factory.create(bitmap, w, h, color);
				drawer.enableAsync();
				return drawer;
			}

			public static Factory wrap(Factory factory) {
				if (factory instanceof Async) {
					return factory;
				} else {
					return new Async(factory);
				}
			}
		}

		public static final class Sync implements Factory {
			private final Factory factory;

			private Sync(Factory factory) {
				this.factory = factory;
			}

			@Override
			public BitmapDrawer create(int[] bitmap, int w, int h, PixelColor color) {
				BitmapDrawer drawer = factory.create(bitmap, w, h, color);
				drawer.disableAsync();
				return drawer;
			}

			public static Factory wrap(Factory factory) {
				if (factory instanceof Sync) {
					return factory;
				} else {
					return new Sync(factory);
				}
			}
		}
	}

	/** Beware of proguard */
	public static class ReflectiveFactory<T extends BitmapDrawer> implements Factory {
		private final Class<? extends T> clazz;

		public ReflectiveFactory(Class<? extends T> clazz) {
			this.clazz = clazz;
		}

		@Override
		public T create(int[] bitmap, int w, int h, PixelColor color) {
			try {
				return clazz.getDeclaredConstructor(int[].class, int.class, int.class, PixelColor.class)
				            .newInstance(bitmap, w, h, color);
			} catch (Exception e) {
				throw new IllegalArgumentException("Cannot instantiate " + clazz, e);
			}
		}
	}
}
