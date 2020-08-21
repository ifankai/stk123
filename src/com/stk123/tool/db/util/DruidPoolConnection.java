package com.stk123.tool.db.util;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.alibaba.druid.pool.DruidPooledConnection;
import com.stk123.tool.util.ConfigUtils;
import oracle.jdbc.OracleConnection;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class DruidPoolConnection {

    private static Logger log = Logger.getLogger(DruidPoolConnection.class);
    private static DruidPoolConnection dbPoolConnection = null;
    private static DruidDataSource druidDataSource = null;

    static {
        Properties properties = ConfigUtils.getProps();
        try {
            druidDataSource = (DruidDataSource)DruidDataSourceFactory.createDataSource(properties); //DruidDataSrouce工厂模式
        } catch (Exception e) {
            e.getStackTrace();
            log.error("获取配置失败");
        }
    }

    /**
     * 数据库连接池单例
     * @return
     */
    public static synchronized DruidPoolConnection getInstance(){
        if (null == dbPoolConnection){
            dbPoolConnection = new DruidPoolConnection();
        }
        return dbPoolConnection;
    }

    /**
     * 返回druid数据库连接
     * @return
     * @throws SQLException
     */
    public Connection getConnection() throws SQLException {
        return druidDataSource.getConnection();
    }

}
