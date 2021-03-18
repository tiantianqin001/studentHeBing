package com.telit.zhkt_three.Utils.Jpush;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.SparseArray;

import com.telit.zhkt_three.Constant.Constant;
import com.telit.zhkt_three.Utils.QZXTools;
import com.telit.zhkt_three.Utils.eventbus.EventBus;

import java.util.Locale;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.JPushMessage;

/**
 * 处理tagalias相关的逻辑
 */
public class TagAliasOperatorHelper {
    public static int sequence = 1;
    /**
     * 增加
     */
    public static final int ACTION_ADD = 1;
    /**
     * 覆盖
     */
    public static final int ACTION_SET = 2;
    /**
     * 删除部分
     */
    public static final int ACTION_DELETE = 3;
    /**
     * 删除所有
     */
    public static final int ACTION_CLEAN = 4;


    public static final int DELAY_SEND_ACTION = 1;


    private Context context;

    private static TagAliasOperatorHelper mInstance;

    private TagAliasOperatorHelper() {
    }

    public static TagAliasOperatorHelper getInstance() {
        if (mInstance == null) {
            synchronized (TagAliasOperatorHelper.class) {
                if (mInstance == null) {
                    mInstance = new TagAliasOperatorHelper();
                }
            }
        }
        return mInstance;
    }

    public void init(Context context) {
        if (context != null) {
            this.context = context.getApplicationContext();
        }
    }

    private SparseArray<Object> setActionCache = new SparseArray<Object>();

    public Object get(int sequence) {
        return setActionCache.get(sequence);
    }


    public void put(int sequence, Object tagAliasBean) {
        setActionCache.put(sequence, tagAliasBean);
    }

    private Handler delaySendHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case DELAY_SEND_ACTION:
                    if (msg.obj != null && msg.obj instanceof TagAliasBean) {
                        QZXTools.logE("on delay time", null);
                        sequence++;
                        TagAliasBean tagAliasBean = (TagAliasBean) msg.obj;
                        setActionCache.put(sequence, tagAliasBean);
                        if (context != null) {
                            handleAction(context, sequence, tagAliasBean);
                        } else {
                            QZXTools.logE("#unexcepted - context was null", null);
                        }
                    } else {
                        QZXTools.logE("#unexcepted - msg obj was incorrect", null);
                    }
                    break;
            }
        }
    };


    /**
     * mHandler的释放
     */
    public void releaseHandler() {
        if (delaySendHandler != null)
            delaySendHandler.removeCallbacksAndMessages(null);
    }

    public void handleAction(Context context, int sequence, String mobileNumber) {
        put(sequence, mobileNumber);
        QZXTools.logE("sequence:" + sequence + ",mobileNumber:" + mobileNumber, null);
        JPushInterface.setMobileNumber(context, sequence, mobileNumber);
    }

    /**
     * 处理设置tag
     */
    public void handleAction(Context context, int sequence, TagAliasBean tagAliasBean) {
        init(context);
        if (tagAliasBean == null) {
            QZXTools.logE("tagAliasBean was null", null);
            return;
        }
        put(sequence, tagAliasBean);
        if (tagAliasBean.isAliasAction) {
            switch (tagAliasBean.action) {
                case ACTION_DELETE:
                    JPushInterface.deleteAlias(context, sequence);
                    break;
                case ACTION_SET:
                    JPushInterface.setAlias(context, sequence, tagAliasBean.alias);
                    break;
                default:
                    QZXTools.logE("unsupport alias action type", null);
                    return;
            }
        } else {
            switch (tagAliasBean.action) {
                case ACTION_ADD:
                    JPushInterface.addTags(context, sequence, tagAliasBean.tags);
                    break;
                case ACTION_DELETE:
                    JPushInterface.deleteTags(context, sequence, tagAliasBean.tags);
                    break;
                case ACTION_CLEAN:
                    JPushInterface.cleanTags(context, sequence);
                    break;
                default:
                    QZXTools.logE("unsupport tag action type", null);
                    return;
            }
        }
    }

    private boolean RetryActionIfNeeded(int errorCode, TagAliasBean tagAliasBean) {
        if (!QZXTools.isNetworkAvailable()) {
            QZXTools.logE("no network", null);
            return false;
        }
        //返回的错误码为6002 超时,6014 服务器繁忙,都建议延迟重试
        if (errorCode == 6002 || errorCode == 6014) {
            QZXTools.logE("need retry", null);
            if (tagAliasBean != null) {
                Message message = new Message();
                message.what = DELAY_SEND_ACTION;
                message.obj = tagAliasBean;
                delaySendHandler.sendMessageDelayed(message, 1000 * 60);
                String logs = getRetryStr(tagAliasBean.isAliasAction, tagAliasBean.action, errorCode);
                QZXTools.popToast(context, logs, false);
                return true;
            }
        }
        return false;
    }

    private String getRetryStr(boolean isAliasAction, int actionType, int errorCode) {
        String str = "Failed to %s %s due to %s. Try again after 60s.";
        str = String.format(Locale.ENGLISH, str, getActionStr(actionType), (isAliasAction ? "alias" : " tags"),
                (errorCode == 6002 ? "timeout" : "server too busy"));
        return str;
    }

    private String getActionStr(int actionType) {
        switch (actionType) {
            case ACTION_ADD:
                return "add";
            case ACTION_SET:
                return "set";
            case ACTION_DELETE:
                return "delete";
            case ACTION_CLEAN:
                return "clean";
        }
        return "unkonw operation";
    }

    public void onTagOperatorResult(Context context, JPushMessage jPushMessage) {
        int sequence = jPushMessage.getSequence();
        QZXTools.logE("action - onTagOperatorResult, sequence:" + sequence + ",tags:" + jPushMessage.getTags(), null);
        QZXTools.logE("tags size:" + jPushMessage.getTags().size(), null);
        init(context);
        //根据sequence从之前操作缓存中获取缓存记录
        TagAliasBean tagAliasBean = (TagAliasBean) setActionCache.get(sequence);
        if (tagAliasBean == null) {
            QZXTools.popToast(context, "获取缓存记录失败", false);
            return;
        }
        if (jPushMessage.getErrorCode() == 0) {
            QZXTools.logE("action - modify tag Success,sequence:" + sequence, null);
            setActionCache.remove(sequence);
            String logs = getActionStr(tagAliasBean.action) + " tags success";
            QZXTools.logE(logs, null);
            QZXTools.popToast(context, logs, false);
        } else {
            String logs = "Failed to " + getActionStr(tagAliasBean.action) + " tags";
            if (jPushMessage.getErrorCode() == 6018) {
                //tag数量超过限制,需要先清除一部分再add
                logs += ", tags is exceed limit need to clean";
            }
            logs += ", errorCode:" + jPushMessage.getErrorCode();
            QZXTools.logE(logs, null);
            if (!RetryActionIfNeeded(jPushMessage.getErrorCode(), tagAliasBean)) {
                QZXTools.popToast(context, logs, false);
            }
        }
    }

    public void onCheckTagOperatorResult(Context context, JPushMessage jPushMessage) {
        int sequence = jPushMessage.getSequence();
        QZXTools.logE("action - onCheckTagOperatorResult, sequence:" + sequence + ",checktag:" + jPushMessage.getCheckTag(), null);
        init(context);
        //根据sequence从之前操作缓存中获取缓存记录
        TagAliasBean tagAliasBean = (TagAliasBean) setActionCache.get(sequence);
        if (tagAliasBean == null) {
            QZXTools.popToast(context, "获取缓存记录失败", false);
            return;
        }
        if (jPushMessage.getErrorCode() == 0) {
            QZXTools.logE("tagBean:" + tagAliasBean, null);
            setActionCache.remove(sequence);
            String logs = getActionStr(tagAliasBean.action) + " tag " + jPushMessage.getCheckTag() + " bind state success,state:" + jPushMessage.getTagCheckStateResult();
            QZXTools.logE(logs, null);
            QZXTools.popToast(context, logs, false);
        } else {
            String logs = "Failed to " + getActionStr(tagAliasBean.action) + " tags, errorCode:" + jPushMessage.getErrorCode();
            QZXTools.logE(logs, null);
            if (!RetryActionIfNeeded(jPushMessage.getErrorCode(), tagAliasBean)) {
                QZXTools.popToast(context, logs, false);
            }
        }
    }

    public void onAliasOperatorResult(Context context, JPushMessage jPushMessage) {
        int sequence = jPushMessage.getSequence();
        QZXTools.logE("action - onAliasOperatorResult, sequence:" + sequence + ",alias:" + jPushMessage.getAlias(), null);
        init(context);
        //根据sequence从之前操作缓存中获取缓存记录
        TagAliasBean tagAliasBean = (TagAliasBean) setActionCache.get(sequence);
        if (tagAliasBean == null) {
            QZXTools.popToast(context, "获取缓存记录失败", false);
            return;
        }
        if (jPushMessage.getErrorCode() == 0) {
            QZXTools.logE("action - modify alias Success,sequence:" + sequence, null);
            setActionCache.remove(sequence);
            String logs = getActionStr(tagAliasBean.action) + " alias success";
            QZXTools.logE(logs, null);
            QZXTools.popToast(context, logs, false);
//            //因为最后删除之后关闭推送---有问题，没有Connect的话，添加alias和tag无效啊
//            EventBus.getDefault().post(logs, Constant.StopJpush);
        } else {
            String logs = "Failed to " + getActionStr(tagAliasBean.action) + " alias, errorCode:" + jPushMessage.getErrorCode();
            QZXTools.logE(logs, null);
            if (!RetryActionIfNeeded(jPushMessage.getErrorCode(), tagAliasBean)) {
                QZXTools.popToast(context, logs, false);
            }
        }
    }

    public static class TagAliasBean {
        int action;
        Set<String> tags;
        String alias;
        boolean isAliasAction;

        @Override
        public String toString() {
            return "TagAliasBean{" +
                    "action=" + action +
                    ", tags=" + tags +
                    ", alias='" + alias + '\'' +
                    ", isAliasAction=" + isAliasAction +
                    '}';
        }
    }


}
