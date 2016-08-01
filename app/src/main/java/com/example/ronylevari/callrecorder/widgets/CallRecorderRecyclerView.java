package com.example.ronylevari.callrecorder.widgets;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.example.ronylevari.callrecorder.utils.Utils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CallRecorderRecyclerView extends RecyclerView {

    public static final String TAG = "Recycler";

    private List<View> mNonEmptyStateViews = Collections.emptyList();
    private List<View> mEmptyStateViews = Collections.emptyList();

    private AdapterDataObserver mObserver = new AdapterDataObserver() {

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            Log.d(TAG, "onItemRangeRemoved");
            toggleViews();
        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            Log.d(TAG, "onItemRangeMoved");
            toggleViews();
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            Log.d(TAG, "onItemRangeInserted");
            toggleViews();
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
            super.onItemRangeChanged(positionStart, itemCount, payload);
            Log.d(TAG, "onItemRangeChanged");
            toggleViews();
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            Log.d(TAG, "onItemRangeChanged");
            toggleViews();
        }

        @Override
        public void onChanged() {
            Log.d(TAG, "onChanged");
            toggleViews();
        }
    };

    public CallRecorderRecyclerView(Context context) {
        super(context);
    }

    public CallRecorderRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CallRecorderRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void toggleViews() {

        Log.d(TAG, "toggling");

        if (getAdapter() != null && !mNonEmptyStateViews.isEmpty() && !mEmptyStateViews.isEmpty()) {

            if (getAdapter().getItemCount() == 0) {
                // if recycler view is in empty state

                // remove the non empty state views
                Utils.hideViews(mNonEmptyStateViews);

                // add the empty state views
                Utils.showViews(mEmptyStateViews);

            } else {
                // if recycler view is in not in empty state

                // add the non empty state views
                Utils.showViews(mNonEmptyStateViews);

                // remove the empty state views
                Utils.hideViews(mEmptyStateViews);
            }
        }
    }

    @Override
    public void setAdapter(Adapter adapter) {
        super.setAdapter(adapter);
        if (adapter != null) {
            adapter.registerAdapterDataObserver(mObserver);
        }
        mObserver.onChanged();
    }

    public void hideIfEmpty(View...views) {
        mNonEmptyStateViews = Arrays.asList(views);
    }

    public void showIfEmpty(View...views) {
        mEmptyStateViews = Arrays.asList(views);
    }

}
