package com.stk123.service;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.stk123.model.bo.StkDataPpiType;
import com.stk123.model.bo.StkIndexNode;
import com.stk123.model.Industry;
import com.stk123.common.db.connection.Pool;
import com.stk123.model.tree.Tree;
import com.stk123.model.tree.TreeNode;
import com.stk123.common.util.CacheUtils;
import com.stk123.common.util.JdbcUtils;
import com.stk123.common.util.JsonUtils;
import com.stk123.common.CommonConstant;
import com.stk123.model.pojo.MetaData;
import com.stk123.model.pojo.Node;

public class IndexService {

	public final static String ID_XZH = "3000";
	public final static int ID_INDUSTRY_PE = 60000;
	public final static int ID_PPI = 700000;

	//private static Tree tree = null;

	public Tree getTree() throws Exception {
		Tree tree = (Tree)CacheUtils.getForever(CacheUtils.KEY_INDEX_TREE);
		if(tree != null){
			return tree;
		}
		Connection conn = null;
		List<TreeNode> treeNodes = new ArrayList<TreeNode>();
		try{
			conn = Pool.getPool().getConnection();
			List<StkIndexNode> nodes = JdbcUtils.list(conn, "select * from stk_index_node where disp_order >= 0 order by node_level asc,disp_order asc", StkIndexNode.class);
			for(StkIndexNode node : nodes){
				TreeNode tn = new TreeNode(node.getId(), node.getNodeLevel(), node.getParentId(), node);
				treeNodes.add(tn);
			}
			tree = Tree.getTree(treeNodes, 1);

			//行业平均PE
			TreeNode industryPENode = tree.getNode(ID_INDUSTRY_PE);
			List<Industry> industryTypes = Industry.getIndsutriesBySource(conn, Industry.INDUSTRY_CNINDEX);
			for(Industry ind : industryTypes){
				StkIndexNode node = new StkIndexNode();
				node.setId(ind.getType().getId().intValue() + ID_INDUSTRY_PE);
				node.setName(ind.getType().getName());
				node.setNodeLevel(2);
				node.setParentId(ID_INDUSTRY_PE);
				TreeNode tn = new TreeNode(node.getId(), node.getNodeLevel(), node.getParentId(), node);
				tn.setParent(industryPENode);
				industryPENode.addChild(tn);
			}
			//大宗商品指标
			TreeNode ppiNode = tree.getNode(ID_PPI);
			List<StkDataPpiType> ppis = JdbcUtils.list(conn, "select * from stk_data_ppi_type order by name asc", StkDataPpiType.class);
			for(StkDataPpiType ppi : ppis){
				StkIndexNode node = new StkIndexNode();
				node.setId(ppi.getId().intValue() + ID_PPI);
				node.setName(ppi.getName());
				node.setNodeLevel(2);
				node.setParentId(ID_PPI);
				TreeNode tn = new TreeNode(node.getId(), node.getNodeLevel(), node.getParentId(), node);
				tn.setParent(ppiNode);
				ppiNode.addChild(tn);
			}

			CacheUtils.putForever(CacheUtils.KEY_INDEX_TREE, tree);
			return tree;
		}finally{
			Pool.getPool().free(conn);
		}
	}

	public String getTreeJson() throws Exception {
		return JsonUtils.getJsonString4JavaPOJO(convertToNode(this.getTree().getChildren()));
	}

	private List<Node> convertToNode(List<TreeNode> list){
		List<Node> rs = new ArrayList<Node>();
		for(TreeNode node : list){
			Node n = indexNodeToNode((StkIndexNode)node.getStkIndexNode());
			if(node.getChildren().size() > 0){
				n.getChildren().addAll(convertToNode(node.getChildren()));
			}else{
				n.getAttr().setIsLeaf(CommonConstant.YES_Y);
			}
			rs.add(n);
		}
		return rs;
	}

	private Node indexNodeToNode(StkIndexNode indexNode){
		Node node = new Node();
		node.setData(indexNode.getName());
		MetaData metadata = new MetaData();
		metadata.setId(indexNode.getId().toString());
		//metadata.setTitle(indexNode.getName());
		node.setAttr(metadata);
		if(node.getAttr().getId().equals(ID_XZH)){
			node.setState("open");
		}
		return node;
	}

	public List<Map> bias(int id) throws Exception {
		Connection conn = null;
		try{
			conn = Pool.getPool().getConnection();
			List<Map> list = JdbcUtils.list2Map(conn, "select distinct report_date d, bias \""+id+"\" from stk_pe order by report_date asc");
			return list;
		}finally{
			Pool.getPool().free(conn);
		}
	}

	public List<Map> getPPI(int id) throws Exception {
		Connection conn = null;
		try{
			conn = Pool.getPool().getConnection();
			List<Map> list = JdbcUtils.list2Map(conn, "select ppi_date d, value \""+id+"\" from stk_data_ppi where type_id="+(id-IndexService.ID_PPI)+" order by ppi_date asc");
			return list;
		}finally{
			Pool.getPool().free(conn);
		}
	}

	public final static String SQL_INDEX_INDUSTRY_PE = "with " +
		"t1 as (select pe_date,pe_ttm from stk_data_industry_pe where industry_id=? and type=1)," +
		"t2 as (select pe_date,pe_ttm from stk_data_industry_pe where industry_id=? and type=2)," +
		"t3 as (select pe_date,pe_ttm from stk_data_industry_pe where industry_id=? and type=3)," +
		"t4 as (select avg(pe_ttm) pe_ttm,pe_date from stk_data_industry_pe where industry_id=? group by pe_date) " +
		"select t1.pe_date d,t1.pe_ttm a,t2.pe_ttm b,t3.pe_ttm c,trunc(t4.pe_ttm,2) v from t1,t2,t3,t4 where t1.pe_date=t2.pe_date and t2.pe_date=t3.pe_date and t3.pe_date=t4.pe_date order by t1.pe_date asc";

	public List<Map> industryPE(int id) throws Exception {
		Connection conn = null;
		try{
			List params = new ArrayList();
			params.add(id);
			params.add(id);
			params.add(id);
			params.add(id);
			conn = Pool.getPool().getConnection();
			List<Map> list = JdbcUtils.list2Map(conn, SQL_INDEX_INDUSTRY_PE, params);
			return list;
		}finally{
			Pool.getPool().free(conn);
		}
	}

	public static void main(String[] args) throws Exception {
		IndexService is = new IndexService();
		String json = is.getTreeJson();
		System.out.println(json);
	}
}
