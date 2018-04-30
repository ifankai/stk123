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
	 * 锟斤拷锟届方锟斤�?
	 * @param nodeId int 锟斤拷锟�?
	 * @param parent Node 锟斤拷锟斤拷锟�
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
     * 锟斤拷锟节碉拷ID
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
	 * 锟矫碉拷锟接斤拷锟斤拷锟�?.
	 * @return int 锟接斤拷锟斤拷锟�?
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
	 * 锟角凤拷锟斤拷锟接斤拷锟�?
	 * @return boolean 锟斤拷锟接斤拷惴碉拷锟絫rue
	 */
	final public boolean hasChildren()
	{
	    return children.size() > 0;
	}

	
	/**
	 * 锟矫碉拷锟斤拷level锟斤拷锟斤拷锟斤拷锟�?
	 * @param level int 锟斤拷锟斤拷锟侥诧拷锟�(level锟斤拷锟节碉拷锟斤拷0锟斤拷小锟节此斤拷锟侥诧拷锟�?
	 * @return Node 锟斤拷level锟斤拷锟斤拷锟斤拷锟�?
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
    //锟矫碉拷锟斤拷诘锟�
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
	 * 锟矫碉拷锟斤拷锟斤拷锟酵拷锟斤拷锟斤拷锟斤拷锟斤拷位锟斤拷.
	 * @return int 锟斤拷锟斤拷锟酵拷锟斤拷锟斤拷锟斤拷锟斤拷位锟斤拷
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
	 * 锟斤拷锟斤拷欠锟斤拷锟酵拷锟斤拷锟斤拷锟斤拷锟斤拷�?��斤拷.
	 * @return boolean 锟角凤拷锟斤拷true
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
	 * 锟斤拷锟斤拷欠锟酵拷锟斤拷锟斤拷牡锟揭伙拷锟�.
	 * @return boolean 锟角凤拷锟斤拷true
	 */
	final public boolean isFirst()
	{
	    return getPosition() == 0;
	}
	
	/**
	 * 锟矫碉拷目录锟斤拷锟斤拷锟斤拷一锟斤拷锟斤拷.
	 * 锟斤拷锟剿斤拷锟斤拷锟侥柯硷拷锟斤拷锟斤拷一锟斤拷锟斤拷锟津返伙拷null
	 * @return Node 锟斤拷一锟斤拷锟斤拷
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
	 * 锟矫碉拷锟斤拷一锟斤拷同锟斤拷锟斤拷锟�.
	 * 没锟斤拷锟斤拷一锟斤拷同锟斤拷锟斤拷惴碉拷锟絥ull
	 * @return Node 锟斤拷一锟斤拷同锟斤拷锟斤拷锟�
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
	 * 锟矫碉拷前一锟斤拷同锟斤拷锟斤拷锟�.
	 * 没锟斤拷前一锟斤拷同锟斤拷锟斤拷惴碉拷锟絥ull
	 * @return Node 前一锟斤拷同锟斤拷锟斤拷锟�
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
	 * 锟矫碉拷前一锟斤拷锟斤拷.
	 * 锟斤拷锟斤拷锟角耙伙拷锟斤拷锟轿猲ull
	 * @return Node 前一锟斤拷锟斤拷
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
