package com.stk123.model;

import java.sql.Connection;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.stk123.bo.Stk;
import com.stk123.bo.StkFnType;
import com.stk123.model.strategy.Strategy16;
import com.stk123.task.InitialData;
import com.stk123.task.InitialKLine;
import com.stk123.task.NoticeRobot;
import com.stk123.task.StkUtils;
import com.stk123.tool.db.util.DBUtil;
import com.stk123.tool.util.HttpUtils;
import com.stk123.tool.util.JdbcUtils;
import com.stk123.tool.util.JsonUtils;
import com.stk123.tool.util.PDFUtils;
import com.stk123.web.action.SyncAction;

//import net.sf.json.JSONException;
//import net.sf.json.JSONObject;
//import net.sf.json.JsonConfig;
//import net.sf.json.util.PropertySetStrategy;


public class AStkTools {

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		//EmailUtils.SEND_MAIL = false;

		Connection conn = null;
		List<String> errors = new ArrayList<String>();
		try {
			conn = DBUtil.getConnection();
			IndexContext context = new IndexContext();
			InitialData.initialIndustryFromCsindex_zjh(conn, 7);
			//InitialData.updateIndustryFromHexun(conn);
			//InitialData.initialIndustryFrom10jqka(conn,"thshy");
			//InitialKLine.strategy(conn, false);
			//InitialKLine.checkIndustry(conn, "20160817");
			//InitialKLine.checkIndustry(conn, "20160712");
			//Industry.updateCapitalFlow(conn, "20170303", "gnzjl");



			List<String> result = new ArrayList<String>();
			String codes = "000001";
			String sql = null;
			if(codes != null && codes.length() > 0){
				sql = "select code,name from stk where market=1 and code in ("+codes+") order by code";
			}else{
				sql = "select code,name from stk where market=1 and cate in (1) and code>=000001 order by code";
			}
			List<Stk> stks = JdbcUtils.list(conn, sql, Stk.class);
			//List<Stk> stks = JdbcUtils.list(conn, "select code,name from stk where market=1 and code in ('000001','000002','000009','000012','000021','000024','000039','000046','000059','000060','000061','000063','000069','000100','000157','000338','000401','000402','000422','000423','000425','000527','000528','000538','000559','000562','000568','000581','000623','000625','000629','000630','000651','000680','000686','000703','000709','000718','000725','000728','000729','000758','000768','000776','000778','000780','000783','000792','000800','000807','000825','000839','000858','000869','000876','000878','000895','000898','000933','000937','000960','000961','000968','000969','000983','000999','002001','002007','002024','002038','002069','002073','002081','002092','002106','002122','002128','002142','002146','002155','002202','002241','002299','002304','002310','002344','002353','002378','002385','002399','002405','002415','002422','002431','002493','002500','002570','002594','002603','600000','600005','600009','600010','600011','600015','600016','600019','600022','600028','600029','600030','600031','600036','600037','600048','600050','600058','600062','600066','600068','600085','600089','600096','600100','600104','600108','600109','600111','600115','600118','600123','600125','600132','600143','600150','600153','600160','600166','600169','600170','600177','600183','600188','600196','600208','600216','600219','600221','600252','600256','600259','600266','600267','600271','600276','600307','600309','600315','600316','600320','600331','600348','600352','600362','600369','600372','600376','600383','600395','600406','600415','600418','600432','600456','600489','600497','600498','600500','600508','600516','600518','600519','600528','600535','600546','600547','600549','600550','600583','600585','600588','600595','600598','600600','600642','600649','600655','600660','600664','600674','600690','600694','600703','600718','600739','600741','600770','600779','600783','600795','600804','600809','600811','600812','600827','600832','600837','600839','600859','600863','600873','600875','600881','600887','600893','600895','600900','600970','600971','600997','600999','601001','601006','601009','601018','601088','601098','601099','601101','601106','601111','601117','601118','601158','601166','601168','601169','601179','601186','601216','601233','601258','601268','601288','601299','601318','601328','601333','601336','601369','601377','601390','601398','601555','601558','601566','601600','601601','601607','601618','601628','601633','601666','601668','601669','601688','601699','601717','601718','601766','601788','601808','601818','601857','601866','601888','601898','601899','601901','601918','601919','601928','601933','601939','601958','601988','601989','601991','601992','601998') order by code", Stk.class);
			List<StkFnType> fnTypes = JdbcUtils.list(conn, "select * from stk_fn_type where market=1 and type=209", StkFnType.class);
			List<Index> indexs = new ArrayList<Index>();
			int i = 0;

			//HttpUtils.setUseProxy(true,"xueqiu.com");
			String today = StkUtils.getToday();
			//today = "20170522";
			/*Index.KLineWhereClause = Index.KLINE_20140101;
			System.out.println("dddddddddddddddddd");
			JdbcUtils.delete(conn, "delete from stk_kline where kline_date>to_char(sysdate,'yyyymmdd')",null);
			System.out.println("dddddddddddddddddd");*/
			//InitialData.initialStk(conn, new Date());



			for(Stk stk : stks){
				try{
					Index index =  new Index(conn,stk.getCode(),stk.getName());
					context.indexs.add(index);
					System.out.println(stk.getCode());
					//InitialData.initOwnership(conn, index);
					//InitialData.initHolderFrom10jqka(conn, index);
					//InitialData.updateStkF9(conn, index);
                    //InitialData.updateStkStaticInfo(conn, stk.getCode());

					//K k = index.getK("20160530");
					/*if(k!=null ){
						List<K> listh = index.getKsHistoryHighPoint("20160530", 60, 6);
						if(listh.size() >= 3){
							K h1 = listh.get(listh.size()-1);
							K h2 = listh.get(listh.size()-2);
							K h3 = listh.get(listh.size()-3);
							if(h1.getHigh() < h2.getHigh() && h2.getHigh() < h3.getHigh()){
								List<K> listl = index.getKsHistoryLowPoint("20160530", 60, 6);
								if(listl.size() >= 2){
									K l1 = listl.get(listl.size()-1);
									K l2 = listl.get(listl.size()-2);
									if(k.getLow() < l1.getLow() && l1.getLow() < l2.getLow()){
										System.out.println(index.getCode()+","+index.getName()+",change="+index.changePercent);
									}
								}
							}
						}

					}*/
					/*K k = index.getK(0);
					if(k==null)continue;
					double ma20 = k.getMA(K.Close, 20);
					if(k.getClose()*1.02 >= ma20 && k.getClose() <= ma20*1.06){
						System.out.println(index.getCode());
					}*/

					//InitialData.initOwnership(conn, index);
					//index.initEarningsForecast();
					//index.updateNtile();


					/*K k = index.getK(today);
					double d = 0.0;
					int count = 0;
					int cnt = 0;
					if(k.getClose() <= k.getOpen()){
						double maxVolumn = k.getVolumn();
						do{
							k = k.before(1);
							if(k.isNoVolumeOrNoChange())break;
							if(k.getClose() <= k.getOpen()){
								maxVolumn = Math.max(maxVolumn, k.getVolumn());
							}else{
								if(count++ >= 20)break;
								if(maxVolumn == 0)continue;
								if(k.getVolumn() > maxVolumn){
									d += k.getVolumn()/maxVolumn;
									maxVolumn = 0;
								}else{
									//System.out.println(k.getDate());
									break;
								}
							}
							if(cnt++ >= 60)break;
						}while(true);
					}
					if(count == 0 || d==0)continue;
					index.changePercent = count * d;
					indexs.add(index);
					System.out.println(stk.getCode()+","+count+","+d);*/

					//K k = index.getK(0);


					//InitialData.initFnDataTTM(conn, StkUtils.now, index, fnTypes);

				}catch(Exception e){
					e.printStackTrace();
					errors.add(stk.getCode());
					System.out.println(stk.getCode());
					throw e;
				}
			}


			Collections.sort(indexs, new Comparator<Index>(){
				@Override
				public int compare(Index o1, Index o2) {
					int i = (int)((o1.changePercent - o2.changePercent)*10000);
					return i;
				}});

			/*result = IndexUtils.getUpGaps(indexs, "20140723", 250, 30);*/
			//indexs = IndexUtils.getCloseNewHighsAndInteract(context.indexs,"20150521",600);
			for(Index index : indexs){
				//System.out.println(index.getCode()+","+index.getName());
				System.out.println(index.getCode()+","+index.getName()+",change="+index.changePercent);
			}

			//Strategy.log = true;

			/*Strategy1 strategy1 = new Strategy1();
			strategy1.run(null, "20160722", context.indexs);*/

			/*Strategy2 strategy2 = new Strategy2();
			strategy2.run(null, "20160308", context.indexs);*/

			/*Strategy3 strategy3 = new Strategy3();
			strategy3.run(null, "20160722", context.indexs);*/

			/*Strategy4 strategy4 = new Strategy4();
			strategy4.run(null, "20160719", context.indexs);*/

			/*Strategy5 strategy5 = new Strategy5();
			strategy5.run(null, "20160722", context.indexs);*/

			/*Strategy6 strategy6 = new Strategy6();
			strategy6.run(null, "20160715", context.indexs);*/

			/*Strategy9 strategy = new Strategy9();
			strategy.run(null, "20160826", context.indexs);
			*/
			//InitialKLine.shortLineChooseStock("20160602", context.indexs);
			//InitialKLine.checkYiPinChaoDiDaShiYiQu(conn, today, context.indexs);

			/*Strategy16 strategy = new Strategy16();
			strategy.run(conn, "20170804", context.indexs);*/

			/*
			Map params = new HashMap();
			params.put("1",100);
			params.put("2","test");
			params.put("3",new Timestamp(new Date().getTime()));
			System.out.println(params);

			String json = JsonUtils.getJsonString4JavaPOJO(params);
			System.out.println("json="+json);
			//json = "{\"0\":100,\"1\":\"test\",\"time\":\"2017-07-30 14:47:47\"}";

			Map<String, Class> m = new HashMap<String, Class>();
			m.put("3", Date.class);

			JsonConfig jsonConfig = JsonUtils.configJson();
			jsonConfig.setRootClass(Map.class);
			jsonConfig.setClassMap(m);
			JSONObject jsonObject = JSONObject.fromObject( json );
			Map map = (Map)JSONObject.toBean(jsonObject, new HashMap(), jsonConfig);

			Map map = (Map)JsonUtils.getObject4Json(json, Map.class, m);

			System.out.println(map);
			System.out.println(map.get("1").getClass());
			System.out.println(map.get("2").getClass());
			System.out.println(map.get("3").getClass());
			System.out.println("------------------------------");

			List ps = new ArrayList();
			ps.add(100);
			ps.add("test");
			ps.add(new Timestamp(new Date().getTime()));
			System.out.println(ps);
			json = SyncAction.parseParamsFromObject(ps);
			System.out.println(json);

			List p = SyncAction.parseParamsFromJson(json);
			System.out.println(p);*/
			//InitialData.initialIndustryFrom10jqka(conn, "gn");




		} finally {
			if (conn != null) conn.close();
		}
		if(errors.size() > 0)
			System.out.println("errors:"+errors);

	}

	public static boolean condition(K k) throws Exception{
		final K kv = k.getKByHVV(200);
		int cnt = k.getKCountWithCondition(80,5, new K.Condition() {
			public boolean pass(K k) throws Exception {
				/*if(k.getDate().equals("20170224")){
					System.out.println(k.getLow() +","+k.getMA(K.Close, 30));
				}*/
				return k.getClose() >= k.getOpen() && k.getVolumn() >= kv.getVolumn()/2
						&& k.getVolumn()/k.getMA(K.Volumn, 10) >= 2;
			}
		});
		if(cnt >= 2){

			if(k.getVolumn() < k.getMA(K.Volumn, 10)
					&& (k.getLow() < k.getMA(K.Close, 30) || k.getLow() < k.getMA(K.Close, 20))){
				K ky = k.before(1);
				K ky30 = k.before(30);
				K ky31 = k.before(31);
				if(k.getMA(K.Close, 60) >= ky.getMA(K.Close, 60)
						|| ky30.getMA(K.Close, 60) >= ky31.getMA(K.Close, 60)){
					//System.out.println(index.getCode()+","+index.getName()+",change="+index.changePercent);
					K kh = k.getKByHHV(200);
					K kl = k.getKByLLV(200);
					if(kh.dateBefore(kl) || (kh.dateAfter(kl) && (kh.getClose() - kl.getClose())/kl.getClose() < 0.5)){
						return true;
					}
				}
			}
		}
		return false;
	}

	public static List<String> kws = new ArrayList<String>();
	static{
		kws.add("释放产能");
		kws.add("产能释放");
		kws.add("逐渐盈利");
		kws.add("高速成长");
		kws.add("高成长");
		kws.add("高增长");
		kws.add("市场前景可观");
	}

	public static void test(Index index) throws Exception {
		String page = HttpUtils.get("http://www.cninfo.com.cn//disclosure/fulltext/stocks/fulltext1y/cninfo/"+index.getCode()+".js?ver="+StkUtils.formatDate(new Date(), StkUtils.sf_ymd12), "gb2312");
		if("404".equals(page)){
			return ;
		}
		//System.out.println(page);
		String json = StringUtils.substringBetween(page, "=", "];");
		List<List> list = JsonUtils.getList4Json(json+"]", List.class);
		for(List item : list){
			String title = String.valueOf(item.get(2));
			if(title.contains("年度报告") || title.contains("季度报告全文")){
				String filePath = String.valueOf(item.get(1));
				String downloadFilePath = NoticeRobot.download(filePath,index);
				String sourceType = String.valueOf(item.get(3));
				String fileType = NoticeRobot.getFileType(downloadFilePath, sourceType);
				if("pdf".equalsIgnoreCase(fileType)){
					String spdf = PDFUtils.getText(downloadFilePath);
					int total = 0;
					for(String kw : kws){
						int n = StkUtils.getMatchAllStrings(spdf, kw).size();
						System.out.println(kw+"="+n);
						total += n;
					}
					System.out.println(title+",total="+total);
				}
				break;
			}
		}
	}


	//二品抄底
	public static double erpinchaodi(Index index, String today) throws Exception {
		//String today = "20150508";
		K k = index.getK(today);
		if(k != null && !index.isStop(today)){
			//趋势:3*SMA((CLOSE-LLV(LOW,27))/(HHV(HIGH,27)-LLV(LOW,27))*100,5,1)
			//      -2*SMA(SMA((CLOSE-LLV(LOW,27))/(HHV(HIGH,27)-LLV(LOW,27))*100,5,1),3,1),COLOR00FFFF;
		    //机构建仓: IF(趋势<5,40,0),COLORFFFFFF,LINETHICK3;

			double x = k.getEMA(3, 1, new K.Calculator(){
				public double calc(K k) throws Exception {
					double llv = k.getLLV(27);
					double hhv = k.getHHV(27);
					return (k.getClose() - llv) / (hhv - llv) * 100;
				}}
			);

			double y = k.getEMA(3, 1, new K.Calculator(){
				public double calc(K k) throws Exception {
					double y = k.getEMA(5, 1, new K.Calculator(){
						public double calc(K k) throws Exception {
							double llv = k.getLLV(27);
							double hhv = k.getHHV(27);
							return (k.getClose() - llv) / (hhv - llv) * 100;
						}}
					);
					return y;
				}}
			);
			double z = 3*x - 2*y;
			System.out.println(today+"="+z);
			return z;
		}
		return 10000;
	}


}
