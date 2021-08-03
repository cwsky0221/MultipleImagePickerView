package com.cwsky.multiimagepicker.bean;

import com.cwsky.multiimagepicker.Status;

import java.util.Objects;

/**
 * Create by wei.chen
 * on 2021/6/25
 */
public class PickerImageBean{
    private String imageUuid;//图片唯一id
    private String imagePath;//图片本地路径
    private Status status;//上传状态

    private String imageUrl;//图片上传阿里云后的url

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setImageUuid(String imageUuid) {
        this.imageUuid = imageUuid;
    }

    public String getImageUuid() {
        return imageUuid;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PickerImageBean imageBean = (PickerImageBean) o;
        return Objects.equals(imageUuid, imageBean.imageUuid) &&
                Objects.equals(imageUrl, imageBean.imageUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(imageUuid, imageUrl);
    }
}
