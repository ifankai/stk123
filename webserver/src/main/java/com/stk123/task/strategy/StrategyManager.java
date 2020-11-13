package com.stk123.task.strategy;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.IOUtils;

import com.stk123.model.bo.StkSearchCondition;
import com.stk123.model.Index;
import com.stk123.model.IndexUtils;
import com.stk123.model.K;
import com.stk123.service.ServiceUtils;
import com.stk123.service.XueqiuUtils;
import com.stk123.common.db.util.CloseUtil;
import com.stk123.common.db.util.DBUtil;
import com.stk123.common.util.EmailUtils;
import com.stk123.common.util.JdbcUtils;
import com.stk123.common.util.JsonUtils;
import com.stk123.common.util.collection.Name2Value;
//import com.stk123.web.action.ScreenerAction;
//import com.stk123.web.form.ScreenerForm;

public class StrategyManager {

	private Connection conn = null;
	private String date;

	public List<Strategy> strategys = new ArrayList<Strategy>();

	public StrategyManager(Connection conn,String date){
		this.conn = conn;
		this.date = date;
	}

	public void init(List<Index> indexs) throws Exception{

		Strategy.allIndexs = indexs;
		Strategy.followIndexs = getFollowIndex(conn);

		//List<Index> erIndexs = getEarningForecastIndex(conn);
		//List<Index> search_9 = getSearchCondition(conn, 9);//锟斤拷锟斤拷源锟斤拷锟斤拷锟斤拷业锟斤拷
		//List<Index> search_16 = getSearchCondition(conn, 16);//小锟斤拷锟斤拷

		strategys.add(new Strategy1());//模型1-60日均线上升且活跃放量
		strategys.add(new Strategy2());//模型2-120日均线上升60日线下降
		strategys.add(new Strategy3());//模型3-ENE活跃个股
		strategys.add(new Strategy4());//模型4-连续5日缩量
		strategys.add(new Strategy5());//模型5-突破趋势线
		strategys.add(new Strategy6());//模型6-放量后连续3日缩量
		strategys.add(new Strategy7());//模型7-连续5日下跌量能却放大
		//strategys.add(new Strategy8()); //模型8-5浪下跌
		strategys.add(new Strategy9());//模型9-最近5倍巨量
		strategys.add(new Strategy10());//模型10-三堆巨量后缩量
		strategys.add(new Strategy11());//模型11-二堆巨量后缩量
		strategys.add(new Strategy12());//模型12-60日内出现天量后回调到60均线下方
		strategys.add(new Strategy13());//模型13-30日内出现天量后回调到20均线下方且出现二品抄底
		strategys.add(new Strategy14());//模型14-次新-小十字星且缩量
		strategys.add(new Strategy15());//模型15-巨量后k线振幅缩小且没有调整太深
		strategys.add(new Strategy16());//模型16-阳线放量阴线缩量
		//strategys.add(new Strategy17());//K锟竭诧拷锟斤拷

		/*List<Strategy> slist = getSearchConditionWithStrategy(conn);
		for(Strategy s : slist){
			strategys.add(s);
		}*/

		List<Index> netProfitGrwothGreaterThanZeroIndexs = getNetProfitGrwothGreaterThanZero(conn);

		//strategys.add(new Strategy20("锟斤拷锟竭短碉拷锟斤拷锟斤拷",getIndexMA20And120(netProfitGrwothGreaterThanZeroIndexs, date),false,true));
		//strategys.add(new Strategy20("前锟节伙拷锟斤拷锟斤拷200%锟斤拷锟斤拷",getIndexHighHsl(netProfitGrwothGreaterThanZeroIndexs, date),false,true));

		strategys.add(new Strategy19("关注C",Strategy.followIndexs,false,true));//模型19-突破趋势线
		//strategys.add(new Strategy18("锟斤拷注C",Strategy.followIndexs,false,true));//锟斤拷选锟斤拷k锟竭碉拷位十锟斤拷锟斤拷
		strategys.add(new Strategy20("关注C",Strategy.followIndexs,false,true));//模型20-十字星
	}

	public static List<Name2Value<String,Name2Value<Integer, Class>>> APPLY_STRATEGY = null;

	public static List<Name2Value<String,Name2Value<Integer, Class>>> getApplyStrategy() {
		if(APPLY_STRATEGY != null)return APPLY_STRATEGY;
		List<Name2Value<String,Name2Value<Integer, Class>>> s = new ArrayList();
		//s.add(new Name2Value("锟斤拷锟斤拷18-锟斤拷位K锟斤拷十锟斤拷锟斤拷", new Name2Value(18, Strategy18.class)));
		s.add(new Name2Value("模型19-突破趋势线", new Name2Value(19, Strategy19.class)));
		s.add(new Name2Value("模型20-十字星", new Name2Value(20, Strategy20.class)));
		return APPLY_STRATEGY = s;
	}

	public static Strategy getStrategyById(String id) throws Exception {
		List<Name2Value<String,Name2Value<Integer, Class>>> list = getApplyStrategy();
		for(Name2Value<String,Name2Value<Integer, Class>> s : list){
			if(s.getValue().getName().intValue() == Integer.parseInt(id)){
				return (Strategy)s.getValue().getValue().newInstance();
			}
		}
		return null;
	}

	public void execute() throws Exception{
		for(Strategy strategy : strategys){
			try{
				System.out.println(strategy.getClass().getSimpleName());
				strategy.run(conn, date);
			}catch(Exception e){
				EmailUtils.sendException(strategy.getClass().getName(), e);
			}
		}
	}


    public static List<Index> getFollowIndex(Connection conn) throws Exception {
        Set<String>	followStks = null;
        try{
            followStks = XueqiuUtils.getFollowStks("关注C");
            followStks.addAll(XueqiuUtils.getFollowStks("我的"));
            IOUtils.writeLines(followStks, null, new FileOutputStream(new File("d:\\care.txt")));
        }catch(Exception e){
            followStks = new HashSet(IOUtils.readLines(new FileInputStream("d:\\care.txt")));
            //EmailUtils.send("雪锟斤拷锟斤拷取锟斤拷锟斤拷注C锟斤拷失锟斤拷", e);//锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟絏ueqiuUtils锟斤拷cookie锟芥换锟斤拷锟斤拷锟铰碉拷
        }

        return IndexUtils.codeToIndex(conn, followStks);
    }

	public static List<Index> getEarningForecastIndex(Connection conn) throws Exception {
		List<String> codes = JdbcUtils.list(conn, "select code from stk_search_mview v where 1=1 and v.Gross_Profit_Margin >= 20.0 and v.er_low >= 30.0 and v.er_pe >= 0.0 and er_date=?",ServiceUtils.getNextQuarter(ServiceUtils.getToday()), String.class);
		return IndexUtils.codeToIndex(conn, codes);
	}

	public static List<Index> getIndexMA20And120(List<Index> indexs, String today) throws Exception {
		List<Index> results = new ArrayList<Index>();
		for(Index index : indexs){
			if(index.isStop(today))continue;
			K k = index.getK(today);
			if(k==null)continue;
			K yk = k.before(1);
			if(k.getMA(K.Close, 20) <= yk.getMA(K.Close, 20)
					&& (k.getMA(K.Close, 120) >= yk.getMA(K.Close, 120) || k.getMA(K.Close, 250) >= yk.getMA(K.Close, 250))){
				results.add(index);
			}
		}
		return results;
	}

	public static List<Index> getIndexHighHsl(List<Index> indexs, String today) throws Exception {
		List<Index> results = new ArrayList<Index>();
		for(Index index : indexs){
			if(index.isStop(today))continue;
			K k = index.getK(20);
			if(k==null)continue;
			int cnt = k.getKCountWithCondition(100, new K.Condition(){
				public boolean pass(K k) throws Exception {
					if(k.getSUM(K.Hsl, 30) >= 200){
						return true;
					}
					return false;
				}
			});
			if(cnt > 0){
				results.add(index);
			}
		}
		return results;
	}

	public static List<Index> getNetProfitGrwothGreaterThanZero(Connection conn) {
		List<String> codes = JdbcUtils.list(conn, "select code from stk_search_mview where net_profit_growth_rate>=30 or revenue_growth_rate>=50", String.class);
		return IndexUtils.codeToIndex(conn, codes);
	}

	public static void main(String[] args) throws Exception{
		Connection conn = DBUtil.getConnection();
		//getIndexFromSearchCondition(conn, 9);
		CloseUtil.close(conn);
	}
}
