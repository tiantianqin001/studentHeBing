package com.telit.zhkt_three.MediaTools.image;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.telit.zhkt_three.R;
import com.telit.zhkt_three.Utils.QZXTools;
import com.tencent.bugly.crashreport.CrashReport;

import java.util.List;

/**
 * author: qzx
 * Date: 2019/5/18 20:52
 * <p>
 * photoview 设置scaleType="Center_Inside"模式，设置最小的视图大小为屏幕的一半，图片可以放大到一半大小
 */
public class PhotoViewAdapter extends PagerAdapter {

    private List<String> datas;
    private Context mContext;

    private String flag;

    private String type;//典型答题 1、优秀作答 2、典型错误

    public PhotoViewAdapter(List<String> lists, Context context,String flag,String type) {
        datas = lists;
        mContext = context;
        this.flag = flag;
        this.type = type;
    }

    @Override
    public int getCount() {
        return datas != null ? datas.size() : 0;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == o;
    }

    /**
     * android:layout_width="@dimen/y861"
     * android:layout_height="@dimen/x645"
     */
    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
//        PhotoView photoView=new PhotoView(mContext);
//        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(mContext.getResources().getDimensionPixelSize(R.dimen.y861),
//                mContext.getResources().getDimensionPixelSize(R.dimen.x645));
//        photoView.setLayoutParams(layoutParams);

        View view = LayoutInflater.from(mContext).inflate(R.layout.media_img_vp_item, container, false);
        ImageView photoView = view.findViewById(R.id.media_photoView);
        ImageView iv_tag = view.findViewById(R.id.iv_tag);

        if (TextUtils.isEmpty(type)){
            iv_tag.setVisibility(View.GONE);
        }else {
            iv_tag.setVisibility(View.VISIBLE);

            if ("1".equals(type)){
                iv_tag.setImageResource(R.mipmap.perfect_answers_icon);
            }else if ("2".equals(type)){
                iv_tag.setImageResource(R.mipmap.typical_mistake_icon);
            }
        }

        QZXTools.logE("url:"+datas.get(position), null);

        if (!TextUtils.isEmpty(flag)){
            Glide.with(mContext).load(datas.get(position)).into(photoView);
        }else {
            Glide.with(mContext)
                    .load(datas.get(position))
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target,
                                                    boolean isFirstResource) {
                            QZXTools.logE("onLoadFailed", e);
                            //报告异常到bugly
                            CrashReport.postCatchedException(e);
                            QZXTools.popCommonToast(mContext, "图片加载失败", false);
                            ((AppCompatActivity) mContext).finish();
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target,
                                                       DataSource dataSource, boolean isFirstResource) {
                            QZXTools.logE("onResourceReady", null);
                            return false;
                        }
                    })
                    .into(new SimpleTarget<Drawable>() {
                        @Override
                        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                            int ScreenWidth = mContext.getResources().getDisplayMetrics().widthPixels;
                            int ScreenHeight = mContext.getResources().getDisplayMetrics().heightPixels;
//                        Log.e("screen width_height", "width " + ScreenWidth + "--------height " + ScreenHeight);

                            int width = resource.getIntrinsicWidth();
                            int height = resource.getIntrinsicHeight();
//                        Log.e("width_height", "width " + width + "--------height " + height);

                            if ((width < ScreenWidth / 2) && (height < ScreenHeight / 2)) {
                                width = ScreenWidth / 2;
                                height = ScreenHeight / 2;
                            }

                            ViewGroup.LayoutParams params = photoView.getLayoutParams();
                            params.height = height;
                            params.width = width;
                            photoView.setLayoutParams(params);

                            Bitmap bitmap = QZXTools.drawableToBitmap(resource);

                            photoView.setImageBitmap(bitmap);


                        }
                    });

        }
        photoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((Activity) mContext).finish();
            }
        });
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
