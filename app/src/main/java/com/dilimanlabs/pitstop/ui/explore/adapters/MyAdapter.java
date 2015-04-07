package com.dilimanlabs.pitstop.ui.explore.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.TextAppearanceSpan;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dilimanlabs.pitstop.R;
import com.dilimanlabs.pitstop.persistence.Establishment;
import com.dilimanlabs.pitstop.ui.explore.widgets.EllipsizeLineSpan;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private final Context mContext;

    private final ArrayList<String> mDataset;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView mTextView;

        public ViewHolder(ViewGroup v) {
            super(v);
            mTextView = (TextView) v.getChildAt(1);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MyAdapter(Context context, ArrayList<String> dataset) {
        mContext = context;
        mDataset = dataset;
        setHasStableIds(true);
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        ViewGroup v = (ViewGroup) LayoutInflater.from(parent.getContext()).inflate(R.layout.list_card, parent, false);
        // set the view's size, margins, paddings and layout parameters

        return new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        final Establishment est = Establishment.getEstablishmentByUrl(mDataset.get(position));
        final SpannableStringBuilder builder = new SpannableStringBuilder();

        builder.append(est.name);
        builder.append("\n");
        int point = builder.length();
        builder.setSpan(new TextAppearanceSpan(mContext,
                R.style.ListPrimaryTextAppearance), 0, point, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.setSpan(new EllipsizeLineSpan(), 0, point, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);


        builder.append(est.location.address);
        builder.setSpan(new TextAppearanceSpan(mContext,
                R.style.ListSecondaryTextAppearance), point, builder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.setSpan(new EllipsizeLineSpan(), point, builder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        holder.mTextView.setText(builder);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    @Override
    public long getItemId(int position) {
        return Establishment.getEstablishmentByUrl(mDataset.get(position)).getId();
    }

    public String getItem(int position) {
        return mDataset.get(position);
    }
}
