package com.stk123.model.tree;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.stk123.model.bo.StkIndexNode;
import org.apache.commons.lang.StringUtils;

/**
 * @author fankai
 * <p>
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class TreeNode<T> implements Serializable {
    private static final long serialVersionUID = 0L;

    private int nodeId = 0;
    private int level = 0;
    private String name = null;
    private int parentId = 0;

    private T data = null;

    @Deprecated
    private StkIndexNode stkIndexNode = null;

    private TreeNode parent;
    private List<TreeNode> children;

    public TreeNode() {
        this.nodeId = 0;
        this.level = 0;
        this.parentId = 0;

        children = new ArrayList<TreeNode>();
        parent = null;
    }

    public TreeNode(int nodeId, int level, int parentId, StkIndexNode data) {
        this.nodeId = nodeId;
        this.level = level;
        this.parentId = parentId;

        children = new ArrayList();
        parent = null;
        this.stkIndexNode = data;
        this.name = data.getName();
    }

    /**
     * @param nodeId int
     * @param parent Node
     */
    public TreeNode(int nodeId, TreeNode parent) {
        this.nodeId = nodeId;
        if (parent != null) {
            this.setParent(parent);
            parent.addChild(this);
        }
    }

    public TreeNode(int nodeId, T data) {
        this();
        this.setNodeData(data);
    }


    public TreeNode findParentNode(TreeNode node, MatchConditionForParentAndChild condition){
        if(node != null){
            for(TreeNode child : getChildren()){
                if(condition.match(child, node)){
                    return child;
                }else{
                    return child.findParentNode(node, condition);
                }
            }
        }
        return null;
    }

    /**
     * @return 是否添加成功
     */
    public boolean addNode(TreeNode node, MatchConditionForParentAndChild condition) {
        TreeNode parentNode = this.findParentNode(node, condition);
        if(parentNode != null){
            parentNode.addChild(node);
            return true;
        }
        return false;
    }

    //default：T = NodeData, 根据parentNode.data.code==childNode.data.parentCode来看是否是父子关系
    public boolean addNode(TreeNode node) {
        return this.addNode(node, new MatchConditionForParentAndChild<NodeData>() {
            @Override
            public boolean match(TreeNode<NodeData> parentNode, TreeNode<NodeData> childNode) {
                if(StringUtils.isEmpty(parentNode.getNodeData().getCode()) || StringUtils.isEmpty(childNode.getNodeData().getParentCode())){
                    return false;
                }
                return StringUtils.equals(parentNode.getNodeData().getCode(), childNode.getNodeData().getParentCode());
            }
        });
    }

    /**
     * @return int
     */
    final public int getNoOfChildren() {
        return children.size();
    }

    final public int getCurrNumber() {
        if (parent == null) {
            return 0;
        } else {
            return getParent().getNoOfChildren();
        }
    }

    /**
     * @return boolean
     */
    final public boolean hasChildren() {
        return children.size() > 0;
    }


    /**
     * @param level int
     * @return Node
     */
    final public TreeNode getParent(int level) {
        if (level < 0 || level >= this.level) {
            throw new ArrayIndexOutOfBoundsException("level is error.");
        }
        TreeNode n = parent;
        for (int i = 1; i < level; i++) {
            n = n.getParent();
        }
        return n;
    }

    final public TreeNode getRoot() {
        TreeNode node = getParent();
        if (node == null) return node;
        else {
            do {
                node = getParent();
            } while (node.getParent() != null);
        }
        return node;
    }


    /**
     * @return int
     */
    final public int getPosition() {
        if (parent == null) {
            return 0;
        }
        return parent.children.indexOf(this);
    }

    /**
     * @return boolean
     */
    final public boolean isLast() {
        if (parent == null) {
            return true;
        }
        return getPosition() == parent.children.size() - 1;
    }

    /**
     * @return boolean
     */
    final public boolean isFirst() {
        return getPosition() == 0;
    }

    /**
     * @return Node
     */
    final public TreeNode getNext() {
        if (children.size() > 0) {
            return (TreeNode) children.get(0);
        }
        TreeNode n = parent;
        while (n != null) {
            TreeNode node = n.getNextSibling();
            if (node != null) {
                return node;
            }
            n = n.getParent();
        }
        return null;
    }

    /**
     * @return Node
     */
    final public TreeNode getNextSibling() {
        if (parent == null) {
            return null;
        }
        int k = getPosition();
        if (k == parent.getNoOfChildren() - 1) {
            return null;
        }
        return (TreeNode) parent.children.get(k + 1);
    }

    /**
     * @return Node
     */
    final public TreeNode getPreviousSibling() {
        int k = getPosition();
        if (k == 0) {
            return null;
        }
        return (TreeNode) parent.children.get(k - 1);
    }

    /**
     * @return Node
     */
    final public TreeNode getPrevious() {
        TreeNode n = getPreviousSibling();
        if (n != null) {
            return n;
        }
        return parent;
    }

    public void addChild(TreeNode node) {
        this.children.add(node);
        node.setParent(this);
        node.setLevel(this.getLevel() + 1);
    }

    public String toString() {
        String str = " ";
        for (int i = 0; i < level; i++) {
            str += " ";
        }
        return str + "node_id=" + this.nodeId + ", name=" + this.name;
    }

    public int getNodeId() {
        return nodeId;
    }

    public void setNodeId(int nodeId) {
        this.nodeId = nodeId;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /*
     */
    public int getParentId() {
        return this.getParent()==null?-1:this.getParent().getParentId();
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public List<TreeNode> getChildren() {
        return children;
    }

    public void setChildren(List<TreeNode> children) {
        this.children = children;
    }

    public TreeNode getParent() {
        return parent;
    }

    public void setParent(TreeNode parent) {
        this.parent = parent;
        parent.setLevel(this.getLevel() - 1);
    }

    public T getNodeData() {
        return data;
    }

    public void setNodeData(T data) {
        this.data = data;
    }

    public StkIndexNode getStkIndexNode() {
        return stkIndexNode;
    }

    public void setStkIndexNode(StkIndexNode stkIndexNode) {
        this.stkIndexNode = stkIndexNode;
    }
}

interface MatchConditionForParentAndChild<T> {
    public boolean match(TreeNode<T> parentNode, TreeNode<T> childNode);
}
