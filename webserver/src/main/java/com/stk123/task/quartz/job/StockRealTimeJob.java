package com.stk123.task.quartz.job;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.stk123.model.Index;
import com.stk123.model.IndexUtils;
import com.stk123.model.K;
import com.stk123.model.mock.Score;
import com.stk123.model.mock.Trade;
import com.stk123.common.db.util.DBUtil;
import com.stk123.common.util.EmailUtils;

public class StockRealTimeJob implements Job {
	
	/**TODO
	 * [+3]MACD底部背离, [-1]60,30,20,10日均线空头排列]两个在一起是加1分？
	 * K线缠绕加分
	 * 资金流入加分
	 * 【策略源码分享】人气指数（AR）
人气指标是以当天开市价为基础，即以当天市价分别比较当天最高、最低价，通过一定时期内开市价在股价中的地位，反映市场买卖人气。其计算公式如下：
AR=N日内（当日最高价―当日开市价）之和 / N日内（当日开市价―当日最低价）之和
N为公式中的设定参数，一般设定为26日。
使用法则
（1）AR值以100为中心地带，其±20之间，即AR值在80－120之间波动时，属于盘整行情，股价走势比较平稳，不会出现剧烈波动。
（2）AR值走高时表示行情活跃，人气旺盛，过高则表示股价进入高价，应选择时机退出，AR值的高度没有具体标准，一般情况下，AR值上升至150以上时，股价随时可能回档下跌。
（3）AR值走低时表示人气衰退，需要充实，过低则暗示股价可能跌入低谷，可考虑伺机介入，一般AR值跌至70以下时，股价有可能随时反弹上升。
（4）从AR曲线可以看出一段时期的买卖气势，并具有先于股价到达峰或跌入谷底的功能，观图时主要凭借经验，以及与其他技术指标配合使用（也是我将要实现的）
回测伪略
1.设置股票池，基准以及其他初始性设置
2.创建AR函数，returnAR
3.在handle_data中进行操作，AR>150卖出，AR<80买入
	 */
	public static void main(String[] args) throws Exception {
		Connection conn = null;
		try{
			conn = DBUtil.getConnection();
			Index idx =  new Index(conn,"603939","");
			//System.out.println(idx.getCode());
			idx.getKsRealTimeOnHalfHour();
			K k = idx.getK();
			
			while(true){
				Trade trade = new Trade(idx, k);
				Score score = trade.getBuyScore();
				if(score.points > 0){
					System.out.println("date="+k.getDate()+"- "+score);
				}
				k = k.before(1);
				if(k.getDate().equals(idx.getKFirstOfDate().getDate())){
					break;
				}
			}
		} finally {
			if (conn != null)
				conn.close();
		}
		
	}
	
	//private static List<String> stks = null;
	
	private SimpleDateFormat DateFormat = new SimpleDateFormat("MM-dd HH:mm");  
	private Date d = new Date();  
	private String returnstr = DateFormat.format(d);
	
	public void execute(JobExecutionContext context) throws JobExecutionException {  
		Connection conn = null;
		try{
			conn = DBUtil.getConnection();
			List<String> stks = IndexUtils.getCareStkFromXueQiu("关注C");
			//System.out.println("["+returnstr+"]"+stks);
			StringBuffer sb = new StringBuffer();
			int cnt = 0;
			for(String code : stks){
				int loc = code.startsWith("SH")?Index.SH:code.startsWith("SZ")?Index.SZ:0;
				Index index =  new Index(conn,code.substring(2),loc);
				index.getKsRealTimeOnHalfHour();
				K k = index.getK();
				
				Trade trade = new Trade(index,k);
				Score score = trade.getBuyScore();
				if(score.points > 0){
					String s = "["+returnstr+"]<br>stk="+(index.getStock()!=null?index.getName():"")+"["+code+"]<br>time="+k.getDate()+"<br>"+score;
					System.out.println(s);
					sb.append(s).append("<br><br>");
					cnt++;
				}
			}
			if(sb.length() > 0){
				EmailUtils.send(EmailUtils.IMPORTANT+"[买入]K线监控["+cnt+"]", JobUtils.getMoneyFlow()+"<br><br>"+sb.toString());
			}
		} catch (Exception e){
			throw new JobExecutionException(e);
		} finally {
			if (conn != null)
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
    }
}


