package com.stk123.model;

import com.stk123.common.CommonConstant;
import com.stk123.common.util.CacheUtils;
import com.stk123.common.util.JdbcUtils;
import com.stk123.common.util.JsonUtils;
import com.stk123.common.util.ListUtils;
import com.stk123.model.bo.Stk;
import com.stk123.service.HttpUtils;
import com.stk123.service.ServiceUtils;
import com.stk123.service.XueqiuService;
import org.apache.commons.lang.StringUtils;

import java.sql.Connection;
import java.util.*;



@SuppressWarnings({ "unchecked", "rawtypes" })
public class IndexUtils implements CommonConstant {

	public static String STKS_NAME_PATTERN = null;

	public static void main(String[] args) throws Exception {
		System.out.println(containAnyStksNameWrapByString("#沙钢股份重大资产重组说明会# 投资者们最关心的莫过于重组的进展，能否顺利成功，还有公司何时能复牌等。分享几个投资者的互动，看看有没有你关心的问题 ☛☛❶想具体了解下现在的重组标的情况，是否能够在短期内成功？❷现在目前并购进展是什么情况？❸审计的海外资产是什么啊，还要停牌多长时","<font color='red'>","</font>"));
	}

	/**
	 * 计算yyyyMMdd这天，创days天新高的index
	 */
	public static List<Index> getNewHighs(List<Index> indexs,String yyyyMMdd,int days) throws Exception{
		List<Index> newHighs = new ArrayList<Index>();
		String today_1 = ServiceUtils.formatDate(ServiceUtils.addDay(yyyyMMdd, -1),ServiceUtils.sf_ymd2);
		String today_20 = ServiceUtils.formatDate(ServiceUtils.addDay(yyyyMMdd, -20),ServiceUtils.sf_ymd2);
		String today_n = ServiceUtils.formatDate(ServiceUtils.addDay(yyyyMMdd, -days),ServiceUtils.sf_ymd2);
		for(Index index : indexs){
			if(index.getKs().size() >= days){
				if(index.getKValueByHCV(today_n, today_20) > index.getK(today_1).getClose()
					&& index.getKValueByHCV(today_n, yyyyMMdd) == index.getK(yyyyMMdd).getClose()){
					newHighs.add(index);
				}
			}
			index.gc();
		}
		return newHighs;
	}

	//创days天新低
    public static List<Index> getNewLows(List<Index> indexs,String yyyyMMdd,int days) throws Exception{
        List<Index> newHighs = new ArrayList<Index>();
        String today_1 = ServiceUtils.formatDate(ServiceUtils.addDay(yyyyMMdd, -1),ServiceUtils.sf_ymd2);
        String today_20 = ServiceUtils.formatDate(ServiceUtils.addDay(yyyyMMdd, -20),ServiceUtils.sf_ymd2);
        String today_n = ServiceUtils.formatDate(ServiceUtils.addDay(yyyyMMdd, -days),ServiceUtils.sf_ymd2);
        for(Index index : indexs){
            if(index.getKs().size() >= days){
                if(index.getKValueByLCV(today_n, today_20) < index.getK(today_1).getClose()
                        && index.getKValueByLCV(today_n, yyyyMMdd) == index.getK(yyyyMMdd).getClose()){
                    newHighs.add(index);
                }
            }
            index.gc();
        }
        return newHighs;
    }

	public static List<Index> getNearNewHighs(List<Index> indexs, String yyyyMMdd, int days) throws Exception{
		List<Index> newHighs = new ArrayList<Index>();
		String today_1 = ServiceUtils.formatDate(ServiceUtils.addDay(yyyyMMdd, -1),ServiceUtils.sf_ymd2);
		String today_20 = ServiceUtils.formatDate(ServiceUtils.addDay(yyyyMMdd, -20),ServiceUtils.sf_ymd2);
		String today_n = ServiceUtils.formatDate(ServiceUtils.addDay(yyyyMMdd, -days),ServiceUtils.sf_ymd2);
		for(Index index : indexs){
			if(!index.isStop(yyyyMMdd) && !index.getK().isUpLimit()){
				if(index.getKs().size() >= days){
					double d = index.getKValueByHCV(today_n, yyyyMMdd);
					if(index.getKValueByHCV(today_n, today_20) > index.getK(today_1).getClose()
						&& d > index.getK(yyyyMMdd).getClose()
						&& d <= index.getK(yyyyMMdd).getClose() * 1.05){
						newHighs.add(index);
					}
				}
			}
		}
		return newHighs;
	}

	public static List<Index> getCloseNewHighsAndInteract(List<Index> indexs,String yyyyMMdd,int days) throws Exception{
		List<Index> newHighs = new ArrayList<Index>();
		String today_1 = ServiceUtils.formatDate(ServiceUtils.addDay(yyyyMMdd, -1),ServiceUtils.sf_ymd2);
		String today_20 = ServiceUtils.formatDate(ServiceUtils.addDay(yyyyMMdd, -20),ServiceUtils.sf_ymd2);
		String today_n = ServiceUtils.formatDate(ServiceUtils.addDay(yyyyMMdd, -days),ServiceUtils.sf_ymd2);
		for(Index index : indexs){
			if(!index.isStop(yyyyMMdd) && !index.getK().isUpLimit()){
				if(index.getKs().size() >= days){
					double d = index.getKValueByHCV(today_n, yyyyMMdd);
					if(index.getKValueByHCV(today_n, today_20) > index.getK(today_1).getClose()
						&& d > index.getK(yyyyMMdd).getClose()
						&& d <= index.getK(yyyyMMdd).getClose() * 1.1){
						if(index.isKIntersect5(yyyyMMdd)){
							newHighs.add(index);
						}
						//System.out.println("new higt stk="+index.getCode());
					}
				}
			}
		}
		return newHighs;
	}

	/**创days日新高不超过百分之percent% */
	public static List<Index> getNewHighs(List<Index> indexs,String yyyyMMdd,int days,double percent) throws Exception{
		List<Index> newHighs = new ArrayList<Index>();
		for(Index index : indexs){
			if(index.getKs().size() >= days){
				K k = index.getK(yyyyMMdd);
				//String today_1 = today.before(1).getDate();
				K k_n = k.before(days/10*2);
				String k_nn = k.before(days).getDate();
				if(index.getKValueByHCV(k_nn, yyyyMMdd) == k.getClose()
						&& k.getClose()/(1+percent) < index.getKValueByHCV(k_nn, k_n.getDate())
						&& index.getKValueByHCV(k_nn, k_n.getDate()) > k_n.getClose()){
					newHighs.add(index);
				}
			}
		}
		return newHighs;
	}

	/**
	 * yyyyMMdd日的close价格 与 前期高点的close比较，
	 * 规则为: 前期高点的close*small <= yyyyMMdd日的close <= 前期高点的close*large
	 */
	public static List<Index> getIndexsByPercentRangeOnEarlyHighPoint(List<Index> indexs,String yyyyMMdd,int days,double small, double large) throws Exception{
		List<Index> result = new ArrayList<Index>();
		int num = days/10; //TODO 取num日的低点,低点日前的新高才算
		for(Index index : indexs){
			boolean flag = false;
			if(index.getKs().size() >= days){
				K k = index.getK(yyyyMMdd);
				K startK = index.getK(yyyyMMdd, days);
				List<K> lowKs = index.getKsHistoryLowPoint(yyyyMMdd, days, num);
				if(lowKs.size() == 0)continue;
				K lowK = lowKs.get(0);
				K highK = index.getKByHCV(startK.getDate(), lowK.getDate());
				if(highK != null && highK.getClose()*small <= k.getClose() && highK.getClose()*large >= k.getClose()){
					result.add(index);
					flag = true;
				}
			}
			if(!flag)
				index.gc();
		}
		return result;
	}

	/**
	 * 取图形收敛的index
	 */
	public static List<Index> getIndexsByConvergence(List<Index> indexs,String yyyyMMdd,int days) throws Exception{
		List<Index> result = new ArrayList<Index>();
		int num = days/5; //分为5段去分析幅度是否收敛
		for(Index index : indexs){
			if(index.getKs().size() >= days){

			}
		}
		return result;
	}

	/**
	 * 得到yyyyMMdd日K线回补daysInterval日前的向上跳空缺口
	 * @param indexs
	 * @param yyyyMMdd
	 * @param days 在一个总的时间范围内
	 * @param daysInterval yyyyMMdd日K线与缺口至少相差的天数，排除昨日缺口，今日回补的情况
	 * @return
	 * @throws Exception
	 */
	public static List<Index> getUpGaps(List<Index> indexs,String yyyyMMdd,int days,int daysInterval) throws Exception{
		List<Index> result = new ArrayList<Index>();
		for(Index index : indexs){
			if(index.getKs().size() >= days){
				Gap up = null;
				List<Gap> gaps = index.getGaps(yyyyMMdd, days);
				for(int i=gaps.size()-1;i>=0;i--){
					Gap gap = gaps.get(i);
					if(gap.isUp()){
						up = gap;
						break;
					}
				}
				if(up != null){
					K k = index.getK(yyyyMMdd);
					if(k.getLow() < up.getHigh() && !up.getEndK().equals(yyyyMMdd)
							&& ServiceUtils.getDaysBetween(ServiceUtils.sf_ymd2.parse(up.getEndK().getDate()), ServiceUtils.sf_ymd2.parse(yyyyMMdd)) >= daysInterval ){
						result.add(index);
					}
				}
			}
		}
		return result;
	}

	public static List<List> search(List<Index> indexs, String date, int n, int m, boolean onlyGrowing) throws Exception {
		List<List> datas = new ArrayList<List>();
		int tmp = n;
		for(Index index : indexs){
			if(onlyGrowing){
				if(index.valuationByGrowing(index.FN_JLRZZL) == 0
					/*	&& !index.valuationByGrowing(Index.FN_ZYSRZZL)*/){
					continue;
				}
			}
			K k = index.getK(date);
			if(k == null) continue;
			double hhv = index.getKValueByHHV(index.getK(date, m).getDate(),date);
			if(k.getHigh() != hhv){
				double hcv = index.getKValueByHCV(index.getK(date, m).getDate(),date);
				if(k.getClose() != hcv)continue;
			}
			double d1 = 0.0;
			double d2 = 0.0;
			boolean flag = false;
			while(true){
				if(n-- <= 0)break;
				K k1 = index.getK(date, n+1);
				K k2 = index.getK(date, n);
				if(k1.getOpen() == k1.getClose() && (k2.getClose() - k1.getClose())/k1.getClose() > 0.08) {
					flag = true;
					break;
				}
				d1 += (k2.getClose()-k1.getClose())/k1.getClose();
				//System.out.println(n+","+k2.getDate()+","+k2.getClose()+","+k1.getClose()+",d1="+d1);
				d2 += (k1.getVolumn()-k2.getVolumn())/k2.getVolumn();
				//System.out.println(n+","+k2.getDate()+","+k2.getVolumn()+","+k1.getVolumn()+",d2="+d2);
			}
			if(flag) continue;
			n = tmp;
			d1 = d1 * 5;
			if(d1 + d2 <= 0 || d2 <= 0)continue;
			List data = new ArrayList();
			data.add(index);
			data.add(ServiceUtils.number2String(index.getCloseChange(date, m)*100,2));
			data.add(StringUtils.join(index.getIndustryName(/*Index.INDUSTRY_WIND*/), CommonConstant.MARK_SLASH));
			datas.add(data);
		}
		Collections.sort(datas, new Comparator(){
			public int compare(Object arg0, Object arg1) {
				double d0 = Double.parseDouble(String.valueOf(((List)arg0).get(1)));
				double d1 = Double.parseDouble(String.valueOf(((List)arg1).get(1)));
				return (int)((d1-d0)*100);
			}
		});
		for(List data:datas){
			//System.out.println(StringUtils.replaceEach(data.toString(), new String[]{"[","]"," "}, new String[]{"","",""}));
			data.set(1, String.valueOf(data.get(1)) + CommonConstant.MARK_PERCENTAGE);
		}
		return datas;
	}

	/**
	 * 缩量上涨
	 * 创m天新高后，n天量价背离，缩量上涨或持平，回调可买入，前提是大盘多头！
	 */
	public static void search(Connection conn, String date, int n, int m, boolean onlyGrowing) throws Exception {
		if(date == null || "".equals(date)){
			date = ServiceUtils.getToday();
		}
		List<Stk> stks = JdbcUtils.list(conn, "select code,name from stk_cn order by code", Stk.class);
		List<Index> indexs = new ArrayList<Index>();
		for(Stk stk : stks){
			Index index = new Index(conn,stk.getCode());
			indexs.add(index);
		}
		IndexUtils.search(indexs, date, n, m, onlyGrowing);
	}

	public static void sortByCloseChangeAndNetProfit(List<Index> indexs,String today) throws Exception {
		//IndexUtils.reportRSByCloseChange(indexs,StkUtils.getToday(),250);
		IndexUtils.sortByNetProfit(indexs, ServiceUtils.getPrevQuarter(today));
		Collections.sort(indexs, new Comparator<Index>(){
			public int compare(Index arg0, Index arg1) {
				int rs1 = arg1.getCanslim().getCloseChangeRank()*3+arg1.getCanslim().getNetProfitGrowthRank();
				int rs0 = arg0.getCanslim().getCloseChangeRank()*3+arg0.getCanslim().getNetProfitGrowthRank();
				return rs0 - rs1;
			}
		});
	}



	/**
	 * 季度jlr相对强度排行
	 * @param yyyyMMdd 季度日期
	 */
	public static void sortByNetProfit(List<Index> indexs, String yyyyMMdd) throws Exception {
		for(Index index : indexs){
			index.getCanslim().setNetProfitGrowth(index.getNetProfitGrowthAsNumber(yyyyMMdd));
		}
		Collections.sort(indexs, new Comparator<Index>(){
			public int compare(Index arg0, Index arg1) {
				return ((int)(arg1.getCanslim().getNetProfitGrowth() - arg0.getCanslim().getNetProfitGrowth()) * 100);
			}
		});
		int i = 1;
		for(Index index : indexs){
			index.getCanslim().setNetProfitGrowthRank(i++);
		}
	}

	//gc
	public static void getTopNByCloseChange(List<Index> indexs,String yyyyMMdd, int days, int n) throws Exception {
		for(Index index : indexs){
			index.getCanslim().setCloseChange(index.getCloseChange(yyyyMMdd, days));
			index.gc();
		}
		ListUtils.getTopN(indexs, n, new ListUtils.Get(){
			public double get(Object o) {
				Index obj = (Index)o;
				return obj.getCanslim().getCloseChange();
		}});
	}

	/**
	 * 股价相对强度排行
	 */
	public static void sortByCloseChange(List<Index> indexs, String yyyyMMdd, int days) throws Exception {
		for(Index index : indexs){
			index.getCanslim().setCloseChange(index.getCloseChange(yyyyMMdd, days));
		}
		Collections.sort(indexs, new Comparator<Index>(){
			public int compare(Index arg0, Index arg1) {
				if(arg1.getCanslim().getCloseChange() > arg0.getCanslim().getCloseChange()){
					return 1;
				}else if(arg1.getCanslim().getCloseChange() == arg0.getCanslim().getCloseChange()){
					return 0;
				}
				return -1;
			}
		});
		int i = 1;
		int cnt = indexs.size()/100;
		for(Index index : indexs){
			index.getCanslim().setCloseChangeRank(i++);
			index.getCanslim().setCloseChangeRS(100 - index.getCanslim().getCloseChangeRank()/cnt);
		}
	}

	public static void sortByCloseChange(List<Index> indexs, String startDate, String endDate) throws Exception {
		for(Index index : indexs){
			index.getCanslim().setCloseChange(index.getCloseChange(startDate, endDate));
		}
		Collections.sort(indexs, new Comparator<Index>(){
			public int compare(Index arg0, Index arg1) {
				if(arg1.getCanslim().getCloseChange() > arg0.getCanslim().getCloseChange()){
					return 1;
				}else if(arg1.getCanslim().getCloseChange() == arg0.getCanslim().getCloseChange()){
					return 0;
				}
				return -1;
			}
		});
		int i = 1;
		int cnt = indexs.size()/100;
		for(Index index : indexs){
			index.getCanslim().setCloseChangeRank(i++);
			index.getCanslim().setCloseChangeRS(100 - index.getCanslim().getCloseChangeRank()/cnt);
		}
	}


	public static void sortByClose(List<Index> indexs, final String date){
		Collections.sort(indexs, new Comparator<Index>(){
			public int compare(Index arg0, Index arg1) {
				try {
					K k0 = arg0.getK(date);
					K k1 = arg1.getK(date);
					if(k0 != null && k1 != null){
						if(k1.getClose() > k0.getClose()){
							return 1;
						}else if(k1.getClose() == k0.getClose()){
							return 0;
						}else{
							return -1;
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				return -1;
			}
		});
	}

	public static Map<String,K> getKsRealTime(Connection conn, List<String> codes) throws Exception{
		List<Index> indexs = new ArrayList<Index>();
		if(codes == null){
			String sql = "select code,name from stk_cn order by code";
			List<Stk> stks = JdbcUtils.list(conn, sql, Stk.class);
			for(Stk stk : stks){
				Index index = new Index(conn, stk.getCode());
				indexs.add(index);
			}
		}else{
			for(String code : codes){
				Index index = new Index(conn, code);
				indexs.add(index);
			}
		}
		Map<String,String> m = new HashMap<String,String>();
		for(Index index : indexs){
			m.put((index.getLoc()==1?"sh":"sz")+StringUtils.substring(index.getCode(), index.getCode().length()-6), index.getCode());
		}
		Map<String,K> map = new HashMap<String,K>();
		StringBuffer sb = new StringBuffer();
		for(int i=0;i<indexs.size();i++){
			Index index = indexs.get(i);
			sb.append((index.getLoc()==1?"sh":"sz")+StringUtils.substring(index.getCode(), index.getCode().length()-6)+",");
			if((i+1) % 200 == 0 || i == indexs.size()-1){
				String page = HttpUtils.get("http://hq.sinajs.cn/list="+sb, null, "GBK");
				//System.out.println(page);
				String[] str = page.split(";");
				for(int j=0;j<str.length;j++){
					String s = str[j];
					if(s.length() > 40){
						String code = StringUtils.substringBefore(s, "=");
						code = StringUtils.substring(code, code.length()-8);
						s = StringUtils.substringBetween(s, "\"", "\"");
						String[] ss = s.split(",");
						K k = new K();
						k.setOpen(Double.parseDouble(ss[1]));
						k.setLastClose(Double.parseDouble(ss[2]));
						k.setClose(Double.parseDouble(ss[3]));
						k.setHigh(Double.parseDouble(ss[4]));
						k.setLow(Double.parseDouble(ss[5]));
						k.setVolumn(Double.parseDouble(ss[8]));
						k.setAmount(Double.parseDouble(ss[9]));
						k.setDate(StringUtils.replace(ss[30], "-", ""));
						map.put(m.get(code), k);
					}
				}
				sb = new StringBuffer();
			}
		}
		return map;
	}

	public static K getKsRealTime(Connection conn, String code) throws Exception{
		List<String> codes = new ArrayList<String>();
		codes.add(code);
		return IndexUtils.getKsRealTime(conn, codes).get(code);
	}

	public static Map<String,K> getKsRealTime(Connection conn) throws Exception{
		return IndexUtils.getKsRealTime(conn, (List)null);
	}

	//getMethod.setRequestHeader("Cookie", "Hm_lpvt_1db88642e346389874251b5a1eded6e3=1401870866; Hm_lvt_1db88642e346389874251b5a1eded6e3=1399279853,1399456116,1399600423,1401761324; xq_a_token=bBfpd2WIHkEiOXxCZuvJKz; xq_r_token=HoJplnghTo9TdCtmaYhQ9C; bid=26948a7b701285b58366203fbc172ea6_hvykico0; xq_im_active=false; __utma=1.1861748176.1401870866.1401870866.1401870866.1; __utmb=1.2.9.1401870869035; __utmc=1; __utmz=1.1401870866.1.1.utmcsr=(direct)|utmccn=(direct)|utmcmd=(none)");
    public static List<String> getCareStkFromXueQiu(String label) throws Exception {
    	String page = HttpUtils.get("http://xueqiu.com/stock/portfolio/stocks.json?size=1000&pid=7&tuid=6237744859&showAll=false", null, XueqiuService.getCookies(), "GBK");
        //System.out.println(page);
        Map<String, Class> m = new HashMap<String, Class>();
        m.put("portfolios", Map.class);
        Map<String, List> map = (Map) JsonUtils.getObject4Json(page, Map.class, m);
        //System.out.println(map.get("portfolios"));
        List<String> stks = new ArrayList<String>();
        for(Object obj : map.get("portfolios")){
            Map care = (Map)obj;
            if(label.equalsIgnoreCase((String)care.get("name"))){
                for(String s : StringUtils.split((String)care.get("stocks"),",")){
                	/*if(s.length() == 8) {//SH601857
                		stks.add(s.substring(2));
                	}*/
                	stks.add(s);
                };
            }
        }
        return stks;
    }

    public static boolean contain(List<Index> indexs, String code){
    	for(Index index : indexs){
    		if(index.getCode().equals(code)){
    			return true;
    		}
    	}
    	return false;
    }

    public static int indexOf(List<Index> indexs, String code){
    	for(int i=0;i<indexs.size();i++){
    		Index index = indexs.get(i);
    		if(index.getCode().equals(code)){
    			return i;
    		}
    	}
    	return -1;
    }

    public static Index remove(List<Index> indexs, String code){
    	for(Index index : indexs){
    		if(index.getCode().equals(code)){
    			indexs.remove(index);
    			return index;
    		}
    	}
    	return null;
    }

    public static String containAnyStksNameWrapByString(String text,String startString,String endString){
    	if(STKS_NAME_PATTERN == null){
    		List<String> stks = JdbcUtils.list("select name from stk_cn", String.class);
    		STKS_NAME_PATTERN = StringUtils.replace(StringUtils.join(stks, '|'), "*", "\\*");
    	}
    	return ServiceUtils.getMatchStringAndWrapByString(text, STKS_NAME_PATTERN, startString, endString);
    }

    private static String PATTERN = "stk_search_pattern";
    private static String PATTERN_MAP = "stk_search_pattern_map";

    public static Set<String> extractCodeFromText(String text){
    	String pattern = null;
    	Map<String,String> map = null;

    	Object p = CacheUtils.get(CacheUtils.KEY_ONE_DAY, PATTERN);
    	if(p == null){
    		map = new HashMap<String,String>();
    		List<String> result = new ArrayList<String>();
    		List<Stk> stks = JdbcUtils.list("select code,name from stk where market=1 and cate=1 order by code", Stk.class);
			for(Stk stk : stks){
				String name = StringUtils.replace(stk.getName(), MARK_STAR, MARK_EMPTY);
				result.add(name);
				map.put(name, stk.getCode());
				String name2 = StringUtils.replace(name, MARK_BLANK_SPACE, MARK_EMPTY);
				if(!name.equals(name2)) {
					result.add(name2);
					map.put(name2, stk.getCode());
				}
			}
			pattern = CommonConstant.MARK_PARENTHESIS_LEFT + StringUtils.join(result, MARK_VERTICAL) + CommonConstant.MARK_PARENTHESIS_RIGHT;
			CacheUtils.put(CacheUtils.KEY_ONE_DAY, PATTERN, pattern);
			CacheUtils.put(CacheUtils.KEY_ONE_DAY, PATTERN_MAP, map);
    	}else{
    		pattern = String.valueOf(p);
    		map = (Map<String,String>)CacheUtils.get(CacheUtils.KEY_ONE_DAY, PATTERN_MAP);
    	}

    	Set<String> listName = ServiceUtils.getMatchStrings(text,  pattern );
    	Set<String> codes = new HashSet<String>();
    	for(String name : listName){
    		if(map.get(name) != null){
    			codes.add(map.get(name));
    		}
    	}
    	return codes;
    }

    public static List<String> indexToCode(Collection<Index> indexs){
		List<String> results = new ArrayList<String>();
		for(Index index : indexs){
			results.add(index.getCode());
		}
		return results;
	}

    public static List<Index> codeToIndex(Connection conn, Collection<String> codes){
		List<Index> results = new ArrayList<Index>();
		for(String code : codes){
			Index index = new Index(conn, code);
			results.add(index);
		}
		return results;
	}

}
