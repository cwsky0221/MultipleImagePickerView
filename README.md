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
mImagePickerView.setMaxNum(10)//最大显示数
        .setItemCount(3)//每行显示数
        .setSpace(SizeUtils.dp2px(8))//边距
        .setCompress(true)//是否压缩
        .init(MainActivity.this, false, true, new OnMultPickerImageListener() {
            @Override
            public void onPickerImageChange() {
                //图片选择回调
            }

            @Override
            public void onImageBrowser(ArrayList<String> imagePaths, int currentPosition) {
                //图片点击浏览
                Toast.makeText(MainActivity.this,"图片点击:"+currentPosition,Toast.LENGTH_SHORT).show();
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
