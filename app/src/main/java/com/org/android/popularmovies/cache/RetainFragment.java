package com.org.android.popularmovies.cache;

import android.app.Fragment;
import android.os.Bundle;

/**
 * A simple non-UI Fragment that stores a single Object and is retained over configuration
 * changes. It will be used to retain the ImageCache object.
 */
public class RetainFragment extends Fragment {
    private Object mObject;

    /**
     * Empty constructor as per the Fragment documentation
     */
    public RetainFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Make sure this Fragment is retained over a configuration change
        setRetainInstance(true);
    }

    /**
     * Store a single object in this Fragment.
     *
     * @param object The object to store
     */
    public void setObject(Object object) {
        mObject = object;
    }

    /**
     * Get the stored object.
     *
     * @return The stored object
     */
    public Object getObject() {
        return mObject;
    }
}
