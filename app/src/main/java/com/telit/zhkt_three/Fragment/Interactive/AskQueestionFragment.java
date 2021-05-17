package com.telit.zhkt_three.Fragment.Interactive;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.telit.zhkt_three.Adapter.VPHomeWorkDetailAdapter;
import com.telit.zhkt_three.Constant.UrlUtils;
import com.telit.zhkt_three.CusomPater;
import com.telit.zhkt_three.JavaBean.Gson.HomeWorkByHandBean;
import com.telit.zhkt_three.JavaBean.HomeWork.QuestionInfoByhand;
import com.telit.zhkt_three.MyApplication;
import com.telit.zhkt_three.R;
import com.telit.zhkt_three.Utils.OkHttp3_0Utils;
import com.telit.zhkt_three.Utils.QZXTools;
import com.telit.zhkt_three.Utils.UserUtils;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AskQueestionFragment extends Fragment {
    private String homeworkId;

    private static final int Server_Error = 0;
    private static final int Error404 = 1;
    private static final int Operator_Success = 2;
    private static final int Commit_Result_Show = 3;

    @BindView(R.id.homework_detail_vp)
    CusomPater homework_vp;
    @BindView(R.id.homework_back)
    ImageView homework_back;
    @BindView(R.id.homework_title)
    TextView homework_title;
    @BindView(R.id.homework_count)
    TextView homework_count;
    @BindView(R.id.homework_btn_commit)
    TextView homework_commit;
    @BindView(R.id.layout_left)
    LinearLayout layout_left;
    @BindView(R.id.layout_right)
    LinearLayout layout_right;
    @BindView(R.id.tv_comment_teacher)
    TextView tv_comment_teacher;
    @BindView(R.id.ll_ti_wen_summit)
    LinearLayout ll_ti_wen_summit;
    @BindView(R.id.rl_home_work_head)
    RelativeLayout rl_home_work_head;
    /**
     * 总题目数
     */
    private int totalQuestionCount;
    private int totalPageCount;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case Server_Error:
                    QZXTools.popToast(MyApplication.getInstance(), "当前网络不佳....", false);

                case Error404:
                    QZXTools.popToast(MyApplication.getInstance(), "没有相关资源！", false);

                    break;
                case Operator_Success:
                    List<QuestionInfoByhand> questionInfoByhandList = (List<QuestionInfoByhand>) msg.obj;

                    totalPageCount = questionInfoByhandList.size();

                    //图片出题的总个数
                    for (int i = 0; i < questionInfoByhandList.size(); i++) {
                        totalQuestionCount += questionInfoByhandList.get(i).getSheetlist().size();
                    }

                    if (totalPageCount > 1) {
                        layout_right.setVisibility(View.VISIBLE);
                        layout_left.setVisibility(View.INVISIBLE);
                    } else {
                        layout_left.setVisibility(View.INVISIBLE);
                        layout_right.setVisibility(View.INVISIBLE);
                    }

                    //塞入Vp的数据  在添加一个类型   0是互动  1是作业 再1是作业的情况下还有作业完成和没有完成 //comType  作业是不是已做
                    VPHomeWorkDetailAdapter vpHomeWorkDetailAdapter = new VPHomeWorkDetailAdapter
                            (getContext(), questionInfoByhandList, null, 1+"",0,2+"");
                    homework_vp.setAdapter(vpHomeWorkDetailAdapter);
                    if (questionInfoByhandList==null ||  questionInfoByhandList.size()== 0){
                        return;
                    }
                    if (!TextUtils.isEmpty(questionInfoByhandList.get(0).getComment())){

                        tv_comment_teacher.setText("老师批改:  "+questionInfoByhandList.get(0).getComment());
                    }
                    break;
                case Commit_Result_Show:


                    String result = (String) msg.obj;
                    QZXTools.popCommonToast(MyApplication.getInstance(), result, false);


                    break;
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_home_work_detail1, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fetchNetHomeWorkDatas();
    }

    private void fetchNetHomeWorkDatas() {
        String url;
        //题库出题和图片出题调用不同的URL

            url = UrlUtils.BaseUrl + UrlUtils.HomeWorkDetailsByHand;

         //   url = UrlUtils.BaseUrl + UrlUtils.HomeWorkDetailsByHandTwo;


        Map<String, String> mapParams = new LinkedHashMap<>();

        mapParams.put("homeworkid", homeworkId);
        mapParams.put("status", "1");
        mapParams.put("studentid", UserUtils.getUserId());

        /**
         * post传参数时，不管是int类型还是布尔类型统一传入字符串的样式即可
         * */
        //查询章节数据
        OkHttp3_0Utils.getInstance().asyncPostOkHttp(url, mapParams, new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                //服务端错误
                mHandler.sendEmptyMessage(Server_Error);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String resultJson = response.body().string();
                    QZXTools.logE("resultJson=" + resultJson, null);
                    Gson gson = new Gson();
                    HomeWorkByHandBean homeWorkByHandBean = gson.fromJson(resultJson, HomeWorkByHandBean.class);
//                    QZXTools.logE("homeWorkByHandBean=" + homeWorkByHandBean, null);

                    Message message = mHandler.obtainMessage();
                    message.what = Operator_Success;
                    message.obj = homeWorkByHandBean.getResult();
                    mHandler.sendMessage(message);
                } else {
                    mHandler.sendEmptyMessage(Error404);
                }
            }
        });
    }

    public void setHomeWordId(String homeworkId) {


        this.homeworkId = homeworkId;
    }
}
