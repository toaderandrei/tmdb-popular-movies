package com.org.android.popularmovies.utils;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import com.org.android.popularmovies.model.MovieVideo;

/**
 * Utility for videos.
 */
public class VideoUtils {

    public static void playVideo(Activity activity, MovieVideo video) {
        if (video.getSite().equals(MovieVideo.SITE_YOUTUBE))
            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + video.getKey())));
        else {
            Toast.makeText(activity, "activity not found", Toast.LENGTH_SHORT).show();
        }
    }
}
