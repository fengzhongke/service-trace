package org.lang.sandbox;

import com.alibaba.jvm.sandbox.api.annotation.Command;
import com.alibaba.jvm.sandbox.api.http.Http;
import com.google.gson.Gson;
import org.lang.sandbox.intercepter.BaseTraceListener;
import org.lang.sandbox.intercepter.ThreadCompressIntercepter;
import org.lang.sandbox.web.StaticResResolver;
import org.lang.sandbox.web.VmViewResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.*;
import java.util.Map.Entry;


public class ModuleViewHttp{

  protected ThreadCompressIntercepter intercepter = new ThreadCompressIntercepter();

  @Http("/page/*")
  public void page(HttpServletResponse response, final Map<String, String> param,
                   HttpServletRequest req,
                   final PrintWriter writer) {
    response.setHeader("Access-Control-Allow-Origin", "*");
    String uri = req.getRequestURI();
    int idx = uri.indexOf("/page");
    if (idx > 0) {
      String page = uri.substring(idx + 6);
      Map<String, Object> map = new HashMap<String, Object>();
      for (Entry<String, String> entry : param.entrySet()) {
        map.put(entry.getKey(), entry.getValue());
      }
      Enumeration<String> names = req.getParameterNames();
      while(names.hasMoreElements()){
        String name = names.nextElement();
        map.put(name, req.getParameter(name));
      }
      System.out.println("param is : " + map);
      VmViewResolver.resolve("page/vm/" + page + ".vm", map, writer);
    }
  }

  @Http("/static/*")
  public void js(
      HttpServletRequest req,
      final PrintWriter writer) throws Exception {
    String uri = req.getRequestURI();
    int idx = uri.indexOf("/static");
    if (idx > 0) {
      StaticResResolver.resolve(uri.substring(idx + 1), writer);
    }
  }

  @Command("clear")
  public void clear(final Map<String, String> param,
                    final Map<String, String[]> params,
                    final PrintWriter writer) throws Exception {
    intercepter.getQueue().clear();
    writer.println("clear ok");
  }

  @Command("record")
  public void record(final Map<String, String> param,
                     final Map<String, String[]> params,
                     final PrintWriter writer) throws Exception {
    Map<String, Boolean> map = new HashMap<String, Boolean>();
    map.put("oldVal", BaseTraceListener.getRecord());
    writer.println(new Gson().toJson(map));
  }

  @Command("change")
  public void change(final Map<String, String> param,
                     final Map<String, String[]> params,
                     final PrintWriter writer) throws Exception {
    boolean record = Boolean.valueOf(param.get("record"));
    boolean newVal = BaseTraceListener.setRecord(record);
    Map<String, Boolean> map = new HashMap<String, Boolean>();
    map.put("oldVal", record);
    map.put("newVal", newVal);
    writer.println(new Gson().toJson(map));
  }
}
