package com.telit.zhkt_three.Fragment.Interactive;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.telit.zhkt_three.CustomView.interactive.CJFL.InterestingSuperClassificationLayout;
import com.telit.zhkt_three.Fragment.Dialog.CJFLResultDialog;
import com.telit.zhkt_three.R;
import com.telit.zhkt_three.Utils.QZXTools;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * author: qzx
 * Date: 2019/8/1 10:10
 * <p>
 * 分类头颜色：#3699FF
 * 分类子项颜色：color:['#EA7351','#E2BBC8','#5ac994','#5dcdbf','#a4c268','#fb8371','#ba68c8'],
 *
 CJFLFragment cjflFragment = new CJFLFragment();

 Map<String, List<String>> map = new LinkedHashMap<>();
 List<String> fruitList = new ArrayList<>();
 fruitList.add("苹果");
 fruitList.add("梨子");
 fruitList.add("香橙");
 map.put("水果", fruitList);

 List<String> vegetabalList = new ArrayList<>();
 vegetabalList.add("包心菜");
 vegetabalList.add("芹菜");
 vegetabalList.add("茭瓜");
 vegetabalList.add("韭菜");
 map.put("蔬菜", vegetabalList);

 List<String> characterList = new ArrayList<>();
 characterList.add("温柔");
 characterList.add("真诚");
 map.put("人物性格", characterList);

 cjflFragment.fillData("三个分类", map, 3, 9);

 FragmentManager fragmentManager = getSupportFragmentManager();
 FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
 fragmentTransaction.replace(R.id.frame_layout, cjflFragment);
 fragmentTransaction.commit();

 */
public class CJFLFragment extends Fragment implements View.OnClickListener, InterestingSuperClassificationLayout.CJFLResultInterface {
    private Unbinder unbinder;
    @BindView(R.id.cjfl_layout)
    FrameLayout cjfl_layout;
    @BindView(R.id.cjfl_title)
    TextView cjfl_title;
    @BindView(R.id.cjfl_time)
    TextView cjfl_time;
    @BindView(R.id.cjfl_view)
    InterestingSuperClassificationLayout cjfl_view;
    @BindView(R.id.cjfl_reset)
    ImageView cjfl_reset;

    private ScheduledExecutorService timeExecutor;
    private long timerCount;
    private boolean isTimeOver = false;

    /**
     * 样式下标 0:冰天雪地 1:万圣狂欢 2:森林旅行 3:泼墨山水
     */
    private int bgIndex;

    /**
     * 超级分类大标题
     */
    private String bigTitle;

    private Map<String, List<String>> datas;

    private int totalItemCount;

    /**
     * 填充数据：
     * 超级分类大标题、分类项标题、分类项、分类样式下标
     */
    public void fillData(String bigTitle, Map<String, List<String>> datas, int bgIndex, int totalItemCount) {
        this.bigTitle = bigTitle;
        this.bgIndex = bgIndex;
        this.datas = datas;
        this.totalItemCount = totalItemCount;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cjfl_layout, container, false);
        unbinder = ButterKnife.bind(this, view);

        cjfl_reset.setOnClickListener(this);
        cjfl_view.setCJFLResultInterface(this);
        //开启互动计时
        timeExecutor = Executors.newSingleThreadScheduledExecutor();
        timeExecutor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                timerCount++;

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!isTimeOver) {
                            cjfl_time.setText("用时：".concat(QZXTools.getTransmitTime(timerCount)));
                        }
                    }
                });
            }
        }, 0, 1000, TimeUnit.MILLISECONDS);

        initCJFLData(true);

        return view;
    }

    @Override
    public void onDestroyView() {
        if (unbinder != null) {
            unbinder.unbind();
        }
        if (timeExecutor != null) {
            isTimeOver = true;
            timeExecutor.shutdown();
            timeExecutor = null;
        }
        super.onDestroyView();
    }

    private void initCJFLData(boolean isInit) {
        if (isInit) {
            //依据bgIndex设置对应的背景图片
            switch (bgIndex) {
                case 0:
                    cjfl_layout.setBackground(getResources().getDrawable(R.mipmap.cjfl_snow_bg));
                    break;
                case 1:
                    cjfl_layout.setBackground(getResources().getDrawable(R.mipmap.cjfl_moon_bg));
                    break;
                case 2:
                    cjfl_layout.setBackground(getResources().getDrawable(R.mipmap.cjfl_forest_bg));
                    break;
                case 3:
                    cjfl_layout.setBackground(getResources().getDrawable(R.mipmap.cjfl_country_bg));
                    break;
            }

            cjfl_title.setText(bigTitle);
        }

        int count = 0;
        Iterator<Map.Entry<String, List<String>>> iterator = datas.entrySet().iterator();
        while (iterator.hasNext()) {
            count++;
            Map.Entry<String, List<String>> entry = iterator.next();
            if (isInit) {
                cjfl_view.addContainerImages(entry.getKey(), datas.size(), count);
            }
            List<String> itemsList = entry.getValue();
            for (int i = 0; i < itemsList.size(); i++) {
                cjfl_view.addItemView(itemsList.get(i), totalItemCount, count);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cjfl_reset:
                cjfl_view.resetView();
                initCJFLData(false);
                break;
        }
    }

    @Override
    public void popCJFLResultDialog() {
        CJFLResultDialog cjflResultDialog = new CJFLResultDialog();
        cjflResultDialog.fillCJFKResult(datas);
        cjflResultDialog.show(getChildFragmentManager(), CJFLResultDialog.class.getSimpleName());
    }
}
