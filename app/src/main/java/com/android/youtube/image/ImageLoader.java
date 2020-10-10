package com.android.youtube.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.DrawableRes;

import com.android.youtube.image.core.GlideLoader;
import com.android.youtube.image.core.IImageLoader;
import com.android.youtube.image.core.IImageRequestBuilder;
import com.bumptech.glide.load.Transformation;

public final class ImageLoader implements IImageLoader {
    private IImageLoader mImageLoaderProxy;

    private static volatile ImageLoader mImageLoader;

    public static ImageLoader getInstance() {
        if (mImageLoader == null) {
            synchronized (ImageLoader.class) {
                if (mImageLoader == null) {
                    mImageLoader = new ImageLoader();
                }
            }
        }
        return mImageLoader;
    }

    private ImageLoader() {

    }

    public void init(Context context) {
        mImageLoaderProxy = new GlideLoader(context);
    }

    @Override
    public IImageRequestBuilder load(String url) {
        return mImageLoaderProxy.load(url);
    }

    @Override
    public IImageRequestBuilder load(String url, int radius) {
        return mImageLoaderProxy.load(url, radius);
    }


    @Override
    public IImageRequestBuilder load(String url, int radius, Transformation<Bitmap> transformation) {
        return mImageLoaderProxy.load(url, radius, transformation);
    }

    @Override
    public IImageRequestBuilder loadCircle(String url) {
        return mImageLoaderProxy.loadCircle(url);
    }

    @Override
    public IImageRequestBuilder loadDrawable(@DrawableRes int resId) {
        return mImageLoaderProxy.loadDrawable(resId);
    }

    @Override
    public IImageRequestBuilder loadDrawable(@DrawableRes int resId, Transformation<Bitmap> transformation) {
        return mImageLoaderProxy.loadDrawable(resId, transformation);
    }

    @Override
    public void clear() {
        mImageLoaderProxy.clear();
    }


}
