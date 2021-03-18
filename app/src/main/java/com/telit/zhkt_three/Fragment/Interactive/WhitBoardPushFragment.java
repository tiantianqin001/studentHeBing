package com.telit.zhkt_three.Fragment.Interactive;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomViewTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.telit.zhkt_three.Activity.InteractiveScreen.ImageUtils;
import com.telit.zhkt_three.Adapter.GalleryLayoutManager;
import com.telit.zhkt_three.Adapter.ImageCardAdapter;
import com.telit.zhkt_three.CustomView.CurveTransformer;
import com.telit.zhkt_three.FlingRecycleView;
import com.telit.zhkt_three.Fragment.CircleProgressDialogFragment;
import com.telit.zhkt_three.R;
import com.telit.zhkt_three.Utils.BitmapUtils;
import com.telit.zhkt_three.Utils.FastBlur;

import java.util.ArrayList;
import java.util.List;

/**
 * author: qzx
 * Date: 2020/3/20 8:52
 */
public class WhitBoardPushFragment extends Fragment {
    private CircleProgressDialogFragment circleProgressDialog;
    private FlingRecycleView rv_white_board;
    List<ImageCardAdapter.CardItem> mCardItems=new ArrayList<>();
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_white_board_img_layout, container, false);
        rv_white_board = view.findViewById(R.id.rv_white_board);
        Bundle bundle = getArguments();

        if (bundle != null) {
            String url = bundle.getString("url_img");
            String[] urls = url.split(",");
            for (String uri : urls){
                mCardItems.add(new ImageCardAdapter.CardItem(uri));
            }


            //滑动减慢
            rv_white_board.setFlingAble(false);
            GalleryLayoutManager layoutManager = new GalleryLayoutManager(GalleryLayoutManager.HORIZONTAL);

            layoutManager.attach(rv_white_board, -1);
            layoutManager.setOnItemSelectedListener(new GalleryLayoutManager.OnItemSelectedListener() {
                @Override
                public void onItemSelected(RecyclerView recyclerView, View item, int position) {
                    //  Bitmap bmp = BitmapUtils.decodeSampledBitmapFromResource(getResources(), mResId.get(position % mResId.size()), 100, 100);
                    //mPagerBg.setImageBitmap(FastBlur.doBlur(bmp, 20, false));
                }
            });
            //设置了角度
            layoutManager.setItemTransformer(new CurveTransformer());
            DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
            ImageCardAdapter imageAdapter = new ImageCardAdapter(getContext(),mCardItems, (int) (displayMetrics.widthPixels * 0.7f), (int) (displayMetrics.heightPixels * 0.8f));
            imageAdapter.setOnItemClickListener(new ImageCardAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    Toast.makeText(getContext(), "click" + mCardItems.get(position).mName, Toast.LENGTH_SHORT).show();
                    //  mPagerRecycleView.smoothScrollToPosition(position);
                }
            });
            rv_white_board.setAdapter(imageAdapter);
        }

        return view;
    }
}
