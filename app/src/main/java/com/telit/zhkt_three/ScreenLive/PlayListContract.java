package com.telit.zhkt_three.ScreenLive;

/**
 * Created by gavin on 2018/1/23.
 */

public class PlayListContract {
    interface View extends BaseView<Presenter> {
        void updateOnliveList();
    }

    interface Presenter extends BasePresenter {
    }
}
