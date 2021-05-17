package com.telit.zhkt_three.CustomView;

import android.content.Context;
import android.graphics.Path;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.telit.zhkt_three.Adapter.QuestionAdapter.LineLeftAdapter;
import com.telit.zhkt_three.Adapter.QuestionAdapter.RVQuestionTvAnswerAdapter;
import com.telit.zhkt_three.Constant.Constant;
import com.telit.zhkt_three.CustomView.QuestionView.matching.ToLineView;
import com.telit.zhkt_three.JavaBean.FillBlankBean;
import com.telit.zhkt_three.JavaBean.HomeWork.QuestionInfo;
import com.telit.zhkt_three.JavaBean.HomeWorkAnswerSave.AnswerItem;
import com.telit.zhkt_three.JavaBean.HomeWorkAnswerSave.LocalTextAnswersBean;
import com.telit.zhkt_three.JavaBean.LineMatchBean;
import com.telit.zhkt_three.JavaBean.WorkOwnResult;
import com.telit.zhkt_three.MyApplication;
import com.telit.zhkt_three.R;
import com.telit.zhkt_three.Utils.QZXTools;
import com.telit.zhkt_three.Utils.UserUtils;
import com.telit.zhkt_three.greendao.LocalTextAnswersBeanDao;
import com.telit.zhkt_three.listener.EdtextListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class LinkLineView extends LinearLayout implements ViewTreeObserver.OnGlobalLayoutListener {
    private static final String TAG = "MulipleChoiseView";
    private Context mContext;

    private final TextView matching_reset;
    private final ToLineView matching_toLine;
    private final RecyclerView rv_matching_show;
    private final LinearLayout ll_match_bind_tag;
    private final TextView practice_head_index;
    //保存连线题 在复用后不能显示的问题
    private List<LineMatchBean> lineMatchs = new ArrayList<>();
    //保存作答痕迹 连线题
    private String saveTrack;
    /**
     * 选中的第一个item
     */
    private View firstChooseView = null;
    private String taskStatus;
    private List<QuestionInfo> questionInfoList;
    private int index;
    private List<QuestionInfo.LeftListBean> leftList;
    private List<QuestionInfo.RightListBean> rightList;
    private LinearLayoutManager layoutManager;
    private List<View> leftViews=new ArrayList<>();

    private List<String> quitntIds=new ArrayList<>();

    public LinkLineView(Context context) {
        this(context, null);
    }

    public LinkLineView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LinkLineView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;


        View itemView = LayoutInflater.from(context).inflate(R.layout.linked_line_image_layout,
                this, true);


        //重置
        matching_reset = itemView.findViewById(R.id.matching_reset);
        //连线的view
        matching_toLine = itemView.findViewById(R.id.matching_toLine);
        rv_matching_show = itemView.findViewById(R.id.rv_matching_show);
        ll_match_bind_tag = itemView.findViewById(R.id.ll_match_bind_tag);
        practice_head_index = itemView.findViewById(R.id.practice_head_index);



    }

    public void setTaskStatus(String taskStatus) {

        this.taskStatus = taskStatus;
    }


    //设置数据
    public void setViewData(List<QuestionInfo.SelectBean> selectBeans, List<QuestionInfo> questionInfoList, int i,
                            String homeworkId) {
        this.questionInfoList = questionInfoList;
        this.index = i;

        Log.i("qin008", "onBindViewHolder: " + questionInfoList.get(i));
        List<Integer> leftPositions = new ArrayList<>();
        List<Integer> rightPositions = new ArrayList<>();
        //作业回显正确答案 左边view 的集合，右边view 的集合

        //连线题
        leftList = questionInfoList.get(i).getLeftList();
        rightList = questionInfoList.get(i).getRightList();
        if (leftList!=null && leftList.size()>0){
            quitntIds.clear();
            for (QuestionInfo.LeftListBean leftListBean : leftList) {
                quitntIds.add(leftListBean.getId());
            }
        }

        LineLeftAdapter lineLeftAdapter = new LineLeftAdapter(mContext, leftList, rightList);
        layoutManager = new LinearLayoutManager(mContext) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };

        rv_matching_show.setLayoutManager(layoutManager);
        rv_matching_show.setAdapter(lineLeftAdapter);
        practice_head_index.setText("第" + (i + 1) + "题 共" + questionInfoList.size() + "题");


        //0未提交  1 已提交  2 已批阅
        if (taskStatus.equals(Constant.Todo_Status) || taskStatus.equals(Constant.Save_Status)) {
            //重置的点击事件
            matching_reset.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (firstChooseView != null) {
                        firstChooseView = null;
                    }
                    saveTrack = "";
                    matching_toLine.resetDrawLine();

                    //同时要清空保存的集合数据
                    Iterator<LineMatchBean> iterator = lineMatchs.iterator();
                    while (iterator.hasNext()) {
                        LineMatchBean lineMatchBean = iterator.next();
                        if (lineMatchBean.getTypeId().equals(questionInfoList.get(i).getId())) {
                            iterator.remove();
                            MyApplication.getInstance().getDaoSession().getLineMatchBeanDao().delete(lineMatchBean);
                        }
                    }

                    MyApplication.getInstance().getDaoSession().getLineMatchBeanDao().deleteAll();
                    //TODO 重置要删除数据库中的数据
                    MyApplication.getInstance().getDaoSession().getLocalTextAnswersBeanDao().deleteAll();
                    //todo 现在的问题是回显返回的saveTrack  还不对，还在想怎么正确获取

                }


            });

            //左边点击连线
            lineLeftAdapter.setOnLeftOnClickListener(new LineLeftAdapter.onLeftOnClickListener() {
                private TextView rightTextView;
                private TextView liftTextView;
                float sx;
                float sy;
                float ex;
                float ey;
                Path path = null;
                int leftPosition;
                int rightPosition;
                boolean isLiftContains = false;
                boolean isRightContains = false;

                @Override
                public void onLeftItemCheck(int position) {
                    //  ((LinkedLineHolder) viewHolder).matching_toLine.resetDrawLine();
                    View view = layoutManager.findViewByPosition(position);
                    //从左边开始点击画线
                    StringBuffer stringBuffer = new StringBuffer();
                    //清空一下
                    stringBuffer.setLength(0);
                    LocalTextAnswersBean linkLocal = MyApplication.getInstance().getDaoSession().getLocalTextAnswersBeanDao()
                            .queryBuilder().where(LocalTextAnswersBeanDao.Properties.QuestionId.eq(questionInfoList.get(i).getId()),
                                    LocalTextAnswersBeanDao.Properties.HomeworkId.eq(homeworkId),
                                    LocalTextAnswersBeanDao.Properties.UserId.eq(UserUtils.getUserId())).unique();

                    Log.i(TAG, "onBindViewHolder: " + linkLocal);
                    if (linkLocal != null) {
                        //回显上次点击的数据  根据id 获取是左边的列表还是右边的列表
                        //获取到下标，然后开始连线
                        String ownAnswer = linkLocal.getAnswerContent();
                        stringBuffer.append(ownAnswer);
                    }
                    if (!TextUtils.isEmpty(saveTrack)) {
                        //stringBuffer.append(saveTrack);
                        stringBuffer.append("|");
                    }

                    liftTextView = view.findViewById(R.id.tv_item_line_word_left);
                    if (isRightContains) {
                        //点击左边第一个已经被连线了，右边的也就不能连线了
                        isRightContains = false;
                        return;
                    }
                    //同一区域的不能选中
                    if (firstChooseView != null && firstChooseView == liftTextView) {
                        isLiftContains = true;
                        return;
                    }
                    isLiftContains = false;
                    //如果集合中有视图表示已经连接过了,或者正在动画中也不执行
                    if (leftPositions.contains(position) || matching_toLine.isAnimRunning()) {
                        return;
                    }


                    if (firstChooseView == null) {
                        firstChooseView = liftTextView;
                        sx = liftTextView.getLeft() + liftTextView.getWidth();
                        sy = view.getTop() + liftTextView.getHeight() * 1.0f / 2.0f;
                        path = new Path();
                        leftPosition = position;
                        liftTextView.setBackground(mContext.getResources().getDrawable(R.drawable.shape_line_bg_with_border));
                    }
                    if (firstChooseView == rightTextView) {

                        //点击第一个是右边的view
                        ex = liftTextView.getLeft() + liftTextView.getWidth();
                        ey = view.getTop() + liftTextView.getHeight() * 1.0f / 2.0f;
                        path.addCircle(sx, sy, 10, Path.Direction.CW);
                        path.moveTo(sx, sy);
                        path.lineTo(ex, ey);
                        path.addCircle(ex, ey, 10, Path.Direction.CW);

                        //绘制Path   这里开始画
                        matching_toLine.getDrawPath(path);
                        //初始化左边的view
                        firstChooseView = null;
                        //处理集合中有视图表示已经连接过了就不要再连接了
                        leftPositions.add(position);
                        rightPositions.add(rightPosition);
                        rightTextView.setBackground(mContext.getResources().getDrawable(R.drawable.shape_line_item_bg));

                        //保存当前的下表的连线题 状态下的 连线的所有的坐标，然后再滑动的时候回显
                        LineMatchBean lineMatchBean = new LineMatchBean();
                        lineMatchBean.setTypeId(questionInfoList.get(i).getId());
                        lineMatchBean.setPosition(i);
                        lineMatchBean.setStartX(sx);
                        lineMatchBean.setStartY(sy);
                        lineMatchBean.setEndX(ex);
                        lineMatchBean.setEndY(ey);

                        lineMatchBean.setLeftId(leftList.get(position).getId());
                        lineMatchBean.setRightId(rightList.get(rightPosition).getId());

                        lineMatchs.add(lineMatchBean);
                        MyApplication.getInstance().getDaoSession().getLineMatchBeanDao().insertOrReplace(lineMatchBean);
                        // saveTrack 是显示绘制的状态 的保留  点击每一个获取点击的id的保存

                        stringBuffer.append(questionInfoList.get(i).getRightList().get(rightPosition).getId());
                        stringBuffer.append(",");
                        stringBuffer.append(questionInfoList.get(i).getLeftList().get(position).getId());
                        saveTrack = stringBuffer.toString();

                        //连线题把连线画好的线保存数据库下次回显
                        //-------------------------答案保存，依据作业题目id   主要就是这个作业id 不一样
                        LocalTextAnswersBean localTextAnswersBean = new LocalTextAnswersBean();
                        localTextAnswersBean.setHomeworkId(homeworkId);
                        localTextAnswersBean.setQuestionId(questionInfoList.get(i).getId());
                        localTextAnswersBean.setQuestionType(questionInfoList.get(i).getQuestionType());
                        localTextAnswersBean.setAnswerContent(saveTrack);
                        localTextAnswersBean.setUserId(UserUtils.getUserId());
//                                QZXTools.logE("Save localTextAnswersBean=" + localTextAnswersBean, null);
                        //插入或者更新数据库
                        MyApplication.getInstance().getDaoSession().getLocalTextAnswersBeanDao().insertOrReplace(localTextAnswersBean);


                        //关闭的时候保留saveTrack
                        matching_toLine.setLocalSave(homeworkId, questionInfoList.get(i), saveTrack);

                    }


                }

                @Override
                public void onRightItemClick(int position) {
                    RelativeLayout view = (RelativeLayout) layoutManager.findViewByPosition(position);
                    rightTextView = view.findViewById(R.id.tv_item_line_word_right);

                    StringBuffer stringBuffer = new StringBuffer();
                    //清空一下
                    stringBuffer.setLength(0);
                    LocalTextAnswersBean linkLocal = MyApplication.getInstance().getDaoSession().getLocalTextAnswersBeanDao()
                            .queryBuilder().where(LocalTextAnswersBeanDao.Properties.QuestionId.eq(questionInfoList.get(i).getId()),
                                    LocalTextAnswersBeanDao.Properties.HomeworkId.eq(homeworkId),
                                    LocalTextAnswersBeanDao.Properties.UserId.eq(UserUtils.getUserId())).unique();

                    Log.i(TAG, "onBindViewHolder: " + linkLocal);
                    if (linkLocal != null) {
                        //回显上次点击的数据  根据id 获取是左边的列表还是右边的列表
                        //获取到下标，然后开始连线
                        String ownAnswer = linkLocal.getAnswerContent();
                        stringBuffer.append(ownAnswer);
                    }
                    if (!TextUtils.isEmpty(saveTrack)) {
                        //  stringBuffer.append(saveTrack);
                        stringBuffer.append("|");
                    }
                    if (isLiftContains) {
                        //点击左边第一个已经被连线了，右边的也就不能连线了 下次再次点击就还原
                        isLiftContains = false;
                        return;
                    }
                    //同一区域的不能选中
                    if (firstChooseView != null && firstChooseView == rightTextView) {
                        return;
                    }

                    //如果集合中有视图表示已经连接过了,或者正在动画中也不执行
                    if (rightPositions.contains(position) || matching_toLine.isAnimRunning()) {
                        isRightContains = true;
                        return;
                    }
                    isRightContains = false;
                    //从右边第一次点击画view
                    if (firstChooseView == null) {
                        firstChooseView = rightTextView;
                        sx = rightTextView.getLeft();
                        sy = view.getTop() + rightTextView.getHeight() * 1.0f / 2.0f;
                        path = new Path();
                        rightPosition = position;
                        rightTextView.setBackground(mContext.getResources().getDrawable(R.drawable.shape_line_bg_with_border));
                    }
                    QZXTools.logE("sx=" + sx + ";sy=" + sy + ";ex=" + ex + ";ey=" + ey, null);
                    if (firstChooseView == liftTextView) {

                        //点击第一个是左边的view
                        ex = rightTextView.getLeft();
                        ey = view.getTop() + rightTextView.getHeight() * 1.0f / 2.0f;
                        path.addCircle(sx, sy, 10, Path.Direction.CW);
                        path.moveTo(sx, sy);
                        path.lineTo(ex, ey);
                        path.addCircle(ex, ey, 10, Path.Direction.CW);

                        //绘制Path   这里开始画
                        matching_toLine.getDrawPath(path);
                        //初始化左边的view
                        firstChooseView = null;
                        rightPositions.add(position);
                        leftPositions.add(leftPosition);
                        liftTextView.setBackground(mContext.getResources().getDrawable(R.drawable.shape_line_item_bg));

                        //保存当前的下表的连线题 状态下的 连线的所有的坐标，然后再滑动的时候回显
                        LineMatchBean lineMatchBean = new LineMatchBean();
                        //设置当前题型的id
                        lineMatchBean.setTypeId(questionInfoList.get(i).getId());
                        lineMatchBean.setLeftId(leftList.get(leftPosition).getId());
                        lineMatchBean.setRightId(rightList.get(position).getId());
                        lineMatchBean.setPosition(i);
                        lineMatchBean.setStartX(sx);
                        lineMatchBean.setStartY(sy);
                        lineMatchBean.setEndX(ex);
                        lineMatchBean.setEndY(ey);


                        lineMatchs.add(lineMatchBean);
                        //把所有的点保存到数据库
                        MyApplication.getInstance().getDaoSession().getLineMatchBeanDao().insertOrReplace(lineMatchBean);

                        stringBuffer.append(questionInfoList.get(i).getLeftList().get(leftPosition).getId());
                        stringBuffer.append(",");
                        stringBuffer.append(questionInfoList.get(i).getRightList().get(position).getId());
                        saveTrack = stringBuffer.toString();

                        //连线题把连线画好的线保存数据库下次回显
                        //-------------------------答案保存，依据作业题目id   主要就是这个作业id 不一样
                        LocalTextAnswersBean localTextAnswersBean = new LocalTextAnswersBean();
                        localTextAnswersBean.setHomeworkId(homeworkId);
                        localTextAnswersBean.setQuestionId(questionInfoList.get(i).getId());
                        localTextAnswersBean.setQuestionType(questionInfoList.get(i).getQuestionType());
                        localTextAnswersBean.setAnswerContent(saveTrack);
                        localTextAnswersBean.setUserId(UserUtils.getUserId());
//                                QZXTools.logE("Save localTextAnswersBean=" + localTextAnswersBean, null);
                        //插入或者更新数据库
                        MyApplication.getInstance().getDaoSession().getLocalTextAnswersBeanDao().insertOrReplace(localTextAnswersBean);

                        //关闭的时候保留saveTrack
                        matching_toLine.setLocalSave(homeworkId, questionInfoList.get(i), saveTrack);

                    }
                }
            });
            ll_match_bind_tag.setTag(questionInfoList.get(i).getId());
            rv_matching_show.setLayoutManager(layoutManager);
            rv_matching_show.setAdapter(lineLeftAdapter);

            //设置当前连线题是不是已经绘制过；
            List<LineMatchBean> currentLineMatchBeans = new ArrayList<>();

            //保存连接的数据已经保存了，现在要回显
            //数据的保存就是根据后台返回的数据题的id 封装成一个javabean  然后遍历回显当前条目是不是保存了 主要是数据驱动视图
            if (lineMatchs.size() > 0) {
                for (int j = 0; j < lineMatchs.size(); j++) {
                    LineMatchBean lineMatchBean = lineMatchs.get(j);
                    if (lineMatchBean.getTypeId().equals(questionInfoList.get(i).getId())) {
                        //当前的连线已经连线过现在要回显、
                        Path pathC = new Path();
                        pathC.addCircle(lineMatchBean.getStartX(), lineMatchBean.getStartY(), 10, Path.Direction.CW);
                        pathC.addCircle(lineMatchBean.getEndX(), lineMatchBean.getEndY(), 10, Path.Direction.CW);
                        Path pathL = new Path();
                        pathL.moveTo(lineMatchBean.getStartX(), lineMatchBean.getStartY());
                        pathL.lineTo(lineMatchBean.getEndX(), lineMatchBean.getEndY());
                        //添加点路径和线路径
                        matching_toLine.addDotPath(pathC, false, questionInfoList.get(i).getId());
                        matching_toLine.addLinePath(pathL, false);

                        currentLineMatchBeans.add(lineMatchBean);

                    } else {
                        if (currentLineMatchBeans.size() > 0) {
                            matching_toLine.setDrawStatus(0);
                        } else {
                            matching_toLine.resetDrawLine(questionInfoList.get(i).getId());
                        }
                    }
                }
            }

            if (taskStatus.equals(Constant.Todo_Status) ){
                //答案的回显
                //查询保存的答案,这是多选，所以存在多个答案
                LocalTextAnswersBean linkLocal = MyApplication.getInstance().getDaoSession().getLocalTextAnswersBeanDao()
                        .queryBuilder().where(LocalTextAnswersBeanDao.Properties.QuestionId.eq(questionInfoList.get(i).getId()),
                                LocalTextAnswersBeanDao.Properties.HomeworkId.eq(homeworkId),
                                LocalTextAnswersBeanDao.Properties.UserId.eq(UserUtils.getUserId())).unique();

                List<LineMatchBean> lineMatchBeans = MyApplication.getInstance().getDaoSession().getLineMatchBeanDao().loadAll();
                Log.i(TAG, "onBindViewHolder: " + linkLocal + ".............." + lineMatchBeans);
                for (int j = 0; j < lineMatchBeans.size(); j++) {
                    LineMatchBean lineMatchBean = lineMatchBeans.get(j);
                    if (lineMatchBean.getTypeId().equals(questionInfoList.get(i).getId())) {
                        Path pathC = new Path();
                        pathC.addCircle(lineMatchBean.getStartX(), lineMatchBean.getStartY(), 10, Path.Direction.CW);
                        pathC.addCircle(lineMatchBean.getEndX(), lineMatchBean.getEndY(), 10, Path.Direction.CW);
                        Path pathL = new Path();
                        pathL.moveTo(lineMatchBean.getStartX(), lineMatchBean.getStartY());
                        pathL.lineTo(lineMatchBean.getEndX(), lineMatchBean.getEndY());
                        //添加点路径和线路径
                        matching_toLine.addDotPath(pathC);
                        matching_toLine.addLinePath(pathL);
                    }
                }
            }else if (taskStatus.equals(Constant.Save_Status)){
                //把保存的画线，画出显示
                //更具id  获取到左边的坐标和右边的左边
                //View加载完成时回调
                rv_matching_show.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        View leftView = null;
                        View rightView = null;
                        View leftTextView = null;
                        View rightTextView = null;
                        //获取所有的recycleview 的item  的布局添加到view 中
                        for (int j = 0; j < leftList.size(); j++) {
                            View view = layoutManager.findViewByPosition(j);
                            leftViews.add(view);
                        }
                        QuestionInfo questionInfo = questionInfoList.get(i);
                        if (questionInfo!=null) {
                            List<WorkOwnResult> ownList = questionInfo.getOwnList();
                            if (ownList != null && ownList.size() > 0) {
                                WorkOwnResult workOwnResult = ownList.get(0);
                                if (workOwnResult != null) {
                                    String answerContent = workOwnResult.getAnswerContent();
                                    if (!TextUtils.isEmpty(answerContent)) {
                                        //获取学生自己的答案

                                        if (answerContent.contains("|")) {
                                            String[] split = answerContent.split("\\|");
                                            for (String item : split) {
                                                String[] trackStr = item.split(",");
                                                //先判断连线的id  是在左边还是在右边
                                                String fistId = trackStr[0];
                                                if (quitntIds.contains(fistId)){
                                                    for (int j = 0; j < leftList.size(); j++) {
                                                        if (trackStr[0].equals(leftList.get(j).getId())) {
                                                            leftView = leftViews.get(j);
                                                            //获取左边的view
                                                            leftTextView = leftView.findViewById(R.id.tv_item_line_word_left);
                                                            break;

                                                        }
                                                    }

                                                    for (int j = 0; j < rightList.size(); j++) {
                                                        if (trackStr[1].equals(rightList.get(j).getId())) {
                                                            rightView = leftViews.get(j);
                                                            //获取左边的view
                                                            rightTextView = rightView.findViewById(R.id.tv_item_line_word_right);
                                                            break;

                                                        }
                                                    }
                                                }else {
                                                    //说明第一个再右边
                                                    for (int j = 0; j < leftList.size(); j++) {
                                                        if (trackStr[1].equals(leftList.get(j).getId())) {
                                                            leftView = leftViews.get(j);
                                                            //获取左边的view
                                                            leftTextView = leftView.findViewById(R.id.tv_item_line_word_left);
                                                            break;

                                                        }
                                                    }

                                                    for (int j = 0; j < rightList.size(); j++) {
                                                        if (trackStr[0].equals(rightList.get(j).getId())) {
                                                            rightView = leftViews.get(j);
                                                            //获取左边的view
                                                            rightTextView = rightView.findViewById(R.id.tv_item_line_word_right);
                                                            break;

                                                        }
                                                    }
                                                }

                                                float sx = leftView.getLeft() + leftTextView.getLeft() + leftTextView.getWidth();
                                                float sy = leftView.getTop() + leftTextView.getTop() + (leftTextView.getHeight() * 1.0f) / 2.0f;
                                                float ex = rightView.getLeft() + rightTextView.getLeft();
                                                float ey = rightView.getTop() + rightTextView.getTop() + (rightTextView.getHeight() * 1.0f) / 2.0f;


                                                //放置同侧相连接
                                                if (sx == ex) {
                                                    return;
                                                }
                                                //画园
                                                Path pathC = new Path();
                                                pathC.addCircle(sx, sy, 10, Path.Direction.CW);
                                                pathC.addCircle(ex, ey, 10, Path.Direction.CW);
                                                //画线
                                                Path pathL = new Path();
                                                pathL.moveTo(sx, sy);
                                                pathL.lineTo(ex, ey);
                                                //添加点路径和线路径
                                                matching_toLine.addDotPath(pathC, false, "");
                                                matching_toLine.addLinePath(pathL, false);

                                            }
                                        } else {
                                            //只有一对曾经作答过
                                            String[] trackStr = answerContent.split(",");
                                            //先判断连线的id  是在左边还是在右边
                                            String fistId = trackStr[0];
                                            if (quitntIds.contains(fistId)) {
                                                for (int j = 0; j < leftList.size(); j++) {
                                                    if (trackStr[0].equals(leftList.get(j).getId())) {
                                                        leftView = leftViews.get(j);
                                                        //获取左边的view
                                                        leftTextView = leftView.findViewById(R.id.tv_item_line_word_left);
                                                        break;

                                                    }
                                                }

                                                for (int j = 0; j < rightList.size(); j++) {
                                                    if (trackStr[1].equals(rightList.get(j).getId())) {
                                                        rightView = leftViews.get(j);
                                                        //获取左边的view
                                                        rightTextView = rightView.findViewById(R.id.tv_item_line_word_right);
                                                        break;

                                                    }
                                                }
                                            } else {
                                                //说明第一个再右边
                                                for (int j = 0; j < leftList.size(); j++) {
                                                    if (trackStr[1].equals(leftList.get(j).getId())) {
                                                        leftView = leftViews.get(j);
                                                        //获取左边的view
                                                        leftTextView = leftView.findViewById(R.id.tv_item_line_word_left);
                                                        break;

                                                    }
                                                }

                                                for (int j = 0; j < rightList.size(); j++) {
                                                    if (trackStr[0].equals(rightList.get(j).getId())) {
                                                        rightView = leftViews.get(j);
                                                        //获取左边的view
                                                        rightTextView = rightView.findViewById(R.id.tv_item_line_word_right);
                                                        break;

                                                    }
                                                }
                                            }

                                            float sx = leftView.getLeft() + leftTextView.getLeft() + leftTextView.getWidth();
                                            float sy = leftView.getTop() + leftTextView.getTop() + (leftTextView.getHeight() * 1.0f) / 2.0f;
                                            float ex = rightView.getLeft() + rightTextView.getLeft();
                                            float ey = rightView.getTop() + rightTextView.getTop() + (rightTextView.getHeight() * 1.0f) / 2.0f;


                                            //放置同侧相连接
                                            if (sx == ex) {
                                                return;
                                            }
                                            //画园
                                            Path pathC = new Path();
                                            pathC.addCircle(sx, sy, 10, Path.Direction.CW);
                                            pathC.addCircle(ex, ey, 10, Path.Direction.CW);
                                            //画线
                                            Path pathL = new Path();
                                            pathL.moveTo(sx, sy);
                                            pathL.lineTo(ex, ey);
                                            //添加点路径和线路径
                                            matching_toLine.addDotPath(pathC, false, "");
                                            matching_toLine.addLinePath(pathL, false);
                                        }
                                    }

                                }
                            }
                        }
                    }
                });



            }


            //开始划线
            matching_toLine.setDrawStatus(0);

            //把数据保存到本地  提交

            if (questionInfoList!=null && questionInfoList.size()>0){
                List<WorkOwnResult> ownList = questionInfoList.get(index).getOwnList();
                if (ownList!=null && ownList.size()>0){
                    WorkOwnResult workOwnResult = questionInfoList.get(index).getOwnList().get(0);
                    LocalTextAnswersBean localTextAnswersBean = new LocalTextAnswersBean();
                    localTextAnswersBean.setHomeworkId(questionInfoList.get(index).getHomeworkId());
                    localTextAnswersBean.setQuestionId(questionInfoList.get(index).getId());
                    localTextAnswersBean.setQuestionType(questionInfoList.get(index).getQuestionType());
                    localTextAnswersBean.setAnswerContent(workOwnResult.getAnswerContent());
                    localTextAnswersBean.setUserId(UserUtils.getUserId());
//                                QZXTools.logE("Save localTextAnswersBean=" + localTextAnswersBean, null);
                    //插入或者更新数据库
                    MyApplication.getInstance().getDaoSession().getLocalTextAnswersBeanDao().insertOrReplace(localTextAnswersBean);
                }

            }

        } else {
            //1 已提交  2 已批阅
            matching_reset.setBackground(mContext.getResources().getDrawable(R.drawable.shape_line_reset_disable));
            matching_reset.setTextColor(0xFFD5D5D5);
            matching_reset.setOnClickListener(null);
            //先更具id 判断是左边第一个和右边第几个连接

            //显示已经批阅的连线



        }


    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //View加载完成时回调
        rv_matching_show.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (leftList!=null && leftList.size()>0){

                    showLineView();
                }
            }
        });



    }

    private void showLineView() {
        if (taskStatus.equals(Constant.Commit_Status) || taskStatus.equals(Constant.Review_Status)|| taskStatus.equals(Constant.Retry_Status)) {
            //获取所有的recycleview 的item  的布局添加到view 中

            for (int j = 0; j < leftList.size(); j++) {
                View view = layoutManager.findViewByPosition(j);
                leftViews.add(view);
            }

            //OnGlobalLayoutListener可能会被多次触发
            //所以完成了需求后需要移除OnGlobalLayoutListener
            rv_matching_show.getViewTreeObserver()
                    .removeOnGlobalLayoutListener(this);

            //todo 连线提做完了，回显的状态还有点小问题，目前之显示出正确答案
            //更具id  获取到左边的坐标和右边的左边
            View leftView = null;
            View rightView = null;
            View leftTextView = null;
            View rightTextView = null;
            //获取学生自己的答案
            if (questionInfoList!=null && questionInfoList.size()>0 ){
                if(questionInfoList.get(index).getOwnList()!=null && leftViews!=null && !leftViews.isEmpty()){
                    List<WorkOwnResult> ownList = questionInfoList.get(index).getOwnList();
                    if (ownList!=null && ownList.size()>0 && rightList!=null && !rightList.isEmpty()){
                        WorkOwnResult workOwnResult =ownList.get(0);
                        String answerContent = workOwnResult.getAnswerContent();
                        if (answerContent.contains("|")) {
                            String[] split = answerContent.split("\\|");
                            for (String item : split) {
                                String[] trackStr = item.split(",");
                                //先判断连线的id  是在左边还是在右边
                                String fistId = trackStr[0];
                                if (quitntIds.contains(fistId)) {
                                    for (int j = 0; j < leftList.size(); j++) {
                                        if (trackStr[0].equals(leftList.get(j).getId())) {
                                            leftView = leftViews.get(j);
                                            //获取左边的view
                                            Log.e(TAG, "showLineView: "+leftViews+"......."+leftList+"....."+layoutManager);
                                            leftTextView = leftView.findViewById(R.id.tv_item_line_word_left);

                                            break;

                                        }
                                    }

                                    for (int j = 0; j < rightList.size(); j++) {
                                        if (trackStr[1].equals(rightList.get(j).getId())) {
                                            rightView = leftViews.get(j);
                                            //获取左边的view
                                            rightTextView = rightView.findViewById(R.id.tv_item_line_word_right);
                                            break;

                                        }
                                    }
                                } else {
                                    //说明第一个再右边
                                    for (int j = 0; j < leftList.size(); j++) {
                                        if (trackStr[1].equals(leftList.get(j).getId())) {
                                            leftView = leftViews.get(j);
                                            //获取左边的view
                                            leftTextView = leftView.findViewById(R.id.tv_item_line_word_left);
                                            break;

                                        }
                                    }

                                    for (int j = 0; j < rightList.size(); j++) {
                                        if (trackStr[0].equals(rightList.get(j).getId())) {
                                            rightView = leftViews.get(j);
                                            //获取左边的view
                                            rightTextView = rightView.findViewById(R.id.tv_item_line_word_right);
                                            break;

                                        }
                                    }
                                }

                                float sx = leftView.getLeft() + leftTextView.getLeft() + leftTextView.getWidth();
                                float sy = leftView.getTop() + leftTextView.getTop() + (leftTextView.getHeight() * 1.0f) / 2.0f;
                                float ex = rightView.getLeft() + rightTextView.getLeft();
                                float ey = rightView.getTop() + rightTextView.getTop() + (rightTextView.getHeight() * 1.0f) / 2.0f;


                                //放置同侧相连接
                                if (sx == ex) {
                                    return;
                                }
                                //画园
                                Path pathC = new Path();
                                pathC.addCircle(sx, sy, 10, Path.Direction.CW);
                                pathC.addCircle(ex, ey, 10, Path.Direction.CW);
                                //画线
                                Path pathL = new Path();
                                pathL.moveTo(sx, sy);
                                pathL.lineTo(ex, ey);
                                //添加点路径和线路径
                                matching_toLine.addDotPath(pathC,false,"");
                                matching_toLine.addLinePath(pathL,false);

                            }
                        } else {
                            //只有一对曾经作答过
                            String[] trackStr = answerContent.split(",");
                            //先判断连线的id  是在左边还是在右边
                            String fistId = trackStr[0];
                            if (quitntIds.contains(fistId)){
                                for (int j = 0; j < leftList.size(); j++) {
                                    if (trackStr[0].equals(leftList.get(j).getId())) {
                                        leftView = leftViews.get(j);
                                        //获取左边的view
                                        leftTextView = leftView.findViewById(R.id.tv_item_line_word_left);
                                        break;

                                    }
                                }

                                for (int j = 0; j < rightList.size(); j++) {
                                    if (trackStr[1].equals(rightList.get(j).getId())) {
                                        rightView = leftViews.get(j);
                                        //获取左边的view
                                        rightTextView = rightView.findViewById(R.id.tv_item_line_word_right);
                                        break;

                                    }
                                }
                            }else {
                                //说明第一个再右边
                                for (int j = 0; j < leftList.size(); j++) {
                                    if (trackStr[1].equals(leftList.get(j).getId())) {
                                        leftView = leftViews.get(j);
                                        //获取左边的view
                                        leftTextView = leftView.findViewById(R.id.tv_item_line_word_left);
                                        break;

                                    }
                                }

                                for (int j = 0; j < rightList.size(); j++) {
                                    if (trackStr[0].equals(rightList.get(j).getId())) {
                                        rightView = leftViews.get(j);
                                        //获取左边的view
                                        rightTextView = rightView.findViewById(R.id.tv_item_line_word_right);
                                        break;

                                    }
                                }
                            }

                            float sx = leftView.getLeft() + leftTextView.getLeft() + leftTextView.getWidth();
                            float sy = leftView.getTop() + leftTextView.getTop() + (leftTextView.getHeight() * 1.0f) / 2.0f;
                            float ex = rightView.getLeft() + rightTextView.getLeft();
                            float ey = rightView.getTop() + rightTextView.getTop() + (rightTextView.getHeight() * 1.0f) / 2.0f;


                            //放置同侧相连接
                            if (sx == ex) {
                                return;
                            }
                            //画园
                            Path pathC = new Path();
                            pathC.addCircle(sx, sy, 10, Path.Direction.CW);
                            pathC.addCircle(ex, ey, 10, Path.Direction.CW);
                            //画线
                            Path pathL = new Path();
                            pathL.moveTo(sx, sy);
                            pathL.lineTo(ex, ey);
                            //添加点路径和线路径
                            matching_toLine.addDotPath(pathC,false,"");
                            matching_toLine.addLinePath(pathL,false);
                        }
                    }

                    //获取正确答案也就是标准答案
                    String answer = questionInfoList.get(index).getAnswer();
                    // answer=answer.substring(0,answer.length()-1);
                    if (answer.contains("|")) {
                        String[] split = answer.split("\\|");
                        for (String item : split) {
                            String[] trackStr = item.split(",");
                            //先判断连线的id  是在左边还是在右边
                            String fistId = trackStr[0];
                            if (quitntIds.contains(fistId)){
                                for (int j = 0; j < leftList.size(); j++) {
                                    if (trackStr[0].equals(leftList.get(j).getId())) {
                                        leftView = leftViews.get(j);
                                        //获取左边的view
                                        leftTextView = leftView.findViewById(R.id.tv_item_line_word_left);
                                        break;

                                    }
                                }

                                for (int j = 0; j < rightList.size(); j++) {
                                    if (trackStr[1].equals(rightList.get(j).getId())) {
                                        rightView = leftViews.get(j);
                                        //获取左边的view
                                        rightTextView = rightView.findViewById(R.id.tv_item_line_word_right);
                                        break;

                                    }
                                }
                            }else {
                                //说明第一个再右边
                                for (int j = 0; j < leftList.size(); j++) {
                                    if (trackStr[1].equals(leftList.get(j).getId())) {
                                        leftView = leftViews.get(j);
                                        //获取左边的view
                                        leftTextView = leftView.findViewById(R.id.tv_item_line_word_left);
                                        break;

                                    }
                                }

                                for (int j = 0; j < rightList.size(); j++) {
                                    if (trackStr[0].equals(rightList.get(j).getId())) {
                                        rightView = leftViews.get(j);
                                        //获取左边的view
                                        rightTextView = rightView.findViewById(R.id.tv_item_line_word_right);
                                        break;

                                    }
                                }
                            }

                            float sx = leftView.getLeft() + leftTextView.getLeft() + leftTextView.getWidth();
                            float sy = leftView.getTop() + leftTextView.getTop() + (leftTextView.getHeight() * 1.0f) / 2.0f;
                            float ex = rightView.getLeft() + rightTextView.getLeft();
                            float ey = rightView.getTop() + rightTextView.getTop() + (rightTextView.getHeight() * 1.0f) / 2.0f;


                            //放置同侧相连接
                            if (sx == ex) {
                                return;
                            }
                            //画园
                            Path pathC = new Path();
                            pathC.addCircle(sx, sy, 10, Path.Direction.CW);
                            pathC.addCircle(ex, ey, 10, Path.Direction.CW);
                            //画线
                            Path pathL = new Path();
                            pathL.moveTo(sx, sy);
                            pathL.lineTo(ex, ey);
                            //添加点路径和线路径
                            matching_toLine.addDotPath(pathC,true,"");
                            matching_toLine.addLinePath(pathL,true);

                        }
                    } else {
                        //只有一对曾经作答过
                        String[] trackStr = answer.split(",");
                        //先判断连线的id  是在左边还是在右边
                        String fistId = trackStr[0];
                        if (quitntIds.contains(fistId)){
                            for (int j = 0; j < leftList.size(); j++) {
                                if (trackStr[0].equals(leftList.get(j).getId())) {
                                    leftView = leftViews.get(j);
                                    //获取左边的view
                                    leftTextView = leftView.findViewById(R.id.tv_item_line_word_left);
                                    break;

                                }
                            }

                            for (int j = 0; j < rightList.size(); j++) {
                                if (trackStr[1].equals(rightList.get(j).getId())) {
                                    rightView = leftViews.get(j);
                                    //获取左边的view
                                    rightTextView = rightView.findViewById(R.id.tv_item_line_word_right);
                                    break;

                                }
                            }
                        }else {
                            //说明第一个再右边
                            for (int j = 0; j < leftList.size(); j++) {
                                if (trackStr[1].equals(leftList.get(j).getId())) {
                                    leftView = leftViews.get(j);
                                    //获取左边的view
                                    leftTextView = leftView.findViewById(R.id.tv_item_line_word_left);
                                    break;

                                }
                            }

                            for (int j = 0; j < rightList.size(); j++) {
                                if (trackStr[0].equals(rightList.get(j).getId())) {
                                    rightView = leftViews.get(j);
                                    //获取左边的view
                                    rightTextView = rightView.findViewById(R.id.tv_item_line_word_right);
                                    break;

                                }
                            }
                        }

                        float sx = leftView.getLeft() + leftTextView.getLeft() + leftTextView.getWidth();
                        float sy = leftView.getTop() + leftTextView.getTop() + (leftTextView.getHeight() * 1.0f) / 2.0f;
                        float ex = rightView.getLeft() + rightTextView.getLeft();
                        float ey = rightView.getTop() + rightTextView.getTop() + (rightTextView.getHeight() * 1.0f) / 2.0f;


                        //放置同侧相连接
                        if (sx == ex) {
                            return;
                        }
                        //画园
                        Path pathC = new Path();
                        pathC.addCircle(sx, sy, 10, Path.Direction.CW);
                        pathC.addCircle(ex, ey, 10, Path.Direction.CW);
                        //画线
                        Path pathL = new Path();
                        pathL.moveTo(sx, sy);
                        pathL.lineTo(ex, ey);
                        //添加点路径和线路径
                        matching_toLine.addDotPath(pathC,true,"");
                        matching_toLine.addLinePath(pathL,true);
                    }

                    //开始连线  这个主要是显示正确答案
                    matching_toLine.setDrawStatus(2);
                }
                    }

        }

        if (taskStatus.equals(Constant.Save_Status)){

            //获取所有的recycleview 的item  的布局添加到view 中
            for (int j = 0; j < leftList.size(); j++) {
                View view = layoutManager.findViewByPosition(j);
                leftViews.add(view);


            }

            //OnGlobalLayoutListener可能会被多次触发
            //所以完成了需求后需要移除OnGlobalLayoutListener
            rv_matching_show.getViewTreeObserver()
                    .removeOnGlobalLayoutListener(this);

            //todo 连线提做完了，回显的状态还有点小问题，目前之显示出正确答案
            //更具id  获取到左边的坐标和右边的左边
            View leftView = null;
            View rightView = null;
            View leftTextView = null;
            View rightTextView = null;
            //获取学生自己的答案
            if (questionInfoList!=null && questionInfoList.size()>0){
                if(questionInfoList.get(index).getOwnList()!=null){
                    List<WorkOwnResult> ownList = questionInfoList.get(index).getOwnList();
                    if (ownList!=null && ownList.size()>0){
                        //作业早保存的时候可能没有做完
                        WorkOwnResult workOwnResult = questionInfoList.get(index).getOwnList().get(0);
                        String answerContent = workOwnResult.getAnswerContent();
                        if (answerContent.contains("|")) {
                            String[] split = answerContent.split("\\|");
                            for (String item : split) {
                                String[] trackStr = item.split(","); //先判断连线的id  是在左边还是在右边
                                String fistId = trackStr[0];
                                if (quitntIds.contains(fistId)){
                                    for (int j = 0; j < leftList.size(); j++) {
                                        if (trackStr[0].equals(leftList.get(j).getId())) {
                                            leftView = leftViews.get(j);
                                            //获取左边的view
                                            leftTextView = leftView.findViewById(R.id.tv_item_line_word_left);
                                            break;

                                        }
                                    }

                                    for (int j = 0; j < rightList.size(); j++) {
                                        if (trackStr[1].equals(rightList.get(j).getId())) {
                                            rightView = leftViews.get(j);
                                            //获取左边的view
                                            rightTextView = rightView.findViewById(R.id.tv_item_line_word_right);
                                            break;

                                        }
                                    }
                                }else {
                                    //说明第一个再右边
                                    for (int j = 0; j < leftList.size(); j++) {
                                        if (trackStr[1].equals(leftList.get(j).getId())) {
                                            leftView = leftViews.get(j);
                                            //获取左边的view
                                            leftTextView = leftView.findViewById(R.id.tv_item_line_word_left);
                                            break;

                                        }
                                    }

                                    for (int j = 0; j < rightList.size(); j++) {
                                        if (trackStr[0].equals(rightList.get(j).getId())) {
                                            rightView = leftViews.get(j);
                                            //获取左边的view
                                            rightTextView = rightView.findViewById(R.id.tv_item_line_word_right);
                                            break;

                                        }
                                    }
                                }

                                float sx = leftView.getLeft() + leftTextView.getLeft() + leftTextView.getWidth();
                                float sy = leftView.getTop() + leftTextView.getTop() + (leftTextView.getHeight() * 1.0f) / 2.0f;
                                float ex = rightView.getLeft() + rightTextView.getLeft();
                                float ey = rightView.getTop() + rightTextView.getTop() + (rightTextView.getHeight() * 1.0f) / 2.0f;


                                //放置同侧相连接
                                if (sx == ex) {
                                    return;
                                }
                                //画园
                                Path pathC = new Path();
                                pathC.addCircle(sx, sy, 10, Path.Direction.CW);
                                pathC.addCircle(ex, ey, 10, Path.Direction.CW);
                                //画线
                                Path pathL = new Path();
                                pathL.moveTo(sx, sy);
                                pathL.lineTo(ex, ey);
                                //添加点路径和线路径
                                matching_toLine.addDotPath(pathC,false,"");
                                matching_toLine.addLinePath(pathL,false);

                            }
                        } else {
                            //只有一对曾经作答过
                            String[] trackStr = answerContent.split(",");
                            //先判断连线的id  是在左边还是在右边
                            String fistId = trackStr[0];
                            if (quitntIds.contains(fistId)){
                                for (int j = 0; j < leftList.size(); j++) {
                                    if (trackStr[0].equals(leftList.get(j).getId())) {
                                        leftView = leftViews.get(j);
                                        //获取左边的view
                                        leftTextView = leftView.findViewById(R.id.tv_item_line_word_left);
                                        break;

                                    }
                                }

                                for (int j = 0; j < rightList.size(); j++) {
                                    if (trackStr[1].equals(rightList.get(j).getId())) {
                                        rightView = leftViews.get(j);
                                        //获取左边的view
                                        rightTextView = rightView.findViewById(R.id.tv_item_line_word_right);
                                        break;

                                    }
                                }
                            }else {
                                //说明第一个再右边
                                for (int j = 0; j < leftList.size(); j++) {
                                    if (trackStr[1].equals(leftList.get(j).getId())) {
                                        leftView = leftViews.get(j);
                                        //获取左边的view
                                        leftTextView = leftView.findViewById(R.id.tv_item_line_word_left);
                                        break;

                                    }
                                }

                                for (int j = 0; j < rightList.size(); j++) {
                                    if (trackStr[0].equals(rightList.get(j).getId())) {
                                        rightView = leftViews.get(j);
                                        //获取左边的view
                                        rightTextView = rightView.findViewById(R.id.tv_item_line_word_right);
                                        break;

                                    }
                                }
                            }



                            float sx = leftView.getLeft() + leftTextView.getLeft() + leftTextView.getWidth();
                            float sy = leftView.getTop() + leftTextView.getTop() + (leftTextView.getHeight() * 1.0f) / 2.0f;
                            float ex = rightView.getLeft() + rightTextView.getLeft();
                            float ey = rightView.getTop() + rightTextView.getTop() + (rightTextView.getHeight() * 1.0f) / 2.0f;


                            //放置同侧相连接
                            if (sx == ex) {
                                return;
                            }
                            //画园
                            Path pathC = new Path();
                            pathC.addCircle(sx, sy, 10, Path.Direction.CW);
                            pathC.addCircle(ex, ey, 10, Path.Direction.CW);
                            //画线
                            Path pathL = new Path();
                            pathL.moveTo(sx, sy);
                            pathL.lineTo(ex, ey);
                            //添加点路径和线路径
                            matching_toLine.addDotPath(pathC,false,"");
                            matching_toLine.addLinePath(pathL,false);
                        }
                    }


                }

                //获取正确答案也就是标准答案
                String answer = questionInfoList.get(index).getAnswer();
                // answer=answer.substring(0,answer.length()-1);
                if (answer.contains("|")) {
                    String[] split = answer.split("\\|");
                    for (String item : split) {
                        String[] trackStr = item.split(",");
                        //drawLigature(trackStr[0], trackStr[1], false);
                        //先判断连线的id  是在左边还是在右边
                        String fistId = trackStr[0];
                        if (quitntIds.contains(fistId)){
                            for (int j = 0; j < leftList.size(); j++) {
                                if (trackStr[0].equals(leftList.get(j).getId())) {
                                    leftView = leftViews.get(j);
                                    //获取左边的view
                                    leftTextView = leftView.findViewById(R.id.tv_item_line_word_left);
                                    break;

                                }
                            }

                            for (int j = 0; j < rightList.size(); j++) {
                                if (trackStr[1].equals(rightList.get(j).getId())) {
                                    rightView = leftViews.get(j);
                                    //获取左边的view
                                    rightTextView = rightView.findViewById(R.id.tv_item_line_word_right);
                                    break;

                                }
                            }
                        }else {
                            //说明第一个再右边
                            for (int j = 0; j < leftList.size(); j++) {
                                if (trackStr[1].equals(leftList.get(j).getId())) {
                                    leftView = leftViews.get(j);
                                    //获取左边的view
                                    leftTextView = leftView.findViewById(R.id.tv_item_line_word_left);
                                    break;

                                }
                            }

                            for (int j = 0; j < rightList.size(); j++) {
                                if (trackStr[0].equals(rightList.get(j).getId())) {
                                    rightView = leftViews.get(j);
                                    //获取左边的view
                                    rightTextView = rightView.findViewById(R.id.tv_item_line_word_right);
                                    break;

                                }
                            }
                        }

                        float sx = leftView.getLeft() + leftTextView.getLeft() + leftTextView.getWidth();
                        float sy = leftView.getTop() + leftTextView.getTop() + (leftTextView.getHeight() * 1.0f) / 2.0f;
                        float ex = rightView.getLeft() + rightTextView.getLeft();
                        float ey = rightView.getTop() + rightTextView.getTop() + (rightTextView.getHeight() * 1.0f) / 2.0f;


                        //放置同侧相连接
                        if (sx == ex) {
                            return;
                        }
                        //画园
                        Path pathC = new Path();
                        pathC.addCircle(sx, sy, 10, Path.Direction.CW);
                        pathC.addCircle(ex, ey, 10, Path.Direction.CW);
                        //画线
                        Path pathL = new Path();
                        pathL.moveTo(sx, sy);
                        pathL.lineTo(ex, ey);
                        //添加点路径和线路径
                        matching_toLine.addDotPath(pathC,true,"");
                        matching_toLine.addLinePath(pathL,true);

                    }
                } else {
                    //只有一对曾经作答过
                    String[] trackStr = answer.split(",");
                    //先判断连线的id  是在左边还是在右边
                    String fistId = trackStr[0];
                    if (quitntIds.contains(fistId)){
                        for (int j = 0; j < leftList.size(); j++) {
                            if (trackStr[0].equals(leftList.get(j).getId())) {
                                leftView = leftViews.get(j);
                                //获取左边的view
                                leftTextView = leftView.findViewById(R.id.tv_item_line_word_left);
                                break;

                            }
                        }

                        for (int j = 0; j < rightList.size(); j++) {
                            if (trackStr[1].equals(rightList.get(j).getId())) {
                                rightView = leftViews.get(j);
                                //获取左边的view
                                rightTextView = rightView.findViewById(R.id.tv_item_line_word_right);
                                break;

                            }
                        }
                    }else {
                        //说明第一个再右边
                        for (int j = 0; j < leftList.size(); j++) {
                            if (trackStr[1].equals(leftList.get(j).getId())) {
                                leftView = leftViews.get(j);
                                //获取左边的view
                                leftTextView = leftView.findViewById(R.id.tv_item_line_word_left);
                                break;

                            }
                        }

                        for (int j = 0; j < rightList.size(); j++) {
                            if (trackStr[0].equals(rightList.get(j).getId())) {
                                rightView = leftViews.get(j);
                                //获取左边的view
                                rightTextView = rightView.findViewById(R.id.tv_item_line_word_right);
                                break;

                            }
                        }
                    }

                    float sx = leftView.getLeft() + leftTextView.getLeft() + leftTextView.getWidth();
                    float sy = leftView.getTop() + leftTextView.getTop() + (leftTextView.getHeight() * 1.0f) / 2.0f;
                    float ex = rightView.getLeft() + rightTextView.getLeft();
                    float ey = rightView.getTop() + rightTextView.getTop() + (rightTextView.getHeight() * 1.0f) / 2.0f;


                    //放置同侧相连接
                    if (sx == ex) {
                        return;
                    }
                    //画园
                    Path pathC = new Path();
                    pathC.addCircle(sx, sy, 10, Path.Direction.CW);
                    pathC.addCircle(ex, ey, 10, Path.Direction.CW);
                    //画线
                    Path pathL = new Path();
                    pathL.moveTo(sx, sy);
                    pathL.lineTo(ex, ey);
                    //添加点路径和线路径
                    matching_toLine.addDotPath(pathC,true,"");
                    matching_toLine.addLinePath(pathL,true);
                }

                //开始连线  这个主要是显示正确答案
                matching_toLine.setDrawStatus(0);

                if (questionInfoList!=null && questionInfoList.size()>0){
                    List<WorkOwnResult> ownList = questionInfoList.get(index).getOwnList();
                    if (ownList!=null&&ownList.size()>0){
                        WorkOwnResult workOwnResult = questionInfoList.get(index).getOwnList().get(0);
                        LocalTextAnswersBean localTextAnswersBean = new LocalTextAnswersBean();
                        localTextAnswersBean.setHomeworkId(questionInfoList.get(index).getHomeworkId());
                        localTextAnswersBean.setQuestionId(questionInfoList.get(index).getId());
                        localTextAnswersBean.setQuestionType(questionInfoList.get(index).getQuestionType());
                        localTextAnswersBean.setAnswerContent(workOwnResult.getAnswerContent());
                        localTextAnswersBean.setUserId(UserUtils.getUserId());
//                                QZXTools.logE("Save localTextAnswersBean=" + localTextAnswersBean, null);
                        //插入或者更新数据库
                        MyApplication.getInstance().getDaoSession().getLocalTextAnswersBeanDao().insertOrReplace(localTextAnswersBean);
                    }


                }

            }
        }

    }


    @Override
    public void onGlobalLayout() {




    }
}
