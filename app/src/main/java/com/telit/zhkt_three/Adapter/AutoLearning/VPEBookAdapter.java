package com.telit.zhkt_three.Adapter.AutoLearning;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.telit.zhkt_three.R;
import com.telit.zhkt_three.Utils.QZXTools;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.zip.ZipFile;

/**
 * author: qzx
 * Date: 2019/6/13 11:00
 */
public class VPEBookAdapter extends PagerAdapter {

    private Context mContext;
    private List<String> imgUrls;
    private ZipFile zipFile;

    public VPEBookAdapter(Context context, List<String> list, ZipFile zipFile) {
        mContext = context;
        imgUrls = list;
        this.zipFile = zipFile;
    }

    @Override
    public int getCount() {
        return imgUrls != null ? (int) (imgUrls.size() / 2.0f + 0.5f) : 0;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == o;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.vp_item_ebook_layout, container, false);
        ImageView left_img = view.findViewById(R.id.ebook_item_left);
        ImageView right_img = view.findViewById(R.id.ebook_item_right);

        //主要判断结尾页是奇数还是偶数，position从0开始的，假设5张，三页
        if (position * 2 + 1 >= imgUrls.size()) {
            Glide.with(mContext).load(getPicBytes(imgUrls.get(position * 2))).into(left_img);
            Glide.with(mContext).load("").into(right_img);
        } else {
            Glide.with(mContext).load(getPicBytes(imgUrls.get(position * 2))).into(left_img);
            Glide.with(mContext).load(getPicBytes(imgUrls.get(position * 2 + 1))).into(right_img);
        }
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    private byte[] getPicBytes(String name) {
        byte[] bitmap = null;
        try {
            InputStream is = zipFile.getInputStream(zipFile
                    .getEntry(name));
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buff = new byte[1024];
            int rc = 0;
            while ((rc = is.read(buff)) > 0) {
                baos.write(buff, 0, rc);
            }
            bitmap = baos.toByteArray();
            baos.close();
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }
}
