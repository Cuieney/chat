package com.android.youtube.image.core;

import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.widget.ImageView;


import com.android.youtube.image.progress.OnProgressListener;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.File;
import java.util.concurrent.ExecutionException;


public interface IImageRequestBuilder {

    IImageRequestBuilder centerCrop();

    IImageRequestBuilder fitCenter();

    IImageRequestBuilder centerInside();

    IImageRequestBuilder diskCacheStrategy(@NonNull DiskCacheStrategy strategy);

    IImageRequestBuilder placeholder(@DrawableRes int resId);

    IImageRequestBuilder error(@DrawableRes int resId);

    IImageRequestBuilder listener(OnProgressListener listener);

    void preload();

    File download() throws ExecutionException, InterruptedException;

    void into(ImageView view);
}
