package com.stk123.tool.tree;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.stk123.bo.StkIndexNode;

/**
 * @author fankai
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class TreeNode implements Serializable
{
    private static final long serialVersionUID = 0L;

    private int nodeId = 0;
    private int level = 0;
    private String name = "";
    private int parentId = 0;

    private TreeNode parent;
    private List children;

    private StkIndexNode data = null;

    public TreeNode()
    {
    	this.nodeId = 0;
        this.level = 0;
        this.parentId = 0;

        children = new ArrayList();
        parent = null;
    }

	public TreeNode(int nodeId,int level,int parentId, StkIndexNode data)
	{
        this.nodeId = nodeId;
        this.level = level;
        this.parentId = parentId;

        children = new ArrayList();
        parent = null;
        this.data = data;
        this.name = data.getName();
    }

	/**
	 * @param nodeId int
	 * @param parent Node
	 */
	public TreeNode(int nodeId, TreeNode parent)
	{
		this.nodeId = nodeId;
	    if (parent != null)
	    {
	      this.parent = parent;
	      parent.children.add(this);
	      this.level = parent.getLevel() + 1;
	    }
	}


    public int getNodeId()
    {
        return nodeId;
    }
    public void setNodeId(int nodeId)
    {
        this.nodeId = nodeId;
    }

	public int getLevel()
	{
		return level;
	}
    public void setLevel(int level)
    {
        this.level = level;
    }

    public String getName()
    {
        return name;
    }
    public void setName(String name)
    {
        this.name = name;
    }

    /*
     */
    public int getParentId()
    {
        return parentId;
    }
    public void setParentId(int parentId)
    {
        this.parentId = parentId;
    }

    public List getChildren()
    {
        return children;
    }
    public void setChildren(List childs)
    {
        this.children = (ArrayList)childs;
    }
    public void addChild(TreeNode node){
        this.children.add(node);
    }

    public TreeNode getParent()
    {
        return parent;
    }
    public void setParent(TreeNode parent)
    {
        this.parent = parent;
    }

	public StkIndexNode getStkIndexNode() {
		return data;
	}

	public void setStkIndexNode(StkIndexNode data) {
		this.data = data;
	}

	/**
	 * @return int
	 */
	final public int getChildsNumber()
	{
	    return children.size();
	}
    final public int getCurrNumber()
    {
        if(parent == null){
            return 0;
        }else{
            return getParent().getChildsNumber();
        }
    }

	/**
	 * @return boolean
	 */
	final public boolean hasChildren()
	{
	    return children.size() > 0;
	}


	/**
	 * @param level int
	 * @return Node
	 */
	final public TreeNode getParent(int level)
	{
		if (level < 0 || level >= this.level)
	    {
            //return null;
            throw new ArrayIndexOutOfBoundsException("level is error.");
	    }
		TreeNode n = parent;
	    for (int i = 1; i < level; i++)
	    {
	      n = n.getParent();
	    }
	    return n;
	}

    final public TreeNode getRoot()
    {
        TreeNode node = getParent();
        if(node == null)return node;
        else{
            do{
                node = getParent();
            }while(node.getParent() != null);
        }
        return node;
    }


	/**
	 * @return int
	 */
	final public int getPosition()
	{
	    if (parent == null)
	    {
	      return 0;
	    }
	    return parent.children.indexOf(this);
	}

	/**
	 * @return boolean
	 */
	final public boolean isLast()
	{
	    if (parent == null)
	    {
	      return true;
	    }
	    return getPosition() == parent.children.size() - 1;
	}

	/**
	 * @return boolean
	 */
	final public boolean isFirst()
	{
	    return getPosition() == 0;
	}

	/**
	 * @return Node
	 */
	final public TreeNode getNext()
	{
	    if (children.size() > 0)
	    {
	      return (TreeNode) children.get(0);
	    }
	    TreeNode n = parent;
	    while (n != null)
	    {
	    	TreeNode node = n.getNextSibling();
	    	if (node != null)
	    	{
	    		return node;
	    	}
	    	n = n.getParent();
	    }
	    return null;
	}

	/**
	 * @return Node
	 */
	final public TreeNode getNextSibling()
	{
	    if (parent == null)
	    {
	      return null;
	    }
	    int k = getPosition();
	    if (k == parent.getChildsNumber() - 1)
	    {
	      return null;
	    }
	    return (TreeNode) parent.children.get(k + 1);
	}

	/**
	 * @return Node
	 */
	final public TreeNode getPreviousSibling()
	{
	    int k = getPosition();
	    if (k == 0)
	    {
	      return null;
	    }
	    return (TreeNode) parent.children.get(k - 1);
	}

	/**
	 * @return Node
	 */
	final public TreeNode getPrevious()
	{
		TreeNode n = getPreviousSibling();
	    if (n != null)
	    {
	      return n;
	    }
	    return parent;
	}

	public String toString()
	{
		String str = " ";
		for(int i = 0;i < level;i ++){
			str += " ";
		}
		return str + "node_id=" + this.nodeId + ", name=" + this.name;
	}
}
