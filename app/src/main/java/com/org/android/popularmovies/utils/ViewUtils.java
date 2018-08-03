package com.org.android.popularmovies.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.View;

/**
 * Utility class for views.
 */
public class ViewUtils {


    /**
     * converts  a view to a bitmap.
     *
     * @param view the view to be converted
     * @return the bitmap out of the view.
     */
    public static Bitmap getBitmapFromView(View view) {
        //Define a bitmap with the same size as the view
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        //Bind a canvas to it
        Canvas canvas = new Canvas(returnedBitmap);
        //Get the view's background
        Drawable bgDrawable = view.getBackground();
        if (bgDrawable != null) {    //has background drawable, then draw it on the canvas
            bgDrawable.draw(canvas);
        } else {   //does not have background drawable, then draw white background on the canvas
            canvas.drawColor(Color.WHITE);
            // draw the view on the canvas
            view.draw(canvas);
            //return the bitmap
        }
        return returnedBitmap;
    }
}
