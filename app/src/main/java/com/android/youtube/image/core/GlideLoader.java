package com.android.youtube.image.core;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.widget.ImageView;


import com.android.youtube.image.progress.ImageProgressManager;
import com.android.youtube.image.progress.OnProgressListener;
import com.android.youtube.image.tranform.CircleTransformation;
import com.android.youtube.image.tranform.RadiusTransformation;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import java.io.File;
import java.util.concurrent.ExecutionException;

import io.reactivex.Maybe;
import io.reactivex.MaybeOnSubscribe;
import io.reactivex.schedulers.Schedulers;


public class GlideLoader implements IImageLoader {
    private final Context mContext;

    public GlideLoader(Context context) {
        this.mContext = context;
    }

    @Override
    public IImageRequestBuilder load(String url) {
        return load(url, 0);
    }

    @Override
    public IImageRequestBuilder load(String url, int radius) {
        return load(url, radius, null);
    }

    @Override
    public IImageRequestBuilder load(String url, int radius, Transformation<Bitmap> transformation) {
        ImageRequestBuilder requestManager = new ImageRequestBuilder(mContext, url);
        requestManager.setRadius(radius);
        requestManager.setTransformation(transformation);
        return requestManager;
    }

    @Override
    public IImageRequestBuilder loadCircle(String url) {
        ImageRequestBuilder requestManager = new ImageRequestBuilder(mContext, url);
        requestManager.setTransformation(new CircleTransformation());
        return requestManager;
    }

    @Override
    public IImageRequestBuilder loadDrawable(@DrawableRes int resId) {
        return loadDrawable(resId, null);
    }

    @Override
    public IImageRequestBuilder loadDrawable(@DrawableRes int resId, Transformation<Bitmap> transformation) {
        ImageRequestBuilder requestManager = new ImageRequestBuilder(mContext, resId);
        requestManager.setTransformation(transformation);
        return requestManager;
    }

    @Override
    public void clear() {
        Maybe.create((MaybeOnSubscribe<String>) emitter -> {
            GlideApp.get(mContext).clearMemory();
            GlideApp.get(mContext).clearDiskCache();
            emitter.onComplete();
        }).subscribeOn(Schedulers.io())
                .ignoreElement()
                .onErrorComplete()
                .subscribe();
    }

    final class ImageRequestBuilder implements IImageRequestBuilder {
        private RequestBuilder<Drawable> mRequestBuilder;
        private RequestOptions mRequestOptions;
        private int mRadius;
        private Context mContext;
        private Transformation<Bitmap> mTransformation;
        private String mUrl;

        private ImageRequestBuilder(Context context, Object object) {
            this.mContext = context;
            if (object instanceof String) {
                mUrl = ((String) object);
            }
            mRequestBuilder = GlideApp.with(mContext).load(object);
            mRequestOptions = new RequestOptions();
            mRequestOptions = mRequestOptions
                    .format(DecodeFormat.PREFER_RGB_565)
                    .diskCacheStrategy(DiskCacheStrategy.ALL);
        }

        private void setRadius(int radius) {
            this.mRadius = radius;
            mTransformation = new RadiusTransformation(mContext, mRadius);
            mRequestBuilder = mRequestBuilder.transform(mTransformation);
        }

        private void setTransformation(Transformation<Bitmap> transformation) {
            if (transformation == null) {
                return;
            }
            this.mTransformation = transformation;
            mRequestBuilder = mRequestBuilder.transform(mTransformation);
        }

        @Override
        public IImageRequestBuilder centerCrop() {
            mRequestBuilder = mRequestBuilder.apply(mRequestOptions.centerCrop());
            return this;
        }

        @Override
        public IImageRequestBuilder fitCenter() {
            mRequestBuilder = mRequestBuilder.apply(mRequestOptions.fitCenter());
            return this;
        }

        @Override
        public IImageRequestBuilder centerInside() {
            mRequestBuilder = mRequestBuilder.apply(mRequestOptions.centerInside());
            return this;
        }

        @Override
        public IImageRequestBuilder diskCacheStrategy(DiskCacheStrategy strategy) {
            mRequestBuilder = mRequestBuilder.diskCacheStrategy(strategy);
            return this;
        }

        @Override
        public IImageRequestBuilder placeholder(@DrawableRes int resId) {
            mRequestBuilder = mRequestBuilder.placeholder(resId);
            return this;
        }

        @Override
        public IImageRequestBuilder error(@DrawableRes int resId) {
            mRequestBuilder = mRequestBuilder.error(resId);
            return this;
        }


        @Override
        public IImageRequestBuilder listener(OnProgressListener listener) {
            ImageProgressManager.addListener(mUrl, listener);
            return this;
        }

        @Override
        public void preload() {
            mRequestBuilder
                    .apply(mRequestOptions)
                    .preload(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL);
        }

        @Override
        public File download() throws ExecutionException, InterruptedException {
            return mRequestBuilder.apply(mRequestOptions).downloadOnly(
                    Target.SIZE_ORIGINAL,
                    Target.SIZE_ORIGINAL).get();
        }

        @Override
        public void into(ImageView view) {
            mRequestBuilder
                    .apply(mRequestOptions)
                    .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                    .into(view);
        }
    }

}
