package com.telit.zhkt_three.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.telit.zhkt_three.R;

import java.util.List;

/**
 * author: qzx
 * Date: 2019/6/17 17:15
 */
public class RVUnityChapterAdapter extends RecyclerView.Adapter<RVUnityChapterAdapter.RVUnityChapterViewHolder> {

    private Context mContext;
    private List<String> chapters;

    private String chooseChapter;

    private View selectedView;

    /**
     * @param chooseChapter 传入的当前章节是为了选中标志
     */
    public RVUnityChapterAdapter(String chooseChapter, Context context, List<String> chapters) {
        this.chooseChapter = chooseChapter;
        mContext = context;
        this.chapters = chapters;
    }


    @NonNull
    @Override
    public RVUnityChapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new RVUnityChapterViewHolder(LayoutInflater.from(mContext).inflate
                (R.layout.item_unity_chapter_layout, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RVUnityChapterViewHolder rvUnityChapterViewHolder, int i) {
        if (chooseChapter.equals(chapters.get(i))) {
            rvUnityChapterViewHolder.chapter_tv.setSelected(true);
            if (selectedView == null) {
                selectedView = rvUnityChapterViewHolder.chapter_tv;
            }
        }
        rvUnityChapterViewHolder.chapter_tv.setText(chapters.get(i));
    }

    @Override
    public int getItemCount() {
        return chapters != null ? chapters.size() : 0;
    }

    public class RVUnityChapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView chapter_tv;

        public RVUnityChapterViewHolder(@NonNull View itemView) {
            super(itemView);
            chapter_tv = itemView.findViewById(R.id.chapter_tv);
            chapter_tv.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.chapter_tv:
                    if (iChapterClick != null) {
                        TextView temp = (TextView) v;
                        if (selectedView != null) {
                            selectedView.setSelected(false);
                        }
                        v.setSelected(true);
                        selectedView = v;
                        iChapterClick.clickChapterView("DigestiveSystem");
                    }
                    break;
            }
        }
    }

    public IChapterClick iChapterClick;

    public void setiChapterClick(IChapterClick iChapterClick) {
        this.iChapterClick = iChapterClick;
    }

    public interface IChapterClick {
        void clickChapterView(String chapter);
    }
}
