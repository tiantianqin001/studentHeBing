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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.telit.zhkt_three.Adapter.interactive.ReceiveFileRVAdapter;
import com.telit.zhkt_three.MyApplication;
import com.telit.zhkt_three.R;
import com.telit.zhkt_three.Utils.QZXTools;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * author: qzx
 * Date: 2019/5/25 10:44
 */
public class ReceiveFilesDialog extends DialogFragment {

    private RecyclerView recyclerView;

    private LinearLayout leak_resource_layout;

    private List<File> fileList;

    private String dirPath;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //使用和忘记密码同样的样式
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.dialogForgetPwd);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_file_receive_dialog_layout, container, false);

        //可以取消重置样式设置
        getDialog().setCanceledOnTouchOutside(true);

        leak_resource_layout = view.findViewById(R.id.leak_resource_layout);

        recyclerView = view.findViewById(R.id.file_receive_dialog_recycler);
        recyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        fileList = new ArrayList<>();

        dirPath = QZXTools.getExternalStorageForFiles(MyApplication.getInstance()
                .getApplicationContext(), null) + "/SaveFile/";

        File file = new File(dirPath);
        if (file.exists() && file.isDirectory()) {
            leak_resource_layout.setVisibility(View.GONE);
            File[] files = file.listFiles();

            for (File file1 : files) {
                fileList.add(file1);
            }

            if (fileList.size() > 0) {
                ReceiveFileRVAdapter receiveFileRVAdapter = new ReceiveFileRVAdapter(getContext(), fileList);
                recyclerView.setAdapter(receiveFileRVAdapter);
            } else {
                leak_resource_layout.setVisibility(View.VISIBLE);
            }
        } else {
            QZXTools.popCommonToast(getContext(), "没有文件", false);
            leak_resource_layout.setVisibility(View.VISIBLE);
        }
        return view;
    }
}
