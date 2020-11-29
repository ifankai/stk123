package com.stk123.model.core;

import com.stk123.common.util.collection.IntRange2IntMap;
import com.stk123.entity.StkKlineEntity;
import com.stk123.model.Ene;
import com.stk123.model.Index;
import com.stk123.model.bo.StkKline;
import com.stk123.util.HttpUtils;
import com.stk123.util.ServiceUtils;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
public class Bar implements Serializable, Cloneable {

	public final static int Close = 1;
	public final static int Open = 2;
	public final static int High = 3;
	public final static int Low = 4;
	public final static int Volumn = 5;
	public final static int Amount = 6;
	public final static int Hsl = 7;

	public final static int MA = 10;
	public final static int SUM = 11;

	private String date;
	private LocalDateTime ts;
	private double open;
	private double close;
	private double high;
	private double low;
	private double volume;
	private double amount;
	private double adjusted;
	private double hsl; //换手率

	private Bar before;
	private Bar after;


	public final static List<String> JSON_INCLUDE_FIELDS = new ArrayList<String>();
	static{
		JSON_INCLUDE_FIELDS.add("date");
		JSON_INCLUDE_FIELDS.add("open");
		JSON_INCLUDE_FIELDS.add("close");
		JSON_INCLUDE_FIELDS.add("high");
		JSON_INCLUDE_FIELDS.add("low");
		JSON_INCLUDE_FIELDS.add("volume");
		JSON_INCLUDE_FIELDS.add("amount");
	}

    /**
     *
     * @param restoration, 股价复权, true:前复权 false:不复权 000863
     * @param market
     * @param kLine,
     * @param lastKLine,
     * @param flag,
     */
	public Bar(boolean restoration, int market, StkKlineEntity bar, Bar barBefore, Bar barAfter){
		if(bar == null)return;
		if(barAfter == null || market == 2 || market == 3 || !restoration){
			this.setDate(bar.getKlineDate());
			this.setOpen(bar.getOpen());
			this.setClose(bar.getClose());
			this.setHigh(bar.getHigh());
			this.setLow(bar.getLow());
			this.setVolume(bar.getVolumn());
			this.setAmount(bar.getAmount()==null?0:bar.getAmount());
			this.setAdjusted(bar.getCloseChange()==null?0:bar.getCloseChange());
			this.setHsl(bar.getHsl()==null?0:bar.getHsl());
		}else{
			this.setDate(bar.getKlineDate());
			double tmpCloseChanged = barAfter.getClose()/barAfter.getAdjusted()*bar.getCloseChange()/bar.getClose();
            tmpCloseChanged = barAfter.getClose()/barAfter.getAdjusted()/bar.getClose();

			this.setOpen(ServiceUtils.numberFormat(bar.getOpen() * tmpCloseChanged, 2));
			this.setClose(ServiceUtils.numberFormat(bar.getClose() * tmpCloseChanged, 2));
			this.setHigh(ServiceUtils.numberFormat(bar.getHigh() * tmpCloseChanged, 2));
			this.setLow(ServiceUtils.numberFormat(bar.getLow() * tmpCloseChanged, 2));
			this.setVolume(bar.getVolumn());
			this.setAmount(bar.getAmount()==null?0:bar.getAmount());
			this.setAdjusted(bar.getCloseChange()==null?0:bar.getCloseChange());
			this.setHsl(bar.getHsl()==null?0:bar.getHsl());
		}
	}

	public static interface Condition{
		public boolean pass(Bar k) throws Exception;
	}

	//n=0是当天
	public Bar before(int n){
		Bar tmp = this;
		while(n-- > 0 && tmp != null && tmp.before != null){
			tmp = tmp.before;
		}
		return tmp;
	}
	public Bar after(int n){
		Bar tmp = this;
		while(n-- > 0 && tmp != null && tmp.after != null){
			tmp = tmp.after;
		}
		return tmp;
	}
	public Bar yesterday(){
		return before(1);
	}
	public Bar tomorrow(){
		return after(1);
	}

	public double getValue(int type) throws Exception {
		if(type == Bar.Close){
			return this.getClose();
		}else if(type == Bar.Open){
			return this.getOpen();
		}else if(type == Bar.High){
			return this.getHigh();
		}else if(type == Bar.Low){
			return this.getLow();
		}else if(type == Bar.Volumn){
			return this.getVolume();
		}else if(type == Bar.Amount){
			return this.getAmount();
		}else if(type == Bar.Hsl){
			return this.getHsl();
		}else{
			throw new Exception("type not support!");
		}
	}

	public double getValue(String field) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
		return Double.parseDouble(BeanUtils.getProperty(this, field));
	}

	/**
	 * @param type open/close/high/low/amount/volume/hsl
	 * @param type2 ma/sum
	 * @param days2, type2多少days2的ma/sum
	 * @return
	 * @throws Exception
	 */
	public double getValue(int type, int type2, int days) throws Exception {
		if(type2 == Bar.MA){
			return this.getMA(type, days);
		}else if(type2 == Bar.SUM){
			return this.getSUM(type, days);
		}else if(type2 == -1){
			return this.getValue(type);
		}else{
			throw new Exception("type not support!"+type2);
		}
	}



	public interface Calc<T> {
		public T calc(Bar k, T d);
	}

	public <T> T getValue(int n, Calc<T> func){
		Bar k = this;
		T result = null;
		while(true){
			if(func != null){
				result = func.calc(k, result);
			}
			if(n++ == 0){
				if(func != null){
					return result;
				}
				return (T)k;
			}
			k = k.before;
			if(k == null)break;
		}
		return null;
	}

	public double getMA(int type, int days) throws Exception {
		double total = 0.0;
		int tmp = days;
		for(int i=0;i<days;i++){
			Bar k = this.before(i);
			if(k == null){
				return 0;
			}
			total += k.getValue(type);
		}
		return ServiceUtils.numberFormat(total/tmp,2);
	}

	public double getMA(int days, Calculator func) throws Exception {
		double total = 0.0;
		int tmp = days;
		for(int i=0;i<days;i++){
			Bar k = this.before(i);
			if(k == null){
				return 0;
			}
			total += func.calc(k);
		}
		return ServiceUtils.numberFormat(total/tmp,2);
	}

	public double getSUM(int type, int days) throws Exception {
		double total = 0.0;
		for(int i=0;i<days;i++){
			Bar k = this.before(i);
			if(k == null){
				return 0;
			}
			total += k.getValue(type);
		}
		return ServiceUtils.numberFormat(total,2);
	}

	public Bar getMax(int type, int days, int type2, int days2) throws Exception {
		double max = 0.0;
		Bar ret = null;
		for(int i=0;i<days;i++){
			Bar k = this.before(i);
			if(k == null){
				continue;
			}
			double d = k.getValue(type, type2, days2);
			if(d > max){
				ret = k;
				max = d;
			}
		}
		return ret;
	}

	//low的低点
	public double getLLV(int n){
		double value = 0.0;
		Bar k = this;
		for(int i=0;i<n;i++){
			double tmp = k.getLow();
			if(value > tmp || value == 0.0){
				value = tmp;
			}
			k = k.before(1);
		}
		return value;
	}

	//low的低点
	public Bar getKByLLV(int n){
		double value = 0.0;
		Bar ret = this;
		Bar k = this;
		for(int i=0;i<n;i++){
			double tmp = k.getLow();
			if(value > tmp || value == 0.0){
				value = tmp;
				ret = k;
			}
			k = k.before(1);
		}
		return ret;
	}
	//volume的低点
	public Bar getKByLVV(int n){
		double value = 0.0;
		Bar ret = this;
		Bar k = this;
		for(int i=0;i<n;i++){
			double tmp = k.getVolume();
			if(value > tmp || value == 0.0){
				value = tmp;
				ret = k;
			}
			k = k.before(1);
		}
		return ret;
	}

	//high的高点
	public double getHHV(int n){
		double value = 0.0;
		Bar k = this;
		for(int i=0;i<n;i++){
			double tmp = k.getHigh();
			if(value < tmp || value == 0.0){
				value = tmp;
			}
			k = k.before(1);
		}
		return value;
	}
	//high的高点
	public Bar getKByHHV(int n){
		double value = 0.0;
		Bar k = this;
		Bar ret = null;
		for(int i=0;i<n;i++){
			double tmp = k.getHigh();
			if(value < tmp || value == 0.0){
				value = tmp;
				ret = k;
			}
			k = k.before(1);
		}
		return ret;
	}
	//close的高点
	public double getHCV(int n) throws Exception {
		double value = 0.0;
		Bar k = this;
		for(int i=0;i<n;i++){
			double tmp = k.getClose();
			if(value < tmp || value == 0.0){
				value = tmp;
			}
			k = k.before(1);
		}
		return value;
	}
	//volume的高点
	public double getHVV(int n) throws Exception {
		double value = 0.0;
		Bar k = this;
		for(int i=0;i<n;i++){
			double tmp = k.getVolume();
			if(value < tmp || value == 0.0){
				value = tmp;
			}
			k = k.before(1);
		}
		return value;
	}
	//volume的高点
	public Bar getKByHVV(int n) throws Exception {
		double value = 0.0;
		Bar k = this;
		Bar ret = null;
		for(int i=0;i<n;i++){
			double tmp = k.getVolume();
			if(value < tmp || value == 0.0){
				value = tmp;
				ret = k;
			}
			k = k.before(1);
		}
		return ret;
	}

	/*public Ene getEne() throws Exception {
		return this.getEne(null);
	}
	public Ene getEne(Index index) throws Exception {
		if(ene != null){
			return this.ene;
		}
		ene = new Ene();
		double value = 0;
		if(index != null){
			String page = HttpUtils.get("http://hq.sinajs.cn/list="+(index.getLoc()==1?"sh":"sz")+index.getCode(), null, "");
			//System.out.println(page);
			String str = StringUtils.substringBetween(page, "=\"", "\";");
			double price = 0;
			if(str != null){
				String[] ss = str.split(",");
				price = Double.parseDouble(ss[3]);
			}else{
				price = 0;
			}
			value = this.getMA(Bar.Close, Ene.N-1);
			value = (value + price)/2;
		}else{
			value = this.getMA(Bar.Close, Ene.N);
		}
		double upper = ServiceUtils.numberFormat((1+Ene.M1/100.0)*value,2);
		double lower = ServiceUtils.numberFormat((1-Ene.M2/100.0)*value,2);
		double ene = ServiceUtils.numberFormat((upper+lower)/2,2);
		this.ene.setEne(ene);
		this.ene.setUpper(upper);
		this.ene.setLower(lower);
		return this.ene;
	}*/

	public double getBIAS(int n) throws Exception{
		double ma = this.getMA(Close, n);
		return (this.getClose() - ma)/ma * 100;
	}


	public double getBIAS4(int m, int n) throws Exception {
		return (this.getMA(Bar.Close, m) - this.getMA(Bar.Close, n)) / this.getMA(Bar.Close, n) * 100;
	}

	public double getChangeOfClose() throws Exception{
		return this.getChange(1, Bar.Close);
	}

	public double getChangeOfClose(int n) throws Exception{
		return this.getChange(n, Bar.Close);
	}

	public double getChange(int n, final int type) throws Exception{
		return this.getChange(n, new Calculator(){
			public double calc(Bar k) throws Exception {
				return k.getValue(type);
			}
		});
	}

	public double getChange(int n, Calculator fun) throws Exception{
		double v = fun.calc(this);
		double kv = this.getRef(n, fun);
		return (v-kv)/kv;
	}

	/*public void setEne(Ene ene) {
		this.ene = ene;
	}*/

	public final double getEMA(int type, int n) throws Exception {
		return this.getEMA(type, n, 2);
	}

	public final double getEMA(int type, final int n, final int m) throws Exception {
		List<Double> list = new ArrayList<Double>();
		int j = 0;
		Bar k = this;
		int total = n * 10;//TODO　具体怎么定这个数字不清楚
		while(true){
			list.add(k.getValue(type));
			if(j++ > total)break;
			k = k.before(1);
		}
		Double x = m / (n + 1.0);// 计算出序数
		Double ema = list.get(list.size()-1);// 第一天ema等于当天收盘价
		for (int i = list.size()-1; i >= 0 ; i--) {
			// 第二天以后，当天收盘 收盘价乘以系数再加上昨天EMA乘以系数-1
			ema = list.get(i) * x + ema * (1 - x);
		}
		return ema;
	}

	public final double getEMA(final int n, final int m, Calculator func) throws Exception {
		List<Double> list = new ArrayList<Double>();
		int j = 0;
		Bar k = this;
		int total = n * 10;//TODO　具体怎么定这个数字不清楚
		while(true){
			list.add(func.calc(k));
			if(j++ > total)break;
			k = k.before(1);
		}
		Double x = m / (n + 1.0);// 计算出序数
		Double ema = list.get(list.size()-1);// 第一天ema等于当天收盘价
		for (int i = list.size()-1; i >= 0 ; i--) {
			// 第二天以后，当天收盘 收盘价乘以系数再加上昨天EMA乘以系数-1
			ema = list.get(i) * x + ema * (1 - x);
		}
		return ema;
	}

	public final double getEMA(final int n, Calculator func) throws Exception {
		return this.getEMA(n, 2, func);
	}

	public double getRef(int n, Calculator fun) throws Exception{
		return fun.calc(this.before(n));
	}

	public static interface Calculator{
		public double calc(Bar k) throws Exception;
	}

	/**
	 * Calculate EMA/EXPMA
	 * @param list:Price list to calculate，the first at head, the last at tail.
	 * @return
	 */
	public static final Double getEMA(final List<Double> list, final int number) {
		// 开始计算EMA值，
		Double k = 2.0 / (number + 1.0);// 计算出序数
		Double ema = list.get(0);// 第一天ema等于当天收盘价
		for (int i = 1; i < list.size(); i++) {
			// 第二天以后，当天收盘 收盘价乘以系数再加上昨天EMA乘以系数-1
			ema = list.get(i) * k + ema * (1 - k);
		}
		return ema;
	}

	public static final Double getEMA(final List<Double> list, final int n, final int m) {
		// 开始计算EMA值，
		Double k = m / (n + 1.0);// 计算出序数
		Double ema = list.get(0);// 第一天ema等于当天收盘价
		for (int i = 1; i < list.size(); i++) {
			// 第二天以后，当天收盘 收盘价乘以系数再加上昨天EMA乘以系数-1
			ema = list.get(i) * k + ema * (1 - k);
		}
		return ema;
	}

	public Bar getKWithCondition(int days, Condition cdn) throws Exception {
		Bar k = this;
		while(k != null){
			//System.out.println("k="+k.getDate());
			if(cdn.pass(k)){
				return k;
			}
			k = k.before(1);
			if(days -- <= 1){
				break;
			}
		}
		return null;
	}

	public int getKCountWithCondition(int days, Condition condition) throws Exception {
		Bar k = this;
		int cnt = 0;
		while(k != null){
			if(condition.pass(k)){
				cnt ++;
			}
			if(days -- <= 1){
				break;
			}
			k = k.before(1);
		}
		return cnt;
	}

	/**
	 * @param days
	 * @param indent 当满足条件时，k线往前进的天数
	 * @param condition
	 * @return
	 * @throws Exception
	 */
	public int getKCountWithCondition(int days, int indent, Condition condition) throws Exception {
		Bar k = this;
		int cnt = 0;
		while(k != null){
			if(condition.pass(k)){
				cnt ++;
				k = k.before(indent);
			}
			if(days -- <= 1){
				break;
			}
			k = k.before(1);
		}
		return cnt;
	}

	/**
	 * 是否涨停
	 */
	public boolean isUpLimit(){
		if(open != 0 && open == close && open == high && open == low){
			return true;
		}
		return false;
	}

	public boolean isNoVolumeOrNoChange(){
		if((open == close && open == high && open == low) || volume == 0){
			return true;
		}
		return false;
	}

	/**
	 * DIF:EMA(CLOSE,SHORT)-EMA(CLOSE,LONG);
	 * DEA:EMA(DIF,MID);
	 * MACD:(DIF-DEA)*2,COLORSTICK;
	 * @return
	 */
	public MACD getMACD(final int s,final int l, int m) throws Exception {
		MACD macd = new MACD();
		double dif = this.getEMA(Bar.Close, s) - this.getEMA(Close, l);
		double dea = this.getEMA(m, 2, new Calculator(){
			public double calc(Bar k) throws Exception {
				return k.getEMA(Bar.Close, s) - k.getEMA(Close, l);
			}});
		macd.dif = dif;
		macd.dea = dea;
		macd.macd = (dif - dea) * 2;
		return macd;
	}

	public MACD getMACD() throws Exception {
		return getMACD(12, 26, 9);
	}

	public class MACD{
		public double dif;
		public double dea;
		public double macd;

		public String toString(){
			return "dif="+dif+",dea="+dea+",macd="+macd;
		}
	}

	public List<Bar> getHistoryLowPoint(int days, int n) throws Exception{
		List<Bar> lowPoints = new ArrayList<Bar>();
		Bar start = this.before(days);
		Bar end = this.before(n);
		Bar k = start;
		while((k = k.after(1)) != null){
			if(k.getDate().compareTo(end.getDate()) >= 0){
				break;
			}
			Bar low = k.after(n).getKByLLV(2*n);
			//K low = this.getLLV(k.before(n).getDate(),k.after(n).getDate());
			if(k.getDate().equals(low.getDate())){
				lowPoints.add(k);
			}
		}
		return lowPoints;
	}

	//一品抄底-趋势线
	public double getTrend() throws Exception {
		double trend = this.getMA(2, new Bar.Calculator() {
			@Override
			public double calc(Bar k) throws Exception {
				double sma_a4_6 = k.getEMA(6, 1, new Bar.Calculator() {
					@Override
					public double calc(Bar k) throws Exception {
						double llv21 = k.getLLV(21);
						double a4 = ((k.getClose() - llv21)/(k.getHHV(21) - llv21)) * 100;
						return a4;
					}
				});

				double sma_a4_5_2 = k.getEMA(5, 1, new Bar.Calculator() {
					@Override
					public double calc(Bar k) throws Exception {
						double sma_a4_5 = k.getEMA(5, 1, new Bar.Calculator() {
							@Override
							public double calc(Bar k) throws Exception {
								double llv21 = k.getLLV(21);
								double a4 = ((k.getClose() - llv21)/(k.getHHV(21) - llv21)) * 100;
								return a4;
							}
						});
						return sma_a4_5;
					}
				});
				return 3 * sma_a4_6 - 2 * sma_a4_5_2;
			}
		});
		return trend;
	}
	//一品抄底-黑马线
	public double getHorse() throws Exception {
		double horse = this.getEMA(5, 2, new Bar.Calculator() {
			@Override
			public double calc(Bar k) throws Exception {
				return k.getEMA(5, 1, new Bar.Calculator() {
					@Override
					public double calc(Bar k) throws Exception {
						double llv55 = k.getLLV(55);
						return ((k.getClose() - llv55)/(k.getHHV(55) - llv55)) * 100;
					}
				});
			}
		});
		return horse;
	}
	//一品抄底-MACD
	public double getYpcdMACD() throws Exception {
		double horse = this.getHorse();
		double trend = this.getTrend();
		double macd = trend - horse;
		return macd;
	}
	//一品抄底-底部
	public boolean getYpcd() throws Exception {
		Bar yk = this.before(1);
		double a2 = (this.getHorse() - yk.getHorse())/yk.getHorse() * 100;
		double ya2 = this.getRef(1, new Bar.Calculator() {
			public double calc(Bar k) throws Exception {
				Bar yk = k.before(1);
				return (k.getHorse() - yk.getHorse())/yk.getHorse() * 100;
			}
		});
		if((a2 <= -10 || ya2 <= -10) && a2 > ya2)return true;
		return false;
	}

	public boolean getYpcd2() throws Exception {
		double macd = this.getYpcdMACD();
		//System.out.println(k.getDate()+"="+macd);
		if(macd >= 4){
			Bar yk = this.before(1);
			double ymacd = yk.getYpcdMACD();
			if(ymacd < 4 && ymacd > -2){
				int cnt = this.getKCountWithCondition(10, new Bar.Condition() {
					@Override
					public boolean pass(Bar k) throws Exception {
						double m = k.getYpcdMACD();
						if(m >= 5)return true;
						return false;
					}
				});
				int cnt2 = this.getKCountWithCondition(10, new Bar.Condition() {
					@Override
					public boolean pass(Bar k) throws Exception {
						double m = k.getYpcdMACD();
						if(m < 3 && m > -3)return true;
						return false;
					}
				});

				if(cnt > 0 || cnt2 >= 6){
					cnt = this.getKCountWithCondition(8, new Bar.Condition() {
						@Override
						public boolean pass(Bar k) throws Exception {
							double m = k.getYpcdMACD();
							if(m < -5)return true;
							return false;
						}
					});
					cnt += this.getKCountWithCondition(20, new Bar.Condition() {
						@Override
						public boolean pass(Bar k) throws Exception {
							double m = k.getYpcdMACD();
							if(m > 40)return true;
							return false;
						}
					});
					//System.out.println(cnt);
					if(cnt == 0){
						cnt = this.getKCountWithCondition(10, new Bar.Condition() {
							@Override
							public boolean pass(Bar k) throws Exception {
								if(k.getYpcdMACD() > -3)return true;
								return false;
							}
						});
						//System.out.println(cnt);
						if(cnt >= 5){
							double trend = this.getTrend();
							if(trend < 25){
								return true;
							}
						}
					}
				}
			}
		}
		return false;
	}

	//二品抄底-买入时机
	public double getEpcd_Mrsj() throws Exception{
		double y = this.getEMA(3, 1, new Bar.Calculator(){
			public double calc(Bar k) throws Exception {
				double llv = k.getLLV(9);
				double hhv = k.getHHV(9);
				return (k.getClose() - llv) / (hhv - llv) * 100;
			}}
		);
		//D:=SMA(K,3,1);
		double z = this.getEMA(3, 1, new Bar.Calculator(){
			public double calc(Bar k) throws Exception {
				double y = k.getEMA(3, 1, new Bar.Calculator(){
					public double calc(Bar k) throws Exception {
						double llv = k.getLLV(9);
						double hhv = k.getHHV(9);
						return (k.getClose() - llv) / (hhv - llv) * 100;
					}}
				);
				return y;
			}}
		);
		//J:=3*K-2*D;
		double v = 3*y - 2*z;
		return v;
	}

	/**
	 * 二品抄底
	 */
	public boolean getEpcd() throws Exception{
		double d = this.getEpcd_Mrsj();
		double yd = this.before(1).getEpcd_Mrsj();
		if(d < 5 && d - yd >= 3){
			return true;
		}
		return false;
	}

	/**
	 * 二品抄底-红线上穿买
	 * VA:=(2*C+H+L)/4;
	 * VB:=LLV(L,5);
	 * VC:=HHV(H,5);
	 * 红线上穿买:EMA((VA-VB)/(VC-VB)*100,5),COLORRED;
	 * @return
	 * @throws Exception
	 */
	public double getEpcd_Hxscm() throws Exception{
		double z = this.getEMA(5, new Bar.Calculator(){
			public double calc(Bar k) throws Exception {
				double va = (2 * k.getClose() + k.getHigh() + k.getLow()) / 4;
				double vb = k.getLLV(5);
				double vc = k.getHHV(5);
				return (va - vb) / (vc - vb) * 100;
			}}
		);
		return ServiceUtils.numberFormat(z, 2);
	}

	/**
	 * 二品抄底-红线下穿卖
	 * EMA(红线上穿买,3),COLORFFFF00;
	 */
	public double getEpcd_Hxxcm() throws Exception{
		double z = this.getEMA(3, new Bar.Calculator(){
			public double calc(Bar k) throws Exception {
				return k.getEpcd_Hxscm();
			}}
		);
		return ServiceUtils.numberFormat(z, 2);
	}

	/**
	 * 二品抄底-趋势
	 * 3*SMA((CLOSE-LLV(LOW,27))/(HHV(HIGH,27)-LLV(LOW,27))*100,5,1)-2*SMA(SMA((CLOSE-LLV(LOW,27))/(HHV(HIGH,27)-LLV(LOW,27))*100,5,1),3,1),COLOR00FFFF;
	 */
	public double getEpcd_Qs() throws Exception{
		double x = this.getEMA(5, 1, new Bar.Calculator(){
			public double calc(Bar k) throws Exception {
				double xa = k.getLLV(27);
				double xb = k.getHHV(27);
				return (k.getClose() - xa) / (xb - xa) * 100;
			}}
		);

		double y = this.getEMA(3, 1, new Bar.Calculator(){
			public double calc(Bar k) throws Exception {
				return k.getEMA(5, 1, new Bar.Calculator(){
					public double calc(Bar k) throws Exception {
						double xa = k.getLLV(27);
						double xb = k.getHHV(27);
						return (k.getClose() - xa) / (xb - xa) * 100;
					}}
				);
			}}
		);
		return ServiceUtils.numberFormat(3*x - 2*y, 2);
	}
	
	//10均量连续上升或下降次数
	public boolean hasHighVolumn(double d) throws Exception{
		IntRange2IntMap range = new IntRange2IntMap();
		//range.define(0, 5);
		range.define(6, 50);
		
		Bar k = this.before(120);
		int upCount = 0;
		int downCount = 0;
		boolean isUp = true;
		double tmp = 0.0;
		while(k != null){
			//System.out.println(k.getDate());
			if(k.getDate().equals(k.tomorrow().getDate()))
				break;
			
			Bar yk = k.yesterday();
			double kv = k.getMA(Bar.Volumn, 10);
			double ykv = yk.getMA(Bar.Volumn, 10);
			if(upCount == 0 && downCount == 0){
				isUp = (kv >= ykv);
			}
			
			if(kv >= ykv && !isUp && downCount > 0){
				isUp = true;
				//range.addCount(downCount);
				downCount = 0;
				tmp = ykv;//前期量能低点
			}
			if(kv < ykv && isUp && upCount > 0){
				isUp = false;
				if(ykv/tmp >= d){
					range.addCount(upCount);
				}
				upCount = 0;
				
			}
			
			if(kv >= ykv && isUp){
				upCount ++;
			}
			if(kv < ykv && !isUp){
				downCount ++;
			}
			
			k = k.tomorrow();
		}
		if(range.getCount(7) >= 2 ){
			return true;
		}
		return false;
	}
	
	/**
	 * 是不是连续n天创新低
	 */
	public boolean isDecrementLow(int n){
		boolean flag = true;
		for(int i=0;i<n;i++){
			Bar refK = this.before(i);
			Bar yrefK = this.before(i+1);
			if(refK.getLow() > yrefK.getLow()){
				flag = false;
				break;
			}
		}
		return flag;
	}
	
	/**
	 * 是不是连续n天增量
	 */
	public boolean isIncrementVolume(int n){
		boolean flag = true;
		for(int i=0;i<n;i++){
			Bar refK = this.before(i);
			Bar yrefK = this.before(i+1);
			if(refK.getVolume() <= yrefK.getVolume()){
				flag = false;
				break;
			}
		}
		return flag;
	}
	
	/**
	 * 是不是连续n天缩量
	 */
	public boolean isDecrementVolume(int n){
		boolean flag = true;
		for(int i=0;i<n;i++){
			Bar refK = this.before(i);
			Bar yrefK = this.before(i+1);
			if(refK.getVolume() >= yrefK.getVolume()){
				flag = false;
				break;
			}
		}
		return flag;
	}
	
	/**
	 * 返回 今天的n天均量比n天前的n天均量的倍数
	 */
	public double getVolumeMA(int n) throws Exception {
		Bar kn = this.before(n);
		double vn = kn.getMA(Bar.Volumn, n);
		double v = this.getMA(Bar.Volumn, n);
		return v/vn;
	}
	
	public int getDayOnMaxOfVolumeMA(int n, int days) throws Exception{
		//通常倍数(times)设为1，指只要大于前面的量就行
		return this.getDayOnMaxOfVolumeMA(n, days, 1);
	}
	/**
	 * 返回  days内  今天的n天均量  大于(或者大times倍于) 前面每天k线的n天均量  的连续天数
	 * 
	 * 即：n日平均量能连续最高(或者大于times倍)保持天数
	 */
	public int getDayOnMaxOfVolumeMA(int n, int days, double times) throws Exception{
		Bar kvh = this.getMax(Bar.Volumn, 5, Bar.MA, 5);
		double v = kvh.getMA(Bar.Volumn, n);
		Bar k = kvh.before(n);
		int i = 0;
		do{
			k = k.before(1);
			double v1 = k.getMA(Bar.Volumn, n);
			if(v < v1 * times){
				break;
			}
			if(++i >= days-n)break;
		}while(true);
		
		return i;
	}
	
	/**
	 * 得到n日均线在days内的斜率(涨/跌幅)
	 */
	public double getSlopeOfMA(int n, int days) throws Exception {
		double ma = this.getMA(Bar.Close, n);
		Bar k = this.before(days);
		double ma1 = k.getMA(Bar.Close, n);
		return (ma - ma1)/ma1;
	}
	
	public boolean dateBefore(Bar k) throws Exception{
		Date d1 = ServiceUtils.sf_ymd2.parse(this.getDate());
		Date d2 = ServiceUtils.sf_ymd2.parse(k.getDate());
		return d1.before(d2);
	}
	public boolean dateBeforeOrEquals(Bar k) throws Exception{
		return this.dateEquals(k) || this.dateBefore(k);
	}
	public boolean dateAfter(Bar k) throws Exception{
		Date d1 = ServiceUtils.sf_ymd2.parse(this.getDate());
		Date d2 = ServiceUtils.sf_ymd2.parse(k.getDate());
		return d1.after(d2);
	}
	public boolean dateAfterOrEquals(Bar k) throws Exception{
		return this.dateEquals(k) || this.dateAfter(k);
	}
	public boolean dateEquals(Bar k){
		return this.getDate().equals(k.getDate());
	}
	
	/**
	 * 是不是前后n天的最高点
	 * @param n
	 * @return
	 */
	public boolean isHHK(int n){
		Bar high = this.after(n).getKByHHV(n*2+1);
		if(this.dateEquals(high)){
			return true;
		}else{
			high = this.after((int)(n*2-Math.round(n*0.618))).getKByHHV(n*2+1);
			if(this.dateEquals(high)){
				return true;
			}else{
				high = this.after((int)(n*2-Math.round(n*1.382))).getKByHHV(n*2+1);
				if(this.dateEquals(high)){
					return true;
				}
			}
			return false;
		}
	}
	
	/**
	 * 是不是前后n天的最低点
	 * @param n
	 * @return
	 */
	public boolean isLLK(int n){
		Bar high = this.after(n).getKByLLV(n*2+1);
		if(this.dateEquals(high)){
			return true;
		}else{
			high = this.after((int)(n*2-Math.round(n*0.618))).getKByLLV(n*2+1);
			if(this.dateEquals(high)){
				return true;
			}else{
				high = this.after((int)(n*2-Math.round(n*1.382))).getKByLLV(n*2+1);
				if(this.dateEquals(high)){
					return true;
				}
			}
			return false;
		}
	}
	
	
	/**
	 * ----------------------override-----------------------------
	 */
	private String string(){
		return "date:"+date+",open:"+open+",close:"+close+",high:"+high+",low:"+low;
	}
	@Override
	public String toString() {
		return string()+",before:"+(before==null?"null":before.string())+",after:"+(after==null?"null":after.string());
	}
	@Override 
    public Object clone() throws CloneNotSupportedException { 
        return super.clone(); 
    }

}
