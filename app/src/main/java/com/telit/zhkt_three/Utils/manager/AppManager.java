package com.telit.zhkt_three.Utils.manager;



import com.telit.zhkt_three.Activity.BaseActivity;
import com.telit.zhkt_three.Utils.QZXTools;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * activity堆栈式管理
 *
 * @author FireAnt（http://my.oschina.net/LittleDY）
 * @created 2014年10月30日 下午6:22:05
 */
public class AppManager {

    //后进先出
    private static Stack<BaseActivity> activityStack;
    private static AppManager instance;

    private AppManager() {
    }

    /**
     * 单一实例
     */
    public static AppManager getAppManager() {
        if (instance == null) {
            instance = new AppManager();
        }

        if (activityStack == null) {
            activityStack = new Stack<BaseActivity>();
        }

        return instance;
    }

    /**
     * 获取指定的Activity
     *
     * @author kymjs
     */
    public static BaseActivity getActivity(Class<?> cls) {
        if (activityStack != null)
            for (BaseActivity activity : activityStack) {
                if (activity.getClass().equals(cls)) {
                    return activity;
                }
            }
        return null;
    }

    /**
     * 添加Activity到堆栈
     */
    public void addActivity(BaseActivity activity) {
        activityStack.add(activity);
    }

    /**
     * @describe 获取当前Activity（堆栈中最后一个压入的）
     * @author luxun
     * create at 2017/2/4 16:25
     */
    public BaseActivity currentActivity() {
        BaseActivity activity = activityStack.lastElement();
        return activity;
    }

    /**
     * 结束当前Activity（堆栈中最后一个压入的）
     */
    public void finishActivity() {
        BaseActivity activity = activityStack.lastElement();
        finishActivity(activity);
    }

    /**
     * 结束指定的Activity
     */
    public void finishActivity(BaseActivity activity) {
        if (activity != null && activityStack.contains(activity)) {
            activityStack.remove(activity);
            activity.finish();
        }
    }

    /**
     * 结束指定的Activity
     */
    public void removeActivity(BaseActivity activity) {
        if (activity != null && activityStack.contains(activity)) {
            activityStack.remove(activity);
        }
    }

    /**
     * 结束指定类名的Activity
     */
    public void finishActivity(Class<?> cls) {
        for (BaseActivity activity : activityStack) {
            if (activity.getClass().equals(cls)) {
                finishActivity(activity);
                break;
            }
        }
    }

    /**
     * @describe 关闭其它类型的Activity
     * @author luxun
     * create at 2017/3/29 0029 15:55
     */
    public void finishOther(Class<?> cls) {
        List<BaseActivity> tempList = new ArrayList<>();
        for (BaseActivity activity : activityStack) {
            if (activity.getClass().equals(cls)) {
                break;
            }
            tempList.add(activity);
        }
        for (BaseActivity activity : tempList) {
            finishActivity(activity);
        }
    }

    /**
     * 结束所有Activity
     */
    public void finishAllActivity() {
        for (int i = 0, size = activityStack.size(); i < size; i++) {
            if (null != activityStack.get(i)) {
//                finishActivity(activityStack.get(i));
                activityStack.get(i).finish();
            }
        }
        activityStack.clear();
    }

    /**
     * 退出应用程序
     */
    public void AppExit() {
        try {
            finishAllActivity();
            System.exit(0);
        } catch (Exception e) {
            e.printStackTrace();
            QZXTools.logE("退出异常", e);
        }
    }
}
