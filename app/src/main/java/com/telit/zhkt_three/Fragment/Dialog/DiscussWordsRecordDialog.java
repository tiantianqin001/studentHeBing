package com.telit.zhkt_three.Fragment.Dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.telit.zhkt_three.Constant.Constant;
import com.telit.zhkt_three.R;
import com.telit.zhkt_three.Utils.QZXTools;
import com.telit.zhkt_three.Utils.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * author: qzx
 * Date: 2019/12/18 10:30
 */
public class DiscussWordsRecordDialog extends DialogFragment {

    private Unbinder unbinder;
    @BindView(R.id.group_conclusion_title)
    TextView group_conclusion_title;
    @BindView(R.id.group_conclusion_et_text)
    EditText group_conclusion_et_text;
    @BindView(R.id.group_conclusion_img_del)
    ImageView group_conclusion_img_del;
    @BindView(R.id.group_conclusion_commit)
    TextView group_conclusion_commit;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.dialogForgetPwd);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_discuss_conclusion_words, container, false);
        unbinder = ButterKnife.bind(this, view);

        group_conclusion_et_text.setText("");

        group_conclusion_et_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    group_conclusion_img_del.setVisibility(View.VISIBLE);
                } else {
                    group_conclusion_img_del.setVisibility(View.GONE);
                }
            }
        });

        group_conclusion_commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String words = group_conclusion_et_text.getText().toString().trim();
                if (TextUtils.isEmpty(words)) {
                    QZXTools.popCommonToast(getContext(), "你没有填写评论文字哦！", false);
                    return;
                }
                EventBus.getDefault().post(words, Constant.Group_Conclusion_Words);
                dismiss();
            }
        });

        group_conclusion_img_del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                group_conclusion_et_text.setText("");
                group_conclusion_et_text.setSelection(0);
            }
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null) {
            unbinder.unbind();
        }
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
