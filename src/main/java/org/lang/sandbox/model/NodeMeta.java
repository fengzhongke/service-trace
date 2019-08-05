package org.lang.sandbox.model;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public class NodeMeta {

  private static Map<String, Map<String, Map<String, Long>>> idMap = new HashMap<String, Map<String, Map<String, Long>>>();

  private static AtomicLong seq = new AtomicLong(0);
  protected static Map<Long, String[]> nameMap = new HashMap<Long, String[]>();

  public static long getId(String type, String service, String method) {
    Map<String, Map<String, Long>> tMap = null;
    if((tMap = idMap.get(type)) == null){
      synchronized (idMap){
        if((tMap = idMap.get(type)) == null){
          idMap.put(type, tMap = new HashMap<String, Map<String, Long>>());
        }
      }
    }
    Map<String, Long> cMap = null;
    if ((cMap = tMap.get(service)) == null) {
      synchronized (tMap) {
        if ((cMap = tMap.get(service)) == null) {
          tMap.put(service, cMap = new HashMap<String, Long>());
        }
      }
    }

    Long id = null;
    if ((id = cMap.get(method)) == null) {
      synchronized (cMap) {
        if ((id = cMap.get(method)) == null) {
          cMap.put(method, id = seq.incrementAndGet());
          nameMap.put(id, new String[] {type, service, method});
        }
      }
    }
    return id;
  }

  public static String[] getName(long id) {
    return nameMap.get(id);
  }
}
