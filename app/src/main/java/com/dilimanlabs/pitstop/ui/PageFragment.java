package com.dilimanlabs.pitstop.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.dilimanlabs.pitstop.persistence.ProductParcel;
import com.dilimanlabs.pitstop.ui.common.BaseFragment;
import com.dilimanlabs.pitstop.ui.widgets.OnScrollListener;
import com.dilimanlabs.pitstop.ui.widgets.PageItemsAdapter;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class PageFragment extends BaseFragment {

    private String mDescription;
    private int mOrder;
    private String primaryImage;
    private ArrayList<ProductParcel> mProducts;
    private String mTitle;
    private String mUrl;

    @InjectView(R.id.primaryImage)
    public ImageView mPrimaryImage;

    @InjectView(R.id.recycler_view)
    public RecyclerView mRecyclerView;

    private float mY;

    public static PageFragment newInstance(Page page) {
        final PageFragment pageFragment = new PageFragment();

        final Bundle args = new Bundle();
        args.putString("description", page.description);
        args.putInt("order", page.order);
        args.putString("primaryImage", page.primaryImage);

        final ArrayList<ProductParcel> products = new ArrayList<>(page.products.size());
        for (Product product : page.products) {
            products.add(ProductParcel.create(product.description, product.intents, product.isPromo, product.isVisible, product.name, product.order, product.primaryImage, product.url));
        }
        args.putParcelableArrayList("products", products);
        args.putString("title", page.title);
        args.putString("url", page.url);

        pageFragment.setArguments(args);
        return pageFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mDescription = getArguments().getString("description");
            mOrder = getArguments().getInt("order");
            primaryImage = getArguments().getString("primaryImage");
            mProducts = getArguments().getParcelableArrayList("products");
            mTitle = getArguments().getString("title");
            mUrl = getArguments().getString("url");
        }

        if (savedInstanceState != null) {
            mY = savedInstanceState.getFloat("PRIMARYIMAGE_TRANSLATIONY", 0f);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_page, container, false);
        ButterKnife.inject(this, rootView);

        if (!"".equals(primaryImage)) {
            final String imageUrl =
                    "http://usepitstop.com"
                            + primaryImage
                            + ".png";

            final Picasso picasso = Picasso.with(getActivity());
            picasso.load(imageUrl).fit().centerCrop().into(mPrimaryImage);
        }
        mPrimaryImage.setTranslationY(mY);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(new PageItemsAdapter(getActivity(), mDescription, mProducts));
        mRecyclerView.setOnScrollListener(new OnScrollListener(0, -Math.round(mY*3)) {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                mPrimaryImage.setTranslationY(-getScrollY() / 3);
            }
        });

        return rootView;
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putFloat("PRIMARYIMAGE_TRANSLATIONY", mPrimaryImage.getTranslationY());
    }

    @Override
    public List<Object> getModules() {
        return null;
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

}
