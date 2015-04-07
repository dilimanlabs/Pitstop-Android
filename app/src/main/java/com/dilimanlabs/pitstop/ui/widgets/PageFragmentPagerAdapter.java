package com.dilimanlabs.pitstop.ui.widgets;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.dilimanlabs.pitstop.persistence.Page;
import com.dilimanlabs.pitstop.ui.PageFragment;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class PageFragmentPagerAdapter extends FragmentPagerAdapter {
    private List<Page> mPages;

    public PageFragmentPagerAdapter(FragmentManager fm, List<Page> pages) {
        super(fm);
        if (pages != null) {
            Collections.sort(pages, new PagesComparator());
        }
        mPages = pages;
    }

    @Override
    public Fragment getItem(int position) {
        PageFragment pageFragment = PageFragment.newInstance(mPages.get(position));

        /*for (Page page : mPages) {
            if (page.order == position) {
                pageFragment = PageFragment.newInstance(page);
            }
        }

        if (pageFragment == null) {
            pageFragment = PageFragment.newInstance(mPages.get(position));
        }*/

        return pageFragment;
    }

    @Override
    public int getCount() {
        return mPages.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        for (Page page : mPages) {
            if (page.order == position) {
                return page.title;
            }
        }

        return "";
    }

    public static class PagesComparator implements Comparator<Page> {
        @Override
        public int compare(Page lhs, Page rhs) {
            return Integer.valueOf(lhs.order).compareTo(rhs.order);
        }
    }
}