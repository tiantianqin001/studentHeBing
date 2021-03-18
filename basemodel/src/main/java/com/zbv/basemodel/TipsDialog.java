package com.zbv.basemodel;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * author: qzx
 * Date: 2019/5/25 10:44
 */
public class TipsDialog extends DialogFragment implements View.OnClickListener {

    private ImageView tips_img;
    private TextView tips_desc;
    private View view_line;
    private TextView tips_cancle;
    private TextView tips_confirm;

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

        tips_img = view.findViewById(R.id.tips_img);
        tips_desc = view.findViewById(R.id.tips_description);
        tips_cancle = view.findViewById(R.id.tips_cancle);
        tips_confirm = view.findViewById(R.id.tips_confirm);
        view_line = view.findViewById(R.id.tips_bottom_line);

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
        int id = v.getId();
        if (id == R.id.tips_cancle) {
            if (clickInterface != null)
                clickInterface.cancle();
        } else if (id == R.id.tips_confirm) {
            if (clickInterface != null)
                clickInterface.confirm();
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


    @Override
    public void show(FragmentManager manager, String tag) {
        try {
            FragmentTransaction ft = manager.beginTransaction();
            ft.add(this, tag);
            ft.commitAllowingStateLoss();
        } catch (IllegalStateException e) {
            Log.d("ABSDIALOGFRAG", "Exception", e);
        }
    }
}
