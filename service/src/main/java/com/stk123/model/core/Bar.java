package com.stk123.model.core;

import com.stk123.common.util.collection.IntRange2IntMap;
import com.stk123.entity.StkKlineEntity;
import com.stk123.util.ServiceUtils;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import static com.stk123.model.core.Bar.EnumCalculationMethod.MA;
import static com.stk123.model.core.Bar.EnumValue.*;

@Data
@NoArgsConstructor
public class Bar implements Serializable, Cloneable {

	public enum EnumValue {
		CLOSE,C,
		OPEN,O,
		HIGH,H,
		LOW,L,
		VOLUME,V,
		AMOUNT,A,
		HSL
	}

	public enum EnumCalculationMethod {
		MA, SUM
	}

    private String code;
	private String date;
	private LocalDateTime ts;
	private double open;
	private double close;
	private double high;
	private double low;
	private double volume;
	private double amount;
	private double lastClose; //昨日收盘价
	private double change; //涨跌幅
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
	public Bar(StkKlineEntity kline){
	    this.setCode(kline.getCode());
		this.setDate(kline.getKlineDate());
		this.setOpen(kline.getOpen());
		this.setClose(kline.getClose());
		this.setHigh(kline.getHigh());
		this.setLow(kline.getLow());
		this.setVolume(kline.getVolumn());
		this.setAmount(kline.getAmount()==null?0:kline.getAmount());
		this.setChange(kline.getPercentage()==null?0:kline.getPercentage());
		this.setLastClose(kline.getLastClose());
		this.setHsl(kline.getHsl()==null?0:kline.getHsl());
	}

	public boolean after(Bar bar) {
		return this.date.compareTo(bar.getDate()) > 0;
	}
	public boolean before(Bar bar) {
		return this.date.compareTo(bar.getDate()) < 0;
	}

	//n=0是当天
	public Bar before(int n){
		Bar tmp = this;
		while(n-- > 0 && tmp != null && tmp.before != null){
			tmp = tmp.before;
		}
		return tmp;
	}
	public Bar before(String date){
		Bar tmp = this;
		while(date.compareTo(tmp.date) < 0 && tmp.before != null){
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

	public double getValue(EnumValue type) {
		switch (type) {
			case C:
			case CLOSE:
				return this.getClose();
			case O:
			case OPEN:
				return this.getOpen();
			case H:
			case HIGH:
				return this.getHigh();
			case L:
			case LOW:
				return this.getLow();
			case V:
			case VOLUME:
				return this.getVolume();
			case A:
			case AMOUNT:
				return this.getAmount();
			case HSL:
				return this.getHsl();
			default:
				throw new RuntimeException("type not support!");
		}
	}

	/*public double getValue(TypeValue typeValue) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
		return Double.parseDouble(BeanUtils.getProperty(this, typeValue.name().toLowerCase()));
	}*/

	/**
	 * @param valueType open/close/high/low/amount/volume/hsl
	 * @param calcType ma/sum
	 * @param days2, type2多少days2的ma/sum
	 * @return
	 * @throws Exception
	 */
	public double getValue(EnumValue typeValue, EnumCalculationMethod typeCalc, int days) {
		switch (typeCalc) {
			case MA:
				return this.getMA(typeValue, days);
			case SUM:
				return this.getSUM(typeValue, days);
			default:
				return this.getValue(typeValue);
		}

	}



	/*public interface Calc<T> {
		public T calc(Bar k, T d);
	}*/

	/*public <T> T getValue(int n, Calc<T> func){
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
	}*/

	public <R, T> R getValue(int n, Function<Bar, R> func) {
		R result;
		Bar k = this;
		while(true) {
			result = func.apply(k);
			if(n++ == 0){
				return result;
			}
			k = k.before;
			if(k == null)break;
		}
		return result;
	}

	public double getChange(int days, Function<Bar, Double> func) {
		double v = func.apply(this);
		double kv = this.getRef(days, func);
		return (v-kv)/kv;
	}

	/**
	 * 得到days前和今天 EnumValue的涨跌幅
	 */
	public double getChange(int days, EnumValue typeValue) {
		return this.getChange(days, bar -> bar.getValue(typeValue));
	}

	public double getAdjustedOfClose() {
		return this.getChange(1, C);
	}

	public double getAdjustedOfClose(int n) {
		return this.getChange(n, C);
	}


	public double getRef(int days, Function<Bar, Double> func) {
		return func.apply(this.before(days));
	}




	public double getMA(EnumValue type, int days) {
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

	public double getMA(int days, Function<Bar, Double> func) {
		double total = 0.0;
		int tmp = days;
		for(int i=0;i<days;i++){
			Bar k = this.before(i);
			if(k == null){
				return 0;
			}
			total += func.apply(k);
		}
		return ServiceUtils.numberFormat(total/tmp,2);
	}


	public double getSUM(EnumValue type, int days) {
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

	public Bar getMax(EnumValue typeValue, int days, EnumCalculationMethod typeCalc, int days2) {
		double max = 0.0;
		Bar ret = null;
		for(int i=0;i<days;i++){
			Bar k = this.before(i);
			if(k == null){
				continue;
			}
			double d = k.getValue(typeValue, typeCalc, days2);
			if(d > max){
				ret = k;
				max = d;
			}
		}
		return ret;
	}


	/**
	 * lowest
	 */
	public Bar getLowestBar(int n, EnumValue ev){
		double value = 0.0;
		Bar ret = this;
		Bar k = this;
		for(int i=0;i<n;i++){
			double tmp = k.getValue(ev);
			if(value > tmp || value == 0.0){
				value = tmp;
				ret = k;
			}
			k = k.before(1);
		}
		return ret;
	}
	public Double getLowest(int n, EnumValue ev){
		return this.getLowestBar(n, ev).getValue(ev);
	}


	/**
	 * highest
	 */
	public Bar getHighestBar(int n, EnumValue ev){
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
	public Double getHighest(int n, EnumValue ev){
		return this.getHighestBar(n, ev).getValue(ev);
	}



	//low的低点
	@Deprecated
	public double getLLV(int n){
		return this.getLowest(n, L);
	}

	//high的高点
	@Deprecated
	public double getHHV(int n){
		return this.getHighest(n, H);
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
		double ma = this.getMA(C, n);
		return (this.getClose() - ma)/ma * 100;
	}


	public double getBIAS4(int m, int n) throws Exception {
		return (this.getMA(C, m) - this.getMA(C, n)) / this.getMA(C, n) * 100;
	}





	/*public void setEne(Ene ene) {
		this.ene = ene;
	}*/

	public final double getEMA(EnumValue typeValue, int n)  {
		return this.getEMA(typeValue, n, 2);
	}

	public final double getEMA(EnumValue typeValue, final int n, final int m) {
		List<Double> list = new ArrayList<Double>();
		int j = 0;
		Bar k = this;
		int total = n * 10;//TODO　具体怎么定这个数字不清楚
		while(true){
			list.add(k.getValue(typeValue));
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

	public final double getEMA(final int n, final int m, Function<Bar, Double> func) {
		List<Double> list = new ArrayList<Double>();
		int j = 0;
		Bar k = this;
		int total = n * 10;//TODO　具体怎么定这个数字不清楚
		while(true){
			list.add(func.apply(k));
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

	public final double getEMA(final int n, Function<Bar, Double> func) {
		return this.getEMA(n, 2, func);
	}



	/*public static interface Calculator{
		public double calc(Bar k) throws Exception;
	}*/

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


	public static interface Condition{
		public boolean pass(Bar k) throws Exception;
	}

	/**
	 * 返回满足条件的Bar，否则返回null
	 * @param days
	 * @param predicate
	 * @return
	 * @throws Exception
	 */
	public Bar getKWithPredicate(int days, Predicate<Bar> predicate) throws Exception {
		Bar k = this;
		while(k != null){
			if(predicate.test(k)){
				return k;
			}
			k = k.before(1);
			if(days -- <= 1){
				break;
			}
		}
		return null;
	}

	/**
	 * 返回满足条件的Bar的个数
	 * @param days
	 * @param condition
	 * @return
	 * @throws Exception
	 */
	public int getKCountWithPredicate(int days, Predicate<Bar> predicate) throws Exception {
		Bar k = this;
		int cnt = 0;
		while(k != null){
			if(predicate.test(k)){
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
	public int getKCountWithPredicate(int days, int indent, Predicate<Bar> predicate) throws Exception {
		Bar k = this;
		int cnt = 0;
		while(k != null){
			if(predicate.test(k)){
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
		double dif = this.getEMA(C, s) - this.getEMA(C, l);
		double dea = this.getEMA(m, 2, k -> k.getEMA(C, s) - k.getEMA(C, l));
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
			Bar low = k.after(n).getLowestBar(2*n, EnumValue.L);
			//K low = this.getLLV(k.before(n).getDate(),k.after(n).getDate());
			if(k.getDate().equals(low.getDate())){
				lowPoints.add(k);
			}
		}
		return lowPoints;
	}

	//一品抄底-趋势线
	public double getTrend() {
		double trend = this.getMA(2, k -> {
			double sma_a4_6 = k.getEMA(6, 1, k1 -> {
					double llv21 = k1.getLLV(21);
					return ((k1.getClose() - llv21)/(k1.getHHV(21) - llv21)) * 100;
				});

			double sma_a4_5_2 = k.getEMA(5, 1, k2 -> k2.getEMA(5, 1, k3 -> {
				double llv21 = k3.getLLV(21);
				return ((k3.getClose() - llv21)/(k3.getHHV(21) - llv21)) * 100;
			}));

			return 3 * sma_a4_6 - 2 * sma_a4_5_2;
		});

		return trend;
	}
	//一品抄底-黑马线
	public double getHorse() {
		double horse = this.getEMA(5, 2, k -> k.getEMA(5, 1, k1 -> {
			double llv55 = k.getLLV(55);
			return ((k.getClose() - llv55)/(k.getHHV(55) - llv55)) * 100;
		}));
		return horse;
	}
	//一品抄底-MACD
	public double getYpcdMACD() {
		double horse = this.getHorse();
		double trend = this.getTrend();
		double macd = trend - horse;
		return macd;
	}
	//一品抄底-底部
	public boolean getYpcd() throws Exception {
		Bar yk = this.before(1);
		double a2 = (this.getHorse() - yk.getHorse())/yk.getHorse() * 100;
		double ya2 = this.getRef(1, k -> {
				Bar yk1 = k.before(1);
				return (k.getHorse() - yk1.getHorse())/yk1.getHorse() * 100;
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
				int cnt = this.getKCountWithPredicate(10, k -> {
						double m = k.getYpcdMACD();
						if(m >= 5)return true;
						return false;
					});
				int cnt2 = this.getKCountWithPredicate(10, k -> {
						double m = k.getYpcdMACD();
						if(m < 3 && m > -3)return true;
						return false;
					});

				if(cnt > 0 || cnt2 >= 6){
					cnt = this.getKCountWithPredicate(8, k -> {
							double m = k.getYpcdMACD();
							if(m < -5)return true;
							return false;
						});
					cnt += this.getKCountWithPredicate(20, k -> {
							double m = k.getYpcdMACD();
							if(m > 40)return true;
							return false;
						});
					//System.out.println(cnt);
					if(cnt == 0){
						cnt = this.getKCountWithPredicate(10, k -> {
								if(k.getYpcdMACD() > -3)return true;
								return false;
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
	public double getEpcd_Mrsj() {
		double y = this.getEMA(3, 1, k -> {
				double llv = k.getLLV(9);
				double hhv = k.getHHV(9);
				return (k.getClose() - llv) / (hhv - llv) * 100;
			});
		//D:=SMA(K,3,1);
		double z = this.getEMA(3, 1, k -> {
				return k.getEMA(3, 1, k1 -> {
						double llv = k1.getLLV(9);
						double hhv = k1.getHHV(9);
						return (k1.getClose() - llv) / (hhv - llv) * 100;
					});
			});
		//J:=3*K-2*D;
		double v = 3*y - 2*z;
		return v;
	}

	/**
	 * 二品抄底
	 */
	public boolean getEpcd() {
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
	public double getEpcd_Hxscm() {
		double z = this.getEMA(5, k -> {
				double va = (2 * k.getClose() + k.getHigh() + k.getLow()) / 4;
				double vb = k.getLLV(5);
				double vc = k.getHHV(5);
				return (va - vb) / (vc - vb) * 100;
			});
		return ServiceUtils.numberFormat(z, 2);
	}

	/**
	 * 二品抄底-红线下穿卖
	 * EMA(红线上穿买,3),COLORFFFF00;
	 */
	public double getEpcd_Hxxcm() {
		double z = this.getEMA(3, k -> {
				return k.getEpcd_Hxscm();
			});
		return ServiceUtils.numberFormat(z, 2);
	}

	/**
	 * 二品抄底-趋势
	 * 3*SMA((CLOSE-LLV(LOW,27))/(HHV(HIGH,27)-LLV(LOW,27))*100,5,1)-2*SMA(SMA((CLOSE-LLV(LOW,27))/(HHV(HIGH,27)-LLV(LOW,27))*100,5,1),3,1),COLOR00FFFF;
	 */
	public double getEpcd_Qs() {
		double x = this.getEMA(5, 1, k -> {
				double xa = k.getLLV(27);
				double xb = k.getHHV(27);
				return (k.getClose() - xa) / (xb - xa) * 100;
			});

		double y = this.getEMA(3, 1, k -> k.getEMA(5, 1, k1 -> {
				double xa = k1.getLLV(27);
				double xb = k1.getHHV(27);
				return (k1.getClose() - xa) / (xb - xa) * 100;
			}));
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
			double kv = k.getMA(V, 10);
			double ykv = yk.getMA(V, 10);
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
		double vn = kn.getMA(V, n);
		double v = this.getMA(V, n);
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
		Bar kvh = this.getMax(V, 5, MA, 5);
		double v = kvh.getMA(V, n);
		Bar k = kvh.before(n);
		int i = 0;
		do{
			k = k.before(1);
			double v1 = k.getMA(V, n);
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
		double ma = this.getMA(C, n);
		Bar k = this.before(days);
		double ma1 = k.getMA(C, n);
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
	public boolean isHighestBeforeAndAfter(int n){
		Bar high = this.after(n).getHighestBar(n*2+1, H);
		if(this.dateEquals(high)){
			return true;
		}else{
			high = this.after((int)(n*2-Math.round(n*0.618))).getHighestBar(n*2+1, H);
			if(this.dateEquals(high)){
				return true;
			}else{
				high = this.after((int)(n*2-Math.round(n*1.382))).getHighestBar(n*2+1, H);
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
	public boolean isLowestBeforeAndAfter(int n){
		Bar high = this.after(n).getLowestBar(n*2+1, L);
		if(this.dateEquals(high)){
			return true;
		}else{
			high = this.after((int)(n*2-Math.round(n*0.618))).getLowestBar(n*2+1, L);
			if(this.dateEquals(high)){
				return true;
			}else{
				high = this.after((int)(n*2-Math.round(n*1.382))).getLowestBar(n*2+1, L);
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
		return "date:"+date+",open:"+open+",close:"+close+",high:"+high+",low:"+low+",change:"+change+",lastClose:"+lastClose;
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
