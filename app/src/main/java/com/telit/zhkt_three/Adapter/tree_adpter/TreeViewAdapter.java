package com.telit.zhkt_three.Adapter.tree_adpter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.telit.zhkt_three.Constant.Constant;
import com.telit.zhkt_three.R;
import com.telit.zhkt_three.Utils.QZXTools;
import com.telit.zhkt_three.Utils.eventbus.EventBus;

import java.util.List;

/**
 * author: qzx
 * Date: 2019/5/17 14:13
 */
public class TreeViewAdapter extends RecyclerView.Adapter<TreeViewAdapter.TreeViewHolder> {

    private List<Node> datas;

    public void setDatas(List<Node> datas) {
        this.datas = datas;
    }

    @NonNull
    @Override
    public TreeViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_rv_tree_item, null);
        return new TreeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final TreeViewHolder treeViewHolder, int i) {

        treeViewHolder.itemView.setPadding(30 * datas.get(i).getLevel(), 10, 10, 10);

        treeViewHolder.itemView.setLayoutParams(new RecyclerView.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT
                , LinearLayout.LayoutParams.WRAP_CONTENT));
        if (datas != null && datas.get(i) != null) {
            treeViewHolder.textView.setText(datas.get(i).getShowText());

            if (datas.get(i).isHideCheckBox()) {
                treeViewHolder.imageView.setVisibility(View.GONE);
            } else {
                treeViewHolder.imageView.setVisibility(View.VISIBLE);
                if (datas.get(i).isExpand()) {
                    if (datas.get(i).getChoosed() == Node.CHOOSE_PART) {
                        treeViewHolder.imageView.setImageResource(R.drawable.check_part);
                    } else if (datas.get(i).getChoosed() == Node.CHOOSE_NONE) {
                        treeViewHolder.imageView.setImageResource(R.drawable.check_none);
                    } else {
                        treeViewHolder.imageView.setImageResource(R.drawable.check_all);
                    }
                }
            }
        }

        //展开或隐藏
        if (!datas.get(i).isExpand() && !TextUtils.isEmpty(datas.get(i).getPid())) {
            //holder.itemView.setVisibility(View.GONE);
            //直接使用GONE方法，效果和invisible效果相同，即收起后仍会用空白位置，所以采用以下方法
            setVisibility(false, treeViewHolder.itemView);
        } else {
            //holder.itemView.setVisibility(View.VISIBLE);
            setVisibility(true, treeViewHolder.itemView);
        }

        /**
         * 如果有子类则展示可拓展箭头，否则不可见
         * */
        if (datas.get(i).getChilds().isEmpty()) {
            treeViewHolder.irrow.setVisibility(View.INVISIBLE);
        } else if (!datas.get(i).getChilds().isEmpty()
                && NodeUtils.getChildExpandStatus(datas.get(i)) != Node.CHILD_EXPAND_ALL) {
            treeViewHolder.irrow.setVisibility(View.VISIBLE);
            treeViewHolder.irrow.setImageResource(R.drawable.arrow_right);
        } else {
            treeViewHolder.irrow.setVisibility(View.VISIBLE);
            treeViewHolder.irrow.setImageResource(R.drawable.arrow_down);
        }

        /**
         * 点击模拟checkbox
         * */
        treeViewHolder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = treeViewHolder.getLayoutPosition();
                Node node = datas.get(position);
                if (node.getChoosed() == Node.CHOOSE_NONE || node.getChoosed() == Node.CHOOSE_PART) {
                    node.setChoosed(Node.CHOOSE_ALL);
                } else {
                    node.setChoosed(Node.CHOOSE_NONE);
                }

                // chooseNodes和chooseParentNodes要联合使用
                NodeUtils.chooseNodes(datas, datas.get(position), datas.get(position).getChoosed());
                NodeUtils.chooseParentNodes(datas, datas.get(position));

                notifyDataSetChanged();
                EventBus.getDefault().post("chooseTree", Constant.Event_Choose_Tree);
            }
        });

        /**
         * 点击文本项
         * */
        treeViewHolder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                QZXTools.logE("adapter position=" + treeViewHolder.getAdapterPosition(), null);
                if (datas.get(treeViewHolder.getAdapterPosition()).getChilds().isEmpty()) {
                    if (ebookClickCallback != null) {
                        int position = treeViewHolder.getLayoutPosition();
                        ebookClickCallback.clickItem(v, position);
                    }
                }

                NodeUtils.showNodes2(datas, datas.get(treeViewHolder.getLayoutPosition()));
                notifyDataSetChanged();
            }
        });

    }

    @Override
    public int getItemCount() {
        return datas == null ? 0 : datas.size();
    }

    public class TreeViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView textView;
        public ImageView irrow;

        public TreeViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.image);
            textView = (TextView) itemView.findViewById(R.id.text);
            irrow = (ImageView) itemView.findViewById(R.id.irrow);
        }
    }

    private void setVisibility(boolean isVisible, View itemView) {
        RecyclerView.LayoutParams param = (RecyclerView.LayoutParams) itemView.getLayoutParams();
        if (param == null) {
            return;
        }
        if (isVisible) {
            param.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            param.width = LinearLayout.LayoutParams.MATCH_PARENT;
            itemView.setVisibility(View.VISIBLE);
        } else {
            itemView.setVisibility(View.GONE);
            param.height = 0;
            param.width = 0;
        }
        itemView.setLayoutParams(param);
    }

    private EbookClickCallback ebookClickCallback;

    public void setEbookClickCallback(EbookClickCallback ebookClickCallback) {
        this.ebookClickCallback = ebookClickCallback;
    }

    public interface EbookClickCallback {
        void clickItem(View view, int position);
    }
}
