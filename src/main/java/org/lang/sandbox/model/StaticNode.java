package org.lang.sandbox.model;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Pattern;

public class StaticNode extends NodeMeta {

  private long id;
  private String path;
  private AtomicLong cnt = new AtomicLong(0L);
  private AtomicInteger err = new AtomicInteger(0);
  private AtomicInteger doing = new AtomicInteger(0);
  private AtomicLong totalRt = new AtomicLong(0L);

  private ConcurrentHashMap<String, StaticNode> sons = new ConcurrentHashMap<String, StaticNode>();

  public StaticNode(long id) {
    this(id, String.valueOf(id));
  }

  public void addDoing() {
    cnt.incrementAndGet();
    doing.incrementAndGet();
  }

  public StaticNode(long id, String path) {
    this.id = id;
    this.path = path;
  }

  public StaticNode addSon(long id) {
    String path = this.id + "," + id;
    StaticNode son = sons.get(path);
    if (son == null) {
      StaticNode oldSon = sons.putIfAbsent(path, son = new StaticNode(id, path));
      if (oldSon != null) {
        son = oldSon;
      }
    }
    son.cnt.incrementAndGet();
    son.doing.incrementAndGet();
    return son;
  }

  public void setDone(boolean err, long rt) {
    if (err) {
      this.err.incrementAndGet();
    }
    this.totalRt.addAndGet(rt);
    this.doing.decrementAndGet();
  }

  public long getCnt() {
    return cnt.get();
  }

  public long getTotalRt() {
    return totalRt.get();
  }

  public long getMid() {
    return id;
  }

  public int getDoing() {
    return doing.get();
  }

  public int getErr() {
    return err.get();
  }

  public List<StaticNode> getSonCopy() {
    return new ArrayList<StaticNode>(sons.values());
  }


  public boolean equal(String type, String service, String method) {
    return getId(type, service, method) == id;
  }

  public void writeFile(Writer writer) throws IOException {
    String[] items = getName(id);
    if (items == null) {
      throw new RuntimeException("name not exists ![" + id + "]");
    }
    writer.write("<");
    writer.write(items[2]);
    writer.write(" cnt='" + cnt.get() + "'");
    writer.write(" rt='" + totalRt.get() + "'");
    writer.write(" doing='" + doing.get() + "'");
    writer.write(" err='" + err.get() + "'");
    writer.write(" class='" + items[1] + "'");
    writer.write(" type='" + items[0] + "'");
    writer.write(">\r\n");
    for (StaticNode son : sons.values()) {
      son.writeFile(writer);
    }
    writer.write("</");
    writer.write(items[2]);
    writer.write(">\r\n");
  }

  public static void main(String[] args) throws IOException {
    Pattern ptn = Pattern.compile("com\\.mallcai\\..*((Controller)|(Service(Impl)?))");
    System.out.println(
        ptn.matcher("com.mallcai.mgr.active.fullreduction.controller.FullReductionController")
            .find());
    System.out
        .println(ptn.matcher("com.mallcai.mgr.active.fullreduction.controller.FullService").find());
    System.out.println(
        ptn.matcher("com.mallcai.mgr.active.fullreduction.controller.FullServiceImpl").find());
  }

}
