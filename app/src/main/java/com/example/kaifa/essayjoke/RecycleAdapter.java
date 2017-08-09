package com.example.kaifa.essayjoke;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.List;

/**
 * Created by kaifa on 2017/7/12.
 * <p>
 * Descripte:
 */
public class RecycleAdapter extends  RecyclerView.Adapter<RecycleAdapter.ViewHold> {
    private List<String> mData;
    private Context mContext;


    public RecycleAdapter(List<String> mData, Context mContext) {
        this.mData = mData;
        this.mContext = mContext;
    }

    @Override
    public ViewHold onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_recycle_imageselect, parent, false);
        ViewHold hold = new ViewHold(view);
        return hold;
    }

    @Override
    public void onBindViewHolder(final ViewHold holder, final int position) {
        Glide.with(mContext).load( mData.get(position)).centerCrop().into(holder.imageView);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mData.remove(position);
                notifyItemRemoved(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    class ViewHold extends RecyclerView.ViewHolder {

        private ImageView imageView;

        public ViewHold(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.iv_item);
        }
    }

}
