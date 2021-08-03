package com.cwsky.multiimagepicker;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.cwsky.multiimagepicker.bean.PickerImageBean;
import com.cwsky.multiimagepicker.callback.OnItemDragClick;
import com.cwsky.multiimagepicker.utils.ImageCompressUtils;
import com.cwsky.multiimagepicker.utils.ImageLoadUtils;
import com.cwsky.multiimagepicker.utils.ImagePickerUtils;
import com.cwsky.multiimagepicker.utils.ScreenUtils;
import com.cwsky.multiimagepicker.utils.SizeUtils;
import com.cwsky.multiimagepicker.utils.SpacesItemDecoration;

import com.cwsky.multiimagepicker.callback.OnMultPickerImageListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;


/**
 * 图片选择，展示，上传控件
 * Create by wei.chen
 * on 2020/11/12
 */
public class MultipleImagePickerView extends LinearLayout {
    private Activity mContext;
    private View mContentView;
    private RecyclerView mRecyclerView;
    private ImagePickerAdapter mAdapter;
    private OnMultPickerImageListener mOnPickerImageListener;
    private int childWidth;
    private int mMaxNum = 5;//最大支持上传的图片数
    private int space = 0;//grid行间距
    private int padding = SizeUtils.dp2px(32);//左右边距
    private int itemCount = 5;//每行个数
    private boolean isCompress = true;//是否压缩图片，默认压缩
    private Handler handler = new Handler();

    public MultipleImagePickerView(Context context) {
        this(context, null);
    }

    public MultipleImagePickerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MultipleImagePickerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MultipleImagePickerView initSize(int space, int itemCount, int padding) {
        this.space = space;
        this.itemCount = itemCount;
        this.padding = padding;
        return this;
    }

    public MultipleImagePickerView setCompress(boolean compress) {
        isCompress = compress;
        return this;
    }

    public MultipleImagePickerView setSpace(int space) {
        this.space = space;
        return this;
    }

    public MultipleImagePickerView setParentPadding(int padding) {
        this.padding = padding;
        return this;
    }

    public MultipleImagePickerView setItemCount(int itemCount) {
        this.itemCount = itemCount;
        return this;
    }

    public MultipleImagePickerView setMaxNum(int maxNum){
        this.mMaxNum = maxNum;
        return this;
    }

    public void init(FragmentActivity activity, OnMultPickerImageListener listener) {
        init(activity, false, false, listener);
    }

    /**
     * @param activity
     * @param addToHead 选择按钮显示位置，true显示在头部，false显示在尾部
     * @param canDrag   是否可以拖拽
     * @param listener
     */
    public void init(FragmentActivity activity, boolean addToHead, boolean canDrag, OnMultPickerImageListener listener) {
        mContext = activity;
        if (mContentView == null) {
            mContentView = LayoutInflater.from(activity).inflate(R.layout.view_image_pick, null, false);
            mRecyclerView = mContentView.findViewById(R.id.recyclerView);

            mAdapter = new ImagePickerAdapter(activity, new ArrayList<>(), mMaxNum, addToHead, canDrag);
            GridLayoutManager gridLayoutManager = new GridLayoutManager(activity, itemCount);
            mRecyclerView.setLayoutManager(gridLayoutManager);
            mRecyclerView.setAdapter(mAdapter);
            if (space > 0) {
                mRecyclerView.addItemDecoration(new SpacesItemDecoration(0, space));
            }
            setDrag();
            removeAllViews();

            LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            layoutParams.gravity = Gravity.CENTER;
            addView(mContentView,layoutParams);
        }
        //计算gridview的子view宽高
        childWidth = (ScreenUtils.getScreenWidth() - (itemCount-1) * space - padding) / itemCount;
        this.mOnPickerImageListener = listener;
    }

    private void setDrag() {
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                if (recyclerView.getLayoutManager() instanceof GridLayoutManager) {
                    final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN |
                            ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
                    final int swipeFlags = 0;
                    return makeMovementFlags(dragFlags, swipeFlags);
                } else {
                    final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
                    final int swipeFlags = 0;
                    return makeMovementFlags(dragFlags, swipeFlags);
                }
            }

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                //得到当拖拽的viewHolder的Position
                int fromPosition = viewHolder.getAbsoluteAdapterPosition();
                //拿到当前拖拽到的item的viewHolder
                int toPosition = target.getAbsoluteAdapterPosition();
                ArrayList<PickerImageBean> datas = new ArrayList<>();
                datas.addAll(mAdapter.getDatas());

                if ((mAdapter.isAddToHead() && toPosition == 0 && mAdapter.getItemCount() < mMaxNum) || (!mAdapter.isAddToHead() && toPosition >= datas.size() - 1 && mAdapter.getItemCount() < mMaxNum)) {
                    //选择添加按钮在头部时，不能移动第一个位置,选择添加按钮在尾部时，不能移动最后一个位置
                    return false;
                }

                if (fromPosition < toPosition) {
                    for (int i = fromPosition; i < toPosition; i++) {
                        Collections.swap(datas, i, i + 1);
                    }
                } else {
                    for (int i = fromPosition; i > toPosition; i--) {
                        Collections.swap(datas, i, i - 1);
                    }
                }
                mAdapter.setData(datas);
                mAdapter.notifyItemMoved(fromPosition, toPosition);
                if (mOnPickerImageListener != null) {
                    mOnPickerImageListener.onPickerImageChange();
                }
                return true;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

            }

            @Override
            public boolean isLongPressDragEnabled() {
                return false;
            }

            @Override
            public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder, int actionState) {
                super.onSelectedChanged(viewHolder, actionState);
                if (ItemTouchHelper.ACTION_STATE_IDLE == actionState) {
                    //拖拽完成，刷新列表
                    mAdapter.notifyDataSetChanged();
                }
            }
        });
        itemTouchHelper.attachToRecyclerView(mRecyclerView);
        mAdapter.setOnItemDragClick(new OnItemDragClick() {
            @Override
            public void onDrag(RecyclerView.ViewHolder vh, boolean open) {
                if (open) {
                    itemTouchHelper.startDrag(vh);
                }
            }
        });
    }

    /**
     * 获取已选择的图片
     * @return
     */
    public ArrayList<PickerImageBean> getPickImages() {
        return mAdapter.getImages();
    }

    /**
     * 获取已选择并上传成功的图片地址
     * @return
     */
    public ArrayList<String> getPickImageUrls(){
        ArrayList<String> urlList = new ArrayList<>();
        ArrayList<PickerImageBean> pickerImageBeans = getPickImages();
        if(pickerImageBeans==null || pickerImageBeans.isEmpty()) return urlList;
        for(int i=0;i<pickerImageBeans.size();i++){
            if(Status.SUCCESS == pickerImageBeans.get(i).getStatus()){
                urlList.add(pickerImageBeans.get(i).getImageUrl());
            }
        }
        return urlList;
    }

    public void setData(List<PickerImageBean> list) {
        mAdapter.setImages(list);
    }

    public void setStringData(List<String> urls) {
        if (urls == null) urls = new ArrayList<>();
        setStringData(urls.toArray(new String[urls.size()]), new String[]{});
    }

    /**
     * 设置图片
     *
     * @param urls
     * @param localUrls
     */
    public void setStringData(String[] urls, String[] localUrls) {
        List<String> urlList;
        if (urls != null && urls.length > 0) {
            urlList = new ArrayList<>(Arrays.asList(urls));
        } else {
            urlList = new ArrayList<>();
        }
        List<String> localUrlList;
        if (localUrls != null && localUrls.length > 0) {
            localUrlList = new ArrayList<>(Arrays.asList(localUrls));
        } else {
            localUrlList = new ArrayList<>();
        }

        if (urlList != null && !urlList.isEmpty()) {
            List<PickerImageBean> pickerImageBeans = new ArrayList<>();
            for (int i = 0; i < urlList.size(); i++) {
                PickerImageBean imageBean = new PickerImageBean();
                imageBean.setImageUrl(urlList.get(i));
                if (localUrlList != null && i < localUrlList.size()) {
                    imageBean.setImagePath(localUrlList.get(i));
                }
                imageBean.setStatus(Status.SUCCESS);
                pickerImageBeans.add(imageBean);
            }
            mAdapter.replaceImages(pickerImageBeans);
        } else {
            mAdapter.clear();
        }
    }

    public void clear() {
        mAdapter.clear();
    }

    public void update(List<PickerImageBean> data) {
        mAdapter.setImages(data);
    }

    public void removeImage(int position) {
        mAdapter.removeItem(position);
    }

    /**
     * 单个刷新（同步）
     *
     * @param filePath
     */
    public synchronized void updatePickImage(String filePath) {
        //上传的集合,每次一个
        ArrayList<PickerImageBean> imageBeans = new ArrayList<>();
        imageBeans.add(buildImagePickerBean(filePath, Status.UPLOADING));
        //显示的集合
        ArrayList<PickerImageBean> listBeans = new ArrayList<>();
        listBeans.addAll(imageBeans);
        update(listBeans);
        uploadFiles(imageBeans);
    }

    public void updatePickImages(ArrayList<String> filePaths) {
        ArrayList<PickerImageBean> imageBeans = new ArrayList<>();
        for (int i = 0; i < filePaths.size(); i++) {
            imageBeans.add(buildImagePickerBean(filePaths.get(i), Status.UPLOADING));
        }
        update(imageBeans);
        uploadFiles(imageBeans);
    }

    /**
     * 构建图片选择数据
     *
     * @param imagePath
     * @param status    图片的上传状态
     * @return
     */
    private PickerImageBean buildImagePickerBean(String imagePath, Status status) {
        PickerImageBean bean = new PickerImageBean();
        bean.setImageUuid(UUID.randomUUID().toString());
        if (status == Status.UPLOADING) {
            //存储本地地址
            bean.setImagePath(imagePath);
        } else {
            //存储网络地址
            bean.setImageUrl(imagePath);
        }
        bean.setStatus(status);
        return bean;
    }

    public void onItemClick(View view, int position) {

        switch (position) {
            case -1:
                //最大能选的图片数
                int max = mMaxNum - mAdapter.getImages().size();
                ImagePickerUtils.showImagePicker(mContext, max, new ImagePickerUtils.OnImagePickerListener() {
                    @Override
                    public void onComplete(ArrayList<String> items) {
                        if (items != null && items.size() > 0) {
                            //显示图片，并上传阿里云
                            updatePickImages(items);
                        }
                    }
                });

                break;
            default:
                if (view.getTag() != null) {
                    PickerImageBean clickBean = (PickerImageBean) view.getTag();
                    if (clickBean.getStatus() == Status.FAIL) {
                        mAdapter.removeItem(clickBean);
                        break;
                    }
                }
                //打开预览
                ArrayList<PickerImageBean> imageBeans = mAdapter.getImages();
                if (imageBeans == null || imageBeans.isEmpty()) {
                    return;
                }
                ArrayList<String> imagePaths = new ArrayList<String>();
                for (int i = 0; i < mAdapter.getImages().size(); i++) {
                    String path = mAdapter.getImages().get(i).getImagePath();
                    if (TextUtils.isEmpty(path)) {
                        //本地地址为空，取网络地址
                        path = mAdapter.getImages().get(i).getImageUrl();
                    }
                    imagePaths.add(path);
                }

                //查看大图
                if(mOnPickerImageListener !=null){
                    mOnPickerImageListener.onImageBrowser(imagePaths,position);
                }
                
                break;
        }
    }

    public int getMaxNum() {
        return mMaxNum - mAdapter.getImages().size();
    }

    /**
     * 检测是否所有图片都上传成功，没有选择也默认是成功
     * @return
     */
    public boolean isUploadDone(){
        ArrayList<PickerImageBean> pickerImageBeans = mAdapter.getImages();
        for(PickerImageBean pickerImageBean:pickerImageBeans){
            if(Status.UPLOADING == pickerImageBean.getStatus()){
                return false;
            }
        }
        return true;
    }

    /**
     * 上传
     *
     * @param data
     */
    public void uploadFiles(final List<PickerImageBean> data) {
        if (data != null && !data.isEmpty()) {
            for (int i = 0; i < data.size(); i++) {
                if (Status.UPLOADING == data.get(i).getStatus()) {
                    //上传状态下才需要上传阿里云
                    String localPath = data.get(i).getImagePath();
                    String imageUuid = data.get(i).getImageUuid();
                    if(isCompress){
                        //超过1M就压缩
                        ImageCompressUtils.compress(getContext(), 1024, localPath, new ImageCompressUtils.OnCommonCompressListener() {
                            @Override
                            public void onSuccess(String filePath) {
                                uploadFile(filePath, imageUuid);
                            }
                        });
                    }

                }
            }

        }
    }

    /**
     * 更新上传状态
     *
     * @param imageUuid
     * @param imageUrl  上传完成后的地址
     * @param status
     */
    private void updateUploadStatus(String imageUuid, String imageUrl, Status status) {
        if (mAdapter.getImages() != null) {
            ArrayList<PickerImageBean> adapterBean = mAdapter.getDatas();
            for (int i = 0; i < adapterBean.size(); i++) {
                PickerImageBean bean = adapterBean.get(i);
                if (bean.getImageUuid() != null && bean.getImageUuid().equals(imageUuid)) {
                    bean.setStatus(status);
                    bean.setImageUrl(imageUrl);
                    mAdapter.notifyItemChanged(i);
                    break;
                }
            }
            //通知调用方，选择的图片有修改
            if (mOnPickerImageListener != null) {
                mOnPickerImageListener.onPickerImageChange();
            }
        }
    }

    /**
     * 上传服务器
     *
     * @param fileUri
     * @param imageUuid
     */
    public void uploadFile(final String fileUri, String imageUuid) {
        //模拟上传到服务器
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                String uploadFileUrl = fileUri;
                updateUploadStatus(imageUuid, uploadFileUrl, Status.SUCCESS);
            }
        },3000);

    }

    public class ImagePickerAdapter extends RecyclerView.Adapter<ImagePickerAdapter.SelectedPicViewHolder> {
        private int maxImgCount;
        private List<PickerImageBean> mData;
        private LayoutInflater mInflater;
        private boolean isAdded;   //是否额外添加了最后一个图片
        private OnItemDragClick mOnItemDragClick;

        private boolean addToHead = false;//添加选择的图片是否在头部，true 自动添加到列表头部  false自动添加到列表尾部
        private boolean canDrag = false;//是否可以拖拽排序

        public void setOnItemDragClick(OnItemDragClick mOnItemDragClick) {
            this.mOnItemDragClick = mOnItemDragClick;
        }

        public boolean isAddToHead() {
            return addToHead;
        }

        public void setImages(List<PickerImageBean> data) {
            setImages(data, false);
        }

        public void setImages(List<PickerImageBean> data, boolean isRemove) {
            if (!isRemove && getImages() != null && !getImages().isEmpty()) {
                //添加已经选择的图片
                data.addAll(0, getImages());
            }
            mData = new ArrayList<>(data);
            if (getItemCount() < maxImgCount) {
                if (addToHead) {
                    mData.add(0, new PickerImageBean());
                } else {
                    mData.add(new PickerImageBean());
                }
                isAdded = true;
            } else {
                isAdded = false;
            }
            notifyDataSetChanged();
        }

        public void replaceImages(List<PickerImageBean> data) {
            replaceImages(data, true);
        }

        public void replaceImages(List<PickerImageBean> data, boolean nofify) {
            mData = new ArrayList<>(data);
            if (getItemCount() < maxImgCount) {
                if (addToHead) {
                    mData.add(0, new PickerImageBean());
                } else {
                    mData.add(new PickerImageBean());
                }
                isAdded = true;
            } else {
                isAdded = false;
            }
            if (nofify) {
                notifyDataSetChanged();
            }
        }

        public void setData(List<PickerImageBean> mData) {
            this.mData = mData;
        }

        public void clear() {
            if (mData != null) {
                mData.clear();
            }
            setImages(new ArrayList<>(), true);
        }


        public void removeItem(int position) {
            PickerImageBean imageBean = getImage(position);
            if (imageBean != null) {
                removeItem(imageBean);
            }
        }

        public void removeItem(PickerImageBean data) {
            //删除上传失败的图片
            mData.remove(data);
            if (isAdded) {
                //如果不足选择的最大张数，同时删除上传按钮
                mData.remove(addToHead ? 0 : mAdapter.getItemCount() - 1);
            }
            setImages(new ArrayList<>(mData), true);
            //通知调用方，选择的图片有修改
            if (mOnPickerImageListener != null) {
                mOnPickerImageListener.onPickerImageChange();
            }
        }

        public ArrayList<PickerImageBean> getImages() {
            //由于图片未选满时，最后一张显示添加图片，因此这个方法返回真正的已选图片
            if (isAdded) {
                if (addToHead) {
                    return new ArrayList<>(mData.subList(1, mData.size()));
                } else {
                    return new ArrayList<>(mData.subList(0, mData.size() - 1));
                }
            }
            return (ArrayList) mData;
        }

        public ArrayList<PickerImageBean> getDatas() {
            if (mData == null) return new ArrayList<>();
            return (ArrayList) mData;
        }


        public PickerImageBean getImage(int position) {
            if (mData != null && mData.size() > position) {
                return mData.get(position);
            }
            return null;
        }

        public ImagePickerAdapter(Context mContext, List<PickerImageBean> data, int maxImgCount, boolean addToHead, boolean canDrag) {
            this.maxImgCount = maxImgCount;
            this.mInflater = LayoutInflater.from(mContext);
            this.addToHead = addToHead;
            this.canDrag = canDrag;
            setImages(data);
        }

        @Override
        public SelectedPicViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = mInflater.inflate(R.layout.view_image_pick_item, parent, false);
            return new SelectedPicViewHolder(view);
        }

        @Override
        public void onBindViewHolder(SelectedPicViewHolder holder, int position) {
            holder.bind(position);
        }

        @Override
        public int getItemCount() {
            return mData.size();
        }

        public class SelectedPicViewHolder extends RecyclerView.ViewHolder {
            private View rootView;
            private View addView;
            private View showView;
            private ImageView imgIv;
            private RelativeLayout mRlStatus;
            private ProgressBar mProgressBar;
            private ImageView mIvUploadFail;
            private ImageView mDelPic;
            private TextView mTvUploadPrompt;
            private TextView mTvAdd;
            private ImageView mIvAdd;

            public SelectedPicViewHolder(View itemView) {
                super(itemView);
                rootView = itemView;
                addView = itemView.findViewById(R.id.ll_add);
                showView = itemView.findViewById(R.id.rl_show);
                imgIv = (ImageView) itemView.findViewById(R.id.iv_img);
                mRlStatus = (RelativeLayout) itemView.findViewById(R.id.rl_status);
                mProgressBar = (ProgressBar) itemView.findViewById(R.id.progress_bar);
                mIvUploadFail = (ImageView) itemView.findViewById(R.id.iv_upload_fail);
                mTvUploadPrompt = (TextView) itemView.findViewById(R.id.tv_upload_prompt);
                mDelPic = (ImageView) itemView.findViewById(R.id.iv_del);
                mTvAdd = (TextView) itemView.findViewById(R.id.tv_add);
                mIvAdd = itemView.findViewById(R.id.iv_add);
            }

            public void bind(int position) {
                //设置子项的宽高
                computeWidth();
                //根据条目位置设置图片
                PickerImageBean item = mData.get(position);
                int clickPosition;

                //选择添加按钮的位置
                int addPosition = addToHead ? 0 : getItemCount() - 1;

                boolean isSelectAdd = isAdded && position == addPosition;

                if (isSelectAdd) {
                    imgIv.setImageResource(R.mipmap.ic_default);
                    clickPosition = -1;
                    addView.setVisibility(VISIBLE);
                    showView.setVisibility(View.GONE);
                    mDelPic.setVisibility(GONE);
                    //设置已上传的数量
                    mTvAdd.setText(mContext.getString(R.string.img_load_count, getItemCount() - 1, mMaxNum));
                } else {
                    clickPosition = position;
                    String loadUrl;
                    if (!TextUtils.isEmpty(item.getImagePath())) {
                        //优先加载本地
                        loadUrl = item.getImagePath();
                    } else {
                        loadUrl = item.getImageUrl();
                    }
                    ImageLoadUtils.getInstance().setContext(mContext).setScaleType(ImageView.ScaleType.FIT_CENTER).setUrl(loadUrl).into(imgIv);
                    addView.setVisibility(GONE);
                    showView.setVisibility(View.VISIBLE);
                    mDelPic.setVisibility(VISIBLE);
                    mDelPic.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (itemView.getTag() != null) {
                                PickerImageBean clickBean = (PickerImageBean) itemView.getTag();
                                mAdapter.removeItem(clickBean);
                            }
                        }
                    });
                }
                //设置条目的点击事件
                itemView.setTag(item);
                itemView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onItemClick(v, clickPosition);
                    }
                });
                if (canDrag) {
                    itemView.setOnLongClickListener(new OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            //选择添加按钮不能拖动
                            boolean openDrag = (isAdded && position == addPosition) ? false : true;
                            if (mOnItemDragClick != null) {
                                mOnItemDragClick.onDrag(SelectedPicViewHolder.this, openDrag);
                            }
                            return false;
                        }
                    });
                }

                if (Status.SUCCESS == item.getStatus()) {
                    mRlStatus.setVisibility(GONE);
                } else {
                    if (Status.UPLOADING == item.getStatus()) {
                        mProgressBar.setVisibility(View.VISIBLE);
                        mIvUploadFail.setVisibility(GONE);
                        mTvUploadPrompt.setText("上传中...");
                    } else {
                        mProgressBar.setVisibility(View.INVISIBLE);
                        mIvUploadFail.setVisibility(VISIBLE);
                        mTvUploadPrompt.setText("上传失败");
                    }
                    mRlStatus.setVisibility(VISIBLE);
                }

            }

            private void computeWidth() {
                //设置每个gridview的子项的宽高
                rootView.getLayoutParams().width = childWidth;
                rootView.getLayoutParams().height = childWidth;

                //设置添加整个view的宽高
                addView.getLayoutParams().width = childWidth;
                addView.getLayoutParams().height = childWidth;

                //设置添加按钮的宽高
                LayoutParams addLp = ( LayoutParams)mIvAdd.getLayoutParams();
                addLp.width = childWidth / 2;
                addLp.height = childWidth / 2;
                addLp.setMargins(0,childWidth / 10,0,0);

                //设置添加文本的字体大小
//                if (narrow) {
//                    mTvAdd.setTextSize(10);
//                } else {
//                    mTvAdd.setTextSize(12);
//                }

                //设置loading的宽高
                mProgressBar.getLayoutParams().width = childWidth / 3;
                mProgressBar.getLayoutParams().height = childWidth / 3;

                //设置加载失败按钮的宽高
                mIvUploadFail.getLayoutParams().width = childWidth / 3;
                mIvUploadFail.getLayoutParams().height = childWidth / 3;

                //设置上传中文案的margin
                RelativeLayout.LayoutParams uploadLp = ( RelativeLayout.LayoutParams)mTvUploadPrompt.getLayoutParams();
                uploadLp.setMargins(0,childWidth / 10,0,0);

            }
        }
    }
}
