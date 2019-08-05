package org.lang.sandbox.intercepter;

import com.alibaba.jvm.sandbox.api.listener.ext.Advice;
import com.alibaba.jvm.sandbox.api.listener.ext.EventWatchBuilder;
import com.alibaba.jvm.sandbox.api.listener.ext.EventWatchBuilder.PatternType;
import com.alibaba.jvm.sandbox.api.listener.ext.EventWatcher;
import com.alibaba.jvm.sandbox.api.resource.ModuleEventWatcher;
import com.google.gson.Gson;

public class DefaultListener extends BaseTraceListener {

    private final String pattern;

    public DefaultListener(String pattern, ThreadCompressIntercepter intercepter,
                           ModuleEventWatcher moduleEventWatcher) {
        super(intercepter, moduleEventWatcher);
        this.pattern = pattern;
    }

    public EventWatcher getWatcher() {
        return new EventWatchBuilder(moduleEventWatcher, PatternType.REGEX).onClass(pattern)
                .onAnyBehavior().onWatch(this);
    }

    @Override
    protected String getType() {
        return "local";
    }

    @Override
    public InvokeNode getInvokeNode(Advice advice) {
        String c = advice.getBehavior().getDeclaringClass().getName();
        String m = advice.getBehavior().getName();
        InvokeNode invokeNode = new InvokeNode(c, m);
        return invokeNode;
    }

    @Override
    protected String getParams(Advice advice) {
        return new Gson().toJson(advice.getParameterArray());
    }
}
