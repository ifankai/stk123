package com.stk123.common.db.util.sequence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.stk123.common.db.connection.Pool;
import com.stk123.common.util.JdbcUtils;

public class SeqInfo {
	private long maxKey; // 当前Sequence载体的最大值
	private long minKey; // 当前Sequence载体的最小值
	private long nextKey; // 下一个Sequence值
	private int poolSize; // Sequence值缓存大小
	private String keyName; // Sequence的名称
	private static final String sql_update = "UPDATE stk_sequence SET seq_value = seq_value + ? WHERE seq_name = ?";
	private static final String sql_query = "SELECT seq_value FROM stk_sequence WHERE seq_name = ?";

	public SeqInfo(String keyName, int poolSize) throws SQLException {
		this.poolSize = poolSize;
		this.keyName = keyName;
		retrieveFromDB();
	}

	public String getKeyName() {
		return keyName;
	}

	public long getMaxKey() {
		return maxKey;
	}

	public long getMinKey() {
		return minKey;
	}

	public int getPoolSize() {
		return poolSize;
	}

	/**
	 * 获取下一个Sequence值
	 * 
	 * @return 下一个Sequence值
	 * @throws SQLException
	 */
	public synchronized long getNextKey() throws SQLException {
		if (nextKey > maxKey) {
			retrieveFromDB();
		}
		return nextKey++;
	}

	/**
	 * 执行Sequence表信息初始化和更新工作
	 * 
	 * @throws SQLException
	 */
	private void retrieveFromDB() throws SQLException {
		Connection conn = null;
		try{
			conn = Pool.getPool().getConnection();
			// 查询数据库
			PreparedStatement pstmt_query = conn.prepareStatement(sql_query);
			pstmt_query.setString(1, keyName);
			ResultSet rs = pstmt_query.executeQuery();
			if (rs.next()) {
				maxKey = rs.getLong(1) + poolSize;
				minKey = maxKey - poolSize + 1;
				nextKey = minKey;
				rs.close();
				pstmt_query.close();
			} else {
				throw new SQLException("stk_sequence表里没有这个sequence："+keyName);
			}
			// 更新数据库
			/*conn.setAutoCommit(false);
			System.out.println("更新Sequence最大值！");
			PreparedStatement pstmt_up = conn.prepareStatement(sql_update);
			pstmt_up.setLong(1, poolSize);
			pstmt_up.setString(2, keyName);
			pstmt_up.executeUpdate();
			pstmt_up.close();
			conn.commit();*/
			
			List params = new ArrayList();
			params.add(poolSize);
			params.add(keyName);
			JdbcUtils.update(conn, sql_update, params);
		}finally{
			Pool.getPool().free(conn);
		}
	}
}
