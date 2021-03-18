package com.telit.zhkt_three.Fragment.Dialog;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.telit.zhkt_three.Adapter.interactive.JoinTopicRVAdapter;
import com.telit.zhkt_three.Adapter.interactive.ReceiveFileRVAdapter;
import com.telit.zhkt_three.R;

/**
 * author: qzx
 * Date: 2019/5/25 10:44
 */
public class JoinTopicsDialog extends DialogFragment {

    private RecyclerView recyclerView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //使用和忘记密码同样的样式
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.dialogForgetPwd);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_join_topic_dialog_layout, container, false);
        recyclerView = view.findViewById(R.id.join_topic_dialog_recycler);
        recyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        JoinTopicRVAdapter joinTopicRVAdapter = new JoinTopicRVAdapter(getContext());
        recyclerView.setAdapter(joinTopicRVAdapter);

        return view;
    }
}
