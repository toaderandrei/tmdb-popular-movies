package com.org.android.popularmovies.utils;

import android.support.annotation.NonNull;
import android.text.Html;
import android.text.Spanned;

import com.org.android.popularmovies.model.GenreItem;

import java.util.List;

/**
 * Utility class for manipulation of strings
 */
public class StringUtils {

    public static String joinGenres(List<GenreItem> genres, String delimiter, @NonNull StringBuilder builder) {
        builder.setLength(0);
        if (!Lists.isEmpty(genres))
            for (GenreItem genre : genres) {
                if (builder.length() > 0) builder.append(delimiter);
                builder.append(genre.getName());
            }
        return builder.toString();
    }

    public static <T> Spanned getSpannedText(String keyOverview, T value, boolean newLine) {
        if (newLine) {
            return Html.fromHtml("<b>" + keyOverview + "</b>" + "<br>" + value);
        }
        return Html.fromHtml("<b>" + keyOverview + "</b>" + " " + value);

    }
}
