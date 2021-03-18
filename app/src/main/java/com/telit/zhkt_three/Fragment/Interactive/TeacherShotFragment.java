package com.telit.zhkt_three.Fragment.Interactive;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomViewTarget;
import com.bumptech.glide.request.target.DrawableImageViewTarget;
import com.bumptech.glide.request.target.ImageViewTarget;
import com.bumptech.glide.request.target.Target;

import com.bumptech.glide.request.transition.Transition;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.telit.zhkt_three.Activity.InteractiveScreen.ImageUtils;
import com.telit.zhkt_three.Fragment.CircleProgressDialogFragment;
import com.telit.zhkt_three.R;

/**
 * author: qzx
 * Date: 2020/3/20 8:52
 */
public class TeacherShotFragment extends Fragment {
    private CircleProgressDialogFragment circleProgressDialog;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        circleProgressDialog = new CircleProgressDialogFragment();
        circleProgressDialog.show(getChildFragmentManager(),CircleProgressDialogFragment.class.getSimpleName());

        View view = inflater.inflate(R.layout.frag_shot_teacher_layout, container, false);

        SubsamplingScaleImageView photoView = view.findViewById(R.id.shot_photo);

        Bundle bundle = getArguments();

        if (bundle != null) {
            String url = bundle.getString("url_img");
            Glide.with(getContext())
                    .setDefaultRequestOptions(
                            new RequestOptions()
                                       .frame(3000000)
                                    .fitCenter())
                                    .load(url)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            circleProgressDialog.dismissAllowingStateLoss();
                            return false;
                        }
                    })

                    .into(new CustomViewTarget<SubsamplingScaleImageView, Drawable>(photoView) {
                        @Override
                        public void onLoadFailed(@Nullable Drawable errorDrawable) {

                        }

                        @Override
                        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                            Bitmap bitmap = ImageUtils.DrawableToBitmap(resource);
                            Drawable drawable = new BitmapDrawable(bitmap);
                            photoView.setMinimumScaleType(SubsamplingScaleImageView.SCALE_TYPE_CENTER_CROP);
                            //photoView.setMinimumScaleType(SubsamplingScaleImageView.PAN_LIMIT_OUTSIDE);
                            //photoView.setImage(ImageSource.bitmap(bitmap));
                            photoView.setBackground(drawable);
                        }

                        @Override
                        protected void onResourceCleared(@Nullable Drawable placeholder) {

                        }
                    });
        }

        return view;
    }
}
