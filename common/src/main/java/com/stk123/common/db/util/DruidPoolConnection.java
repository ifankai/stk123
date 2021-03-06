package com.stk123.common.db.util;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.stk123.common.util.ConfigUtils;
import lombok.extern.apachecommons.CommonsLog;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

@CommonsLog
public class DruidPoolConnection {

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
