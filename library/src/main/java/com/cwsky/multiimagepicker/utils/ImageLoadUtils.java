package com.cwsky.multiimagepicker.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

/**
 * @desc :图片加载
 */
public class ImageLoadUtils {

    private static ImageLoadUtils imageLoadUtils;
    private Context context;
    private String url;
    private int radius;
    private int placeholderId;
    private ImageView.ScaleType scaleType;


    public static ImageLoadUtils getInstance() {
        if (imageLoadUtils == null) {
            imageLoadUtils = new ImageLoadUtils();
        }
        return imageLoadUtils;
    }

    public ImageLoadUtils setContext(Context context) {
        this.context = context;
        return imageLoadUtils;
    }

    public ImageLoadUtils setUrl(String url) {
        this.url = url;
        return imageLoadUtils;
    }

    public ImageLoadUtils setRadius(int radius) {
        this.radius = radius;
        return imageLoadUtils;
    }

    public ImageLoadUtils setPlaceholderId(int placeholderId) {
        this.placeholderId = placeholderId;
        return imageLoadUtils;
    }

    public ImageLoadUtils setScaleType(ImageView.ScaleType scaleType) {
        this.scaleType = scaleType;
        return imageLoadUtils;
    }

    public void into(ImageView imageView) {
        if (context == null) {
            return;
        }
        RequestOptions options = new RequestOptions().centerInside();
        if (scaleType == ImageView.ScaleType.CENTER_CROP) {
            if (radius > 0) {
                options = options.transform(new CenterCrop(), new RoundedCorners(SizeUtils.dp2px(radius)));
            } else {
                options = options.transform(new CenterCrop());
            }
        } else if (scaleType == ImageView.ScaleType.CENTER_INSIDE) {
            options = options.centerInside();
        } else {
            options = options.fitCenter();
        }
        Glide.with(context).load(url).apply(options).into(imageView);
    }

    /**
     * 加载图片
     * @param imageView
     * @param showOriginal 是否显示原图
     * @param callback
     */
    public void into(ImageView imageView, boolean showOriginal, OnImageLoadCallback callback) {
        if (context == null) {
            return;
        }
        RequestOptions options = new RequestOptions().centerInside();
        if (scaleType == ImageView.ScaleType.CENTER_CROP) {
            if (radius > 0) {
                options = options.transform(new CenterCrop(), new RoundedCorners(SizeUtils.dp2px(radius)));
            } else {
                options = options.transform(new CenterCrop());
            }
        } else if (scaleType == ImageView.ScaleType.CENTER_INSIDE) {
            options = options.centerInside();
        } else {
            options = options.fitCenter();
        }
        if(showOriginal){
            //是否显示原图
            options.override(Target.SIZE_ORIGINAL,Target.SIZE_ORIGINAL);
            options.format(DecodeFormat.PREFER_RGB_565);
        }
        options.skipMemoryCache(true);
        options.diskCacheStrategy(DiskCacheStrategy.RESOURCE);
        Glide.with(context).asBitmap().load(url).apply(options).listener(new RequestListener<Bitmap>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                if (callback != null) {
                    callback.onResourceReady(resource, url);
                }
                return false;
            }
        }).into(imageView);
    }

    public interface OnImageLoadCallback {
        void onResourceReady(Bitmap resource, String url);
    }

}
