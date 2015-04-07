package com.dilimanlabs.pitstop.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.dilimanlabs.pitstop.R;
import com.dilimanlabs.pitstop.persistence.Page;
import com.dilimanlabs.pitstop.persistence.Product;
import com.dilimanlabs.pitstop.ui.widgets.OnScrollListener;
import com.dilimanlabs.pitstop.ui.widgets.PageItemsAdapter;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class PageFragment extends Fragment {

    private String description;
    private int order;
    private String primaryImage;
    private ArrayList<Product> products;
    private String title;
    private String url;

    @InjectView(R.id.primaryImage)
    public ImageView mPrimaryImage;

    @InjectView(R.id.recycler_view)
    public RecyclerView mRecyclerView;

    public static PageFragment newInstance(Page page) {
        PageFragment pageFragment = new PageFragment();

        Bundle args = new Bundle();
        args.putString("description", page.description);
        args.putInt("order", page.order);
        args.putString("primaryImage", page.primaryImage);

        ArrayList<Product> products = new ArrayList<>(page.products.size());
        products.addAll(page.products);
        args.putParcelableArrayList("products", products);

        args.putString("title", page.title);
        args.putString("url", page.url);
        pageFragment.setArguments(args);

        return pageFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        description = getArguments().getString("description");
        order = getArguments().getInt("order");
        primaryImage = getArguments().getString("primaryImage");
        products = getArguments().getParcelableArrayList("products");
        title = getArguments().getString("title");
        url = getArguments().getString("url");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_page, container, false);
        ButterKnife.inject(this, rootView);

        if (!"".equals(primaryImage)) {
            final String imageUrl =
                    "http://pitstop.dilimanlabs.com/api"
                            + primaryImage
                            + ".png";

            final Picasso picasso = Picasso.with(getActivity());
            picasso.load(imageUrl).fit().centerCrop().into(mPrimaryImage);
        }

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(new PageItemsAdapter(getActivity(), description, products));

        final float neg_h = -getResources().getDimension(R.dimen.actionBarSizex4);
        mRecyclerView.setOnScrollListener(new OnScrollListener(){
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                mPrimaryImage.setTranslationY(Math.max(neg_h, -getScrollY()/3));
            }
        });

        return rootView;
    }


}
