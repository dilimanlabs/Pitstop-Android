package com.dilimanlabs.pitstop.picasso;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.squareup.picasso.Transformation;

public class MarkerTransform implements Transformation {
    int x, y;

    @Override
    public Bitmap transform(Bitmap source) {
        int size = Math.min(source.getWidth(), source.getHeight());

        this.x = (source.getWidth() - size) / 2;
        this.y = (source.getHeight() - size) / 2;

        int border = 4;
        int shadow = 4;

        Bitmap.Config config = source.getConfig() != null ? source.getConfig() : Bitmap.Config.ARGB_8888;
        Bitmap bitmap = Bitmap.createBitmap(size + 2*(border + shadow), size + 2*(border + shadow), config);

        Canvas canvas = new Canvas(bitmap);

        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setShadowLayer(shadow, 0, 0, Color.BLACK);
        paint.setAntiAlias(true);
        canvas.drawCircle((size + 2*(border + shadow))/2, (size + 2*(border + shadow))/2, (size + border)/2, paint);

        canvas.drawBitmap(source, border + shadow, border + shadow, null);

        source.recycle();
        return bitmap;
    }

    @Override
    public String key() {
        return "marker(x=" + x + ",y=" + y + ")";
    }
}
