package com.telit.zhkt_three.Fragment.Dialog;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.telit.zhkt_three.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * author: qzx
 * Date: 2019/5/25 10:44
 * <p>
 * <p>
 * builder.setCancelable(false);
 * <p>
 * builder.setOnKeyListener(new DialogInterface.OnKeyListener() {
 *
 * @Override public boolean onKey(DialogInterface dialog, int keyCode,
 * KeyEvent event) {
 * if (keyCode == KeyEvent.KEYCODE_SEARCH) {
 * return true;
 * } else {
 * // TODO Auto-generated method stub
 * return false;
 * }
 * <p>
 * <p>
 * }
 * });
 * ————————————————
 * 版权声明：本文为CSDN博主「flying_sheep1988」的原创文章，遵循 CC 4.0 BY-SA 版权协议，转载请附上原文出处链接及本声明。
 * 原文链接：https://blog.csdn.net/flying_sheep1988/article/details/53312154
 */
public class TipsDialog extends DialogFragment implements View.OnClickListener {

    private Unbinder unbinder;
    @BindView(R.id.tips_img)
    ImageView tips_img;
    @BindView(R.id.tips_description)
    TextView tips_desc;
    @BindView(R.id.tips_bottom_line)
    View view_line;
    @BindView(R.id.tips_cancle)
    TextView tips_cancle;
    @BindView(R.id.tips_confirm)
    TextView tips_confirm;

    private String tipsContent;
    private @Nullable
    String cancle;
    private String confirm;
    private int resImgId;

    private Bitmap bitmap;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //使用和忘记密码同样的样式
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.dialogForgetPwd);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tips_dialog_layout, container, false);
        unbinder = ButterKnife.bind(this, view);

        tips_cancle.setOnClickListener(this);
        tips_confirm.setOnClickListener(this);

        if (resImgId != -1) {
            tips_img.setVisibility(View.VISIBLE);
            tips_img.setImageResource(resImgId);
        } else {
            if (bitmap != null) {
                tips_img.setVisibility(View.VISIBLE);
                tips_img.setImageBitmap(bitmap);
            } else {
                tips_img.setVisibility(View.GONE);
            }
        }

        if (cancle == null) {
            view_line.setVisibility(View.GONE);
            tips_cancle.setVisibility(View.GONE);
        } else {
            tips_cancle.setText(cancle);
        }

        tips_confirm.setText(confirm);
        tips_desc.setText(tipsContent);

        return view;
    }

    private boolean isBackNoMiss;

    /**
     * 设置返回键Dialog不消失标志位true
     */
    public void setBackNoMiss() {
        isBackNoMiss = true;
    }

    /**
     * 返回键不消失
     */
    private void setBackNoEffect() {
        getDialog().setCancelable(false);
        getDialog().setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_SEARCH) {
                    return true;
                } else {
                    return false;
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        if (unbinder != null) {
            unbinder.unbind();
        }
        super.onDestroyView();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isBackNoMiss) {
            //在Dialog生成后使用
            setBackNoEffect();
        }
    }

    /**
     * 设置dialog样式
     *
     * @param tipsContent 提示内容主体描述
     * @param cancle      取消文本 如果为空Null则表示仅仅存在确定按钮
     * @param confirm     确定文本
     * @param resImgId    图片资源id 如果是-1则表示不存在图片描述
     */
    public void setTipsStyle(String tipsContent, @Nullable String cancle, String confirm, int resImgId) {
        this.tipsContent = tipsContent;
        this.cancle = cancle;
        this.confirm = confirm;
        this.resImgId = resImgId;
    }

    public void setTipsStyle(String tipsContent, @Nullable String cancle, String confirm, Bitmap bitmap) {
        this.tipsContent = tipsContent;
        this.cancle = cancle;
        this.confirm = confirm;
        this.bitmap = bitmap;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tips_cancle:
                if (clickInterface != null)
                    clickInterface.cancle();
                break;
            case R.id.tips_confirm:
                if (clickInterface != null)
                    clickInterface.confirm();
                break;
        }
    }

    private ClickInterface clickInterface;

    public void setClickInterface(ClickInterface clickInterface) {
        this.clickInterface = clickInterface;
    }

    public interface ClickInterface {
        void cancle();

        void confirm();
    }
}
