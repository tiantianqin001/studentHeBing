package com.telit.zhkt_three.Adapter.AutoLearning;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.telit.zhkt_three.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.zip.ZipFile;

/**
 * author: qzx
 * Date: 2019/6/13 20:28
 */
public class FlipViewAdapter extends BaseAdapter {

    private Context mContext;
    private List<String> imgUrls;
    private ZipFile zipFile;

    public FlipViewAdapter(Context context, List<String> list, ZipFile zipFile) {
        mContext = context;
        imgUrls = list;
        this.zipFile = zipFile;
    }

    @Override
    public int getCount() {
        if (imgUrls != null && imgUrls.size() > 0) {
            return (int) (imgUrls.size() / 2.0f + 0.5f);
        } else {
            return 0;
        }
    }

    @Override
    public Object getItem(int position) {
        return imgUrls.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder mViewHolder;
        if (convertView == null) {
            mViewHolder = new ViewHolder();
            //加载布局初始化item组件
            convertView = LayoutInflater.from(mContext).inflate(R.layout.vp_item_ebook_layout, parent, false);
            mViewHolder.left_img = (ImageView) convertView.findViewById(R.id.ebook_item_left);
            mViewHolder.right_img = (ImageView) convertView.findViewById(R.id.ebook_item_right);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }

//        /*
//         *  Glide.with(mContext).load(getPicBytes(imgUrls.get(position * 2))).into(left_img);会卡顿和白屏
//         * */
//        if (position * 2 + 1 >= imgUrls.size()) {
//            mViewHolder.left_img.setImageBitmap(BitmapFactory.decodeFile(imgUrls.get(position * 2)));
//            mViewHolder.right_img.setImageDrawable(null);
//        } else {
//            mViewHolder.left_img.setImageBitmap(BitmapFactory.decodeFile(imgUrls.get(position * 2)));
//            mViewHolder.right_img.setImageBitmap(BitmapFactory.decodeFile(imgUrls.get(position * 2 + 1)));
//        }

        //---------------------------------------------------------
        if (imgUrls.size() > 0) {
            if (position * 2 + 1 >= imgUrls.size()) {
                if (position * 2<=imgUrls.size()){
                    byte[] pic_left = getPicBytes(imgUrls.get(position * 2));
                    mViewHolder.left_img.setImageBitmap(BitmapFactory.decodeByteArray(pic_left, 0, pic_left.length));
                }else {
                    mViewHolder.left_img.setImageDrawable(null);
                }
                mViewHolder.right_img.setImageDrawable(null);
            } else {
                if (position * 2<=imgUrls.size()){
                    byte[] pic_left = getPicBytes(imgUrls.get(position * 2));
                    byte[] pic_right = getPicBytes(imgUrls.get(position * 2 + 1));
                    mViewHolder.left_img.setImageBitmap(BitmapFactory.decodeByteArray(pic_left, 0, pic_left.length));
                    mViewHolder.right_img.setImageBitmap(BitmapFactory.decodeByteArray(pic_right, 0, pic_right.length));
                }else {
                    mViewHolder.left_img.setImageDrawable(null);
                    mViewHolder.right_img.setImageDrawable(null);
                }
            }
        }
        return convertView;
    }

//    private void clearImages(ViewHolder viewHolder) {
//        try {
//            Bitmap leftBitmap = ((BitmapDrawable) viewHolder.left_img.getDrawable()).getBitmap();
//            viewHolder.left_img.setImageDrawable(null);
//            if (leftBitmap != null && !leftBitmap.isRecycled()) {
//                leftBitmap.recycle();
//                leftBitmap = null;
//            }
//            Bitmap rightBitmap = ((BitmapDrawable) viewHolder.right_img.getDrawable()).getBitmap();
//            viewHolder.right_img.setImageDrawable(null);
//            if (rightBitmap != null && !rightBitmap.isRecycled()) {
//                rightBitmap.recycle();
//                rightBitmap = null;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    private static class ViewHolder {
        ImageView left_img;
        ImageView right_img;
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
