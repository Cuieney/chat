package com.android.youtube.image.tranform;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.NonNull;


import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.util.Util;

import java.security.MessageDigest;

public class RadiusTransformation extends BitmapTransformation {
    private final String id = getClass().getName();
    private static final float ROUND_HALF_DICIMAL = 0.5f;

    private int radius;

    public RadiusTransformation(Context context, int mRadius) {
        this.radius = dpToPxInt(context, mRadius);
    }

    public float dpToPx(Context context, float dp) {
        if (context == null) {
            return -1;
        }
        return dp * context.getResources().getDisplayMetrics().density;
    }

    public int dpToPxInt(Context context, float dp) {
        return (int) (dpToPx(context, dp) + ROUND_HALF_DICIMAL);
    }
    @Override
    protected Bitmap transform(@NonNull BitmapPool pool, @NonNull Bitmap toTransform, int outWidth, int outHeight) {
        int width = toTransform.getWidth();
        int height = toTransform.getHeight();

        Bitmap bitmap = pool.get(width, height, Bitmap.Config.ARGB_8888);
        bitmap.setHasAlpha(true);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setShader(new BitmapShader(toTransform, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP));
        canvas.drawRoundRect(new RectF(0, 0, width, height), radius, radius, paint);
        return bitmap;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof RadiusTransformation) {
            RadiusTransformation other = (RadiusTransformation) obj;
            return radius == other.radius;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Util.hashCode(id.hashCode(), Util.hashCode(radius));
    }

    @Override
    public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {
        messageDigest.update((id + radius).getBytes(CHARSET));
    }

}
