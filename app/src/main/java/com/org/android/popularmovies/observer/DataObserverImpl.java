package com.org.android.popularmovies.observer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by andrei on 11/27/15.
 */
public class DataObserverImpl implements DataObserver<DataObservable> {

    private List<DataObservable> dataObservables = Collections.synchronizedList(new ArrayList<DataObservable>());

    public DataObserverImpl() {
    }

    @Override
    public void registerListener(DataObservable observable) {
        if (observable != null && !dataObservables.contains(observable)) {
            dataObservables.add(observable);
        }
    }

    @Override
    public void unregisterListener(DataObservable dataObservable) {
        if (dataObservable != null && dataObservables.contains(dataObservable)) {
            dataObservables.remove(dataObservable);
        }
    }

    @Override
    public void notifyListenersFail(String message) {
        for (DataObservable dataObservable : dataObservables) {
            dataObservable.updateFailed(message);
        }
    }

    @Override
    public void notifyListenersSuccess(String message) {
        for (DataObservable dataObservable : dataObservables) {
            dataObservable.updateSuccess(message);
        }
    }
}
