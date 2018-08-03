package com.org.android.popularmovies.activities.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.org.android.popularmovies.R;

import java.util.ArrayList;

/**
 * Created by andrei on 12/12/15.
 */
public class ModeSpinnerAdapter extends BaseAdapter {

    private Context context;

    public ModeSpinnerAdapter(Context context) {
        this.context = context;
    }

    private ArrayList<ModeSpinnerItem> mItems = new ArrayList<ModeSpinnerItem>();

    public void clear() {
        mItems.clear();
    }

    public void addItem(String tag, String title, boolean indented) {
        mItems.add(new ModeSpinnerItem(false, tag, title, indented));
    }

    public void addHeader(String title) {
        mItems.add(new ModeSpinnerItem(true, "", title, false));
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private boolean isHeader(int position) {
        return position >= 0 && position < mItems.size()
                && mItems.get(position).isHeader;
    }

    @Override
    public View getDropDownView(int position, View view, ViewGroup parent) {
        if (view == null || !view.getTag().toString().equals("DROPDOWN")) {
            view = getLayoutInflater().inflate(R.layout.item_toolbar_spinner_dropdown, parent, false);
            view.setTag("DROPDOWN");
        }

        TextView headerTextView = (TextView) view.findViewById(R.id.header_text);
        View dividerView = view.findViewById(R.id.divider_view);
        TextView normalTextView = (TextView) view.findViewById(android.R.id.text1);

        if (isHeader(position)) {
            headerTextView.setText(getTitle(position));
            headerTextView.setVisibility(View.VISIBLE);
            normalTextView.setVisibility(View.GONE);
            dividerView.setVisibility(View.VISIBLE);
        } else {
            headerTextView.setVisibility(View.GONE);
            normalTextView.setVisibility(View.VISIBLE);
            dividerView.setVisibility(View.GONE);

            setUpNormalDropdownView(position, normalTextView);
        }

        return view;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        if (view == null || !view.getTag().toString().equals("NON_DROPDOWN")) {
            view = getLayoutInflater().inflate(R.layout.item_toolbar_spinner, parent, false);
            view.setTag("NON_DROPDOWN");
        }
        TextView textView = (TextView) view.findViewById(android.R.id.text1);
        textView.setText(getTitle(position));
        return view;
    }

    protected LayoutInflater getLayoutInflater() {
        return LayoutInflater.from(context);
    }

    private String getTitle(int position) {
        return position >= 0 && position < mItems.size() ? mItems.get(position).title : "";
    }

    public String getMode(int position) {
        return position >= 0 && position < mItems.size() ? mItems.get(position).mode : "";
    }

    private void setUpNormalDropdownView(int position, TextView textView) {
        textView.setText(getTitle(position));
    }

    @Override
    public boolean isEnabled(int position) {
        return !isHeader(position);
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }
}