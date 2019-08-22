package com.songu.shadow.drawing.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;

import com.nostra13.universalimageloader.cache.disc.impl.TotalSizeLimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.utils.StorageUtils;

import java.io.File;

public class SyncImageLoader
{
    ImageLoaderConfiguration config;
    ImageLoader imageLoader;
    DisplayImageOptions  options;

    public SyncImageLoader(Context paramContext)
    {
        File localFile = StorageUtils.getCacheDirectory(paramContext);

        this.config = new ImageLoaderConfiguration.Builder(paramContext).memoryCacheExtraOptions(480, 800).discCacheExtraOptions(480, 800, Bitmap.CompressFormat.PNG, 80, null).threadPoolSize(5).threadPriority(4).tasksProcessingOrder(QueueProcessingType.FIFO).denyCacheImageMultipleSizesInMemory().memoryCache(new LruMemoryCache(10485760)).memoryCacheSize(10485760).memoryCacheSizePercentage(13).discCache(new TotalSizeLimitedDiscCache(localFile, 20971520)).discCacheFileNameGenerator(new HashCodeFileNameGenerator()).imageDownloader(new BaseImageDownloader(paramContext)).defaultDisplayImageOptions(DisplayImageOptions.createSimple()).writeDebugLogs().build();
        this.options = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisc(true).bitmapConfig(Bitmap.Config.RGB_565).build();
        this.imageLoader = ImageLoader.getInstance();
        this.imageLoader.init(this.config);
    }

    public void displayImage(final ImageView paramImageView, String paramString)
    {
        imageLoader.clearMemoryCache();
        imageLoader.clearDiscCache();
        imageLoader.displayImage(paramString, paramImageView, this.options, new SimpleImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {

            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {

            }
        });
    }
}