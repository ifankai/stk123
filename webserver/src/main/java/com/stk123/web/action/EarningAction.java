package com.stk123.web.action;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import com.stk123.model.bo.StkEarningsForecast;
import com.stk123.model.bo.cust.StkEarningsNoticeCust;
import com.stk123.model.Index;
import com.stk123.model.Industry;
import com.stk123.model.User;
import com.stk123.util.ServiceUtils;
import com.stk123.common.util.JdbcUtils;
import com.stk123.common.util.JsonUtils;
import com.stk123.common.CommonConstant;
import com.stk123.web.WebUtils;
import com.stk123.web.context.StkContext;

public class EarningAction {
	
	public String perform() throws Exception {
		StkContext sc = StkContext.getContext();
		User user = sc.getUser();
		if(user == null){
			return CommonConstant.ACTION_404;
		}
		Map<String, String> map = JsonUtils.testJson(user.getStkUser().getEarningSearchParams());
		sc.put("searchparams", map);
		return CommonConstant.ACTION_SUCC;
	}
	
	public void getEarningNotice() throws Exception {
		StkContext sc = StkContext.getContext();
		User user = sc.getUser();
		Connection conn = sc.getConnection();
		List<List> list = new ArrayList<List>();
		
		String fnDate = null;
		String erlow = null;
		String erlow2 = null;
		String erhigh = null;
		String pelow = null;
		String pehigh = null;
		String deletestk = null;
		String mvlow = null;
		String mvhigh = null;
		String noticefrom = null;
		String noticeto = null;
		String querystk = null;
		String comments = null;
		String realpelow = null;
		String realpehigh = null;
		String realpblow = null;
		String realpbhigh = null;
		String realnetlow = null;
		String realnethigh = null;
		String cashlow = null;
		String cashhigh = null;
		
		HttpServletRequest request = sc.getRequest();
		String ajax = request.getParameter("ajax");
		if("true".equalsIgnoreCase(ajax)){
			Map map = new HashMap();
			fnDate = map("fndate", request, map);
			erlow = map("erlow", request, map);
			erlow2 = map("erlow2", request, map);
			erhigh = map("erhigh", request, map);
			pelow = map("pelow", request, map);
			pehigh = map("pehigh", request, map);
			deletestk = map("deletestk", request, map);
			mvlow = map("mvlow", request, map);
			mvhigh = map("mvhigh", request, map);
			noticefrom = map("noticefrom", request, map);
			noticeto = map("noticeto", request, map);
			//querystk = map("querystk", request, map);
			comments = map("comments", request, map);
			realpelow = map("realpelow", request, map);
			realpehigh = map("realpehigh", request, map);
			realpblow = map("realpblow", request, map);
			realpbhigh = map("realpbhigh", request, map);
			realnetlow = map("realnetlow", request, map);
			realnethigh = map("realnethigh", request, map);
			cashlow = map("cashlow", request, map);
			cashhigh = map("cashhigh", request, map);
			user.updateEarningSearchParams(conn, JsonUtils.getJsonString4JavaPOJO(map));
		}else{
			Map<String, String> map = JsonUtils.testJson(user.getStkUser().getEarningSearchParams());
			fnDate = map.get("fndate");
			erlow = map.get("erlow");
			erlow2 = map.get("erlow2");
			erhigh = map.get("erhigh");
			pelow = map.get("pelow");
			pehigh = map.get("pehigh");
			deletestk = map.get("deletestk");
			mvlow = map.get("mvlow");
			mvhigh = map.get("mvhigh");
			noticefrom = map.get("noticefrom");
			noticeto = map.get("noticeto");
			realpelow = map.get("realpelow");
			realpehigh = map.get("realpehigh");
			realpblow = map.get("realpblow");
			realpbhigh = map.get("realpbhigh");
			realnetlow = map.get("realnetlow");
			realnethigh = map.get("realnethigh");
			cashlow = map.get("cashlow");
			cashhigh = map.get("cashhigh");
		}
		querystk = request.getParameter("querystk");
		String querycarestkonly = request.getParameter("querycarestkonly");
		String real = request.getParameter("real");
		
		boolean flag = true;
		//具体标的查询，所以不用排除
		if("true".equals(querycarestkonly) || !StringUtils.isEmpty(querystk)){
			flag = false;
		}
		
		StringBuffer sql = new StringBuffer();
		
		if("true".equals(real)){
			sql.append("select * from ( ");
			sql.append("select * from ( ");
			sql.append("select k.*,(s.total_capital*k.close)/10000 mv from stk_kline k, stk s, ");
			sql.append("(select max(kline_date) kdate from stk_kline where code in ('01000852','399006')) d ");
			sql.append("where k.code=s.code and k.kline_date = d.kdate) c ");
			sql.append("left join ");
			sql.append("(select a.* from stk_earnings_notice a, ");
			sql.append("(select code,max(fn_date) fn_date from stk_earnings_notice group by code) b ");
			sql.append("where a.code=b.code and a.fn_date=b.fn_date) e on c.code=e.code");
			
			sql.append(" left join (select t.fn_value as roe,t.type,t.code");
			sql.append(" from (select s.*,ROW_NUMBER() over(PARTITION by code,type order by fn_date desc) as num");
            sql.append(" from stk_fn_data s ) t");
            sql.append(" where t.num = 1) fn on c.code=fn.code and fn.type=109");//109:ROE
            
            sql.append(" left join (select t.fn_value as Gross_margin,t.type,t.code");
			sql.append(" from (select s.*,ROW_NUMBER() over(PARTITION by code,type order by fn_date desc) as num");
            sql.append(" from stk_fn_data s ) t");
            sql.append(" where t.num = 1) fn on c.code=fn.code and fn.type=106");//106:毛利率
			
			sql.append(" ) ");
			sql.append(" where 1=1 ");
			
			if(!StringUtils.isEmpty(realpelow)){
				sql.append(" and pe_ttm >= "+realpelow);
			}
			if(!StringUtils.isEmpty(realpehigh)){
				sql.append(" and pe_ttm <= "+realpehigh);
			}
			if(!StringUtils.isEmpty(realpblow)){
				sql.append(" and pb_ttm >= "+realpblow);
			}
			if(!StringUtils.isEmpty(realpbhigh)){
				sql.append(" and pb_ttm <= "+realpbhigh);
			}
			if(!StringUtils.isEmpty(mvlow)){
				sql.append(" and mv >= "+mvlow);
			}
			if(!StringUtils.isEmpty(mvhigh)){
				sql.append(" and mv <= "+mvhigh);
			}
		}else{
		
			sql.append("select * from stk_earnings_notice c");
			
			sql.append(" left join (select t.fn_value as roe,t.type,t.code");
			sql.append(" from (select s.*,ROW_NUMBER() over(PARTITION by code,type order by fn_date desc) as num");
            sql.append(" from stk_fn_data s ) t");
            sql.append(" where t.num = 1) fn on c.code=fn.code and fn.type=109");//109:ROE
            
            sql.append(" left join (select t.fn_value as gross_margin,t.type,t.code");
			sql.append(" from (select s.*,ROW_NUMBER() over(PARTITION by code,type order by fn_date desc) as num");
            sql.append(" from stk_fn_data s ) t");
            sql.append(" where t.num = 1) fn on c.code=fn.code and fn.type=106");//106:毛利率
			
			sql.append(" where ");
			
			if("true".equals(querycarestkonly)){
				sql.append("(c.code, c.fn_date) in (select code,max(fn_date) from stk_earnings_notice where 1=1");
				String[] stks = deletestk.split(",");
				List l = new ArrayList();
				for(String s : stks){
					l.add("'"+ServiceUtils.getNumberFromString(s)+"'");
				}
				sql.append(" and c.code in (" + StringUtils.join(l,",") + ")");
				sql.append(" group by code)");
				sql.append(" order by insert_time desc");
			}else{
				if(!StringUtils.isEmpty(querystk)){
					sql.append(" c.code = '"+querystk+"'");
					sql.append(" order by fn_date desc");
				}else{
					if(StringUtils.isEmpty(fnDate)){
						String now = ServiceUtils.getToday();
						String prev = ServiceUtils.getPrevQuarter(now);
						String next = ServiceUtils.getNextQuarter(now);
						sql.append("fn_date in ('"+prev+"', '"+next+"')");
					}else{
						sql.append("fn_date='"+fnDate+"'");
					}
					if(!StringUtils.isEmpty(erlow)){
						sql.append(" and er_low >= "+erlow);
					}
					if(!StringUtils.isEmpty(erlow2)){
						sql.append(" and er_low <= "+erlow2);
					}
					if(!StringUtils.isEmpty(erhigh)){
						sql.append(" and er_high >= "+erhigh);
					}
					if(!StringUtils.isEmpty(noticefrom)){
						sql.append(" and notice_date >= "+ ServiceUtils.formatDate(noticefrom, ServiceUtils.sf_ymd14, ServiceUtils.sf_ymd2));
					}
					if(!StringUtils.isEmpty(noticeto)){
						sql.append(" and notice_date <= "+ ServiceUtils.formatDate(noticeto, ServiceUtils.sf_ymd14, ServiceUtils.sf_ymd2));
					}
					//sql.append(" and last_amount>0 ");
					sql.append(" order by insert_time desc");
				}
			}
			
			
		}
		
		System.out.println(sql);
		List<StkEarningsNoticeCust> cares = JdbcUtils.list(conn,sql.toString(),	StkEarningsNoticeCust.class);
		
		Date start30 = ServiceUtils.addDay(ServiceUtils.getToday(), -30);
		Date start180 = ServiceUtils.addDay(ServiceUtils.getToday(), -180);
		for(StkEarningsNoticeCust care : cares){
			System.out.print('.');
			try{
				Index index = new Index(conn, care.getCode());
				
				if("true".equals(real)){
					double net = index.getNetProfitGrowthLastestQuarter();
					if(!StringUtils.isEmpty(realnetlow) && net < Double.parseDouble(realnetlow)){
						continue;
					}
					if(!StringUtils.isEmpty(realnethigh) && net > Double.parseDouble(realnethigh)){
						continue;
					}
					Double cashGrowth = index.getFnDataLastestByType(CommonConstant.FN_TYPE_CN_JYHDXJL).getFnValue();
					if(cashGrowth != null){
						if(!StringUtils.isEmpty(cashlow) && cashGrowth < Double.parseDouble(cashlow)){
							continue;
						}
						if(!StringUtils.isEmpty(cashhigh) && cashGrowth > Double.parseDouble(cashhigh)){
							continue;
						}
					}else{
						continue;
					}
				}
				
				List map = new ArrayList();
				Industry ind = index.getIndustryByMyFn();
				map.add(ServiceUtils.wrapCodeAndNameAsHtml(index.getCode(), index.getName()) + (ind!=null?ind.getType().getName().substring(0, 1):"-"));
				map.add(index.getIndustryDefault()!=null?index.getIndustryDefault().getType().getName():"-");
				List<Industry> inds = index.getIndustriesBySource("qq_conception");
				List<String> sb = new ArrayList<String>();
				for(Industry i : inds){
					sb.add(i.getType().getName());
				}
				List<Map> mbs = index.getMainBusiness();
			    for(Map mb : mbs){
			        sb.add(String.valueOf(mb.get("name")));
			    }
				map.add(WebUtils.display(StringUtils.join(sb, ","), 6, false) );
				double mv = index.getTotalMarketValue();
				if(!StringUtils.isEmpty(mvlow) && flag){
					if(mv < Double.parseDouble(mvlow)){
						continue;
					}
				}
				if(!StringUtils.isEmpty(mvhigh) && flag){
					if(mv > Double.parseDouble(mvhigh)){
						continue;
					}
				}
				map.add(ServiceUtils.number2String(mv,2));
				//毛利率
				//StkFnDataCust fn = index.getFnDataLastestByType(index.FN_GROSS_MARGIN);
				map.add(ServiceUtils.numberFormat2Digits(care.getGrossMargin()) + "%");
				map.add(care.getFnDate()==null?"":ServiceUtils.formatDate(care.getFnDate()));
				if(care.getErLow() == null){
					map.add("-");
				}else if(care.getErLow().equals(care.getErHigh())){
					map.add(createLink(care.getErLow()+"%", care.getCode()));
				}else{
					map.add(createLink(care.getErLow()+"%~"+care.getErHigh()+"%", care.getCode()));
				}
				
				map.add(care.getRoe());
				map.add(index.getNetProfitGrowthLastestQuarterAsString()+"%");
				map.add(index.getK().getKline().getPeTtm());
				Double pe = null;
				
				double perGrowth = 0.0;
				if(care.getErLow() == null && care.getErHigh() == null){
					//continue;
				}else{
					perGrowth = (care.getErLow()+care.getErHigh())/100/2;
				}
				if(care.getRealDate() != null){
					Double jlr = null;
					try{
						String str = ServiceUtils.getMatchString(care.getDetail(),"-?[0-9]*(\\.?)[0-9]*万元.{1}-?[0-9]*(\\.?)[0-9]*万元");
						double jlr2 = care.getLastAmount()/10000*(1+perGrowth);
						if(str != null){
							double jlrlow = Double.parseDouble(StringUtils.substringBefore(str, "万元"));
							double jlrhigh = Double.parseDouble(ServiceUtils.getNumberFromString(StringUtils.substringAfter(str, "万元")).replaceAll("-", ""));
							jlr2 = (jlrlow+jlrhigh)/2/10000;
						}else{
							if("预盈".equals(care.getErType()) && care.getLastAmount() < 0){
								jlr2 = Math.abs(care.getLastAmount()/10000*(1+perGrowth));//因为预盈，所以jlr不可能是负数
							}
						}
						if(care.getFnDate().endsWith(ServiceUtils.MMDD_Q4)){
							jlr = jlr2;
						}else if(care.getFnDate().endsWith(ServiceUtils.MMDD_Q3)){
							jlr = jlr2+
									index.getNetProfitByOneQuarter(ServiceUtils.getQuarter(care.getFnDate(), 3));
						}else if(care.getFnDate().endsWith(ServiceUtils.MMDD_Q2)){
							jlr = jlr2+
								   	index.getNetProfitByOneQuarter(ServiceUtils.getQuarter(care.getFnDate(), 2)) +
									index.getNetProfitByOneQuarter(ServiceUtils.getQuarter(care.getFnDate(), 3));
						}else if(care.getFnDate().endsWith(ServiceUtils.MMDD_Q1)){
							jlr = jlr2+
									index.getNetProfitByOneQuarter(ServiceUtils.getQuarter(care.getFnDate(), 1)) +
								   	index.getNetProfitByOneQuarter(ServiceUtils.getQuarter(care.getFnDate(), 2)) +
									index.getNetProfitByOneQuarter(ServiceUtils.getQuarter(care.getFnDate(), 3));
						}
						//System.out.println("pe jlr="+jlr2);
						pe = ServiceUtils.numberFormat(index.getTotalMarketValue()/jlr, 2);
						//if(pe <= 0 && flag)continue;
					}catch(Exception e){
						
					}
				}else{
					pe = index.getK().getKline().getPeTtm();
				}
				
				if(!StringUtils.isEmpty(pelow) && flag){
					if(pe == null || pe < Double.parseDouble(pelow)){
						continue;
					}
				}
				if(!StringUtils.isEmpty(pehigh) && flag){
					if(pe == null || pe > Double.parseDouble(pehigh)){
						continue;
					}
				}
				//业绩预告后pe
				map.add(pe==null?"-":pe);
				//历史低pe
				//业绩预告后pe/历史低pe
				double totalpe = 0.0;
				double maxpe = 0.0;
				int m = 0;
				List<List> his = index.getValueHistoryList();
				for(List<String> l : his){
					double d = Double.parseDouble(l.get(5));
					if(d > 0 && d < 100){
						totalpe += d;
						m++;
						if(d > maxpe)maxpe = d;
					}
				}
				if(m==0){
					map.add("-");
					map.add("-");
				}else if(m==1){
					map.add(totalpe);
					map.add(pe==null?"-":ServiceUtils.number2String(pe/totalpe, 2));
				}else{
					double dd = (totalpe-maxpe)/(m-1);
					map.add(ServiceUtils.number2String(dd, 2));
					map.add(pe==null?"-":ServiceUtils.number2String(pe/dd, 2));
				}
				
				//peg
				double jlrzz = index.getCAGRByEarningsForecast();
				map.add(pe==null||jlrzz == 0?"-":ServiceUtils.number2String(pe/jlrzz, 2));
				
				//业绩修正
				/*List<StkImportInfo> infos = index.getImportInfoAfterDate(260, start30);
				map.add(infos.size()>0?"上调":"-");*/
				List<StkEarningsForecast> efs = index.getEarningsForecast();
				boolean has1 = false;
				boolean has2 = false;
				if(efs!=null && efs.size()>0){
					for(StkEarningsForecast ef : efs){
						if(ef.getForecastYear().equals(String.valueOf(ServiceUtils.YEAR))){
							map.add(ServiceUtils.numberFormat(mv/ef.getForecastNetProfit(), 2));
							has1 = true;
						}
						if(ef.getForecastYear().equals(String.valueOf(ServiceUtils.YEAR+1))){
							map.add(ServiceUtils.numberFormat(mv/ef.getForecastNetProfit(), 2));
							has2 = true;
						}
						if(has1 && has2)break;
					}
				}
				if(!has1){
					map.add("-");
				}
				if(!has2){
					map.add("-");
				}
				
				//业绩预告公告日期
				map.add(care.getNoticeDate()!=null?ServiceUtils.formatDate(care.getNoticeDate()):"-");
				map.add(care.getRealDate()!=null?ServiceUtils.formatDate(care.getRealDate()):"-");
				map.add(care.getInsertTime()!=null?ServiceUtils.formatDate(care.getInsertTime(), ServiceUtils.sf_ymd):"-");
				//亮点
				/*List<String> ld = new ArrayList<String>();
				infos = index.getImportInfoAfterDate(140, start180);
				if(infos.size() > 0){
					ld.add("重组并购("+infos.size()+")");
				}
				//非公定增
				infos = index.getImportInfoAfterDate(150, start180);
				if(infos.size() > 0){
					ld.add("非公定增("+infos.size()+")");
				}
				//增持员工持股
				infos = index.getImportInfoAfterDate(120, start180);
				if(infos.size() > 0){
					ld.add("增持员持("+infos.size()+")");
				}
				//拐点
				infos = index.getImportInfoAfterDate(210, start180);
				if(infos.size() > 0){
					ld.add("拐点("+infos.size()+")");
				}
				map.add(WebUtils.display(StringUtils.join(ld, ","), 6, false));*/
				
				list.add(map);
			}catch(Exception e){
				System.err.println("err code:"+care.getCode());
				throw e;
			}
		}
		String json = JsonUtils.getJsonString4JavaPOJO(list);
		sc.setResponse("{\"data\":" + json + "}");
		System.out.println();
	}
	
	private String createLink(String s, String code){
		return "<a target=\"_blank\" href=\"http://finance.sina.com.cn/realstock/stock_predict/predict_notice/detail_"+code+".html\">"+s+"</a>";
	}
	
	private String map(String name, HttpServletRequest request, Map map){
		String val = request.getParameter(name);
		map.put(name, val);
		return val;
	}
}
