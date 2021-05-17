package com.telit.zhkt_three.Utils.Jpush;

import android.content.Context;
import android.text.TextUtils;

import com.telit.zhkt_three.MyApplication;
import com.telit.zhkt_three.Utils.UserUtils;

import java.util.LinkedHashSet;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;

/**
 * author: qzx
 * Date: 2019/5/22 15:16
 * <p>
 * 处理alias和tags
 * alias:学生id
 * tags：班级id以及用户id
 * <p>
 * API - init  API - stopPush  API - resumePush  API - isPushStopped   需要应用的Context
 * <p>
 * todo 网络改变可能会影响推送
 */
public class JpushApply {

    private static JpushApply jpushApply;

    public static JpushApply getIntance() {
        if (jpushApply == null) {
            synchronized (JpushApply.class) {
                if (jpushApply == null) {
                    jpushApply = new JpushApply();
                }
            }
        }
        return jpushApply;
    }


    /**
     * 添加alias以及tags
     */
    public void registJpush(Context context) {
        //如果调用stopPush的话，必须使用resumePush恢复，之后的其他api调用才会生效
        if (JPushInterface.isPushStopped(MyApplication.getInstance())) {
            JPushInterface.resumePush(MyApplication.getInstance());
        }

        //学生id设置为别名,因为讯飞的id超过40字符，无效所以使用用户id
    //    setAlias(context, UserUtils.getUserId());

        //班级id和用户id设置为标签
        String classId = UserUtils.getClassId();
        if (classId.contains("-")) {
            classId = classId.replace("-", "");
        }
//        String shortClassId = UserUtils.getShortClassId();
//        if (TextUtils.isEmpty(shortClassId)) {
//            classId = classId.replace("-", "");
//            addTags(context, classId);
//        } else {
//            addTags(context, shortClassId);
//        }
       // addTags(context, classId);
    }

    /**
     * 移除alias以及tags
     */
    public void unRegistJpush(Context context) {
        if (JPushInterface.isPushStopped(MyApplication.getInstance())) {
            //与Connect有关
            JPushInterface.resumePush(MyApplication.getInstance());
        }

      //  deleteAlias(context);
     //   clearTags(context);
    }

    /**
     * 检测推送服务是否已经开启了
     * 1、已经登录进去
     * 2、推送服务没有停止
     *
     * @return true表示服务运行中 false表示服务中断了
     */
    public static boolean checkJpush() {
        if (JPushInterface.isPushStopped(MyApplication.getInstance())) {
            //推送服务停止了
            return false;
        }
        return true;
    }

    /**
     * 设置alias
     */
    public void setAlias(Context context, String alias) {
        TagAliasOperatorHelper.TagAliasBean tagAliasBean = new TagAliasOperatorHelper.TagAliasBean();
        tagAliasBean.isAliasAction = true;
        tagAliasBean.alias = alias;
        tagAliasBean.action = TagAliasOperatorHelper.ACTION_SET;
        TagAliasOperatorHelper.sequence++;
        TagAliasOperatorHelper.getInstance().handleAction(context, TagAliasOperatorHelper.sequence, tagAliasBean);
    }

    /**
     * 删除alias
     * alias传null
     */
    public void deleteAlias(Context context) {
        TagAliasOperatorHelper.TagAliasBean tagAliasBean = new TagAliasOperatorHelper.TagAliasBean();
        tagAliasBean.isAliasAction = true;
        tagAliasBean.alias = null;
        tagAliasBean.action = TagAliasOperatorHelper.ACTION_DELETE;
        TagAliasOperatorHelper.sequence++;
        TagAliasOperatorHelper.getInstance().handleAction(context, TagAliasOperatorHelper.sequence, tagAliasBean);
    }

    /**
     * 添加tags
     */
    public void addTags(Context context, String... tags) {
        Set<String> tagSet = new LinkedHashSet<String>();
        TagAliasOperatorHelper.TagAliasBean tagAliasBean = new TagAliasOperatorHelper.TagAliasBean();
        tagAliasBean.isAliasAction = false;
        tagAliasBean.action = TagAliasOperatorHelper.ACTION_ADD;
        TagAliasOperatorHelper.sequence++;
        for (String tag : tags) {
            tagSet.add(tag);
        }
        tagAliasBean.tags = tagSet;
        TagAliasOperatorHelper.getInstance().handleAction(context, TagAliasOperatorHelper.sequence, tagAliasBean);
    }

    /**
     * 删除tag
     */
    public void deleteTag(Context context, String... delTags) {
        Set<String> tagSet = new LinkedHashSet<String>();
        TagAliasOperatorHelper.TagAliasBean tagAliasBean = new TagAliasOperatorHelper.TagAliasBean();
        tagAliasBean.isAliasAction = false;
        tagAliasBean.action = TagAliasOperatorHelper.ACTION_DELETE;
        TagAliasOperatorHelper.sequence++;
        for (String tag : delTags) {
            tagSet.add(tag);
        }
        tagAliasBean.tags = tagSet;
        TagAliasOperatorHelper.getInstance().handleAction(context, TagAliasOperatorHelper.sequence, tagAliasBean);
    }

    /**
     * 删除全部的tag
     * tags传null
     */
    public void clearTags(Context context) {
        TagAliasOperatorHelper.TagAliasBean tagAliasBean = new TagAliasOperatorHelper.TagAliasBean();
        tagAliasBean.isAliasAction = false;
        tagAliasBean.action = TagAliasOperatorHelper.ACTION_CLEAN;
        TagAliasOperatorHelper.sequence++;
        tagAliasBean.tags = null;
        TagAliasOperatorHelper.getInstance().handleAction(context, TagAliasOperatorHelper.sequence, tagAliasBean);
    }
}
