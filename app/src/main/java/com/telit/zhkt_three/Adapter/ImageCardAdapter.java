package com.telit.zhkt_three.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.telit.zhkt_three.BuildConfig;
import com.telit.zhkt_three.R;

import java.util.List;



/**
 * Created by chensuilun on 2016/11/15.
 */
public class ImageCardAdapter extends RecyclerView.Adapter<ImageCardAdapter.ViewHolder> implements View.OnClickListener {
    private static final String TAG = "ImageCardAdapter";
    private Context context;
    private List<CardItem> items;
    private OnItemClickListener mOnItemClickListener;
    private int mWidth;
    private int mHeight;

    public ImageCardAdapter(Context context, List<CardItem> items, int width, int height) {
        this.context = context;
        this.items = items;
        mWidth = width;
        mHeight = height;
    }

    public ImageCardAdapter setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
        return this;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (BuildConfig.DEBUG) {
            Log.e(TAG, "onCreateViewHolder: type:" + viewType);
        }
        View v;
        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycel_image, parent, false);
        v.setOnClickListener(this);
        v.setLayoutParams(new RecyclerView.LayoutParams(mWidth, mHeight));
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "onBindViewHolder: position:" + position);
        }
        CardItem item = items.get(position);
       // holder.image.setImageResource(item.mResId);
        Glide.with(context)
                .load(item.mName)
                .into(holder.image);
        holder.itemView.setTag(position);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public void onClick(final View v) {
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick(v, (int) v.getTag());
        }
    }

    /**
     * @author chensuilun
     */
    protected static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView image;

        public ViewHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.imageView);
        }
    }

    /**
     * @author chensuilun
     */
    public interface OnItemClickListener {

        void onItemClick(View view, int position);

    }

    /**
     *
     */
    public static class CardItem {
        public String mName;

        public CardItem( String name) {
            mName = name;
        }
    }
}
