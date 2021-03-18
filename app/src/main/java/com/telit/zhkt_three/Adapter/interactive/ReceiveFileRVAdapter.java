package com.telit.zhkt_three.Adapter.interactive;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import com.telit.zhkt_three.R;
import com.telit.zhkt_three.Utils.QZXTools;

import java.io.File;
import java.util.List;

/**
 * author: qzx
 * Date: 2019/6/23 16:03
 * <p>
 * 依据文件名的后缀判断文件类型就可以了
 */
public class ReceiveFileRVAdapter extends RecyclerView.Adapter<ReceiveFileRVAdapter.ReceiveFileRVViewHolder> {

    private Context mContext;
    private List<File> mList;

    public ReceiveFileRVAdapter(Context context, List<File> files) {
        mContext = context;
        mList = files;
    }

    @NonNull
    @Override
    public ReceiveFileRVViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ReceiveFileRVViewHolder(LayoutInflater.from(mContext).inflate(R.layout.adapter_item_file_receive, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ReceiveFileRVViewHolder receiveFileRVViewHolder, int i) {
        String fileName = mList.get(i).getName();
        receiveFileRVViewHolder.receive_file_name.setText(fileName);
        QZXTools.logE("fileName=" + fileName, null);
        String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
        switch (suffix) {
            case "mp3":
                receiveFileRVViewHolder.receive_file_icon.setImageResource(R.mipmap.voice);
                break;
            case "mp4":
                receiveFileRVViewHolder.receive_file_icon.setImageResource(R.mipmap.video);
                break;
            case "ppt":
                receiveFileRVViewHolder.receive_file_icon.setImageResource(R.mipmap.ppt);
                break;
            case "xls":
            case "xlsx":
                receiveFileRVViewHolder.receive_file_icon.setImageResource(R.mipmap.excel);
                break;
            case "doc":
            case "docx":
                receiveFileRVViewHolder.receive_file_icon.setImageResource(R.mipmap.word);
                break;
            case "pdf":
                receiveFileRVViewHolder.receive_file_icon.setImageResource(R.mipmap.pdf);
                break;
            case "jpg":
            case "png":
                receiveFileRVViewHolder.receive_file_icon.setImageResource(R.mipmap.picture);
                break;
            default:
                receiveFileRVViewHolder.receive_file_icon.setImageResource(R.mipmap.file);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mList != null ? mList.size() : 0;
    }

    public class ReceiveFileRVViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView receive_file_icon;
        private TextView receive_file_name;
        private TextView receive_delete_file;
        private TextView receive_open_file;

        public ReceiveFileRVViewHolder(@NonNull View itemView) {
            super(itemView);
            receive_file_icon = itemView.findViewById(R.id.receive_file_icon);
            receive_file_name = itemView.findViewById(R.id.receive_file_name);
            receive_delete_file = itemView.findViewById(R.id.receive_delete_file);
            receive_open_file = itemView.findViewById(R.id.receive_open_file);

            receive_open_file.setOnClickListener(this);
            receive_delete_file.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.receive_open_file:
                    QZXTools.openFile(mList.get(getLayoutPosition()), mContext);
                    break;
                case R.id.receive_delete_file:
                    boolean result = QZXTools.deleteFileOrDirectory(mList.get(getLayoutPosition()).getAbsolutePath());
                    if (result) {
                        mList.remove(getLayoutPosition());
                        notifyDataSetChanged();
                    }
                    break;
            }
        }
    }
}
