package com.org.android.popularmovies.observer;

/**
 * Created by andrei on 11/27/15.
 */
public interface DataObserver<T> {

    void registerListener(T observable);

    void unregisterListener(T dataObservable);

    void notifyListenersFail(String message);

    void notifyListenersSuccess(String message);

}
