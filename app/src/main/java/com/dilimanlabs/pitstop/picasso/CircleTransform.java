package com.dilimanlabs.pitstop.picasso;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.media.ThumbnailUtils;

import com.squareup.picasso.Transformation;

public class CircleTransform implements Transformation {
    int x, y;

    @Override
    public Bitmap transform(Bitmap source) {
        int size = Math.max(source.getWidth(), source.getHeight());

        this.x = (source.getWidth() - size) / 2;
        this.y = (source.getHeight() - size) / 2;

        //Bitmap squaredBitmap = Bitmap.createBitmap(source, x, y, size, size);
        Bitmap squaredBitmap = ThumbnailUtils.extractThumbnail(source, size, size);

        if (squaredBitmap != source) {
            source.recycle();
        }

        // GIF files generate null configs, assume ARGB_8888
        Bitmap.Config config = source.getConfig() != null ? source.getConfig() : Bitmap.Config.ARGB_8888;
        Bitmap bitmap = Bitmap.createBitmap(size, size, config);

        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        BitmapShader shader = new BitmapShader(squaredBitmap,
                BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
        paint.setShader(shader);
        paint.setAntiAlias(true);

        float r = size / 2f;
        canvas.drawCircle(r, r, r, paint);

        squaredBitmap.recycle();
        return bitmap;
    }

    @Override
    public String key() {
        return "circle(x=" + x + ",y=" + y + ")";
    }
}
