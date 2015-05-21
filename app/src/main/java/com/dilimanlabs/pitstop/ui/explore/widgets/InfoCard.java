package com.dilimanlabs.pitstop.ui.explore.widgets;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.dilimanlabs.pitstop.R;
import com.dilimanlabs.pitstop.persistence.Business;
import com.dilimanlabs.pitstop.persistence.Establishment;
import com.dilimanlabs.pitstop.picasso.CircleTransform;
import com.dilimanlabs.pitstop.ui.DetailsActivity;
import com.facebook.rebound.SimpleSpringListener;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringConfig;
import com.facebook.rebound.SpringSystem;
import com.squareup.picasso.Picasso;

public class InfoCard extends CardView {

    private ImageView mEstImage;
    private TextView mEstName;
    private TextView mBrandName;
    private TextView mEstDetails;
    private ImageButton mBtnMore;

    private Spring mInfoCardSpring, mBtnMoreSpring;

    public InfoCard(Context context) {
        this(context, null);
    }

    public InfoCard(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public InfoCard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        inflate(getContext(), R.layout.view_infocard, this);
        mEstImage = (ImageView) findViewById(R.id.estImage);
        mEstName = (TextView) findViewById(R.id.estName);
        mBrandName = (TextView) findViewById(R.id.brandName);
        mEstDetails = (TextView) findViewById(R.id.estDetails);
        mBtnMore = (ImageButton) findViewById(R.id.btnMore);

        setRadius(getResources().getDimension(R.dimen.dp4));

        SpringSystem ss = SpringSystem.create();
        mInfoCardSpring = ss.createSpring().setSpringConfig(SpringConfig.fromOrigamiTensionAndFriction(40, 5));
        mInfoCardSpring.addListener(new SimpleSpringListener() {
            @Override
            public void onSpringUpdate(Spring spring) {
                final float currentValue = (float) spring.getCurrentValue();

                setScaleX(1f + currentValue);
                setScaleY(1f + currentValue);
            }
        });

        mBtnMoreSpring = ss.createSpring().setSpringConfig(SpringConfig.fromOrigamiTensionAndFriction(40, 5));
        mBtnMoreSpring.addListener(new SimpleSpringListener() {
            @Override
            public void onSpringUpdate(Spring spring) {
                final float currentValue = (float) spring.getCurrentValue();

                mBtnMore.setScaleX(1f + currentValue);
                mBtnMore.setScaleY(1f + currentValue);
            }
        });

        mBtnMore.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (v.getId() == R.id.btnMore) {
                    int action = event.getAction();
                    switch (action) {
                        case MotionEvent.ACTION_DOWN:
                            mBtnMoreSpring.setEndValue(+0.05);
                            break;
                        case MotionEvent.ACTION_UP:
                            mBtnMoreSpring.setEndValue(0);
                            break;
                    }
                }

                return false;
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mInfoCardSpring.setEndValue(+0.05);
                break;
            case MotionEvent.ACTION_UP:
                mInfoCardSpring.setEndValue(0);
                break;
        }

        super.onTouchEvent(event);
        return true;
    }


    public void display(final Establishment est, final Business biz) {
        clear();

        final String estName = est.name;
        final String estAddress = est.location.address;

        if (!"".equals(est.primaryImage)) {
            final String imageUrl =
                    "http://usepitstop.com"
                            + est.primaryImage
                            + ".png";
                            //+ "?"
                            //+ "height=" + (int) getResources().getDimension(R.dimen.dp40)
                            //+ "&"
                            //+ "width=" + (int) getResources().getDimension(R.dimen.dp40);

            final Picasso picasso = Picasso.with(getContext());
            picasso.load(imageUrl).into(mEstImage);
            //picasso.setIndicatorsEnabled(true);
            //.placeholder(R.color.light_blue_500)
            //.error(R.color.light_blue_100)
        }

        String bizName = "";
        if (biz != null) {
            bizName = biz.name;
        }

        mEstName.setText(estName);
        mBrandName.setText(bizName);
        mEstDetails.setText(estAddress);

        mBtnMore.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent = new Intent(getContext(), DetailsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);//| Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                final Bundle data = new Bundle();
                data.putString("EST_URL", est.url);
                intent.putExtra("DATA", data);

                getContext().startActivity(intent);
            }
        });
    }

    public void clear() {
        mEstImage.setImageDrawable(null);
        mBrandName.setText("");
        mEstDetails.setText("");
        mBtnMore.setOnClickListener(null);
    }
}
