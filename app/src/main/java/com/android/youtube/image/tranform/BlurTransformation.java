package com.android.youtube.image.tranform;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;


import com.android.youtube.utils.BlurUtils;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.util.Util;

import java.security.MessageDigest;


public class BlurTransformation extends BitmapTransformation {
    private final String id = getClass().getName();

    private static final int MAX_RADIUS = 25;
    private static final int DEFAULT_SAMPLING = 1;

    private Context context;
    private int radius; //模糊半径0～25
    private int sampling; //取样0～25
    private static final int DISK_CACHE_KEY = 10;

    public BlurTransformation(Context mContext) {
        this(mContext, MAX_RADIUS, DEFAULT_SAMPLING);
    }

    public BlurTransformation(Context mContext, int mRadius) {
        this(mContext, mRadius, DEFAULT_SAMPLING);
    }

    public BlurTransformation(Context mContext, int mRadius, int mSampling) {
        this.context = mContext;
        this.radius = mRadius > MAX_RADIUS ? MAX_RADIUS : mRadius;
        this.sampling = mSampling > MAX_RADIUS ? MAX_RADIUS : mSampling;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected Bitmap transform(@NonNull BitmapPool pool, @NonNull Bitmap toTransform, int outWidth, int outHeight) {
        int width = toTransform.getWidth();
        int height = toTransform.getHeight();
        int scaledWidth = width / sampling;
        int scaledHeight = height / sampling;

        Bitmap bitmap = pool.get(scaledWidth, scaledHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.scale(1 / (float) sampling, 1 / (float) sampling);
        Paint paint = new Paint();
        paint.setFlags(Paint.FILTER_BITMAP_FLAG);
        canvas.drawBitmap(toTransform, 0, 0, paint);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            bitmap = BlurUtils.rsBlur(context, bitmap, radius);
        } else {
            bitmap = BlurUtils.blur(bitmap, radius);
        }
        return bitmap;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof BlurTransformation) {
            BlurTransformation other = (BlurTransformation) obj;
            return radius == other.radius && sampling == other.sampling;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Util.hashCode(id.hashCode(), Util.hashCode(radius, Util.hashCode(sampling)));
    }

    @Override
    public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {
        messageDigest.update((id + radius * DISK_CACHE_KEY + sampling).getBytes(CHARSET));
    }
}
