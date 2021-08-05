# MultipleImagePickerView
图片选择并展示控件  
1、支持单图，多图  
2、支持显示上传状态，失败状态   
3、支持删除图片，替换图片  
4、支持拖拽排序列表  
5、支持图片压缩  

# 使用方式：
```
MultipleImagePickerView mImagePickerView = findViewById(R.id.imagePickerView);
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
```

# 效果
![动图](https://github.com/cwsky0221/MultipleImagePickerView/blob/main/demo.gif?raw=true)
![效果图](http://yximstatic.clouderwork.com/1a7c8c4e-23db-41ba-aabd-5cd5ed92b725.jpg)
# 感谢：
[鲁班](https://github.com/Curzibn/Luban)  
[Glide](https://github.com/bumptech/glide)  
[YImagePicker](https://github.com/yangpeixing/YImagePicker)
