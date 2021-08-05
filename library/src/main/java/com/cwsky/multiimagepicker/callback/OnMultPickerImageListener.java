package com.cwsky.multiimagepicker.callback;

import java.util.ArrayList;

/**
 * Create by wei.chen
 * on 2021/6/25
 */
public interface OnMultPickerImageListener {
    /**
     * 图片选择监听
     */
    void onPickerImageChange();

    /**
     * 图片点击放大
     */
    void onImageBrowser(ArrayList<String> imagePaths,int currentPosition);

    /**
     * 图片上传
     */
    void onImageUpload(String fileUri,OnImageUploadListener listener);
}
