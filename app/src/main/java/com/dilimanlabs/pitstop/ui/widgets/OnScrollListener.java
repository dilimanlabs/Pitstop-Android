package com.dilimanlabs.pitstop.ui.widgets;

import android.support.v7.widget.RecyclerView;

public class OnScrollListener extends RecyclerView.OnScrollListener {
    private int scrollX = 0;
    private int scrollY = 0;

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        scrollX += dx;
        scrollY += dy;
        super.onScrolled(recyclerView, dx, dy);
    }

    public int getScrollX() {
        return scrollX;
    }

    public int getScrollY() {
        return scrollY;
    }
}
