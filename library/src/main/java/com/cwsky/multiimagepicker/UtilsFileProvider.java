package com.cwsky.multiimagepicker;

import android.app.Application;

import androidx.core.content.FileProvider;

import com.cwsky.multiimagepicker.utils.Utils;

/**
 * Create by wei.chen
 * on 2021/8/3
 */
public class UtilsFileProvider extends FileProvider {

    @Override
    public boolean onCreate() {
        Utils.init((Application) getContext().getApplicationContext());
        return true;
    }

}
