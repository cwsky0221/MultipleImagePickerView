package com.cwsky.multiimagepicker.utils;

import android.annotation.SuppressLint;
import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @desc :  控制间距
 */
public class SpacesItemDecoration extends RecyclerView.ItemDecoration {

    private int edgeSpace;
    private int centerSpace;

    public SpacesItemDecoration(int space) {
        this.edgeSpace = space;
        this.centerSpace = space;
    }

    public SpacesItemDecoration(int edgeSpace, int centerSpace) {
        this.edgeSpace = edgeSpace;
        this.centerSpace = centerSpace;
    }

    @SuppressLint("WrongConstant")
    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        int childPosition = parent.getChildAdapterPosition(view);
        if (layoutManager instanceof GridLayoutManager) {
            GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
            int totalCount = layoutManager.getItemCount();
            int spanCount = gridLayoutManager.getSpanCount();
            int surplusCount = totalCount % spanCount;
            boolean isLeftFirst = (childPosition + spanCount) % spanCount == 0;
            boolean isRightLast = (childPosition + spanCount + 1) % spanCount == 0;
            if (gridLayoutManager.getOrientation() == LinearLayoutManager.VERTICAL) {
                //第一行
                if (childPosition <= spanCount - 1) {
                    if (isLeftFirst) {
                        outRect.left = edgeSpace;
                        outRect.right = centerSpace;
                    } else if (isRightLast) {
                        outRect.left = 0;
                        outRect.right = edgeSpace;
                    } else {
                        outRect.left = 0;
                        outRect.right = centerSpace;
                    }
                    outRect.top = edgeSpace;
                    outRect.bottom = 0;
                } else if ((surplusCount > 0 && childPosition >= totalCount - surplusCount) ||
                        (surplusCount == 0 && childPosition >= totalCount - spanCount)) {
                    //最后一行
                    if (isLeftFirst) {
                        outRect.left = edgeSpace;
                        outRect.right = centerSpace;
                    } else if (isRightLast) {
                        outRect.left = 0;
                        outRect.right = edgeSpace;
                    } else {
                        outRect.left = 0;
                        outRect.right = centerSpace;
                    }
                    outRect.top = centerSpace;
                    outRect.bottom = edgeSpace;
                } else {
                    //中间
                    if (isLeftFirst) {
                        outRect.left = edgeSpace;
                        outRect.right = centerSpace;
                    } else if (isRightLast) {
                        outRect.left = 0;
                        outRect.right = edgeSpace;
                    } else {
                        outRect.left = 0;
                        outRect.right = centerSpace;
                    }
                    outRect.top = centerSpace;
                    outRect.bottom = 0;
                }
            }
        } else if (layoutManager instanceof LinearLayoutManager) {
            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
            int childCount = parent.getAdapter().getItemCount();
            if (linearLayoutManager.getOrientation() == LinearLayoutManager.HORIZONTAL) {
                if (childPosition == 0) {
                    outRect.left = edgeSpace;
                    outRect.right = centerSpace;
                } else if (childPosition == (childCount - 1)) {
                    outRect.left = 0;
                    outRect.right = edgeSpace;
                } else {
                    outRect.left = 0;
                    outRect.right = centerSpace;
                }
            } else {
                if (childPosition == 0) {
                    outRect.top = edgeSpace;
                    outRect.bottom = centerSpace;
                } else if (childPosition == (childCount - 1)) {
                    outRect.top = 0;
                    outRect.bottom = edgeSpace;
                } else {
                    outRect.top = 0;
                    outRect.bottom = centerSpace;
                }
            }
        }
    }
}
