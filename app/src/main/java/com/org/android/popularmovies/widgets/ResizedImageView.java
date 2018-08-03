package com.org.android.popularmovies.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.org.android.popularmovies.R;

/**
 * Custom Image view.
 */
public class ResizedImageView extends ImageView {

    private float aspectRatio = 0;
    private static final float DEFAULT_ASPECT_RATIO = 0.0f;

    public ResizedImageView(Context context) {
        super(context);
    }

    public ResizedImageView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.AspectResizedImageView);
        aspectRatio = typedArray.getFloat(R.styleable.AspectResizedImageView_imgAspectRatio, 0);
        typedArray.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        float localRatio = aspectRatio;

        if (localRatio == DEFAULT_ASPECT_RATIO) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        } else {
            int mWidth = MeasureSpec.getSize(widthMeasureSpec);
            int mHeight = MeasureSpec.getSize(heightMeasureSpec);
            if (mWidth == 0 && mHeight == 0) {
                throw new IllegalArgumentException("this cannot be null.");
            }
            int hPadding = getPaddingLeft() + getPaddingRight();
            int vPadding = getPaddingTop() + getPaddingBottom();
            //remove the span.
            mHeight -= vPadding;
            mWidth -= hPadding;
            if (mHeight > 0 && (mWidth > mHeight * localRatio)) {
                mWidth = (int) (mHeight * localRatio + .5);
            } else {
                mHeight = (int) (mWidth / localRatio + .5);
            }
            //add the span.
            mHeight += vPadding;
            mWidth += hPadding;
            super.onMeasure(MeasureSpec.makeMeasureSpec(mWidth, MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(mHeight, MeasureSpec.EXACTLY));
        }
    }

    public void setAspectRation(float ration) {
        this.aspectRatio = ration;
    }
}
