package com.org.android.popularmovies.controller;

/*
* Controller callback listener.
* */
public interface ControllerCallback {
    void onSuccess();

    void onFail(String message);
}
