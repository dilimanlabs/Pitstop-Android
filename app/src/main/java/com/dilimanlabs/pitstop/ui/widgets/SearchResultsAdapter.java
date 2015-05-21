package com.dilimanlabs.pitstop.ui.widgets;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dilimanlabs.pitstop.R;
import com.dilimanlabs.pitstop.persistence.Contact;
import com.dilimanlabs.pitstop.persistence.Establishment;
import com.dilimanlabs.pitstop.persistence.Location;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class SearchResultsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static class EstablishmentViewHolder extends RecyclerView.ViewHolder {
        @InjectView(R.id.primaryImage)
        public ImageView primaryImage;

        @InjectView(R.id.name)
        public TextView name;

        @InjectView(R.id.description)
        public TextView description;

        public EstablishmentViewHolder(View v) {
            super(v);
            ButterKnife.inject(this, v);
        }
    }

    private Context mContext;
    private List<Establishment> mEstablishments;

    public SearchResultsAdapter(Context context, List<Establishment> establishments) {
        mContext = context;
        mEstablishments = establishments;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case 0: {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.establishment_card, parent, false);
                return new EstablishmentViewHolder(v);
                // set the view's size, margins, paddings and layout parameters
            }
            default: {
                return null;
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case 0: {
                final Establishment establishment = mEstablishments.get(position);

                if (!TextUtils.isEmpty(establishment.primaryImage)) {
                    final String imageUrl =
                            "http://usepitstop.com"
                                    + establishment.primaryImage
                                    + ".png";

                    final Picasso picasso = Picasso.with(mContext);
                    picasso.load(imageUrl).fit().centerCrop().into(((EstablishmentViewHolder) holder).primaryImage);
                }

                ((EstablishmentViewHolder) holder).name.setText(establishment.name);

                ((EstablishmentViewHolder) holder).description.setText(establishment.location.address);

                ((EstablishmentViewHolder) holder).itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Location location = establishment.location;
                        location.save();
                        Contact contact = establishment.contact;
                        contact.save();

                        new Establishment(establishment.categories, contact, location, establishment.name, establishment.primaryImage, establishment.url).save();

                        final android.content.Intent resultIntent = new android.content.Intent();
                        resultIntent.putExtra("ESTABLISHMENT_URL", establishment.url);
                        ((AppCompatActivity) mContext).setResult(Activity.RESULT_OK, resultIntent);
                        ((AppCompatActivity) mContext).finish();
                    }
                });

                break;
            }
        }
    }

    @Override
    public int getItemCount() {
        return mEstablishments.size();
    }
}
