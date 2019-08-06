package org.lang.sandbox.intercepter;

import com.alibaba.jvm.sandbox.api.listener.ext.Advice;
import com.alibaba.jvm.sandbox.api.listener.ext.AdviceListener;
import com.alibaba.jvm.sandbox.api.listener.ext.EventWatcher;
import com.alibaba.jvm.sandbox.api.resource.ModuleEventWatcher;
import org.lang.sandbox.io.ObjectSerializer;


public abstract class BaseTraceListener extends AdviceListener {

    private static volatile boolean record = false;

    protected ThreadCompressIntercepter intercepter;
    protected ModuleEventWatcher moduleEventWatcher;

    public BaseTraceListener(ThreadCompressIntercepter intercepter,
                             ModuleEventWatcher moduleEventWatcher) {
        this.intercepter = intercepter;
        this.moduleEventWatcher = moduleEventWatcher;
    }

    /**
     * whether to record params and result
     */
    public static boolean setRecord(boolean toRecord){
        record = toRecord;
        return record;
    }

    public static boolean getRecord(){
        return record;
    }

    protected void before(Advice advice) throws Throwable {
        InvokeNode invokeNode = getInvokeNode(advice);
        if (invokeNode != null) {
            String params = null;
            if(record){
                params = getParams(advice);
            }
            intercepter.start(getType(), invokeNode.c, invokeNode.m, params);
        }
    }

    protected void afterReturning(Advice advice) throws Throwable {
        InvokeNode invokeNode = getInvokeNode(advice);
        if (invokeNode != null) {
            String ret = null;
            if(record){
                ret = getRet(false, advice);
            }
            intercepter.end(invokeNode.c, invokeNode.m, false, ret);
        }
    }

    protected void afterThrowing(Advice advice) throws Throwable {
        InvokeNode invokeNode = getInvokeNode(advice);
        if (invokeNode != null) {
            String ret = null;
            if(record){
                ret = getRet(true, advice);
            }
            intercepter.end(invokeNode.c, invokeNode.m, true, ret);
        }
    }

    public abstract EventWatcher getWatcher();

    protected abstract InvokeNode getInvokeNode(Advice advice);

    protected abstract String getParams(Advice advice);

    protected String getRet(boolean isErr, Advice advice) {
        if(isErr){
            return advice.getThrowable().getClass() + "|" + advice.getThrowable().getMessage();
        }else{
            return getStrValue(advice.getReturnObj());
        }
    }


    protected String getStrValue(Object obj){
        return ObjectSerializer.getStrValue(obj);
    }

    /**
     * get service type category
     */
    protected abstract String getType();

    public static class InvokeNode {

        public InvokeNode(String c, String m) {
            this.c = c.replaceAll("<|>", "").replaceAll("\\$", ".");
            this.m = m.replaceAll("<|>", "").replaceAll("\\$", ".");
        }

        final String c;
        final String m;
    }

}
