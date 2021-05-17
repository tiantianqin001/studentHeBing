package com.telit.zhkt_three.Adapter.QuestionAdapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomViewTarget;
import com.bumptech.glide.request.transition.Transition;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.github.chrisbanes.photoview.PhotoView;
import com.telit.zhkt_three.Activity.InteractiveScreen.ImageUtils;
import com.telit.zhkt_three.R;
import com.telit.zhkt_three.Utils.QZXTools;
import com.telit.zhkt_three.dialoge.LoadDialog;

import java.util.List;

/**
 * author: qzx
 * Date: 2019/5/23 16:30
 */
public class RVQuestionImgAdapter extends RecyclerView.Adapter<RVQuestionImgAdapter.RVQuestionImgViewHolder> {

    private List<String> urlImgs;
    private Context mContext;
    private LoadDialog loadDialog;

    public RVQuestionImgAdapter(Context context, List<String> list) {
        mContext = context;
        urlImgs = list;
    }

    @NonNull
    @Override
    public RVQuestionImgViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
      /*  loadDialog = new LoadDialog(mContext);
        loadDialog.show();*/
        View view = LayoutInflater.from(mContext).inflate(R.layout.rv_question_img_item_layout, viewGroup, false);

        return new RVQuestionImgViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RVQuestionImgViewHolder rvQuestionImgViewHolder, int i) {
        Glide.with(mContext).load(urlImgs.get(i)).into(rvQuestionImgViewHolder.photoView);
   /*     Glide.with(mContext).load(urlImgs.get(i)).into(new CustomViewTarget<SubsamplingScaleImageView, Drawable>(rvQuestionImgViewHolder.photoView) {
            @Override
            public void onLoadFailed(@Nullable Drawable errorDrawable) {

            }

            @Override
            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                Bitmap bitmap = ImageUtils.DrawableToBitmap(resource);
                rvQuestionImgViewHolder.photoView.setMinimumScaleType(SubsamplingScaleImageView.SCALE_TYPE_CENTER_CROP);
                rvQuestionImgViewHolder.photoView.setImage(ImageSource.bitmap(bitmap));
            }

            @Override
            protected void onResourceCleared(@Nullable Drawable placeholder) {

            }
        });*/
    }

    @Override
    public int getItemCount() {
        return urlImgs != null ? urlImgs.size() : 0;
    }

    public class RVQuestionImgViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private PhotoView photoView;

        public RVQuestionImgViewHolder(@NonNull View itemView) {
            super(itemView);
            photoView = itemView.findViewById(R.id.photoView_question_item);
            //点击图片进入图片查看器
            photoView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            QZXTools.logE("click photoView position=" + getLayoutPosition(), null);
        }
    }
}
