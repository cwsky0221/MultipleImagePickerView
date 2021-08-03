package com.cwsky.multiimagepicker.utils;

import android.app.Activity;
import android.graphics.Color;

import com.ypx.imagepicker.ImagePicker;
import com.ypx.imagepicker.bean.ImageItem;
import com.ypx.imagepicker.bean.MimeType;
import com.ypx.imagepicker.bean.SelectMode;
import com.ypx.imagepicker.bean.selectconfig.CropConfig;
import com.ypx.imagepicker.data.OnImagePickCompleteListener;

import java.util.ArrayList;

/**
 * @desc : 本地图片选择器
 */
public class ImagePickerUtils {

    public interface OnImagePickerListener {
        void onComplete(ArrayList<String> items);
    }

    public static void showImagePicker(Activity context, OnImagePickerListener onImagePickerListener) {
        showImagePicker(context, 1, onImagePickerListener);
    }

    public static void showImagePicker(Activity context, int maxCount, OnImagePickerListener onImagePickerListener) {
        ImagePicker.withMulti(new WeChatPresenter())
                //设置选择的最大数
                .setMaxCount(maxCount)
                //设置列数
                .setColumnCount(4)
                //设置要加载的文件类型，可指定单一类型
                .mimeTypes(MimeType.ofImage())
                //设置需要过滤掉加载的文件类型
                .filterMimeTypes(MimeType.GIF)
                //显示拍照
                .showCamera(true)
                //开启预览
                .setPreview(true)
                //显示原图
                .setOriginal(true)
                //显示原图时默认原图选项开关
                .setDefaultOriginal(false)
                //设置单选模式，当maxCount==1时，可执行单选（下次选中会取消上一次选中）
                .setSelectMode(SelectMode.MODE_SINGLE)
                .pick(context, new OnImagePickCompleteListener() {
                    @Override
                    public void onImagePickComplete(ArrayList<ImageItem> items) {
                        //图片选择回调，主线程
                        if (onImagePickerListener != null) {
                            ArrayList paths = new ArrayList<>();
                            for (int i = 0; i < items.size(); i++) {
                                paths.add(items.get(i).getPath());
                            }
                            onImagePickerListener.onComplete(paths);
                        }
                    }
                });
    }

    public static void showImagePickerWithCrop(Activity context, OnImagePickerListener onImagePickerListener) {
        ImagePicker.withMulti(new WeChatPresenter())
                //设置选择的最大数
                .setMaxCount(1)
                //设置列数
                .setColumnCount(4)
                //设置要加载的文件类型，可指定单一类型
                .mimeTypes(MimeType.ofImage())
                //设置需要过滤掉加载的文件类型
                .filterMimeTypes(MimeType.GIF)
                //显示拍照
                .showCamera(true)
                //开启预览
                .setPreview(true)
                //显示原图
                .setOriginal(true)
                //剪裁完成的图片是否保存在DCIM目录下
                //true：存储在DCIM下 false：存储在 data/包名/files/imagePicker/ 目录下
                .cropSaveInDCIM(false)
                //设置剪裁比例
                .setCropRatio(1, 1)
                //设置剪裁框间距，单位px
                .cropRectMinMargin(50)
//                //是否圆形剪裁，圆形剪裁时，setCropRatio无效
//                .cropAsCircle()
                //设置剪裁模式，留白或充满  CropConfig.STYLE_GAP 或 CropConfig.STYLE_FILL
                .cropStyle(CropConfig.STYLE_FILL)
                //设置留白模式下生成的图片背景色，支持透明背景
                .cropGapBackgroundColor(Color.TRANSPARENT)
                .crop(context, new OnImagePickCompleteListener() {
                    @Override
                    public void onImagePickComplete(ArrayList<ImageItem> items) {
                        //图片剪裁回调，主线程
                        if (onImagePickerListener != null) {
                            ArrayList paths = new ArrayList<>();
                            for (int i = 0; i < items.size(); i++) {
                                paths.add(items.get(i).getCropUrl());
                            }
                            onImagePickerListener.onComplete(paths);
                        }
                    }
                });
    }

} 