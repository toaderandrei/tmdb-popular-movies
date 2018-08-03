package com.org.android.popularmovies.activities.adapter;

/**
 * Created by andrei on 12/12/15.
 */
public class ModeSpinnerItem {
    boolean isHeader;
    String mode, title;
    boolean indented;

    ModeSpinnerItem(boolean isHeader, String mode, String title, boolean indented) {
        this.isHeader = isHeader;
        this.mode = mode;
        this.title = title;
        this.indented = indented;
    }
}