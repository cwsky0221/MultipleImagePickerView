package com.cwsky.multiimagepicker.callback;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Create by wei.chen
 * on 2021/7/21
 */
public interface OnItemDragClick {
    void onDrag(RecyclerView.ViewHolder vh, boolean open);
}
