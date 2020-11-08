package com.stk123.tool.thread;

import java.util.List;

import com.stk123.bo.Stk;
import com.stk123.model.Index;
import com.stk123.task.StkUtils;
import com.stk123.tool.db.TableTools;
import com.stk123.tool.db.connection.ConnectionPool;
import com.stk123.tool.util.ConfigUtils;
import com.stk123.tool.util.JdbcUtils;


public class StkTest {
	public static void main(String[] args) throws Exception {
		ConfigUtils.setPropsFromResource(TableTools.class, "db.properties");
		ConnectionPool pool = null;
		try {
			long start = System.currentTimeMillis();
			List<Stk> stks = JdbcUtils.list(pool.getConnection(), "select code,name from stk order by code", Stk.class);
			
			pool = ConnectionPool.getInstance();
			WorkerMgmt workerMgmt = new WorkerMgmt(5); 
			workerMgmt.startWorkers();
			for(Stk stk : stks){
				Index index = new Index(stk.getCode());
				Request request = new IndexRequest(pool, index);
				request.addExecuteMethod("getPEFromXueQiu", String.class, StkUtils.getToday());
				workerMgmt.putRequest(request);
			}
			workerMgmt.stopAllWorkers();
			long end = System.currentTimeMillis();
			System.out.println("time:"+((end-start)/1000D));
			
		} finally {
			if (pool != null) pool.closePool();
		}
	}
}
