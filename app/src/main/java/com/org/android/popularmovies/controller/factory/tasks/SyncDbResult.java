package com.org.android.popularmovies.controller.factory.tasks;

/**
 * Task that can result after a db operation.
 * Success is for when there is a success operation.
 * Failure is for when there is a failed operation.
 * NONE is for undefined.
 */
public class SyncDbResult {
    public static final int SUCCESS = 1;

    public static final int FAILURE = 0;

    public static final int NONE = -1;
}
