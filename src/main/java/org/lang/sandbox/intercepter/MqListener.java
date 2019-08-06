package org.lang.sandbox.intercepter;

import com.alibaba.jvm.sandbox.api.listener.ext.Advice;
import com.alibaba.jvm.sandbox.api.listener.ext.EventWatchBuilder;
import com.alibaba.jvm.sandbox.api.listener.ext.EventWatchBuilder.PatternType;
import com.alibaba.jvm.sandbox.api.listener.ext.EventWatcher;
import com.alibaba.jvm.sandbox.api.resource.ModuleEventWatcher;

import java.lang.reflect.Method;
import java.util.List;

public class MqListener extends BaseTraceListener {

  private final String PATTERN = "org\\.apache\\.rocketmq\\.client\\.(consumer\\.listener\\.MessageListener|producer\\.DefaultMQProducer)";
  private final String METHOD = "(consume.*)|(send.*)";

  private final String MSG_CLASS_NAME = "org.apache.rocketmq.common.message.Message";


  public MqListener(ThreadCompressIntercepter intercepter,
      ModuleEventWatcher moduleEventWatcher) {
    super(intercepter, moduleEventWatcher);
  }

  public EventWatcher getWatcher() {
    return new EventWatchBuilder(moduleEventWatcher, PatternType.REGEX).onClass(PATTERN)
        .includeSubClasses()
        .onBehavior(METHOD).onWatch(this);
  }

  @Override
  protected String getType() {
    return "mq";
  }

  @Override
  public InvokeNode getInvokeNode(Advice advice) {
    InvokeNode invokeNode = null;
    Object[] array = advice.getParameterArray();
    if (array.length > 0) {
      String topic = getTopic(array);
      if (msgClass == null) {
        try {
          msgClass = advice.getBehavior().getDeclaringClass().getClassLoader()
              .loadClass(MSG_CLASS_NAME);
        } catch (Exception e) {
        }
      }
      String className = advice.getBehavior().getDeclaringClass().getName();
      if (topic != null) {
        invokeNode = new InvokeNode(topic, className);
      }
    }
    return invokeNode;
  }

  @Override
  protected String getParams(Advice advice) {
    return getStrValue(advice.getParameterArray());
  }

  private static Class<?> msgClass;
  private static Method getTopic;

  private final String getTopic(Object[] array) {
    String topic = null;
    if (msgClass != null) {
      for (Object obj : array) {
        if (obj instanceof List && ((List<?>) obj).size() > 0) {
          Object msg = ((List<?>) obj).get(0);
          if (msgClass.isAssignableFrom(msg.getClass())) {
            if (getTopic == null) {
              try {
                getTopic = msg.getClass().getMethod("getTopic");
                getTopic.setAccessible(true);
              } catch (Exception e) {
                //e.printStackTrace();
              }
            }
            if (getTopic != null) {
              try {
                topic = (String) getTopic.invoke(msg);
              } catch (Exception e) {
                //e.printStackTrace();
              }
            }
          }
        }
        if (topic != null) {
          break;
        }
      }
    }

    return topic;
  }
}
