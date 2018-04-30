package com.stk123.tool.db.connection;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Vector;

import com.stk123.tool.db.util.DBUtil;


public class ConnectionPool {

	private Vector<Connection> pool;

	/**
	 * 连接池的大小，也就是连接池中有多少个数据库连接。
	 */
	private static int poolSize = 1;

	private static ConnectionPool instance = null;

	/**
	 * 私有的构造方法，禁止外部创建本类的对象，要想获得本类的对象，通过<code>getIstance</code>方法。 使用了设计模式中的单子模式。
	 */
	private ConnectionPool() {
		init();
	}

	/**
	 * 连接池初始化方法，读取属性文件的内容 建立连接池中的初始连接
	 */
	private void init() {
		pool = new Vector<Connection>(poolSize);
		addConnection();
	}

	/**
	 * 返回连接到连接池中
	 */
	public synchronized void release(Connection conn) {
		pool.add(conn);
	}

	/**
	 * 关闭连接池中的所有数据库连接
	 */
	public synchronized void closePool() {
		for (int i = 0; i < pool.size(); i++) {
			try {
				((Connection) pool.get(i)).close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			pool.remove(i);
		}
	}

	/**
	 * 返回当前连接池的一个对象
	 */
	public static synchronized ConnectionPool getInstance() {
		if (instance == null) {
			instance = new ConnectionPool();
		}
		return instance;
	}

	/**
	 * 返回连接池中的一个数据库连接
	 */
	public synchronized Connection getConnection() {
		if (pool.size() > 0) {
			Connection conn = pool.get(0);
			pool.remove(conn);
			return conn;
		} else {
			addConnection();
			return getConnection();
		}
	}

	/**
	 * 在连接池中创建初始设置的的数据库连接
	 */
	private synchronized void addConnection() {
		Connection conn = null;
		for (int i = 0; i < poolSize; i++) {
			try {
				conn = DBUtil.getConnection();
				pool.add(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}
	
	public Vector<Connection> getPool(){
		return this.pool;
	}
	
}
