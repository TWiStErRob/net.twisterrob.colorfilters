package net.twisterrob.colorfilters.android.image;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Environment;
import android.util.Log;

import net.twisterrob.android.view.color.ColorMath;
import net.twisterrob.android.view.color.FastMath;
import net.twisterrob.android.view.color.swatches.pixel.color.PixelColor;
import net.twisterrob.android.view.color.swatches.pixel.drawer.LineByLineBitmapDrawer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public final class LogoWriter {
    private LogoWriter() {
    }

    public static File write(int... sizes) {
        File file = null;
        for (int size : sizes) {
            file = write(size);
        }
        return file;
    }

    public static File write(int size) {
        int[] pixels = new int[size * size];
        new LineByLineBitmapDrawer(pixels, size, size, new RadialHSBGradientLogo()).draw();
        OutputStream out = null;
        try {
            Bitmap bitmap = Bitmap.createBitmap(pixels, size, size, Bitmap.Config.ARGB_8888);
            File picturesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            File output = new File(picturesDir, "colorfilters_logo_" + size + ".png");
            out = new FileOutputStream(output);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            return output;
        } catch (IOException ex) {
            Log.e("LOGO", ex.getMessage(), ex);
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }
        return null;
    }

    private static class RadialHSBGradientLogo implements PixelColor {
        private int w;
        private int cx, cy;

        @Override
        public void initializeInvariants(int w, int h) {
            this.w = w / 2 / 6;
            this.cx = w / 2;
            this.cy = w / 2;
        }

        @Override
        public int getPixelColorAt(int x, int y) {
            float angle = FastMath.Atan2Faster.atan2(y - cy, x - cx) + ColorMath.PI; // [0, 2pi]
            float dist = (float) Math.sqrt((x - cx) * (x - cx) + (y - cy) * (y - cy));
            float bri = ColorMath.ofMap(dist, 4 * w, 6 * w, 1, 0);
            float alp = ColorMath.ofMap(dist, 5 * w, 6 * w, 1, 0);
            float sat = ColorMath.ofMap(dist, 0, 4 * w, 0, 1);
            float hue = angle / (ColorMath.PI * 2);
            if (cx <= x) {
                hue = 1 - hue; // mirror on the right
            }
            hue = hue * 2 % 1; // draw 2 rounds in 1 circle
            int color = ColorMath.fromHsb(hue, sat, bri, alp);
            if (cx <= x) {
                color = desaturate(color);
            }
            return color;
        }

        /**
         * Twisted desaturation with off-ratios.
         */
        private int desaturate(int color) {
            int r = (int) (0.4 * Color.red(color) + 0.4 * Color.green(color) + 0.2 * Color.blue(color));
            int g = (int) (0.2 * Color.red(color) + 0.6 * Color.green(color) + 0.2 * Color.blue(color));
            int b = (int) (0.1 * Color.red(color) + 0.5 * Color.green(color) + 0.4 * Color.blue(color));
            return Color.argb(Color.alpha(color), r, g, b);
        }
    }
}
