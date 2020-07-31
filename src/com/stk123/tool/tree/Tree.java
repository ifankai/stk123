package com.stk123.tool.tree;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;


/**
 * @author fankai
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Tree extends TreeNode {

    private static final long serialVersionUID = 1L;

    private Tree(){
        super();
	}

	public static Tree getTree(List list, int rootId)
	{
		Tree rs = new Tree();
	    Collections.sort(list, new Compare());
	    for (int i = 0; i < list.size(); i++)
	    {
	      TreeNode tnode = (TreeNode) list.get(i);
	      if (tnode.getLevel() == rootId)
	      {
	        rs.getChildren().add(tnode);
	      }
	      else
	      {
	      	TreeNode parent = null;
	      	for (int j = 0; j < rs.getChildren().size(); j++) {
	      		TreeNode tmp = (TreeNode) rs.getChildren().get(j);
	      		if ( tmp.getNodeId() == tnode.getParentId())
	      		{
	      			parent = tmp;
	      		}
	      		else
	      		{
	      			parent = rs.getNode(tnode.getParentId());
	      		}
	      		if (parent == null)
	      		{
	      			throw new NullPointerException("Node " + tnode.getParentId() + " not found.");
	      		}
	      		else
	      		{
	      			tnode.setParent(parent);
	      	        parent.getChildren().add(tnode);
	      			break;
	      		}
	      	}
	      }
	    }
	    return rs;
	}

	/**
	 * @param id String
	 * @return Node
	 */
	public TreeNode getNode(int id)
	{
	    if (id == getNodeId())
	    {
	      return this;
	    }
	    return getNode(getChildren().iterator(), id);
	}

	private TreeNode getNode(Iterator it, int id)
	{
		while (it.hasNext())
	    {
		  TreeNode n = (TreeNode) it.next();
	      if (n.getNodeId() == id)
	      {
	        return n;
	      }
	      if (n.getChildsNumber() > 0)
	      {
	        n = getNode(n.getChildren().iterator(), id);
	        if (n != null)
	        {
	          return n;
	        }
	      }
	    }
	    return null;
	}

	/*
	 * 得到所有节点
	 */
	public static void getNodeList(TreeNode tree,List po_list)
	{
		if(tree.getChildren().size() > 0){
			for(int i = 0;i < tree.getChildren().size(); i ++){
				TreeNode node = (TreeNode)tree.getChildren().get(i);
				po_list.add(node);
				if(node.getChildren().size() > 0){
					getNodeList(node,po_list);
			    }
			}
		}
	}

    final public String getFullName(int id,String sep)
    {
        String fullName = String.valueOf(id);
        TreeNode node = getNode(id);
        TreeNode parent = node;
        while(parent.getParent() != null){
            parent = parent.getParent();
            fullName = parent.getNodeId()+sep+fullName;
        }
        while(node.hasChildren()){
            node = (TreeNode)node.getChildren().get(0);
            fullName += sep+node.getNodeId();
        }
        return fullName;
    }

}

class Compare implements Comparator
{
	public Compare()
	{}

	public int compare(Object obj1, Object obj2)
	{
		int id1 = ( (TreeNode) obj1).getLevel();
		int id2 = ( (TreeNode) obj2).getLevel();
		return id1 - id2;
	}
}
