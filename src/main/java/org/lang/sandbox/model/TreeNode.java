package org.lang.sandbox.model;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class TreeNode extends NodeMeta {

    private TreeNode parent;
    private Integer invokeCnt = 1;
    private Long time;
    private Long tid;
    private long id;

    private boolean err;
    private boolean doing;
    private long totalRt;

    private String params;
    private String ret;

    private BlockingQueue<TreeNode> sons = new LinkedBlockingQueue<TreeNode>();

    public TreeNode(long id, String params, long tid){
        this(id, params);
        invokeCnt = 0;
        this.tid = tid;
        this.time = System.currentTimeMillis();
    }
    private TreeNode(long id, String params) {
        this.id = id;
        doing = true;
        this.params = params;
    }

    public TreeNode addSon(long id, String params) {
        TreeNode son = new TreeNode(id, params);
        addInvoke();
        son.parent =this;
        sons.add(son);
        return son;
    }

    public void addInvoke() {
        if (parent != null) {
            parent.addInvoke();
        }else{
            invokeCnt ++;
        }
    }

    public void setDone(boolean err, long rt, String ret) {
        this.err =err;
        this.totalRt = rt;
        this.ret = ret;
        this.doing = false;
        this.ret = ret;
    }

    public Long getTid(){
        return tid;
    }
    public Long getTime(){
        return time;
    }
    public long getMid() {
        return id;
    }

    public boolean getDoing() {
        return doing;
    }

    public boolean getErr() {
        return err;
    }

    public long getTotalRt() {
        return totalRt;
    }

    public Integer getInvokeCnt() {
        return invokeCnt;
    }
    public String getParams(){
        return params;
    }
    public String getRet(){
        return ret;
    }

    public List<TreeNode> getSonCopy() {
        if (sons.isEmpty()) {
            return null;
        }
        return new ArrayList<TreeNode>(sons);
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
        writer.write(" rt='" + totalRt + "'");
        writer.write(" doing='" + doing + "'");
        writer.write(" err='" + err + "'");
        writer.write(" class='" + items[1] + "'");
        writer.write(" type='" + items[0] + "'");
        writer.write(">\r\n");
        for (TreeNode son : sons) {
            son.writeFile(writer);
        }
        writer.write("</");
        writer.write(items[2]);
        writer.write(">\r\n");
    }

    public static void main(String[] args) throws IOException {
        long id = TreeNode.getId("t", "com.test.Service", "main");
        TreeNode node = new TreeNode(id, "");
        node.addSon(TreeNode.getId("t", "com.test.Service", "main1"), "");
        node.addSon(TreeNode.getId("t", "com.test.Service1", "main"), "");
        node.addSon(TreeNode.getId("t", "com.test.Service1", "main"), "");
        node.addSon(TreeNode.getId("t", "com.test.Service1", "main"), "");
        node.addSon(TreeNode.getId("t", "com.test.Service", "main"), "");
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter("/tmp/test.xml"));
            node.writeFile(writer);
        } finally {
            if (writer != null) {
                writer.close();
            }
        }

    }

}
