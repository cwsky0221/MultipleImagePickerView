package com.cwsky.pickview_demo;

import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.cwsky.multiimagepicker.MultipleImagePickerView;
import com.cwsky.multiimagepicker.callback.OnImageUploadListener;
import com.cwsky.multiimagepicker.callback.OnMultPickerImageListener;
import com.cwsky.multiimagepicker.utils.SizeUtils;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private MultipleImagePickerView mImagePickerView;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mImagePickerView = findViewById(R.id.imagePickerView);
        mImagePickerView.setMaxNum(10)
                .setItemCount(3)
                .setSpace(SizeUtils.dp2px(8))
                .setCompress(true)
                .init(MainActivity.this, false, true, new OnMultPickerImageListener() {
                    @Override
                    public void onPickerImageChange() {
                        //图片选择变化
                    }

                    @Override
                    public void onImageBrowser(ArrayList<String> imagePaths, int currentPosition) {
                        Toast.makeText(MainActivity.this,"图片点击:"+currentPosition,Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onImageUpload(String fileUri, OnImageUploadListener listener) {
                        //模拟上传到服务器
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                listener.onUploadSuccess(fileUri);
                            }
                        },3000);
                    }
                });
    }
}