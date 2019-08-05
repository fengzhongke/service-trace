package org.lang.sandbox.intercepter;

import java.util.Map;
import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicLong;

import org.lang.sandbox.model.NodeMeta;
import org.lang.sandbox.model.StaticNode;
import org.lang.sandbox.model.TreeNode;


public class ThreadCompressIntercepter {

  private final AtomicLong SEED = new AtomicLong(0L);
  private final int SIZE = 10;
  private ConcurrentHashMap<Long, LinkedBlockingQueue<Long>> queues = new ConcurrentHashMap<Long, LinkedBlockingQueue<Long>>();
  private ConcurrentHashMap<Long, TreeNode> pool = new ConcurrentHashMap<Long, TreeNode>();
  private final ThreadLocal<Stack<TreeNode>> t_stack = new ThreadLocal<Stack<TreeNode>>();
  private final ThreadLocal<Stack<StaticNode>> s_stack = new ThreadLocal<Stack<StaticNode>>();
  private final ThreadLocal<Stack<Long>> t_time = new ThreadLocal<Stack<Long>>();

  private ConcurrentHashMap<Long, StaticNode> staticQueue = new ConcurrentHashMap<Long, StaticNode>();


  public Map<Long, LinkedBlockingQueue<Long>> getQueue() {
    return queues;
  }

  public TreeNode getNode(long key) throws InterruptedException {
    TreeNode node = pool.get(key);
    return node;
  }

  public StaticNode getScene(long id) {
    StaticNode node = staticQueue.get(id);
    return node;
  }

  public void start(String t, String c, String m, String params) {
    long metaId = NodeMeta.getId(t, c, m);

    Stack<TreeNode> stack = t_stack.get();
    Stack<StaticNode> sStack = s_stack.get();
    Stack<Long> time = t_time.get();
    if (stack == null) {
      t_stack.set(stack = new Stack<TreeNode>());
      s_stack.set(sStack = new Stack<StaticNode>());

      TreeNode node = new TreeNode(metaId, params, Thread.currentThread().getId());

      StaticNode staticNode = staticQueue.get(metaId);
      if (staticNode == null) {
        StaticNode newStaticNode = staticQueue.putIfAbsent(metaId, staticNode = new StaticNode(metaId));
        if (newStaticNode != null) {
          staticNode = newStaticNode;
        }
      }
      staticNode.addDoing();

      LinkedBlockingQueue<Long> queue = queues.get(metaId);
      if (queue == null) {
        LinkedBlockingQueue<Long> newQueue = queues
            .putIfAbsent(metaId, queue = new LinkedBlockingQueue<Long>(SIZE));
        if (newQueue != null) {
          queue = newQueue;
        }
      }
      long seed = SEED.incrementAndGet();
      while (!queue.offer(seed)) {
        queue.poll();
      }
      pool.put(seed, node);
      stack.add(node);
      sStack.add(staticNode);

      t_time.set(time = new Stack<Long>());
    }


    if (stack != null && !stack.isEmpty()) {
      if (!time.isEmpty()) {
        stack.push(stack.peek().addSon(metaId, params));
        sStack.push(sStack.peek().addSon(metaId));
      }
      time.add(System.currentTimeMillis());
    }
  }

  public void end(String c, String m, boolean err, String ret) {
    Stack<TreeNode> stack = t_stack.get();
    Stack<StaticNode> sStack = s_stack.get();
    Stack<Long> time = t_time.get();
    if (stack != null && !stack.isEmpty()) {
      long diff = System.currentTimeMillis() - time.pop();
      stack.pop().setDone(err, diff, ret);
      sStack.pop().setDone(err, diff);
    }
    if (stack != null && stack.isEmpty()) {
      t_stack.set(null);
      s_stack.set(null);
      t_time.set(null);
    }
  }
}
