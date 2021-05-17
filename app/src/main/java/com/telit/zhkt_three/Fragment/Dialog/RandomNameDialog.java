package com.telit.zhkt_three.Fragment.Dialog;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.telit.zhkt_three.R;
import com.telit.zhkt_three.Utils.QZXTools;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * author: qzx
 * Date: 2020/3/24 16:56
 */
public class RandomNameDialog extends Fragment {
    private static final String TAG = "RandomNameDialog";
    /**
     * 随机点名的学生姓名
     */
    private String name;

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // setStyle(DialogFragment.STYLE_NO_TITLE, R.style.dialogForgetPwd);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_random_name_layout, container, false);
        TextView textView = view.findViewById(R.id.tv_random);
        ImageView board_avatar=view.findViewById(R.id.board_avatar);

        Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), "PingFang-SimpleBold.ttf");
        textView.setTypeface(typeface);
        QZXTools.logE("onCreateView: "+name,null);
        if (!TextUtils.isEmpty(name)){
            try {
                JSONObject jsonData = new JSONObject(name);
                String studentName = jsonData.optString("studentName");
                if (!TextUtils.isEmpty(studentName)){
                    textView.setText(studentName);
                }
                String photoName = jsonData.optString("photo");
                if (!TextUtils.isEmpty(photoName)){
                    Glide.with(getActivity())
                            .asBitmap()
                            .load(photoName)
                            .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                            .into(board_avatar);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


   /*     ScheduledExecutorService scheduledExecutorService = new ScheduledThreadPoolExecutor(1);
        scheduledExecutorService.schedule(new Runnable() {
            @Override
            public void run() {
               // dismiss();

                scheduledExecutorService.shutdown();
            }
        }, 10, TimeUnit.SECONDS);*/

        return view;
    }
}
