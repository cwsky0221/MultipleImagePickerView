package com.cwsky.multiimagepicker.utils;

import android.content.Context;
import android.text.TextUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import top.zibin.luban.CompressionPredicate;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

/**
 * 图片压缩
 * Create by wei.chen
 * on 2021/6/21
 */
public class ImageCompressUtils {
    /**
     * 异步压缩
     * @param context
     * @param ignoreSize 不压缩的阈值，单位为K
     * @param path 单个文件路径
     */
    public static void compress(Context context, int ignoreSize, String path, OnCommonCompressListener listener){
        List<String> photos = new ArrayList<>();
        photos.add(path);
        compress(context,ignoreSize,photos,listener);
    }

    /**
     * 异步压缩
     * @param context
     * @param ignoreSize 不压缩的阈值，单位为K
     * @param photos
     */
    public static void compress(Context context, int ignoreSize, List<String> photos, OnCommonCompressListener listener){
        Luban.with(context)
                .load(photos)
                .ignoreBy(ignoreSize)
                .setTargetDir(Utils.getCachePath(context))
                .filter(new CompressionPredicate() {
                    @Override
                    public boolean apply(String path) {
                        return !(TextUtils.isEmpty(path) || path.toLowerCase().endsWith(".gif"));
                    }
                })
                .setCompressListener(new OnCompressListener() {
                    @Override
                    public void onStart() {
                        // TODO 压缩开始前调用，可以在方法内启动 loading UI
                    }

                    @Override
                    public void onSuccess(File file) {
                        // TODO 压缩成功后调用，返回压缩后的图片文件
                        if(listener!=null){
                            listener.onSuccess(file.getAbsolutePath());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        // TODO 当压缩过程出现问题时调用
                    }
                }).launch();
    }

    public interface OnCommonCompressListener {
        void onSuccess(String filePath);
    }

}
