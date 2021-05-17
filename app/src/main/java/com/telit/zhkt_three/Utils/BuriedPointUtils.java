package com.telit.zhkt_three.Utils;

import android.util.Log;

import com.android.volley.Response;
import com.hjq.toast.ToastUtils;

import com.telit.zhkt_three.Constant.UrlUtils;
import com.telit.zhkt_three.MyApplication;
import com.zbv.meeting.util.SharedPreferenceUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Callback;

import static com.pedro.rtplibrary.view.OpenGlViewBase.TAG;

public class BuriedPointUtils {
    public static void buriedPoint(String type, String interactionId, String groupType,String optContent, String matchCode) {
        HashMap<String, String> map = new HashMap<>();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("handleIp", QZXTools.getIPAddress()); //ip地址
            jsonObject.put("userId", UserUtils.getUserId()); //用户编号
            jsonObject.put("handleTime", getTime()); //操作时间
            jsonObject.put("userName", UserUtils.getStudentName() );////用户名称
            jsonObject.put("schoolId", UserUtils.getShoolId());//;学校编号
            jsonObject.put("interactionId", interactionId); //课堂互动id
            jsonObject.put("classId", UserUtils.getClassId());//班级编号
            jsonObject.put("optContent", optContent);//操作内容
            jsonObject.put("className", UserUtils.getClassName());//;班级名称
            jsonObject.put("groupType", groupType);//分组讨论
            jsonObject.put("matchCode", matchCode);//匹配编号
            jsonObject.put("teacherId",  SharedPreferenceUtil.getInstance(MyApplication.getInstance()).getString("teacherId"));// 教师id
            jsonObject.put("snCode", QZXTools.getDeviceSN());//sn编号
            jsonObject.put("userType", "student");//匹配编号
        } catch (JSONException e) {
            e.printStackTrace();
        }

        map.put("flag", type);
        //初始化teacherId
        SharedPreferenceUtil.getInstance(MyApplication.getInstance()).setString("teacherId","");
        map.put("operationLog", jsonObject.toString());
        OkHttp3_0Utils.getInstance().asyncPostOkHttp(UrlUtils.BaseUrl + UrlUtils.student_operation_Log, map, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                ToastUtils.show("埋点失败");
            }

            @Override
            public void onResponse(Call call, okhttp3.Response response) throws IOException {
            }
        });
    }
    private static String getTime() {
        long time = System.currentTimeMillis();//long now = android.os.SystemClock.uptimeMillis();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:SS");
        Date d1 = new Date(time);
        String t1 = format.format(d1);
        return t1;
    }
}
