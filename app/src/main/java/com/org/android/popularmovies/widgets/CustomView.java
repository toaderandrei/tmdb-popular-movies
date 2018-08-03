package com.org.android.popularmovies.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.org.android.popularmovies.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * CustomView class that is used for
 */
public class CustomView extends LinearLayout {

    @BindView(R.id.message_view_text)
    TextView mTextView;
    @BindView(R.id.message_view_image)
    ImageView mImageView;

    private View mRoot;

    private CustomView(Context context) {
        super(context, null, 0);
        initialize(context, null, 0);
    }

    public CustomView(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
        initialize(context, attrs, 0);
    }

    private void initialize(Context context, AttributeSet attrs, int defStyleAttr) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View root = inflater.inflate(R.layout.custom_view_widget_layout, this, true);
        ButterKnife.bind(root, this);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomStateViewStyle, defStyleAttr, 0);

        String text = typedArray.getString(R.styleable.CustomStateViewStyle_messageText);
        Drawable image = typedArray.getDrawable(R.styleable.CustomStateViewStyle_messageImage);
        if (mTextView != null) {
            mTextView.setText(text);
            mImageView.setImageDrawable(image);
        }
        typedArray.recycle();
    }

}