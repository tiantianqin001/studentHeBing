package com.telit.zhkt_three.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.telit.zhkt_three.Fragment.Dialog.TipsDialog;
import com.telit.zhkt_three.JavaBean.Resource.FillResource;
import com.telit.zhkt_three.JavaBean.UnityResource.UnityContent;
import com.telit.zhkt_three.R;

import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * author: qzx
 * Date: 2019/6/10 8:47
 * <p>
 * 资源类型：视频、图片、音频和作业
 */
public class RVUnityContentGridAdapter extends RecyclerView.Adapter<RVUnityContentGridAdapter.RVUnityContentGridViewHolder> {

    private Context mContext;
    private List<UnityContent> unityContents;

    public RVUnityContentGridAdapter(Context context, List<UnityContent> unityContents) {
        mContext = context;
        this.unityContents = unityContents;
    }

    @NonNull
    @Override
    public RVUnityContentGridViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new RVUnityContentGridViewHolder(LayoutInflater.from(mContext).
                inflate(R.layout.item_unity_content_layout, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RVUnityContentGridViewHolder rvUnityContentGridViewHolder, int i) {
        Glide.with(mContext).load(unityContents.get(i).getContentImgUrl()).placeholder(R.mipmap.no_cover)
                .error(R.mipmap.no_cover).into(rvUnityContentGridViewHolder.unity_content_img);

        rvUnityContentGridViewHolder.unity_content_see.setText(unityContents.get(i).getContentSee() + "");

        rvUnityContentGridViewHolder.unity_content_name.setText(unityContents.get(i).getAlias());

        rvUnityContentGridViewHolder.unity_content_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (unityClickInterface != null) {
                    unityClickInterface.unity_click(unityContents.get(i).getContentName());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return unityContents != null ? unityContents.size() : 0;
    }

    public class RVUnityContentGridViewHolder extends RecyclerView.ViewHolder {

        private ImageView unity_content_img;
        private TextView unity_content_see;
        private TextView unity_content_name;

        public RVUnityContentGridViewHolder(@NonNull View itemView) {
            super(itemView);
            unity_content_img = itemView.findViewById(R.id.unity_content_img);
            unity_content_see = itemView.findViewById(R.id.unity_content_see);
            unity_content_name = itemView.findViewById(R.id.unity_content_name);
        }
    }

    private UnityClickInterface unityClickInterface;

    public UnityClickInterface getUnityClickInterface() {
        return unityClickInterface;
    }

    public void setUnityClickInterface(UnityClickInterface unityClickInterface) {
        this.unityClickInterface = unityClickInterface;
    }

    public interface UnityClickInterface {
        void unity_click(String name);
    }
}
