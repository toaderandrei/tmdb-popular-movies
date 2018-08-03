package com.org.android.popularmovies.observer;

/**
 * Interface used to notify UI about
 * changes.
 * */
public interface DataObservable {

    void updateFailed(String message);

    void updateSuccess(String message);

}
