package org.lang.sandbox;

import com.alibaba.jvm.sandbox.api.annotation.Command;
import org.lang.sandbox.model.NodeMeta;
import org.lang.sandbox.model.StaticNode;
import org.lang.sandbox.model.TreeNode;

import java.io.PrintWriter;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.LinkedBlockingQueue;

public class ModuleXmlHttp extends ModuleDataHttp{

  @Command("listxml")
  public void listxml(final Map<String, String> param,
      final Map<String, String[]> params,
      final PrintWriter writer) {
    Map<Long, LinkedBlockingQueue<Long>> queues = intercepter.getQueue();
    Map<String, Map<String, Map<String, Long>>> resultMap = new TreeMap<String, Map<String, Map<String, Long>>>();
    for (Entry<Long, LinkedBlockingQueue<Long>> entry : queues.entrySet()) {
      long mid = entry.getKey();
      String[] items = NodeMeta.getName(mid);
      Map<String, Map<String, Long>> cMap = resultMap.get(items[0]);
      if (cMap == null) {
        resultMap.put(items[0], cMap = new TreeMap<String, Map<String, Long>>());
      }
      Map<String, Long> mMap = cMap.get(items[1]);
      if (mMap == null) {
        cMap.put(items[1], mMap = new TreeMap<String, Long>());
      }
      mMap.put(items[2], mid);

    }
    writer.println("<?xml version='1.0' encoding='UTF-8' ?>");
    writer.println("<visits>");
    for (Entry<String, Map<String, Map<String, Long>>> entry : resultMap.entrySet()) {
      String t = entry.getKey();
      Map<String, Map<String, Long>> cMap = entry.getValue();
      writer.print("<" + t + ">");
      for (Entry<String, Map<String, Long>> cEntry : cMap.entrySet()) {
        String c = cEntry.getKey();
        Map<String, Long> mMap = cEntry.getValue();
        writer.print("<" + c + ">");
        for (Entry<String, Long> mEntry : mMap.entrySet()) {
          String m = mEntry.getKey();
          long mid = mEntry.getValue();
          writer.print("<" + m + " mid='" + mid + "'>");
          LinkedBlockingQueue<Long> queue = queues.get(mid);
          for (long id : queue) {
            writer.print("<id>" + id + "</id>");
          }
          writer.print("</" + m + ">");
        }
        writer.print("</" + c + ">");
      }
      writer.print("</" + t + ">");
    }

    writer.println("</visits>");
  }

  @Command("nodexml")
  public void visitxml(final Map<String, String> param,
      final Map<String, String[]> params,
      final PrintWriter writer) throws Exception {

    String idStr = param.get("id");
    long id = Long.valueOf(idStr);

    TreeNode node = intercepter.getNode(id);
    if (node != null) {
      writer.write("<?xml version='1.0' encoding='UTF-8' ?>");
      node.writeFile(writer);
    } else {
      writer.write("no result no record");
    }
  }

  @Command("scenexml")
  public void all(final Map<String, String> param,
      final Map<String, String[]> params,
      final PrintWriter writer) throws Exception {

    String idStr = param.get("id");
    long id = Long.valueOf(idStr);

    StaticNode node = intercepter.getScene(id);
    if (node != null) {
      writer.write("<?xml version='1.0' encoding='UTF-8' ?>");
      node.writeFile(writer);
    } else {
      writer.write("no result no record");
    }
  }

}
