package com.dilimanlabs.pitstop.ui.widgets;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.devspark.robototextview.util.RobotoTextViewUtils;
import com.devspark.robototextview.util.RobotoTypefaceManager;
import com.dilimanlabs.pitstop.R;
import com.dilimanlabs.pitstop.persistence.ProductParcel;
import com.dilimanlabs.pitstop.ui.ImageActivity;
import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class PageItemsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static class HeaderViewHolder extends RecyclerView.ViewHolder {
        @InjectView(R.id.description)
        public TextView description;

        public HeaderViewHolder(View v) {
            super(v);
            ButterKnife.inject(this, v);
        }
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        @InjectView(R.id.li)
        public LinearLayout li;

        @InjectView(R.id.name)
        public TextView name;

        @InjectView(R.id.primaryImage)
        public ImageView primaryImage;

        @InjectView(R.id.description)
        public TextView description;

        public ProductViewHolder(View v) {
            super(v);
            ButterKnife.inject(this, v);
        }
    }

    private Context mContext;

    private String mDescription;
    private List<ProductParcel> mProducts;

    private boolean mHasHeader = false;

    public PageItemsAdapter(Context context, String description, List<ProductParcel> products) {
        mContext = context;

        mDescription = description;
        if (!TextUtils.isEmpty(mDescription)) { // TODO checks if header should be displayed
            mHasHeader = true;
        }

        if (products != null) {
            Collections.sort(products, new ProductsComparator());
        }
        mProducts = products;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case 0: {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.header_card, parent, false);
                return new HeaderViewHolder(v);
                // set the view's size, margins, paddings and layout parameters
            }
            case 1: {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_card, parent, false);
                return new ProductViewHolder(v);
                // set the view's size, margins, paddings and layout parameters
            }
            default: {
                return null;
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (mHasHeader) {
            if (position == 0) {
                return 0;
            } else {
                return 1;
            }
        } else {
            return 1;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case 0: {
                ((HeaderViewHolder) holder).description.setText(mDescription);

                break;
            }
            case 1: {
                final int adjustedPosition = position - (mHasHeader ? 1 : 0);

                ProductParcel product = mProducts.get(adjustedPosition);
                /*for (Product prod : mProducts) {
                    if (prod.order == adjustedPosition) {
                        product = prod;
                    }
                }

                if (product == null) {
                    product = mProducts.get(adjustedPosition);
                }*/

                if (!TextUtils.isEmpty(product.getPrimaryImage())) {
                    final String imageUrl =
                            "http://pitstop.dilimanlabs.com/api"
                                    + product.getPrimaryImage()
                                    + ".png";

                    final Picasso picasso = Picasso.with(mContext);
                    picasso.load(imageUrl).fit().centerCrop().into(((ProductViewHolder) holder).primaryImage);
                }
                final String productPrimaryImage = product.getPrimaryImage();
                ((ProductViewHolder) holder).primaryImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final Intent intent = new Intent(v.getContext(), ImageActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);//| Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                        final Bundle data = new Bundle();
                        data.putString("IMG_URL", productPrimaryImage);
                        intent.putExtra("DATA", data);

                        v.getContext().startActivity(intent);
                    }
                });

                ((ProductViewHolder) holder).name.setText(product.getName());

                ((ProductViewHolder) holder).description.setText(product.getDescription());

                for (final com.dilimanlabs.pitstop.persistence.Intent intent : product.getIntents()) {
                    final TextView btn = new TextView(mContext);

                    Typeface typeface = RobotoTypefaceManager.obtainTypeface(
                            mContext,
                            RobotoTypefaceManager.Typeface.ROBOTO_MEDIUM);
                    RobotoTextViewUtils.setTypeface(btn, typeface);
                    btn.setIncludeFontPadding(false);
                    btn.setTextSize(TypedValue.COMPLEX_UNIT_SP,14);
                    btn.setTextColor(mContext.getResources().getColor(R.color.light_blue_500));

                    final LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    layoutParams.setMargins(0, 0, 0, 0);
                    btn.setPadding(
                            0,//(int) mContext.getResources().getDimension(R.dimen.dp16),
                            (int) mContext.getResources().getDimension(R.dimen.dp16),
                            (int) mContext.getResources().getDimension(R.dimen.dp16),
                            0);
                    layoutParams.gravity = Gravity.RIGHT;
                    btn.setLayoutParams(layoutParams);

                    btn.setText("DIAL");
                    btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if ("ACTION_DIAL".equals(intent.action)) {
                                final Intent sysIntent = new Intent(android.content.Intent.ACTION_DIAL);
                                sysIntent.setData(Uri.parse("tel:" + intent.value));

                                if (sysIntent.resolveActivity(mContext.getPackageManager()) != null) {
                                    mContext.startActivity(sysIntent);
                                }
                            }
                        }
                    });

                    ((ProductViewHolder) holder).li.addView(btn);
                }

                break;
            }
        }
    }

    @Override
    public int getItemCount() {
        return (mHasHeader ? 1 : 0) + (mProducts == null ? 0 : mProducts.size());
    }

    public static class ProductsComparator implements Comparator<ProductParcel> {
        @Override
        public int compare(ProductParcel lhs, ProductParcel rhs) {
            return Integer.valueOf(lhs.getOrder()).compareTo(rhs.getOrder());
        }
    }
}
