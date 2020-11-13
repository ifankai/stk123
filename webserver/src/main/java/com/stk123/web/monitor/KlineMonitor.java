package com.stk123.web.monitor;

import java.sql.Connection;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.stk123.common.util.EmailUtils;
import com.stk123.service.HttpUtils;
import com.stk123.common.util.JsonUtils;
import org.apache.commons.lang.StringUtils;

import com.stk123.model.bo.StkMonitor;
import com.stk123.model.Ene;
import com.stk123.model.Index;
import com.stk123.service.ServiceUtils;
import com.stk123.service.XueqiuUtils;
import com.stk123.service.baidu.BaiDuHi;
import com.stk123.common.db.TableTools;
import com.stk123.common.db.connection.Pool;
import com.stk123.common.db.util.DBUtil;
import com.stk123.common.util.*;
import com.stk123.service.DictService;


public class KlineMonitor extends Monitor {

	public String execute(StkMonitor sm,Connection conn,boolean updateResult) throws Exception {
		Index index = new Index(conn, sm.getCode());
		//System.out.println(index.getCode());
		double param1 = 0;
		if("1".equals(sm.getParam1())){
			param1 = index.getK().getClose();
		}else if("2".equals(sm.getParam1())){
			param1 = index.getK().getHigh();
		}else if("3".equals(sm.getParam1())){
			param1 = index.getK().getLow();
		}else if("4".equals(sm.getParam1())){
			param1 = index.getK().getOpen();
		}else if("-1".equals(sm.getParam1())){//收盘后 -1 的价格等于close的价格
			String page = HttpUtils.get("http://hq.sinajs.cn/list="+(index.getLoc()==1?"sh":"sz")+index.getCode(), null, "");
			//System.out.println(page);
			String str = StringUtils.substringBetween(page, "=\"", "\";");
			if(str != null){
				String[] ss = str.split(",");
				if(ss.length < 3)return null;
				param1 = Double.parseDouble(ss[3]);
                sm.setParam4(String.valueOf(param1));//param4 临时存贮current price
				if(param1 == 0)return null;
			}else{
				return null;
			}
		}else if("5".equals(sm.getParam1())){
			param1 = index.getK().getVolumn();
		}
		
		boolean trigger = false;
		double param3 = Double.parseDouble(sm.getParam3());
		if("1".equals(sm.getParam2())){
			if(param1 >= param3)trigger = true;
		}else if("2".equals(sm.getParam2())){
			if(param1 <= param3)trigger = true;
		}else if("3".equals(sm.getParam2())){
			if(param1 == param3)trigger = true;
		}else if("4".equals(sm.getParam2())){
			if(param1 > param3)trigger = true;
		}else if("5".equals(sm.getParam2())){
			if(param1 < param3)trigger = true;
		}else if("6".equals(sm.getParam2())){
			if(param1 != param3)trigger = true;
		}
		String sb = translate(sm,conn)+",日期"+ServiceUtils.getToday();
		//System.out.println(sb);
		if(trigger){
			if(updateResult){
				List params = new ArrayList();
				params.add(sb.toString());
				params.add(sm.getId());
				//JdbcUtils.update(conn, "update stk_monitor set trigger_date=sysdate(),result_1=? where id=?", params);
				JdbcUtils.update(conn, "update stk_monitor set trigger_date=sysdate,result_1=? where id=?", params);
			}
			return sb;
		}
		return null;
	}

	@Override
	public String translate(StkMonitor sm, Connection conn) throws Exception {
		if(sm.getTriggerDate() == null){
			Index index = new Index(conn, sm.getCode());
			StringBuffer sb = new StringBuffer();
			sb.append(index.getName()+"["+index.getCode()+"]");
			String param1 = DictService.getDict(DictService.MONITOR_K_PARAM1, sm.getParam1());
			String param2 = DictService.getDict(DictService.MONITOR_K_PARAM2, sm.getParam2());
			sb.append(param1+param2+sm.getParam3());
			return sb.toString();
		}else{
			return "<strong>"+sm.getResult1()+"</strong>";
		}
	}

    //getMethod.setRequestHeader("Cookie", "Hm_lpvt_1db88642e346389874251b5a1eded6e3=1401870866; Hm_lvt_1db88642e346389874251b5a1eded6e3=1399279853,1399456116,1399600423,1401761324; xq_a_token=bBfpd2WIHkEiOXxCZuvJKz; xq_r_token=HoJplnghTo9TdCtmaYhQ9C; bid=26948a7b701285b58366203fbc172ea6_hvykico0; xq_im_active=false; __utma=1.1861748176.1401870866.1401870866.1401870866.1; __utmb=1.2.9.1401870869035; __utmc=1; __utmz=1.1401870866.1.1.utmcsr=(direct)|utmccn=(direct)|utmcmd=(none)");
    public static void getCareAFromXueQiu(Set<String> careA) throws Exception {
    	String page = HttpUtils.get("http://xueqiu.com/stock/portfolio/stocks.json?size=1000&pid=7&tuid=6237744859&showAll=false", null, XueqiuUtils.getCookies(), "GBK");
        System.out.println(page);
        Map<String, Class> m = new HashMap<String, Class>();
        m.put("portfolios", Map.class);
        Map<String, List> map = (Map) JsonUtils.getObject4Json(page, Map.class, m);
        //System.out.println(map.get("portfolios"));
        for(Object obj : map.get("portfolios")){
            Map care = (Map)obj;
            if("关注C".equals(care.get("name"))){
                for(String s : StringUtils.split((String)care.get("stocks"),",")){
                	careA.add(s.substring(2));
                };
            }
        }
    }

    private static Set<String> careA = Collections.synchronizedSet(new HashSet<String>());
	
	//实时监控数据 k 
	public static void main(String[] args) throws Exception {
		ConfigUtils.setPropsFromResource(TableTools.class, "db.properties");
		Connection conn = null;
        try{
            getCareAFromXueQiu(careA);
        }catch(Exception e){
        	e.printStackTrace();
            EmailUtils.send("实时K线价格监控出错", e);
        }
        System.out.println("care A:"+careA);
		try {
			conn = DBUtil.getConnection();
			int noOfError = 0;
			ExecutorService exec = Executors.newFixedThreadPool(4);
			while(true){
				try{
					List<StkMonitor> sms = JdbcUtils.list(conn, "select * from stk_monitor where type=1 and trigger_date is null order by insert_date asc", StkMonitor.class);
					for(final StkMonitor sm : sms){
						final Index index = new Index(conn, sm.getCode());
						//System.out.println(index.getCode());
						if(index.getMarket() != 1)continue;
						if("5".equals(sm.getParam1()))continue;//volume
						
						final Runnable run = new Runnable() {
							public void run() {
								Connection conn = null;
								try {
									conn = Pool.getPool().getConnection();
									String result = null;
									if("-1".equals(sm.getParam1())){
										result = Monitor.getInstance(sm).execute(sm,conn,true);
									}else{
										sm.setParam1("-1");
										result = Monitor.getInstance(sm).execute(sm,conn,false);
									}
                                    //check实时价格是否超过了ene upper
									boolean eneTrigger = false;
                                    boolean sell = false;
									if(careA.contains(index.getCode())){
                                        Ene ene = index.getK().getEne(index);
                                        if(Double.parseDouble(sm.getParam4()) >= ene.getUpper()){
                                            result = (result==null?"":result+"<br/>") + index.getName()+"["+index.getCode()+"]"+"实时价格:"+sm.getParam4()+" >= ene upper ";
                                            sell = true;
                                            eneTrigger = true;
                                            careA.remove(index.getCode());
                                            //EmailUtils.send("kai.fan@suncorp.com.au", result, result);
                                        }
                                        else if(0 < Double.parseDouble(sm.getParam4()) && Double.parseDouble(sm.getParam4()) <= ene.getLower()){
                                            result = (result==null?"":result+"<br/>") + index.getName()+"["+index.getCode()+"]"+"实时价格:"+sm.getParam4()+" <= ene lower ";
                                            sell = false;
                                            eneTrigger = true;
                                            careA.remove(index.getCode());
                                            //EmailUtils.send("kai.fan@suncorp.com.au", result, result);
                                        }
                                    }
									if(result != null){
										Ene ene = index.getK().getEne(index);
										result = result+"; "+ene;
										if(result != null){
											EmailUtils.send("实时K线价格监控"+(eneTrigger?(sell?"【ENE卖出】":"【ENE买入】"):""), result);
										}
										try{
											BaiDuHi.sendSMS(result);
										}catch(Exception e){
											//ExceptionUtils.insertLog(conn, null, e);
										}
									}
								} catch (Exception e) {
									e.printStackTrace();
								}finally{
									Pool.getPool().free(conn);
								}
							}
						};
						exec.execute(run);
						
					}
					
				}catch(Exception e){
					noOfError ++;
					EmailUtils.send("实时K线价格监控出错", e);
					e.printStackTrace();
					//if(noOfError > 5)break;
				}
				Thread.sleep(1000*60*1);
				Date now = new Date();
				System.out.println("[KlineMonitor]"+now);
				if(now.getHours() >= 15){
					break;
				}
			}
			exec.shutdown();
			System.exit(0);
		} finally {
			if (conn != null) conn.close();
		}
	}

}
