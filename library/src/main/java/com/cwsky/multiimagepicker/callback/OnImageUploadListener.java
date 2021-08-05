package com.cwsky.multiimagepicker.callback;

/**
 * Create by wei.chen
 * on 2021/8/5
 */
public interface OnImageUploadListener {
    void onUploadSuccess(String uploadFileUrl);

    void onUploadFail();
}
