package com.dilimanlabs.pitstop.ui.explore.widgets;

import android.content.Context;
import android.support.v7.widget.RecyclerView.Adapter;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import com.dilimanlabs.pitstop.R;

import org.lucasr.twowayview.ItemClickSupport;
import org.lucasr.twowayview.ItemSelectionSupport;
import org.lucasr.twowayview.widget.DividerItemDecoration;
import org.lucasr.twowayview.widget.TwoWayView;

public class ExploreList extends FrameLayout{
    private final Context mContext;

    private TwoWayView mRecyclerView;
    private Adapter mAdapter;

    private ItemSelectionSupport mItemSelectionSupport;
    private ItemClickSupport mItemClickSupport;

    public ExploreList(Context context) {
        this(context, null);
    }

    public ExploreList(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ExploreList(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mContext = context;

        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_explore_list, this);
        mRecyclerView = (TwoWayView) getChildAt(0);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLongClickable(true);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getResources().getDrawable(R.drawable.divider)));
    }

    public void setRecyclerViewAdapter(Adapter adapter) {
        mAdapter = adapter;
        mRecyclerView.setAdapter(mAdapter);

        mItemSelectionSupport = ItemSelectionSupport.addTo(mRecyclerView);
        mItemSelectionSupport.setChoiceMode(ItemSelectionSupport.ChoiceMode.SINGLE);

        mItemClickSupport = ItemClickSupport.addTo(mRecyclerView);
    }

    public void onAdapterDataChanged() {
        mItemSelectionSupport.onAdapterDataChanged();
        mAdapter.notifyDataSetChanged();
    }

    public ItemClickSupport getItemClickSupport() {
        return mItemClickSupport;
    }

    public ItemSelectionSupport getItemSelectionSupport() {
        return mItemSelectionSupport;
    }
}
