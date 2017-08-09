package com.example.kaifa.essayjoke.imageselect;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.kaifa.essayjoke.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhb on 2017/7/11.
 * <p>
 * Descripte:
 */
public class SelectImageListAdapter extends RecyclerView.Adapter<SelectImageListAdapter.ViewHold> {
    private List<String> mData;
    private Context mContext;
    //选择图片的集合
    private ArrayList<String> mResultList;
    private int mMaxCount;
    private File tempFile;

    public SelectImageListAdapter(List<String> mData, Context mContext, ArrayList<String> mResultList, int mMaxCount) {
        this.mData = mData;
        this.mContext = mContext;
        this.mResultList = mResultList;
        this.mMaxCount = mMaxCount;
    }

    @Override
    public ViewHold onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_recycle_imageselect, parent, false);
        ViewHold hold = new ViewHold(view);
        return hold;
    }

    @Override
    public void onBindViewHolder(final ViewHold holder, final int position) {
        if (TextUtils.isEmpty(mData.get(position))) {
            //显示拍照
            holder.camera.setVisibility(View.VISIBLE);
            Glide.with(mContext).load(R.drawable.ctid_face_make).centerCrop().into(holder.camera);
            holder.checkBox.setVisibility(View.GONE);
            holder.imageView.setVisibility(View.GONE);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tempFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                            + File.separator + "temp.jpg");
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    Uri u = Uri.fromFile(tempFile);
                    intent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, u);
                    intent.putExtra("return-data", true);
                    ((Activity) mContext).startActivityForResult(intent, 3);

                }
            });
        } else {
            //显示图片
            holder.camera.setVisibility(View.GONE);
            holder.imageView.setVisibility(View.VISIBLE);
            holder.checkBox.setVisibility(View.VISIBLE);
            //利用glide
            Glide.with(mContext).load(mData.get(position)).centerCrop().into(holder.imageView);

            if (mResultList.contains(mData.get(position))) {
                //选中图片
                holder.checkBox.setChecked(true);
            } else {
                holder.checkBox.setChecked(false);
            }
            //给条目增加点击事件
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //没有就加入集合，有就移除集合
                    if (mResultList.contains(mData.get(position))) {
                        mResultList.remove(mData.get(position));
                        holder.checkBox.setChecked(false);

                    } else {
                        if (mResultList.size() >= mMaxCount) {
                            Toast.makeText(mContext, "最多只能选取" + mMaxCount + "张图片", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        mResultList.add(mData.get(position));
                        holder.checkBox.setChecked(true);
                    }

                    notifyItemChanged(position);
                    //通知显示
                    if (mListener != null) {
                        mListener.select();
                    }
                }
            });

        }

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    class ViewHold extends RecyclerView.ViewHolder {

        private ImageView imageView, camera;
        private CheckBox checkBox;

        public ViewHold(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.iv_item);
            checkBox = (CheckBox) itemView.findViewById(R.id.cb_item);
            camera = (ImageView) itemView.findViewById(R.id.iv_camera);

        }
    }

    //设置选择图片监听
    private SelectImageListener mListener;

    public void setOnSelectImageListener(SelectImageListener listener) {
        this.mListener = listener;
    }
}
