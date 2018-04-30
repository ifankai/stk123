package com.stk123.tool.db.connection;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Vector;

import com.stk123.tool.db.util.DBUtil;


public class ConnectionPool {

	private Vector<Connection> pool;

	/**
	 * ���ӳصĴ�С��Ҳ�������ӳ����ж��ٸ����ݿ����ӡ�
	 */
	private static int poolSize = 1;

	private static ConnectionPool instance = null;

	/**
	 * ˽�еĹ��췽������ֹ�ⲿ��������Ķ���Ҫ���ñ���Ķ���ͨ��<code>getIstance</code>������ ʹ�������ģʽ�еĵ���ģʽ��
	 */
	private ConnectionPool() {
		init();
	}

	/**
	 * ���ӳس�ʼ����������ȡ�����ļ������� �������ӳ��еĳ�ʼ����
	 */
	private void init() {
		pool = new Vector<Connection>(poolSize);
		addConnection();
	}

	/**
	 * �������ӵ����ӳ���
	 */
	public synchronized void release(Connection conn) {
		pool.add(conn);
	}

	/**
	 * �ر����ӳ��е��������ݿ�����
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
	 * ���ص�ǰ���ӳص�һ������
	 */
	public static synchronized ConnectionPool getInstance() {
		if (instance == null) {
			instance = new ConnectionPool();
		}
		return instance;
	}

	/**
	 * �������ӳ��е�һ�����ݿ�����
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
	 * �����ӳ��д�����ʼ���õĵ����ݿ�����
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
