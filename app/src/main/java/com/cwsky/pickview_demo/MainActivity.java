package com.cwsky.pickview_demo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import com.cwsky.multiimagepicker.MultipleImagePickerView;
import com.cwsky.multiimagepicker.callback.OnMultPickerImageListener;
import com.cwsky.multiimagepicker.utils.SizeUtils;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private MultipleImagePickerView mImagePickerView;

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

                    }

                    @Override
                    public void onImageBrowser(ArrayList<String> imagePaths, int currentPosition) {
                        Toast.makeText(MainActivity.this,"图片点击:"+currentPosition,Toast.LENGTH_SHORT).show();
                    }
                });
    }
}