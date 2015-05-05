package com.dilimanlabs.pitstop.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;

import com.dilimanlabs.pitstop.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import butterknife.ButterKnife;
import butterknife.InjectView;
import uk.co.senab.photoview.PhotoViewAttacher;

public class ImageActivity extends AppCompatActivity {
    @InjectView(R.id.toolbar)
    public Toolbar mToolbar;

    @InjectView(R.id.image)
    public ImageView mImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_image);
        ButterKnife.inject(this);

        final Bundle data = getIntent().getBundleExtra("DATA");
        final String imgUrl = data.getString("IMG_URL");

        if(imgUrl == null || "".equals(imgUrl)) {
            finish();
        }


        final String imageUrl =
                "http://pitstop.dilimanlabs.com/api"
                        + imgUrl
                        + ".png";

        final Picasso picasso = Picasso.with(this);
        picasso.load(imageUrl).into(mImage, new Callback() {
            @Override
            public void onSuccess() {
                final PhotoViewAttacher mAttacher = new PhotoViewAttacher(mImage);
            }

            @Override
            public void onError() {

            }
        });


        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
        // These need to be set after the call to setSupportActionBar()
        //mToolbar.setNavigationIcon(R.drawable.ic_stub_circle);
        //mToolbar.setLogo();
        //mToolbar.setTitle("");
        //mToolbar.setSubtitle("");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                finish();

                return true;
            } default: {

                return super.onOptionsItemSelected(item);
            }
        }
    }
}
