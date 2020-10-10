package com.android.youtube.image.core;

import android.content.Context;
import android.support.annotation.NonNull;


import com.android.youtube.R;
import com.android.youtube.image.progress.ImageProgressManager;
import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.bitmap_recycle.LruBitmapPool;
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory;
import com.bumptech.glide.load.engine.cache.LruResourceCache;
import com.bumptech.glide.load.engine.cache.MemorySizeCalculator;
import com.bumptech.glide.load.engine.executor.GlideExecutor;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.module.AppGlideModule;
import com.bumptech.glide.request.RequestOptions;

import java.io.InputStream;

import static com.bumptech.glide.load.engine.executor.GlideExecutor.newDiskCacheExecutor;

@GlideModule
public class ImageLoaderModule extends AppGlideModule {

    private static final int MEMORY_CACHE_SCREEN = 2; //内存缓存的屏数
    private static final int BITMAP_POOL_SCREEN = 3; //BitmapPool的屏数
    private static final int DISK_CACHE_SIZE = 1024 * 1024 * 100; //设置disk cache size

    @Override
    public void applyOptions(@NonNull Context context, @NonNull GlideBuilder builder) {
        super.applyOptions(context, builder);
        RequestOptions requestOptions = new RequestOptions().format(DecodeFormat.PREFER_RGB_565)
                .diskCacheStrategy(DiskCacheStrategy.ALL); //默认配置memory disk cache

        MemorySizeCalculator calculator = new MemorySizeCalculator.Builder(context)
                .setMemoryCacheScreens(MEMORY_CACHE_SCREEN)
                .setBitmapPoolScreens(BITMAP_POOL_SCREEN)
                .build();


        GlideExecutor.UncaughtThrowableStrategy uncaughtThrowableStrategy = t -> { //设置未捕获的异常策略
        };

        builder.setDefaultRequestOptions(requestOptions);

        builder.setMemoryCache(new LruResourceCache(calculator.getMemoryCacheSize()));

        builder.setDiskCache(new InternalCacheDiskCacheFactory(context, DISK_CACHE_SIZE));

        builder.setBitmapPool(new LruBitmapPool(calculator.getBitmapPoolSize()));

        builder.setDiskCacheExecutor(newDiskCacheExecutor(uncaughtThrowableStrategy));

    }

    @Override
    public void registerComponents(@NonNull Context context, @NonNull Glide glide, @NonNull Registry registry) {
        super.registerComponents(context, glide, registry);
        registry.replace(GlideUrl.class, InputStream.class,
                new OkHttpUrlLoader.Factory(ImageProgressManager.getOkHttpClient()));
    }

    @Override
    public boolean isManifestParsingEnabled() {
        return false;
    }
}
