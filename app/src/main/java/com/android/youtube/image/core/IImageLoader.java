package com.android.youtube.image.core;

import android.graphics.Bitmap;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;


import com.bumptech.glide.load.Transformation;


public interface IImageLoader {

    IImageRequestBuilder load(String url);

    IImageRequestBuilder load(String url, int radius);

    IImageRequestBuilder load(String url, int radius, Transformation<Bitmap> transformation);

    IImageRequestBuilder loadCircle(String url);

    IImageRequestBuilder loadDrawable(@DrawableRes int resId);

    IImageRequestBuilder loadDrawable(@DrawableRes int resId, @NonNull Transformation<Bitmap> transformation);

    void clear();

}
