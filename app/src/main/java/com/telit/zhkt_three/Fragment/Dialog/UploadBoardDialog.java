package com.telit.zhkt_three.Fragment.Dialog;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.telit.zhkt_three.Constant.UrlUtils;
import com.telit.zhkt_three.CustomView.ToUsePullView;
import com.telit.zhkt_three.Fragment.CircleProgressDialogFragment;
import com.telit.zhkt_three.R;
import com.telit.zhkt_three.Utils.OkHttp3_0Utils;
import com.telit.zhkt_three.Utils.QZXTools;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * author: qzx
 * Date: 2019/6/23 14:59
 */
public class UploadBoardDialog extends DialogFragment implements View.OnClickListener {

    private Unbinder unbinder;
    @BindView(R.id.upload_img)
    ImageView upload_img;
    @BindView(R.id.upload_pullView)
    ToUsePullView upload_pullView;
    @BindView(R.id.upload_cancel)
    TextView upload_cancel;
    @BindView(R.id.upload_confirm_btn)
    Button upload_confirm_btn;

    private List<String> upload_pulls;

    private String actualSaveFilePath;

    /**
     * 传入保存的文件路径
     */
    public void setActualSaveFilePath(String actualSaveFilePath) {
        this.actualSaveFilePath = actualSaveFilePath;
    }

    private static final int Server_Error = 0;
    private static final int Error404 = 1;
    private static final int Operator_Upload_Success = 2;

    private CircleProgressDialogFragment circleProgressDialogFragment;


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case Server_Error:
                    QZXTools.popToast(getContext(), getContext().getResources().getString(R.string.current_net_err), false);
                    if (circleProgressDialogFragment != null && circleProgressDialogFragment.isVisible()) {
                        circleProgressDialogFragment.dismissAllowingStateLoss();
                        circleProgressDialogFragment = null;
                    }
                    break;
                case Error404:
                    QZXTools.popToast(getContext(), "没有相关资源！", false);
                    if (circleProgressDialogFragment != null && circleProgressDialogFragment.isVisible()) {
                        circleProgressDialogFragment.dismissAllowingStateLoss();
                        circleProgressDialogFragment = null;
                    }
                    break;
                case Operator_Upload_Success:
                    QZXTools.popToast(getContext(), (String) msg.obj, false);
                    if (circleProgressDialogFragment != null && circleProgressDialogFragment.isVisible()) {
                        circleProgressDialogFragment.dismissAllowingStateLoss();
                        circleProgressDialogFragment = null;
                    }
                    dismiss();
                    break;
            }
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //使用和忘记密码同样的样式
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.dialogForgetPwd);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_upload_dialog_layout, container, false);
        unbinder = ButterKnife.bind(this, view);

        upload_img.setImageBitmap(BitmapFactory.decodeFile(actualSaveFilePath));

        upload_pulls = new ArrayList<>();
        upload_pulls.add("语文");
        upload_pulls.add("数学");
        upload_pulls.add("英语");
        upload_pulls.add("其他");

        upload_pullView.setDataList(upload_pulls);

        upload_pullView.setSpinnerClick(new ToUsePullView.SpinnerClickInterface() {
            @Override
            public void spinnerClick(View parent, String text) {
                ((ToUsePullView) parent).setPullContent(text);
            }
        });

        upload_cancel.setOnClickListener(this);
        upload_confirm_btn.setOnClickListener(this);

        return view;
    }

    @Override
    public void onDestroyView() {
        if (unbinder != null) {
            unbinder.unbind();
        }
        //防泄漏
        mHandler.removeCallbacksAndMessages(null);
        QZXTools.setmToastNull();
        super.onDestroyView();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.upload_cancel:
                dismiss();
                break;
            case R.id.upload_confirm_btn:
                //上传
                whiteBoardUpload();
                break;
        }
    }

    /**
     * 白板上传
     */
    private void whiteBoardUpload() {
        if (circleProgressDialogFragment != null && circleProgressDialogFragment.isVisible()) {
            circleProgressDialogFragment.dismissAllowingStateLoss();
            circleProgressDialogFragment = null;
        }
        circleProgressDialogFragment = new CircleProgressDialogFragment();
        circleProgressDialogFragment.show(getChildFragmentManager(), CircleProgressDialogFragment.class.getSimpleName());

        String url = UrlUtils.BaseUrl + UrlUtils.WhiteBoardUpload;
        File file = new File(actualSaveFilePath);
        OkHttp3_0Utils.getInstance().asyncPostSingleOkHttp(url, "attachement", null, file, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                //服务端错误
                mHandler.sendEmptyMessage(Server_Error);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String resultJson = response.body().string();
                    /*
                     * {"success":true,"errorCode":"1","msg":"上传成功",
                     * "result":["http://172.16.4.40:8090/filesystem/whiteboard/4e48124809ed104c58085f30c46f833c4d98.jpg"],"total":0,"pageNo":0}
                     * */
                    QZXTools.logE("resultJson=" + resultJson, null);

                    Gson gson = new Gson();
                    Map<String, Object> resultMap = gson.fromJson(resultJson, new TypeToken<Map<String, Object>>() {
                    }.getType());

                    Message message = mHandler.obtainMessage();
                    message.what = Operator_Upload_Success;
                    message.obj = resultMap.get("msg");
                    mHandler.sendMessage(message);

                } else {
                    mHandler.sendEmptyMessage(Error404);
                }
            }
        });
    }
}
