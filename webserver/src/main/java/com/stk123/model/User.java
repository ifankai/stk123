package com.stk123.model;

import java.io.Serializable;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import com.stk123.service.ServiceConstant;
import com.stk123.model.bo.StkUser;
import com.stk123.common.db.util.sequence.SequenceUtils;
import com.stk123.common.util.JdbcUtils;
import com.stk123.common.CommonConstant;

public class User implements Serializable{
	
	private StkUser stkUser;
	
	public User(StkUser su){
		this.stkUser = su;
	}
	
	private final static String SQL_LOAD_BY_NAME = "select * from stk_user where email=?";
	
	public static User loadByEmail(Connection conn, String email){
		StkUser su = JdbcUtils.load(conn, SQL_LOAD_BY_NAME, email, StkUser.class);
		if(su == null)return null;
		return new User(su); 
	}
	
	private final static String SQL_LOAD_BY_NAME_OR_EMAIL = "select * from stk_user where nickname=? or email=lower(?)";
	
	public static User loadByNameOrEmail(Connection conn, String name, String email){
		List params = new ArrayList();
		params.add(name);
		params.add(email);
		StkUser su = JdbcUtils.load(conn, SQL_LOAD_BY_NAME_OR_EMAIL, params, StkUser.class);
		if(su == null)return null;
		return new User(su); 
	}
	
	public static User create(Connection conn, String name, String email, String pw){
		long userId = SequenceUtils.getSequenceNextValue(SequenceUtils.SEQ_USER_ID);
		List params = new ArrayList();
		params.add(userId);
		params.add(name);
		params.add(pw);
		params.add(email);
		int i = JdbcUtils.insert(conn, "insert into stk_user(id,nickname,password,email) values (?,?,?,?)", params);
		if(i > 0){
			StkUser stkUser = new StkUser();
			stkUser.setId((int)userId);
			stkUser.setNickname(name);
			stkUser.setEmail(email);
			return new User(stkUser);
		}else{
			return null;
		}
	}
	
	public void updateEarningSearchParams(Connection conn, String json) {
		stkUser.setEarningSearchParams(json);
		List params = new ArrayList();
		params.add(json);
		params.add(stkUser.getId());
		JdbcUtils.update(conn, "update stk_user set earning_search_params=? where id=?", params);
	}

	public StkUser getStkUser() {
		return stkUser;
	}

	public void setStkUser(StkUser stkUser) {
		this.stkUser = stkUser;
	}
	
	public String getUserUploadImagePath(){
		return ServiceConstant.WEB_IMAGE_PATH + this.getStkUser().getId() + CommonConstant.MARK_SLASH;
	}
	
	private final static String PATH_IMAGES = "/images/";
	
	public String getUserUploadImageRelativePath(){
		return PATH_IMAGES + this.getStkUser().getId() + CommonConstant.MARK_SLASH;
	}
}
