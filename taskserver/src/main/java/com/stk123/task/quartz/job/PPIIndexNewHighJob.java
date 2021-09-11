package com.stk123.task.quartz.job;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import com.stk123.common.db.util.CloseUtil;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.stk123.model.bo.StkDataPpi;
import com.stk123.model.bo.StkDataPpiType;
import com.stk123.util.ServiceUtils;
import com.stk123.common.db.util.DBUtil;
import com.stk123.common.util.EmailUtils;
import com.stk123.util.ExceptionUtils;
import com.stk123.common.util.JdbcUtils;

public class PPIIndexNewHighJob implements Job {

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		System.out.println("PPIIndexNewHighJob");
		try {
			run();
		} catch (Exception e) {
			EmailUtils.send("PPIIndexNewHighJob Error", ExceptionUtils.getExceptionAsString(e));
		}
	}
	
	public static void run() {
		Connection conn = null;
		try {
            List params = new ArrayList();
            conn = DBUtil.getConnection();
            List<List<String>> datas = new ArrayList<List<String>>();

            List<StkDataPpiType> types = JdbcUtils.list(conn, "select * from stk_data_ppi_type order by id", StkDataPpiType.class);
            for (StkDataPpiType type : types) {
                params.clear();
                params.add(type.getId());
                StkDataPpi latest = JdbcUtils.load(conn, "select * from stk_data_ppi where type_id=? and ppi_date between to_char(sysdate-60,'yyyymmdd') and to_char(sysdate,'yyyymmdd') order by ppi_date desc", params, StkDataPpi.class);


                if (latest != null) {
                    //System.out.println(type.getName()+","+latest.getValue());
                    Double max = JdbcUtils.load(conn, "select max(value) from stk_data_ppi where type_id=? and ppi_date between to_char(sysdate-350,'yyyymmdd') and to_char(sysdate,'yyyymmdd')", params, Double.class);
                    if (latest.getValue() >= max) {
                        List<String> data = new ArrayList<String>();
                        data.add("<a href='http://localhost:8089/q/" + type.getName() + "' target='_black'>" + type.getName() + "</a>");
                        data.add("<a href='http://www.iwencai.com/stockpick/search?typed=1&preParams=&ts=1&f=1&qs=result_rewrite&selfsectsn=&querytype=&searchfilter=&tid=stockpick&w=" + type.getName() + "&queryarea=' target='_black'>查问财</a>");

                        StkDataPpi latest2 = JdbcUtils.load(conn, 2, "select * from stk_data_ppi where type_id=? and ppi_date between to_char(sysdate-60,'yyyymmdd') and to_char(sysdate,'yyyymmdd') order by ppi_date desc", params, StkDataPpi.class);
                        params.add(latest2.getPpiDate());
                        max = JdbcUtils.load(conn, "select max(value) from stk_data_ppi where type_id=? and ppi_date between to_char(sysdate-350,'yyyymmdd') and ?", params, Double.class);
                        if (latest2.getValue() < max) {
                            data.add("新加入");
                        } else {
                            data.add("");
                        }
                        datas.add(data);
                        System.out.println("new high:" + type.getName());
                    }
                }
            }
            if (datas.size() > 0) {
                List<String> titles = new ArrayList<String>();
                titles.add("大宗商品");
                titles.add("");
                titles.add("");
                EmailUtils.send("大宗商品 new high, 个数：" + datas.size(), ServiceUtils.createHtmlTable(titles, datas));
            }
        }catch (Exception e){
            e.printStackTrace();
		}finally{
            CloseUtil.close(conn);
		}
	}

	public static void main(String[] args) throws Exception {
		run();
	}

}
