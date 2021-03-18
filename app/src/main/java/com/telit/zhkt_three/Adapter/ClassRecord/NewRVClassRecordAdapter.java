package com.telit.zhkt_three.Adapter.ClassRecord;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.telit.zhkt_three.Activity.ClassRecord.RecordDiscussShowActivity;
import com.telit.zhkt_three.Activity.ClassRecord.RecordVoteResultActivity;
import com.telit.zhkt_three.Constant.Constant;
import com.telit.zhkt_three.CustomView.ClassRecord.ClassRecordItemView;
import com.telit.zhkt_three.JavaBean.ClassRecord.ActualOrderClassRecord;
import com.telit.zhkt_three.MediaTools.image.ImageLookActivity;
import com.telit.zhkt_three.R;
import com.telit.zhkt_three.Utils.QZXTools;

import java.util.ArrayList;
import java.util.List;

/**
 * author: qzx
 * Date: 2019/12/26 19:43
 * <p>
 * 优化：同样的id,这样在外层的点击一次代码就可以了
 */
public class NewRVClassRecordAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<ActualOrderClassRecord> mDatas;

    //是否没有可加载的数据
    private boolean isAllEnd = false;

    public boolean isAllEnd() {
        return isAllEnd;
    }

    public void setAllEnd(boolean allEnd) {
        isAllEnd = allEnd;
    }

    //底部是否可见
    private boolean isFootVisible = false;

    public void setFootVisible(boolean footVisible) {
        isFootVisible = footVisible;
    }

    public boolean isFootVisible() {
        return isFootVisible;
    }

    public NewRVClassRecordAdapter(Context context, List<ActualOrderClassRecord> actualOrderClassRecords) {
        mContext = context;
        mDatas = actualOrderClassRecords;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int type) {
        RecyclerView.ViewHolder viewHolder = null;
        QZXTools.logE("type=" + type, null);
        if (type == Constant.Head_ClassRecord) {
            viewHolder = new HeadNewRVClassRecordViewHolder(LayoutInflater.from(mContext)
                    .inflate(R.layout.rv_class_record_item_head_layout, viewGroup, false));
        } else if (type == Constant.Mid_ClassRecord) {
            viewHolder = new MidNewRVClassRecordViewHolder(LayoutInflater.from(mContext)
                    .inflate(R.layout.rv_class_record_item_mid_layout, viewGroup, false));
        } else if (type == Constant.Foot_ClassRecord) {
            viewHolder = new FootNewRVClassRecordViewHolder(LayoutInflater.from(mContext)
                    .inflate(R.layout.rv_class_record_item_foot_layout, viewGroup, false));
        } else if (type == Constant.End_Foot) {
            viewHolder = new FootViewHolder(LayoutInflater.from(mContext)
                    .inflate(R.layout.rv_load_more_layout, viewGroup, false));
        } else if (type == Constant.Only_One_Row) {
            viewHolder = new OnlyOneRowViewHolder(LayoutInflater.from(mContext)
                    .inflate(R.layout.rv_class_record_item_only_one_row_layout, viewGroup, false));
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        if (viewHolder instanceof HeadNewRVClassRecordViewHolder) {
            HeadNewRVClassRecordViewHolder headNewRVClassRecordViewHolder = (HeadNewRVClassRecordViewHolder) viewHolder;
            headNewRVClassRecordViewHolder.class_record_date_text.setText(mDatas.get(i).getSameDate());
            if (mDatas.get(i).isFirst()) {
                headNewRVClassRecordViewHolder.class_record_one_circle_opaque.setVisibility(View.VISIBLE);
            } else {
                headNewRVClassRecordViewHolder.class_record_one_circle_opaque.setVisibility(View.GONE);
            }

            String[] time_one = mDatas.get(i).getOne().getCreateDate().split(" ");
            headNewRVClassRecordViewHolder.head_class_record_one.setDatas(time_one[1], mDatas.get(i).getOne().getUserName()
                    , Integer.valueOf(mDatas.get(i).getOne().getType()));

            String[] time_two = mDatas.get(i).getTwo().getCreateDate().split(" ");
            headNewRVClassRecordViewHolder.head_class_record_two.setDatas(time_two[1], mDatas.get(i).getTwo().getUserName()
                    , Integer.valueOf(mDatas.get(i).getTwo().getType()));

            String[] time_three = mDatas.get(i).getThree().getCreateDate().split(" ");
            headNewRVClassRecordViewHolder.head_class_record_three.setDatas(time_three[1], mDatas.get(i).getThree().getUserName()
                    , Integer.valueOf(mDatas.get(i).getThree().getType()));

            String[] time_four = mDatas.get(i).getFour().getCreateDate().split(" ");
            headNewRVClassRecordViewHolder.head_class_record_four.setDatas(time_four[1], mDatas.get(i).getFour().getUserName()
                    , Integer.valueOf(mDatas.get(i).getFour().getType()));

        } else if (viewHolder instanceof MidNewRVClassRecordViewHolder) {
            MidNewRVClassRecordViewHolder midNewRVClassRecordViewHolder = (MidNewRVClassRecordViewHolder) viewHolder;

            String[] time_one = mDatas.get(i).getOne().getCreateDate().split(" ");
            midNewRVClassRecordViewHolder.mid_class_record_one.setDatas(time_one[1], mDatas.get(i).getOne().getUserName()
                    , Integer.valueOf(mDatas.get(i).getOne().getType()));

            String[] time_two = mDatas.get(i).getTwo().getCreateDate().split(" ");
            midNewRVClassRecordViewHolder.mid_class_record_two.setDatas(time_two[1], mDatas.get(i).getTwo().getUserName()
                    , Integer.valueOf(mDatas.get(i).getTwo().getType()));

            String[] time_three = mDatas.get(i).getThree().getCreateDate().split(" ");
            midNewRVClassRecordViewHolder.mid_class_record_three.setDatas(time_three[1], mDatas.get(i).getThree().getUserName()
                    , Integer.valueOf(mDatas.get(i).getThree().getType()));

            String[] time_four = mDatas.get(i).getFour().getCreateDate().split(" ");
            midNewRVClassRecordViewHolder.mid_class_record_four.setDatas(time_four[1], mDatas.get(i).getFour().getUserName()
                    , Integer.valueOf(mDatas.get(i).getFour().getType()));

        } else if (viewHolder instanceof FootNewRVClassRecordViewHolder) {
            FootNewRVClassRecordViewHolder footNewRVClassRecordViewHolder = (FootNewRVClassRecordViewHolder) viewHolder;

            if (mDatas.get(i).isLast()) {
                footNewRVClassRecordViewHolder.class_record_one_circle_opaque.setVisibility(View.VISIBLE);
            } else {
                footNewRVClassRecordViewHolder.class_record_one_circle_opaque.setVisibility(View.GONE);
            }

            String[] time_one = mDatas.get(i).getOne().getCreateDate().split(" ");
            footNewRVClassRecordViewHolder.foot_class_record_one.setDatas(time_one[1], mDatas.get(i).getOne().getUserName()
                    , Integer.valueOf(mDatas.get(i).getOne().getType()));

            if (mDatas.get(i).getTwo() == null) {
                footNewRVClassRecordViewHolder.foot_class_record_two.setVisibility(View.INVISIBLE);
            } else {
                footNewRVClassRecordViewHolder.foot_class_record_two.setVisibility(View.VISIBLE);
                String[] time_two = mDatas.get(i).getTwo().getCreateDate().split(" ");
                footNewRVClassRecordViewHolder.foot_class_record_two.setDatas(time_two[1], mDatas.get(i).getTwo().getUserName()
                        , Integer.valueOf(mDatas.get(i).getTwo().getType()));
            }

            if (mDatas.get(i).getThree() == null) {
                footNewRVClassRecordViewHolder.foot_class_record_three.setVisibility(View.INVISIBLE);
            } else {
                footNewRVClassRecordViewHolder.foot_class_record_three.setVisibility(View.VISIBLE);
                String[] time_three = mDatas.get(i).getThree().getCreateDate().split(" ");
                footNewRVClassRecordViewHolder.foot_class_record_three.setDatas(time_three[1], mDatas.get(i).getThree().getUserName()
                        , Integer.valueOf(mDatas.get(i).getThree().getType()));
            }

            if (mDatas.get(i).getFour() == null) {
                footNewRVClassRecordViewHolder.foot_class_record_four.setVisibility(View.INVISIBLE);
            } else {
                footNewRVClassRecordViewHolder.foot_class_record_four.setVisibility(View.VISIBLE);
                String[] time_four = mDatas.get(i).getFour().getCreateDate().split(" ");
                footNewRVClassRecordViewHolder.foot_class_record_four.setDatas(time_four[1], mDatas.get(i).getFour().getUserName()
                        , Integer.valueOf(mDatas.get(i).getFour().getType()));
            }

        } else if (viewHolder instanceof FootViewHolder) {
            FootViewHolder footViewHolder = (FootViewHolder) viewHolder;
            if (isAllEnd) {
                footViewHolder.loadMoreTv.setVisibility(View.VISIBLE);
                footViewHolder.loadMoreProgress.setVisibility(View.GONE);
                footViewHolder.loadMoreTv.setText("---已经是底线了---");
            } else {
                if (isFootVisible) {
                    footViewHolder.loadMoreTv.setVisibility(View.VISIBLE);
                    footViewHolder.loadMoreProgress.setVisibility(View.VISIBLE);
                    footViewHolder.loadMoreTv.setText("加载中...");
                } else {
                    footViewHolder.loadMoreTv.setVisibility(View.GONE);
                    footViewHolder.loadMoreProgress.setVisibility(View.GONE);
                }
            }

        } else if (viewHolder instanceof OnlyOneRowViewHolder) {
            OnlyOneRowViewHolder onlyOneRowViewHolder = (OnlyOneRowViewHolder) viewHolder;

            onlyOneRowViewHolder.class_record_date_text.setText(mDatas.get(i).getSameDate());

            if (mDatas.get(i).isFirst()) {
                onlyOneRowViewHolder.class_record_one_circle_opaque_head.setVisibility(View.VISIBLE);
            } else {
                onlyOneRowViewHolder.class_record_one_circle_opaque_head.setVisibility(View.GONE);
            }

            if (mDatas.get(i).isLast()) {
                onlyOneRowViewHolder.class_record_one_circle_opaque_foot.setVisibility(View.VISIBLE);
            } else {
                onlyOneRowViewHolder.class_record_one_circle_opaque_foot.setVisibility(View.GONE);
            }

            String[] time_one = mDatas.get(i).getOne().getCreateDate().split(" ");
            onlyOneRowViewHolder.head_class_record_one.setDatas(time_one[1], mDatas.get(i).getOne().getUserName()
                    , Integer.valueOf(mDatas.get(i).getOne().getType()));

            if (mDatas.get(i).getTwo() == null) {
                onlyOneRowViewHolder.head_class_record_two.setVisibility(View.INVISIBLE);
            } else {
                onlyOneRowViewHolder.head_class_record_two.setVisibility(View.VISIBLE);
                String[] time_two = mDatas.get(i).getTwo().getCreateDate().split(" ");
                onlyOneRowViewHolder.head_class_record_two.setDatas(time_two[1], mDatas.get(i).getTwo().getUserName()
                        , Integer.valueOf(mDatas.get(i).getTwo().getType()));
            }

            if (mDatas.get(i).getThree() == null) {
                onlyOneRowViewHolder.head_class_record_three.setVisibility(View.INVISIBLE);
            } else {
                onlyOneRowViewHolder.head_class_record_three.setVisibility(View.VISIBLE);
                String[] time_three = mDatas.get(i).getThree().getCreateDate().split(" ");
                onlyOneRowViewHolder.head_class_record_three.setDatas(time_three[1], mDatas.get(i).getThree().getUserName()
                        , Integer.valueOf(mDatas.get(i).getThree().getType()));
            }

            if (mDatas.get(i).getFour() == null) {
                onlyOneRowViewHolder.head_class_record_four.setVisibility(View.INVISIBLE);
            } else {
                onlyOneRowViewHolder.head_class_record_four.setVisibility(View.VISIBLE);
                String[] time_four = mDatas.get(i).getFour().getCreateDate().split(" ");
                onlyOneRowViewHolder.head_class_record_four.setDatas(time_four[1], mDatas.get(i).getFour().getUserName()
                        , Integer.valueOf(mDatas.get(i).getFour().getType()));
            }

        }
    }

    @Override
    public int getItemCount() {
        return mDatas != null ? mDatas.size() + 1 : 0;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == mDatas.size()) {
            return Constant.End_Foot;
        }
        int type = mDatas.get(position).getType();
        return type;
    }

    public class HeadNewRVClassRecordViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView class_record_date_text;
        private View class_record_one_circle_opaque;

        private ClassRecordItemView head_class_record_one;
        private ClassRecordItemView head_class_record_two;
        private ClassRecordItemView head_class_record_three;
        private ClassRecordItemView head_class_record_four;

        public HeadNewRVClassRecordViewHolder(@NonNull View itemView) {
            super(itemView);
            class_record_date_text = itemView.findViewById(R.id.class_record_date_text);
            class_record_one_circle_opaque = itemView.findViewById(R.id.class_record_one_circle_opaque);
            head_class_record_one = itemView.findViewById(R.id.head_class_record_one);
            head_class_record_two = itemView.findViewById(R.id.head_class_record_two);
            head_class_record_three = itemView.findViewById(R.id.head_class_record_three);
            head_class_record_four = itemView.findViewById(R.id.head_class_record_four);

            head_class_record_one.setOnClickListener(this);
            head_class_record_two.setOnClickListener(this);
            head_class_record_three.setOnClickListener(this);
            head_class_record_four.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.head_class_record_one:
                    handlerDetail(mDatas.get(getLayoutPosition()).getOne().getType()
                            , mDatas.get(getLayoutPosition()).getOne().getFileUrl()
                            , mDatas.get(getLayoutPosition()).getOne().getRecordId());
                    break;
                case R.id.head_class_record_two:
                    handlerDetail(mDatas.get(getLayoutPosition()).getTwo().getType()
                            , mDatas.get(getLayoutPosition()).getTwo().getFileUrl()
                            , mDatas.get(getLayoutPosition()).getTwo().getRecordId());
                    break;
                case R.id.head_class_record_three:
                    handlerDetail(mDatas.get(getLayoutPosition()).getThree().getType()
                            , mDatas.get(getLayoutPosition()).getThree().getFileUrl()
                            , mDatas.get(getLayoutPosition()).getThree().getRecordId());
                    break;
                case R.id.head_class_record_four:
                    handlerDetail(mDatas.get(getLayoutPosition()).getFour().getType()
                            , mDatas.get(getLayoutPosition()).getFour().getFileUrl()
                            , mDatas.get(getLayoutPosition()).getFour().getRecordId());
                    break;
            }
        }
    }

    public class MidNewRVClassRecordViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ClassRecordItemView mid_class_record_one;
        private ClassRecordItemView mid_class_record_two;
        private ClassRecordItemView mid_class_record_three;
        private ClassRecordItemView mid_class_record_four;

        public MidNewRVClassRecordViewHolder(@NonNull View itemView) {
            super(itemView);
            mid_class_record_one = itemView.findViewById(R.id.mid_class_record_one);
            mid_class_record_two = itemView.findViewById(R.id.mid_class_record_two);
            mid_class_record_three = itemView.findViewById(R.id.mid_class_record_three);
            mid_class_record_four = itemView.findViewById(R.id.mid_class_record_four);

            mid_class_record_one.setOnClickListener(this);
            mid_class_record_two.setOnClickListener(this);
            mid_class_record_three.setOnClickListener(this);
            mid_class_record_four.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.mid_class_record_one:
                    handlerDetail(mDatas.get(getLayoutPosition()).getOne().getType()
                            , mDatas.get(getLayoutPosition()).getOne().getFileUrl()
                            , mDatas.get(getLayoutPosition()).getOne().getRecordId());
                    break;
                case R.id.mid_class_record_two:
                    handlerDetail(mDatas.get(getLayoutPosition()).getTwo().getType()
                            , mDatas.get(getLayoutPosition()).getTwo().getFileUrl()
                            , mDatas.get(getLayoutPosition()).getTwo().getRecordId());
                    break;
                case R.id.mid_class_record_three:
                    handlerDetail(mDatas.get(getLayoutPosition()).getThree().getType()
                            , mDatas.get(getLayoutPosition()).getThree().getFileUrl()
                            , mDatas.get(getLayoutPosition()).getThree().getRecordId());
                    break;
                case R.id.mid_class_record_four:
                    handlerDetail(mDatas.get(getLayoutPosition()).getFour().getType()
                            , mDatas.get(getLayoutPosition()).getFour().getFileUrl()
                            , mDatas.get(getLayoutPosition()).getFour().getRecordId());
                    break;
            }
        }
    }


    public class FootNewRVClassRecordViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private View class_record_one_circle_opaque;

        private ClassRecordItemView foot_class_record_one;
        private ClassRecordItemView foot_class_record_two;
        private ClassRecordItemView foot_class_record_three;
        private ClassRecordItemView foot_class_record_four;

        public FootNewRVClassRecordViewHolder(@NonNull View itemView) {
            super(itemView);
            class_record_one_circle_opaque = itemView.findViewById(R.id.class_record_one_circle_opaque);
            foot_class_record_one = itemView.findViewById(R.id.foot_class_record_one);
            foot_class_record_two = itemView.findViewById(R.id.foot_class_record_two);
            foot_class_record_three = itemView.findViewById(R.id.foot_class_record_three);
            foot_class_record_four = itemView.findViewById(R.id.foot_class_record_four);

            foot_class_record_one.setOnClickListener(this);
            foot_class_record_two.setOnClickListener(this);
            foot_class_record_three.setOnClickListener(this);
            foot_class_record_four.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.foot_class_record_one:
                    handlerDetail(mDatas.get(getLayoutPosition()).getOne().getType()
                            , mDatas.get(getLayoutPosition()).getOne().getFileUrl()
                            , mDatas.get(getLayoutPosition()).getOne().getRecordId());
                    break;
                case R.id.foot_class_record_two:
                    handlerDetail(mDatas.get(getLayoutPosition()).getTwo().getType()
                            , mDatas.get(getLayoutPosition()).getTwo().getFileUrl()
                            , mDatas.get(getLayoutPosition()).getTwo().getRecordId());
                    break;
                case R.id.foot_class_record_three:
                    handlerDetail(mDatas.get(getLayoutPosition()).getThree().getType()
                            , mDatas.get(getLayoutPosition()).getThree().getFileUrl()
                            , mDatas.get(getLayoutPosition()).getThree().getRecordId());
                    break;
                case R.id.foot_class_record_four:
                    handlerDetail(mDatas.get(getLayoutPosition()).getFour().getType()
                            , mDatas.get(getLayoutPosition()).getFour().getFileUrl()
                            , mDatas.get(getLayoutPosition()).getFour().getRecordId());
                    break;
            }
        }
    }

    public class FootViewHolder extends RecyclerView.ViewHolder {

        TextView loadMoreTv;
        ProgressBar loadMoreProgress;

        public FootViewHolder(View itemView) {
            super(itemView);
            loadMoreProgress = itemView.findViewById(R.id.load_more_progress);
            loadMoreTv = itemView.findViewById(R.id.load_more_text);
        }
    }

    public class OnlyOneRowViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView class_record_date_text;
        private View class_record_one_circle_opaque_head;
        private View class_record_one_circle_opaque_foot;

        private ClassRecordItemView head_class_record_one;
        private ClassRecordItemView head_class_record_two;
        private ClassRecordItemView head_class_record_three;
        private ClassRecordItemView head_class_record_four;

        public OnlyOneRowViewHolder(@NonNull View itemView) {
            super(itemView);
            class_record_date_text = itemView.findViewById(R.id.class_record_date_text);
            class_record_one_circle_opaque_head = itemView.findViewById(R.id.class_record_one_circle_opaque_head);
            class_record_one_circle_opaque_foot = itemView.findViewById(R.id.class_record_one_circle_opaque_foot);
            head_class_record_one = itemView.findViewById(R.id.head_class_record_one);
            head_class_record_two = itemView.findViewById(R.id.head_class_record_two);
            head_class_record_three = itemView.findViewById(R.id.head_class_record_three);
            head_class_record_four = itemView.findViewById(R.id.head_class_record_four);

            head_class_record_one.setOnClickListener(this);
            head_class_record_two.setOnClickListener(this);
            head_class_record_three.setOnClickListener(this);
            head_class_record_four.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.head_class_record_one:
                    handlerDetail(mDatas.get(getLayoutPosition()).getOne().getType()
                            , mDatas.get(getLayoutPosition()).getOne().getFileUrl()
                            , mDatas.get(getLayoutPosition()).getOne().getRecordId());
                    break;
                case R.id.head_class_record_two:
                    handlerDetail(mDatas.get(getLayoutPosition()).getTwo().getType()
                            , mDatas.get(getLayoutPosition()).getTwo().getFileUrl()
                            , mDatas.get(getLayoutPosition()).getTwo().getRecordId());
                    break;
                case R.id.head_class_record_three:
                    handlerDetail(mDatas.get(getLayoutPosition()).getThree().getType()
                            , mDatas.get(getLayoutPosition()).getThree().getFileUrl()
                            , mDatas.get(getLayoutPosition()).getThree().getRecordId());
                    break;
                case R.id.head_class_record_four:
                    handlerDetail(mDatas.get(getLayoutPosition()).getFour().getType()
                            , mDatas.get(getLayoutPosition()).getFour().getFileUrl()
                            , mDatas.get(getLayoutPosition()).getFour().getRecordId());
                    break;
            }
        }
    }

    /**
     * 依据Type处理点击的详情
     *
     * @param type   跳转的类型
     * @param imgUrl 图片的预览地址
     * @param id     详情的参数id
     */
    private void handlerDetail(String type, String imgUrl, String id) {
        int sign = Integer.valueOf(type);
        switch (sign) {
            case Constant.Class_Record_Shot:

                //直接展示图片
                Intent intent_img = new Intent(mContext, ImageLookActivity.class);
                ArrayList<String> imgFilePathList = new ArrayList<>();
                imgFilePathList.add(imgUrl);
                intent_img.putStringArrayListExtra("imgResources", imgFilePathList);
                intent_img.putExtra("curImgIndex", 0);
                mContext.startActivity(intent_img);

                break;
            case Constant.Class_Record_Vote:

                Intent intent_vote = new Intent(mContext, RecordVoteResultActivity.class);
                intent_vote.putExtra("recordId", id);
                intent_vote.putExtra("type", type);
                mContext.startActivity(intent_vote);

                break;
            case Constant.Class_Record_Discuss:

                Intent intent_discuss = new Intent(mContext, RecordDiscussShowActivity.class);
                intent_discuss.putExtra("recordId", id);
                intent_discuss.putExtra("type", type);
                mContext.startActivity(intent_discuss);

                break;
        }
    }
}
