package com.org.android.popularmovies.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Environment;
import android.os.StatFs;

import com.org.android.popularmovies.application.PopularMoviesApp;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

/**
 * Utility class for dealing with files.
 */
public class FileUtils {

    /* Check how much usable space is available at a given path.
   *
   * @param path The path to check
   * @return The space available in bytes
   */
    public static long getUsableSpace(File path) {
        final StatFs stats = new StatFs(path.getPath());
        if (ApiVersion.hasJellyBeanMR2()) {
            return getSizeApi18(stats);
        }
        return getSizeApiLessThan18(stats);
    }

    @TargetApi(14)
    private static long getSizeApiLessThan18(StatFs stats) {
        return (long) stats.getBlockSize() * stats.getAvailableBlocks();
    }

    @TargetApi(18)
    private static long getSizeApi18(StatFs stats) {
        return stats.getAvailableBlocksLong() * stats.getBlockSizeLong();
    }

    public static File getDiskCacheDir(Context context, String rootFolderImagesName) {
        File diskCacheDir = null;
        if (android.os.Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            diskCacheDir = getExternalCacheDir(context);
        } else {
            diskCacheDir = context.getCacheDir();
        }
        return new File(diskCacheDir.getPath() + File.separator + rootFolderImagesName);
    }


    private static File getExternalCacheDir(Context context) {
        if (ApiVersion.hasFroyo()) {
            return context.getExternalCacheDir();
        }
        final String cacheDir = "/Android/data/" + context.getPackageName() + "/cache/";
        return new File(Environment.getExternalStorageDirectory().getPath() + cacheDir);
    }

    /**
     * A hashing method that changes a string (like a URL) into a hash suitable for using as a
     * disk filename.
     */
    public static String hashKeyForDisk(String key) {
        String cacheKey;
        try {
            final MessageDigest mDigest = MessageDigest.getInstance("MD5");
            mDigest.update(key.getBytes());
            cacheKey = bytesToHexString(mDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            cacheKey = String.valueOf(key.hashCode());
        }
        return cacheKey;
    }

    /**
     * A hashing method that changes a string (like a URL) into a hash suitable for using as a
     * disk filename.
     */
    public static String hashKeyForHttp(String key) {
        String cacheKey;
        try {
            final MessageDigest mDigest = MessageDigest.getInstance("MD5");
            key += UUID.randomUUID().toString();
            mDigest.update(key.getBytes());
            cacheKey = bytesToHexString(mDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            cacheKey = String.valueOf(key.hashCode());
        }
        return cacheKey;
    }


    private static String bytesToHexString(byte[] bytes) {
        // http://stackoverflow.com/questions/332079
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }

    private static PopularMoviesApp getApp() {
        return PopularMoviesApp.getInstance();
    }
}
