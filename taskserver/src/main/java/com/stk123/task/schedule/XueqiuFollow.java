package com.stk123.task.schedule;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.stk123.common.db.util.CloseUtil;
import com.stk123.util.ServiceUtils;
import com.stk123.service.XueqiuService;
import lombok.extern.apachecommons.CommonsLog;
import org.apache.commons.lang.StringUtils;

import com.stk123.model.bo.Stk;
import com.stk123.model.Index;
import com.stk123.common.db.TableTools;
import com.stk123.common.db.util.DBUtil;
import com.stk123.common.util.ConfigUtils;
import com.stk123.util.HttpUtils;
import com.stk123.common.util.JdbcUtils;
import org.springframework.stereotype.Component;

@Component
@CommonsLog
public class XueqiuFollow {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
        XueqiuFollow xueqiuFollow = new XueqiuFollow();
        xueqiuFollow.run();
	}

	public void run() {
        Connection conn = null;
        try {
            ConfigUtils.setPropsFromResource(TableTools.class, "db.properties");
            conn = DBUtil.getConnection();
            List<Stk> stks = JdbcUtils.list(conn, "select code,name from stk order by code", Stk.class);
            Map<String, String> cookies = XueqiuService.getCookies();
            for (Stk stk : stks) {
                try {
                    Index index = new Index(conn, stk.getCode(), stk.getName());
                    log.info("xueqiu followers:" + index.getCode());
                    //update雪球关注人数
                    updateStkFollows(conn, index, cookies);
                } catch (Exception e) {
                    e.printStackTrace();
                    //throw e;
                }
                Thread.currentThread().sleep(15000);
            }
        }catch (Exception e){
            log.error("error", e);
        } finally {
            CloseUtil.close(conn);
        }
    }
	
	public static void updateStkFollows(Connection conn, Index index, Map<String, String> requestHeaders) throws Exception {
		String code = index.getCode();
		if(index.getMarket() == 1){
			code = index.getLocationAsString()+code;
		}
		String page = HttpUtils.get("http://xueqiu.com/recommend/pofriends.json?type=1&code="+code+"&start=0&count=14&_="+new Date().getTime(),null, requestHeaders, "GBK");
		String totalNumber = StringUtils.substringBetween(page, "\"totalcount\":", ",");
		if(totalNumber != null && ServiceUtils.isAllNumeric(totalNumber)){
			int tn = Integer.parseInt(totalNumber);
			List params = new ArrayList();
			params.add(tn);
			params.add(index.getCode());
			JdbcUtils.update(conn, "update stk set hot=? where code=?", params);
		}
	}

}
