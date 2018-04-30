package com.stk123.model;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.stk123.bo.StkImportInfoType;
import com.stk123.task.StkUtils;
import com.stk123.tool.db.connection.Pool;
import com.stk123.tool.util.JdbcUtils;

public class News {
	
	//select * from stk_import_info_type where type < 100 order by type;
	public final static int TYPE_1 = 1; //����|�б�|��ͬ
	public final static int TYPE_3 = 3; //ţɢ
	public final static int TYPE_4 = 4; //����|����|�ǹ�
	public final static int TYPE_5 = 5; //��Ȩ����
	public final static int TYPE_20 = 20; //��ӪԶ��������
	public final static int TYPE_21 = 21; //ļ���ʽ�ʹ�ô���80%
	
	private static List<StkImportInfoType> newsTypes = null;
	private static List<StkImportInfoType> sytemTypes = null;
	
	public static List<StkImportInfoType> getTypes() throws Exception{
		if(newsTypes != null)return newsTypes;
		Connection conn = null;
		try{
			conn = Pool.getPool().getConnection();
			return newsTypes = JdbcUtils.list(conn, "select * from stk_import_info_type where type >= 100 order by type", StkImportInfoType.class);
		}finally{
			Pool.getPool().free(conn);
		}
	}
	
	public static List<StkImportInfoType> getSystemTypes() throws Exception{
		if(sytemTypes != null)return sytemTypes;
		Connection conn = null;
		try{
			conn = Pool.getPool().getConnection();
			return sytemTypes = JdbcUtils.list(conn, "select * from stk_import_info_type where type < 100 order by type", StkImportInfoType.class);
		}finally{
			Pool.getPool().free(conn);
		}
	}
	
	public static StkImportInfoType getType(long type) throws Exception {
		List<StkImportInfoType> newsTypes = News.getTypes();
		for(StkImportInfoType news : newsTypes){
			if(news.getType() == type){
				return news;
			}
		}
		return null;
	}
	

}
