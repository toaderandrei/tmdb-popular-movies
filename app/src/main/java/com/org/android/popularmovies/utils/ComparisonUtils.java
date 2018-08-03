package com.org.android.popularmovies.utils;

/**
 * Utility class used for comparing primitive data, like
 * strings, integers, doubles, longs, booleans,etc.
 */

/**
 * Utility class providing methods to compare objects.
 */
public class ComparisonUtils {
    /**
     * Compares the two given objects and returns true, if they are equal and
     * false, if they are not.
     *
     * @param a one of the two objects to compare
     * @param b the other one of the two objects to compare
     * @return if the two given lists are equal.
     */
    public static boolean areObjectsEqual(Object a, Object b) {

        if (a == null && b == null)
            return true;
        if (a == null || b == null)
            return false;
        return a.equals(b);
    }
}
