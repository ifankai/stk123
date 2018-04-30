package com.stk123.model;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.stk123.bo.StkLabel;
import com.stk123.bo.StkLabelText;
import com.stk123.bo.StkText;
import com.stk123.task.StkUtils;
import com.stk123.tool.db.TableTools;
import com.stk123.tool.db.connection.Pool;
import com.stk123.tool.db.util.DBUtil;
import com.stk123.tool.db.util.sequence.SequenceUtils;
import com.stk123.tool.util.ConfigUtils;
import com.stk123.tool.util.JdbcUtils;
import com.stk123.web.StkConstant;

public class Label {
	
	private List<StkLabel> labels;
	private int userId;
	private int id;

	public Label(int userId){
		this.userId = userId;
	}
	
	public Label(int userId, int id){
		this.userId = userId;
		this.id = id;
	}
	
	public final static String SQL_SELECT_LABEL_ALL = "select * from stk_label where user_id=? order by F_TRANS_PINYIN_CAPITAL(name)";
	public List<StkLabel> getLabels() throws Exception {
		if(labels != null){
			return labels;
		}
		Connection conn = null;
		try{
			conn = Pool.getPool().getConnection();
			List params = new ArrayList();
			params.add(this.userId);
			labels = JdbcUtils.list(conn, SQL_SELECT_LABEL_ALL, params, StkLabel.class);
			return labels;
		}finally{
			Pool.getPool().free(conn);
		}
	}
	
	//public final static String SQL_INSERT_LABEL = "insert into stk_label(id,name,insert_time,update_time) values(?,?,sysdate(),null)";
	public final static String SQL_INSERT_LABEL = "insert into stk_label(id,name,insert_time,update_time,user_id) values(?,?,sysdate,null,?)";
	public StkLabel add(String name) throws Exception {
		long seq = SequenceUtils.getSequenceNextValue(SequenceUtils.SEQ_LABEL_ID);//JdbcUtils.getSequence(conn, "s_label_id");
		Connection conn = null;
		try{
			conn = Pool.getPool().getConnection();
			List params = new ArrayList();
			params.add(seq);
			params.add(name);
			params.add(this.userId);
			int n = JdbcUtils.insert(conn, SQL_INSERT_LABEL, params);
			if(n > 0){
				this.labels = null;
				StkLabel label = load(seq);
				return label;
			}
			return null;
		}finally{
			Pool.getPool().free(conn);
		}
	}
	public final static String SQL_SELECT_LABEL_BY_ID = "select * from stk_label where id=? and user_id=?";
	public StkLabel load(long id) throws Exception{
		List<StkLabel> labels = this.getLabels();
		for(StkLabel label : labels){
			if(label.getId() == id){
				return label;
			}
		}
		return null;
	}
	
	private final static String SQL_SELECT_LABEL_BY_NAME = "select * from stk_label where user_id=? and name=?";
	public StkLabel getByName(String name) throws Exception {
		List<StkLabel> labels = this.getLabels();
		for(StkLabel label : labels){
			if(label.getName().equals(name)){
				return label;
			}
		}
		return null;
	}
	
	private final static String SQL_DELETE_LABEL_TEXT_BY_NOTIN_LABLE_ID = "delete from stk_label_text where text_id=? and label_id not in (";
	public void addLink(List<StkLabel> labels, long textId) throws Exception{
		List<Integer> labelIds = new ArrayList<Integer>();
		for(StkLabel sl : labels){
			if(sl.getId() == 0){
				StkLabel label = getByName(sl.getName());
				if(label == null){
					label = add(sl.getName());
					addLink(label.getId(), textId);
				}else{
					StkLabelText link = loadLink(label.getId(),textId);
					if(link == null){
						addLink(label.getId(),textId);
					}
				}
				sl.setId(label.getId());
			}
			labelIds.add(sl.getId());
		}
		Connection conn = null;
		try{
			conn = Pool.getPool().getConnection();
			List params = new ArrayList();
			params.add(textId);
			JdbcUtils.delete(conn, SQL_DELETE_LABEL_TEXT_BY_NOTIN_LABLE_ID + StringUtils.join(labelIds, StkConstant.MARK_COMMA) + StkConstant.MARK_PARENTHESIS_RIGHT, params);
		}finally{
			Pool.getPool().free(conn);
		}
	}
	
	//public final static String SQL_INSERT_LABEL_TEXT = "insert into stk_label_text(id,label_id,text_id,insert_time,update_time) values(?,?,?,sysdate(),null)";
	public final static String SQL_INSERT_LABEL_TEXT = "insert into stk_label_text(id,label_id,text_id,insert_time,update_time) values(?,?,?,sysdate,null)";
	public long addLink(long labelId, long textId) throws Exception{
		long seq = SequenceUtils.getSequenceNextValue(SequenceUtils.SEQ_LABEL_TEXT_ID);//JdbcUtils.getSequence(conn, "s_label_text_id");
		Connection conn = null;
		try{
			conn = Pool.getPool().getConnection();
			List params = new ArrayList();
			params.add(seq);
			params.add(labelId);
			params.add(textId);
			JdbcUtils.insert(conn, SQL_INSERT_LABEL_TEXT, params);
			return seq;
		}finally{
			Pool.getPool().free(conn);
		}
	}
	
	public final static String SQL_SELECT_LABEL_TEXT_BY_LABEL_ID_AND_TEXT_ID = "select * from stk_label_text where label_id=? and text_id=?";
	public StkLabelText loadLink(long labelId, long textId) throws Exception {
		Connection conn = null;
		try{
			conn = Pool.getPool().getConnection();
			List params = new ArrayList();
			params.add(labelId);
			params.add(textId);
			return JdbcUtils.load(conn, SQL_SELECT_LABEL_TEXT_BY_LABEL_ID_AND_TEXT_ID, params, StkLabelText.class);
		}finally{
			Pool.getPool().free(conn);
		}
	}
	
	public final static String SQL_DELETE_LABEL_TEXT_BY_TEXT_ID = "delete from stk_label_text where text_id=?";
	public int deleteLink(String textId) throws Exception {
		Connection conn = null;
		try{
			conn = Pool.getPool().getConnection();
			List params = new ArrayList();
			params.add(textId);
			return JdbcUtils.delete(conn, SQL_DELETE_LABEL_TEXT_BY_TEXT_ID, params);
		}finally{
			Pool.getPool().free(conn);
		}
	}
	
	public final static String SQL_SELECT_LABEL_TEXT_BY_TEXT_ID = "select * from stk_label_text where text_id=? order by insert_time asc";
	public List<StkLabelText> getLabelTexts(int textId) throws Exception {
		Connection conn = null;
		try{
			conn = Pool.getPool().getConnection();
			List params = new ArrayList();
			params.add(textId);
			return JdbcUtils.list(conn, SQL_SELECT_LABEL_TEXT_BY_TEXT_ID, params, StkLabelText.class);
		}finally{
			Pool.getPool().free(conn);
		}
	}
	
	public int delete() throws Exception {
		Connection conn = null;
		try{
			conn = Pool.getPool().getConnection();
			List params = new ArrayList();
			params.add(this.id);
			params.add(this.id);
			params.add(this.userId);
			JdbcUtils.delete(conn, "delete from stk_label_text where label_id=? and exists (select 1 from stk_label where label_id=? and user_id=?)", params);
			params.clear();
			params.add(this.id);
			params.add(this.userId);
			return JdbcUtils.delete(conn, "delete from stk_label where id=? and user_id=?", params);
		}finally{
			Pool.getPool().free(conn);
		}
	}
	
	
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		ConfigUtils.setPropsFromResource(TableTools.class, "db.properties");
		Connection conn = null;
		try {
			conn = DBUtil.getConnection();
			Label label = new Label(1);
			List<StkText> sts = JdbcUtils.list(conn, "select id,title,disp_order from stk_text where type=0 order by disp_order desc,insert_time desc", StkText.class);
			for(StkText text : sts){
				Set<String> labels = StkUtils.getLabels(text.getTitle());
				labels.addAll(StkUtils.getLabels(text.getText()));
				if(labels.size() > 0){
					//label.addLink(labels, text.getId());
				}
			}
		}finally {
			if (conn != null) conn.close();
		}
	}

}
