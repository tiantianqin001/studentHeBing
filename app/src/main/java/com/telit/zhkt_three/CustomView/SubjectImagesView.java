package com.telit.zhkt_three.CustomView;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.telit.zhkt_three.JavaBean.SubjeatSaveBean;
import com.telit.zhkt_three.MyApplication;
import com.telit.zhkt_three.R;
import com.telit.zhkt_three.Utils.QZXTools;
import com.telit.zhkt_three.Utils.eventbus.EventBus;
import com.telit.zhkt_three.greendao.SubjeatSaveBeanDao;

import java.util.ArrayList;
import java.util.List;

public class SubjectImagesView extends RelativeLayout implements View.OnClickListener {


    private FrameLayout subjective_answer_frame_one;
    private FrameLayout subjective_answer_frame_two;
    private FrameLayout subjective_answer_frame_three;

    private ImageView subjective_img_one;
    private ImageView subjective_img_two;
    private ImageView subjective_img_three;


    private RelativeLayout subjective_del_layout_one;
    private RelativeLayout subjective_del_layout_two;
    private RelativeLayout subjective_del_layout_three;
    private ImageView subjective_del_one;
    private ImageView subjective_del_two;
    private ImageView subjective_del_three;
    private Context mContext;
    public SubjectImagesView(Context context) {
        this(context, null);
    }

    public SubjectImagesView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SubjectImagesView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mContext = context;

        View view = LayoutInflater.from(context).inflate(R.layout.subjective_option_images_layout,
                this, true);


        subjective_answer_frame_one = view.findViewById(R.id.subjective_answer_frame_one);
        subjective_answer_frame_two = view.findViewById(R.id.subjective_answer_frame_two);
        subjective_answer_frame_three = view.findViewById(R.id.subjective_answer_frame_three);

        subjective_img_one = view.findViewById(R.id.subjective_img_one);
        subjective_img_two = view.findViewById(R.id.subjective_img_two);
        subjective_img_three = view.findViewById(R.id.subjective_img_three);

        subjective_del_layout_one = view.findViewById(R.id.subjective_del_layout_one);
        subjective_del_layout_two = view.findViewById(R.id.subjective_del_layout_two);
        subjective_del_layout_three = view.findViewById(R.id.subjective_del_layout_three);

        subjective_del_one = view.findViewById(R.id.subjective_del_one);
        subjective_del_two = view.findViewById(R.id.subjective_del_two);
        subjective_del_three = view.findViewById(R.id.subjective_del_three);

        //设置点击事件
        subjective_del_one.setOnClickListener(this);
        subjective_del_two.setOnClickListener(this);
        subjective_del_three.setOnClickListener(this);

    }
    public void fromCameraCallback(ArrayList<String> listImages) {
        if (listImages.size() == 1){
            subjective_answer_frame_one.setVisibility(VISIBLE);
            Glide.with(mContext)
                    .load(listImages.get(0))
                    .into(subjective_img_one);
        }else if (listImages.size() == 2){
            subjective_answer_frame_one.setVisibility(VISIBLE);
            subjective_answer_frame_two.setVisibility(VISIBLE);
            Glide.with(mContext)
                    .load(listImages.get(0))
                    .into(subjective_img_one);
            Glide.with(mContext)
                    .load(listImages.get(1))
                    .into(subjective_img_two);
        }else if (listImages.size() == 3){
            subjective_answer_frame_one.setVisibility(VISIBLE);
            subjective_answer_frame_two.setVisibility(VISIBLE);
            subjective_answer_frame_three.setVisibility(VISIBLE);
            Glide.with(mContext)
                    .load(listImages.get(0))
                    .into(subjective_img_one);
            Glide.with(mContext)
                    .load(listImages.get(1))
                    .into(subjective_img_two);
            Glide.with(mContext)
                    .load(listImages.get(2))
                    .into(subjective_img_three);
        }else {
            subjective_answer_frame_one.setVisibility(INVISIBLE);
            subjective_answer_frame_two.setVisibility(INVISIBLE);
            subjective_answer_frame_three.setVisibility(INVISIBLE);
        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.subjective_del_one:
                //要知道点击的题的id
                String tag_one = (String) getTag();
                    removeImage(tag_one,0);
                break;
            case R.id.subjective_del_two:
                String tag_two = (String) getTag();
                removeImage(tag_two,1);

                break;
            case R.id.subjective_del_three:
                String tag_three = (String) getTag();
                removeImage(tag_three,2);
                break;
        }
    }

    private void removeImage(String tag, int index) {
        SubjeatSaveBean subjeatSaveBean = new SubjeatSaveBean();
        subjeatSaveBean.setId(tag);
        SubjeatSaveBean saveBean = MyApplication.getInstance().getDaoSession().getSubjeatSaveBeanDao()
                .queryBuilder().where(SubjeatSaveBeanDao.Properties.Id.eq(tag)).unique();
        subjeatSaveBean.setLayoutPosition(saveBean.getLayoutPosition());
        if (saveBean != null) {
            if (!TextUtils.isEmpty(saveBean.getImages())) {
                String images = saveBean.getImages();
                String[] strings = images.split("\\|");
                List<String> stringList = new ArrayList<>();
                for (String string : strings) {
                    stringList.add(string);
                }
                stringList.remove(index);

                StringBuffer sb=new StringBuffer();

                if (stringList.size() == 0){
                    subjeatSaveBean.setImages("");
                }else {
                    for (String s : stringList) {
                        sb.append(s+"|");

                    }

                    String imagesWord=sb.toString();
                    imagesWord=imagesWord.substring(0,imagesWord.length()-1);
                    subjeatSaveBean.setImages(imagesWord);
                    QZXTools.logE("imagesWord="+imagesWord,null);
                }





                MyApplication.getInstance().getDaoSession().getSubjeatSaveBeanDao().insertOrReplace(subjeatSaveBean);

                EventBus.getDefault().post("deleteItemImage","deleteItemImage");
            }


        }
    }

    public void setHideDel() {
        subjective_del_one.setVisibility(GONE);
        subjective_del_two .setVisibility(GONE);
        subjective_del_three .setVisibility(GONE);
    }
}
