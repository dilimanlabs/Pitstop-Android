package com.dilimanlabs.pitstop.ui.widgets;

import android.content.Context;
import android.net.Uri;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.devspark.robototextview.widget.RobotoTextView;
import com.dilimanlabs.pitstop.R;
import com.dilimanlabs.pitstop.picasso.CircleTransform;
import com.squareup.picasso.Picasso;

public class NavHeaderView extends RelativeLayout {
    private ImageView mImage;
    private RobotoTextView mName;
    private RobotoTextView mId;

    public NavHeaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.view_navheader, this);
        mImage = (ImageView) findViewById(R.id.image);
        mName = (RobotoTextView) findViewById(R.id.name);
        mId = (RobotoTextView) findViewById(R.id.id);

        setBackgroundColor(getResources().getColor(R.color.light_blue_500));
    }

    public void set(String url, String name, String id) {
        setImage(url);
        setName(name);
        setId(id);
    }

    private void setImage(String url) {
        Picasso.with(getContext())
                .load(url)
                .error(R.drawable.ic_person_light)
                .resize((int) getResources().getDimension(R.dimen.dp56),
                        (int) getResources().getDimension(R.dimen.dp56))
                .centerCrop()
                .transform(new CircleTransform())
                .into(mImage);
    }

    public void setImage(Uri uri) {
        Picasso.with(getContext())
                .load(uri)
                .error(R.drawable.ic_person_light)
                .resize((int) getResources().getDimension(R.dimen.dp56),
                        (int) getResources().getDimension(R.dimen.dp56))
                .centerCrop()
                .transform(new CircleTransform())
                .into(mImage);
    }

    private void setName(String name) {
        mName.setText(name);
    }

    private void setId(String id) {
        mId.setText(id);
    }
}
