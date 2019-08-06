package org.lang.sandbox.intercepter;

import com.alibaba.jvm.sandbox.api.listener.ext.Advice;
import com.alibaba.jvm.sandbox.api.listener.ext.EventWatchBuilder;
import com.alibaba.jvm.sandbox.api.listener.ext.EventWatchBuilder.PatternType;
import com.alibaba.jvm.sandbox.api.listener.ext.EventWatcher;
import com.alibaba.jvm.sandbox.api.resource.ModuleEventWatcher;

import java.lang.reflect.Method;

public class DbListener extends BaseTraceListener {

    private final String CLASS = "org.apache.ibatis.binding.MapperProxy";
    private final String METHOD = "invoke";

    public DbListener(ThreadCompressIntercepter intercepter, ModuleEventWatcher moduleEventWatcher) {
        super(intercepter, moduleEventWatcher);
    }

    public EventWatcher getWatcher() {
        return new EventWatchBuilder(moduleEventWatcher, PatternType.WILDCARD).onClass(CLASS)
                .onBehavior(METHOD).onWatch(this);
    }

    @Override
    protected String getType() {
        return "db";
    }

    @Override
    public InvokeNode getInvokeNode(Advice advice) {
        InvokeNode invokeNode = null;
        Object[] array = advice.getParameterArray();
        if (array != null && array.length > 1) {
            Object method = array[1];
            if (method instanceof Method) {
                Method m = (Method) method;
                invokeNode = new InvokeNode(m.getDeclaringClass().getName(), m.getName());
            }
        }
        return invokeNode;
    }

    @Override
    protected String getParams(Advice advice) {
        Object[] array = advice.getParameterArray();
        if (array != null && array.length > 2) {
            Object params = array[2];
            return getStrValue(params);
        }
        return null;
    }

    private Method getInterface;

    private String getClassName(Object invoker) {
        String c = null;
        if (invoker != null) {
            if (getInterface == null) {
                try {
                    getInterface = invoker.getClass().getMethod("getInterface");
                    getInterface.setAccessible(true);
                } catch (NoSuchMethodException e) {
                    //e.printStackTrace();
                }
            }
            if (getInterface != null) {
                try {
                    c = ((Class<?>) getInterface.invoke(invoker)).getName();
                } catch (Exception e) {
                    //e.printStackTrace();
                }
            }

        }
        return c;
    }


    private Method getMethodName;

    private String getMethodName(Object invocation) {
        String m = null;
        if (invocation != null) {
            if (getMethodName == null) {
                try {
                    getMethodName = invocation.getClass().getMethod("getMethodName");
                    getMethodName.setAccessible(true);
                } catch (Exception e) {
                    //e.printStackTrace();
                }
            }
            if (getMethodName != null) {
                try {
                    m = (String) getMethodName.invoke(invocation);
                } catch (Exception e) {
                    //e.printStackTrace();
                }
            }
        }
        return m;
    }
}
