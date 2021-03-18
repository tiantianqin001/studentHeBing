package com.telit.zhkt_three.ProxyForMaiDian;

import com.telit.zhkt_three.Utils.QZXTools;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * author: qzx
 * Date: 2019/11/13 9:13
 */
public class OnClickProxy {

    private Object target;

    public OnClickProxy(Object target) {
        this.target = target;
    }

    public Object getProxyObject() {
        return Proxy.newProxyInstance(target.getClass().getClassLoader(), target.getClass().getInterfaces(),
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        //添加埋点请求
                        QZXTools.logE("埋点请求中...", null);

                        Object result = method.invoke(target, args);
                        return result;
                    }
                });
    }

}
