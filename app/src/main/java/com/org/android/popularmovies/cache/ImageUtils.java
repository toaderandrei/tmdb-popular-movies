package com.org.android.popularmovies.cache;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;

import com.org.android.popularmovies.utils.ApiVersion;

import java.io.FileDescriptor;

/**
 * Utility class for dealing with Bitmaps.
 */
public class ImageUtils {

    public static Bitmap decodeBitmapFromFileDescriptor(FileDescriptor fd, int maxWidth, int maxHeight, ImageCache imageCache) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        //this sets the fields for the bitmap but doesn't allocate the memory, allowing for a query of the bitmap.
        options.inJustDecodeBounds = true;

        BitmapFactory.decodeFileDescriptor(fd, null, options);

        options.inSampleSize = computeInSampleSize(options, maxWidth, maxHeight);

        options.inJustDecodeBounds = false;
        if (ApiVersion.hasHoneycomb()) {
            addInBitmapOptions(options, imageCache);
        }
        return BitmapFactory.decodeFileDescriptor(fd, null, options);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private static void addInBitmapOptions(BitmapFactory.Options options, ImageCache cache) {
        //BEGIN_INCLUDE(add_bitmap_options)
        // inBitmap only works with mutable bitmaps so force the decoder to
        // return mutable bitmaps.
        options.inMutable = true;

        if (cache != null) {
            // Try and find a bitmap to use for inBitmap
            Bitmap inBitmap = cache.getBitmapFromReusableSet(options);

            if (inBitmap != null) {
                options.inBitmap = inBitmap;
            }
        }
        //END_INCLUDE(add_bitmap_options)
    }


    /**
     * Trying to compute proper pixel sampling. The sample size specifies a factor of which each side of the image is scaled,
     * for example a factor of 2 on a 640×480 image will result in a 320×240 image being decoded. When setting a sample size,
     * you are not guaranteed the image will be scaled down exactly according to that number, but at least it will never be smaller.
     * The final touch is when comparing the total amount of pixels(after in sample is applied) with the total amount of pixels
     * from the original image.
     *
     * @param options   the BitmapFactory options.
     * @param maxWidth  the max width of the image.
     * @param maxHeight the max height of the image.
     * @return the in sample size.
     */
    private static int computeInSampleSize(BitmapFactory.Options options, int maxWidth, int maxHeight) {
        final int width = options.outWidth;
        final int height = options.outHeight;
        int inSampleSize = 1;
        if (width > maxWidth || height > maxHeight) {
            //down-sample one time;
            int halfWidth = width / 2;
            int halfHeight = height / 2;
            while ((halfWidth / inSampleSize) > maxWidth &&
                    (halfHeight / inSampleSize) > maxHeight) {
                inSampleSize *= 2;
            }
            long totalPixels = width * height / inSampleSize;

            long totalMaxPixels = maxWidth * maxHeight * 2;
            while (totalPixels > totalMaxPixels) {
                inSampleSize *= 2;
                totalPixels /= 2;
            }
        }
        return inSampleSize;
    }

    /**
     * Get the size in bytes of a bitmap in a BitmapDrawable. Note that from Android 4.4 (KitKat)
     * onward this returns the allocated memory size of the bitmap which can be larger than the
     * actual bitmap data byte count (in the case it was re-used).
     *
     * @param value
     * @return size in bytes
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static int getBitmapSize(BitmapDrawable value) {
        Bitmap bitmap = value.getBitmap();

        // From KitKat onward use getAllocationByteCount() as allocated bytes can potentially be
        // larger than bitmap byte count.
        if (ApiVersion.hasKitKat()) {
            return bitmap.getAllocationByteCount();
        }

        if (ApiVersion.hasHoneycombMR1()) {
            return bitmap.getByteCount();
        }

        // Pre HC-MR1
        return bitmap.getRowBytes() * bitmap.getHeight();
    }


    public static int getBytesPerPixel(Bitmap.Config config) {
        if (config == Bitmap.Config.ARGB_8888) {
            return 4;
        } else if (config == Bitmap.Config.RGB_565) {
            return 2;
        } else if (config == Bitmap.Config.ARGB_4444) {
            return 2;
        } else if (config == Bitmap.Config.ALPHA_8) {
            return 1;
        }
        return 1;
    }
}
