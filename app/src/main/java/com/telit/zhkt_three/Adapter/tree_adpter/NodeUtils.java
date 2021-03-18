package com.telit.zhkt_three.Adapter.tree_adpter;

import android.text.TextUtils;

import com.telit.zhkt_three.Utils.QZXTools;

import java.util.ArrayList;
import java.util.List;

/**
 * author: qzx
 * Date: 2019/5/17 14:28
 */
public class NodeUtils {
    /**
     * 获取nodes根节点(可能有多个根节点)
     *
     * @param nodes
     * @return
     */
    public static List<Node> getNodesRoot(List<Node> nodes) {
        List<Node> roots = new ArrayList<>();
        if (nodes == null || nodes.size() == 0) {
            return roots;
        }
        for (Node node : nodes) {
            if (TextUtils.isEmpty(node.getPid())) {
                roots.add(node);
            }
        }
        return roots;
    }

    /**
     * 设置nodes中各节点子节点
     *
     * @param nodes
     * @return
     */
    public static List<Node> tidyNodes(List<Node> nodes) {
        for (int i = 0; i < nodes.size(); i++) {
            for (int j = i + 1; j < nodes.size(); j++) {
                //i 是 j 的父节点
                if (nodes.get(i).getId().equals(nodes.get(j).getPid())) {
                    nodes.get(i).getChilds().add(nodes.get(j));
                }
                //j 是 i 的父节点
                else if (nodes.get(i).getPid().equals(nodes.get(j).getId())) {
                    nodes.get(j).getChilds().add(nodes.get(i));
                }
            }
        }

        return nodes;
    }

    /**
     * 设置显示层级
     *
     * @param nodes
     * @param level
     */
    public static void setShowLevel(List<Node> nodes, int level) {
        for (Node node : nodes) {
            if (node.getLevel() <= level) {
                node.setExpand(true);
            } else {
                node.setExpand(false);
            }
        }
    }

    /**
     * 寻找叶节点
     *
     * @param nodes
     * @return
     */
    public static List<Node> findLeafs(List<Node> nodes) {
        List<Node> leafs = new ArrayList<>();
        if (nodes == null || nodes.size() == 0) {
            return leafs;
        }
        for (Node node : nodes) {
            if (node.getChilds().isEmpty()) {
                leafs.add(node);
            }
        }
        return leafs;
    }

    public static void showNodes(List<Node> nodes, Node node, boolean show) {
        for (Node item : nodes) {
            if (item.getId().equals(node.getId())) {
                for (Node child : item.getChilds()) {
                    child.setExpand(show);
                    if (child.getChilds().isEmpty()) {
                        continue;
                    }
                    showNodes(nodes, child, show);
                }
            }
        }
    }

    /**
     * 设置节点的expand属性，收起或展开
     *
     * @param nodes
     * @param node
     */
    public static void showNodes2(List<Node> nodes, Node node) {
        boolean show = true;
        for (Node item : nodes) {
            if (item.getId().equals(node.getId())) {
                for (Node child : item.getChilds()) {
                    if (child.isExpand()) {
                        show = false;
                        break;
                    }
                }
            }
        }
        for (Node item : nodes) {
            if (item.getId().equals(node.getId())) {
                for (Node child : item.getChilds()) {
                    child.setExpand(show);
                    showNodes2(item.getChilds(), child, false);
                }
            }
        }
    }

    public static void showNodes2(List<Node> nodes, Node node, boolean show) {
        for (Node item : nodes) {
            for (Node child : item.getChilds()) {
                child.setExpand(show);
                showNodes2(item.getChilds(), child, show);
            }
        }
    }

    /**
     * 设置节点选中情况，当前节点选中情况决定了父节点和子节点的勾选情况
     *
     * @param nodes
     * @param node
     * @param chooseStatus
     */
    public static void chooseNodes(List<Node> nodes, Node node, int chooseStatus) {
        QZXTools.logE("node id=" + node.getId() + ";pid=" + node.getPid() + ";chooseStatus=" + chooseStatus, null);
        //设置子节点
        for (Node item : nodes) {
            if (item.getId().equals(node.getId())) {
                for (Node child : item.getChilds()) {
                    child.setChoosed(chooseStatus);

                    if (child.getChilds().isEmpty()) {
                        continue;
                    }
                    chooseNodes(nodes, child, chooseStatus);
                }
            }
        }
    }

    /**
     * 先执行子节点选中，然后进行父类判断
     * <p>
     * chooseNodes和chooseParentNodes要联合使用
     */
    public static void chooseParentNodes(List<Node> nodes, Node node) {
        //设置父节点
        List<Node> parents = new ArrayList<>();
        getParents(nodes, node, parents);
        for (Node parent : parents) {
            QZXTools.logE("parent id=" + parent.getId() + ";pid=" + parent.getPid(), null);
            int countChoosed = 0;
            if (parent == null) {
//                QZXTools.logE("parents is null", null);
                //本例中只有一个root节点
                parent = getNodesRoot(nodes).get(0);
            } else {
//                QZXTools.logE("parents not null = " + parent.getChilds().size(), null);
                /**
                 * 解决parent都是没有子节点的bug
                 * */
                if (parent.getChilds().size() <= 0) {
                    if (!parent.getId().equals(node.getId())) {
                        continue;
                    }
                }

                for (Node node1 : parent.getChilds()) {
                    countChoosed = countChoosed + node1.getChoosed();
                }
                if (countChoosed == Node.CHOOSE_ALL * parent.getChilds().size()) {
                    parent.setChoosed(Node.CHOOSE_ALL);
                } else if (countChoosed == Node.CHOOSE_NONE * parent.getChilds().size()) {
                    parent.setChoosed(Node.CHOOSE_NONE);
                } else {
                    parent.setChoosed(Node.CHOOSE_PART);
                }

            }
        }
    }

    /**
     * 通过id查询node
     *
     * @param nodes
     * @param id
     * @return
     */
    public static Node getNode(List<Node> nodes, String id) {
        for (Node node : nodes) {
            if (node.getId().equals(id)) {
                return node;
            }
        }
        return null;
    }

    /**
     * 获取节点的所有父节点（包括间接父节点）
     *
     * @param nodes
     * @param node
     * @param results
     */
    public static void getParents(List<Node> nodes, Node node, List<Node> results) {
        QZXTools.logE("parent node id=" + node.getId() + ";pid=" + node.getPid(), null);
        for (Node item : nodes) {
            if (TextUtils.isEmpty(item.getPid())) {
                results.add(item);
            } else if (node.getPid().equals(item.getId())) {
                results.add(item);
                getParents(nodes, item, results);
            }
        }
    }

    /**
     * 获取直接子节点的展开状况
     *
     * @param node
     * @return
     */
    public static int getChildExpandStatus(Node node) {
        if (node.getChilds() == null || node.getChilds().size() == 0) {
            return Node.CHILD_EXPAND_ALL;
        } else {
            int expandCount = 0;
            for (Node child : node.getChilds()) {
                if (child.isExpand()) {
                    expandCount++;
                }
            }
            if (expandCount == node.getChilds().size()) {
                return Node.CHILD_EXPAND_ALL;
            } else if (expandCount == 0) {
                return Node.CHILD_EXPAND_NONE;
            } else {
                return Node.CHILD_EXPAND_PART;
            }
        }
    }
}
