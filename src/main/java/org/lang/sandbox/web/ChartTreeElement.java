package org.lang.sandbox.web;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.lang.sandbox.model.NodeMeta;
import org.lang.sandbox.model.StaticNode;
import org.lang.sandbox.model.TreeNode;

public class ChartTreeElement {

    public String color;
    public String name;

    public Long cnt;
    public NodeTooltip tooltip;
    //public Data data;
    public List<ChartTreeElement> children;

    public class NodeTooltip {
        public String type;
        public String clazz;
        public String method;
        public String time;
        public Integer err;
        public long cost;
        public Integer doing;
        public Long tid;

        public String params;
        public String ret;
    }

    public static NodeTooltip parse(TreeNode node, NodeTooltip tooltip) {
        String[] meta = NodeMeta.getName(node.getMid());
        tooltip.type = meta[0];
        tooltip.clazz = meta[1];
        tooltip.method = meta[2];

        Long time = node.getTime();
        if(time != null) {
            tooltip.time = new SimpleDateFormat("HH:mm:ss").format(node.getTime());
        }
        tooltip.tid = node.getTid();

        tooltip.cost = node.getTotalRt();
        tooltip.doing = node.getDoing() ? 1 : 0;
        tooltip.err = node.getErr() ? 1 : 0;
        tooltip.params = node.getParams();
        tooltip.ret = node.getRet();

        return tooltip;
    }

//  public static Data parse(TreeNode node, Data data){
//    data.params = node.getParams();
//    data.ret = node.getRet();
//    return data;
//  }

    public static ChartTreeElement parse(TreeNode node) {
        ChartTreeElement element = new ChartTreeElement();
        String[] meta = NodeMeta.getName(node.getMid());
        element.color = getColor(meta[0]);
        element.name = meta[2];

        element.tooltip = parse(node, element.new NodeTooltip());
        //element.data = parse(node, element.new Data());

        List<TreeNode> sons = node.getSonCopy();
        if (sons != null) {
            for (TreeNode son : sons) {
                if (element.children == null) {
                    element.children = new ArrayList<ChartTreeElement>();
                }
                element.children.add(parse(son));
            }
        }
        return element;
    }

    public static NodeTooltip parse(StaticNode node, NodeTooltip tooltip) {
        String[] meta = NodeMeta.getName(node.getMid());
        tooltip.type = meta[0];
        tooltip.clazz = meta[1];
        tooltip.method = meta[2];
        tooltip.cost = node.getTotalRt();
        tooltip.doing = node.getDoing();
        tooltip.err = node.getErr();
        return tooltip;
    }


    public static ChartTreeElement parse(StaticNode node) {
        ChartTreeElement element = new ChartTreeElement();
        String[] meta = NodeMeta.getName(node.getMid());
        element.color = getColor(meta[0]);
        element.name = meta[2];
        element.cnt = node.getCnt();
        element.tooltip = parse(node, element.new NodeTooltip());

        List<StaticNode> sons = node.getSonCopy();
        if (sons != null) {
            for (StaticNode son : sons) {
                if (element.children == null) {
                    element.children = new ArrayList<ChartTreeElement>();
                }
                element.children.add(parse(son));
            }
        }
        return element;
    }


    private static String getColor(String type) {
        String color = "mistyrose";
        if (type.equalsIgnoreCase("dubbo")) {
            color = "burlywood ";
        } else if (type.equalsIgnoreCase("db")) {
            color = "green";
        } else if (type.equalsIgnoreCase("redis")) {
            color = "pink";
        } else if (type.equalsIgnoreCase("mq")) {
            color = "yellow";
        }
        return color;
    }

}
