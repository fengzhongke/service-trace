package org.lang.sandbox;

import com.alibaba.jvm.sandbox.api.Information;
import com.alibaba.jvm.sandbox.api.LoadCompleted;
import com.alibaba.jvm.sandbox.api.Module;
import com.alibaba.jvm.sandbox.api.resource.ModuleEventWatcher;
import com.alibaba.jvm.sandbox.api.resource.ModuleEventWatcher.Progress;
import javax.annotation.Resource;
import org.kohsuke.MetaInfServices;
import org.lang.sandbox.intercepter.DefaultListener;
import org.lang.sandbox.intercepter.DubbleListener;
import org.lang.sandbox.intercepter.MqListener;
import org.lang.sandbox.intercepter.RedisListener;
import org.lang.sandbox.intercepter.DbListener;


@MetaInfServices(Module.class)
@Information(id = "service-trace", version = "0.0.1", author = "nkhanlang@163.com")
public class ServiceTraceModule extends ModuleXmlHttp implements Module, LoadCompleted {

  @Resource
  private ModuleEventWatcher watcher;

  public void repairCheckState() {
    new DbListener(intercepter, watcher).getWatcher().withProgress(progress);
    new DefaultListener(".*\\..*((Controller)|(Service(Impl)?))$", intercepter,
        watcher).getWatcher().withProgress(progress);
    new DubbleListener(intercepter, watcher).getWatcher().withProgress(progress);
    new RedisListener(intercepter, watcher).getWatcher().withProgress(progress);
    new MqListener(intercepter, watcher).getWatcher().withProgress(progress);
  }

  @Override
  public void loadCompleted() {
    repairCheckState();
  }

  private Progress progress = new Progress() {
    @Override
    public void begin(int i) {
    }

    @Override
    public void progressOnSuccess(Class aClass, int i) {
    }

    @Override
    public void progressOnFailed(Class aClass, int i, Throwable throwable) {

    }

    @Override
    public void finish(int i, int i1) {
    }
  };

  public static void main(String[] args) {
    System.out.println("<cinit>".replaceAll("<|>", ""));
  }
}