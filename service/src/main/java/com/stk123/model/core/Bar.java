package com.stk123.model.core;

import cn.hutool.core.collection.CollUtil;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.stk123.common.CommonUtils;
import com.stk123.common.ml.KhivaUtils;
import com.stk123.common.util.AlgorithmUtils;
import com.stk123.common.util.collection.IntRange2IntMap;
import com.stk123.entity.StkKlineEntity;
import com.stk123.model.json.View;
import com.stk123.util.ServiceUtils;
import lombok.Data;
import lombok.ToString;
import lombok.extern.apachecommons.CommonsLog;
import org.elasticsearch.common.util.CollectionUtils;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;

import static com.stk123.model.core.Bar.EnumCalculationMethod.MA;
import static com.stk123.model.core.Bar.EnumValue.*;

@Data
@CommonsLog
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
    @JsonProperty("d")@JsonView(View.Default.class)
	private String date;
	private LocalDateTime ts;
    @JsonProperty("o")@JsonView(View.Default.class)
	private double open;
    @JsonProperty("c")@JsonView(View.Default.class)
	private double close;
    @JsonProperty("h")@JsonView(View.Default.class)
	private double high;
    @JsonProperty("l")@JsonView(View.Default.class)
	private double low;
    @JsonProperty("v")@JsonView(View.Default.class)
	private double volume;
    @JsonProperty("a")@JsonView(View.Default.class)
	private double amount;
    @JsonProperty("lc")@JsonView(View.Default.class)
	private double lastClose; //昨日收盘价
    @JsonProperty("p")@JsonView(View.Default.class)
	private double change; //涨跌幅
	private double hsl; //换手率

	private double peTtm;
	private double pbTtm;

	private Bar before;
	private Bar after;

	private MACD macd;
	private EMA ema;

	public Bar(){}


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

	public Bar before(){
		return this.getBefore();
	}
	public Bar yesterday(){
		return this.getBefore();
	}

	public Bar go(int n) {
	    if(n == 0){
	        return this;
        }else if(n > 0){
	        return before(n);
        }else{
	        return after(n);
        }
    }

	//n=0是当天
	public Bar before(int n){
		Bar tmp = this;
		while(n-- > 0 && tmp.before != null){
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
		while(n-- > 0 && tmp.after != null){
			tmp = tmp.after;
		}
		return tmp;
	}

	public Bar after() {
		return this.getAfter();
	}
	public Bar tomorrow(){
		return this.getAfter();
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


    public double getValue(int days, Function<Bar, Double> func) {
        return func.apply(this.go(days));
    }

	/*public double getValue(TypeValue typeValue) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
		return Double.parseDouble(BeanUtils.getProperty(this, typeValue.name().toLowerCase()));
	}*/


	public double getValue(int days, EnumValue typeValue, EnumCalculationMethod typeCalc) {
		switch (typeCalc) {
			case MA:
				return this.getMA(days, typeValue);
			case SUM:
				return this.getSUM(days, typeValue);
			default:
			    throw new RuntimeException("not support calc method.");
				//return this.getValue(typeValue);
		}

	}


	//感觉没什么用？
	public <R> R each(int n, Function<Bar, R> func) {
		R result;
		Bar k = this;
		while(true) {
			result = func.apply(k);
			if(n-- <= 0){
				return result;
			}
			k = k.before;
			if(k == null)break;
		}
		return result;
	}

	public double getChange(int days, Function<Bar, Double> func) {
		double v = func.apply(this);
		if(days == 0)return 0;
		double kv = this.getValue(days, func);
		return days > 0 ? (v-kv)/kv : (kv-v)/v;
	}

	/**
	 * 得到days前和今天 EnumValue的涨跌幅
	 */
	public double getChange(int days, EnumValue typeValue) {
		return this.getChange(days, bar -> bar.getValue(typeValue));
	}



	public double getMA(int days, EnumValue type) {
		return ServiceUtils.numberFormat(getSUM(days, type) / days,2);
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


	public double getSUM(int days, EnumValue type) {
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

	public Bar getLowestBar(int days, EnumValue typeValue, int days2, EnumCalculationMethod typeCalc) {
		double min = this.getValue(days2, typeValue, typeCalc);
		Bar ret = null;
		for(int i=0;i<days;i++){
			Bar k = this.before(i);
			if(k == null){
				break;
			}
			double d = k.getValue(days2, typeValue, typeCalc);
			if(d < min){
				ret = k;
				min = d;
			}
		}
		return ret;
	}

	/**
	 * highest
	 */
	public Bar getHighestBar(int n, EnumValue ev){
		double value = 0.0;
		Bar k = this;
		Bar ret = null;
		for(int i=0;i<n;i++){
			double tmp = k.getValue(ev);
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

    public Bar getHighestBar(int days, EnumValue typeValue, int days2, EnumCalculationMethod typeCalc) {
        double max = 0.0;
        Bar ret = null;
        for(int i=0;i<days;i++){
            Bar k = this.before(i);
            if(k == null){
				break;
            }
            double d = k.getValue(days2, typeValue, typeCalc);
            if(d > max){
                ret = k;
                max = d;
            }
        }
        return ret;
    }

    public Bar getHighestBar(int n, Function<Bar, Double> function){
        double value = 0.0;
        Bar k = this;
        Bar ret = null;
        for(int i=0;i<n;i++){
            double tmp = function.apply(k);
            if(value < tmp || value == 0.0){
                value = tmp;
                ret = k;
            }
            k = k.before(1);
        }
        return ret;
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

	public double getBIAS(int n) {
		double ma = this.getMA(n, C);
		return (this.getClose() - ma)/ma * 100;
	}


	public double getBIAS4(int m, int n) {
		return (this.getMA(m, C) - this.getMA(n, C)) / this.getMA(n, C) * 100;
	}





	/*public void setEne(Ene ene) {
		this.ene = ene;
	}*/

	public final double getEMA(int n, EnumValue typeValue)  {
		return this.getEMA(n, 2, typeValue);
	}

	public static class EMA{
	    private HashMap<String, Double> emas = new HashMap<>();
	    public Double getEMA(int n){
	        return emas.get(n + C.name());
        }
        public Double getEMA(int n, String s){
            return emas.get(n + s);
        }
        public void setEMA(int n, String s, double value){
	        emas.put(n + s, value);
        }
    }

    public double getEMA(int n){
	    return this.getEMA(n, 2, C);
    }

    public final double getEMA(final int n, final int m, EnumValue typeValue) {
        if(ema != null){
            Double d = ema.getEMA(n, typeValue.name());
            if(d != null) {
                return d;
            }
        }

        Bar bar = this.before(n * 10);
        if(bar != null) {
            double emaValue = bar.getValue(typeValue);
            while (true) {
                bar = bar.after();
                if(bar == null){
                    break;
                }
                // EMA（12） = 前一日EMA（12） * 11/13 + 今日收盘价 * 2/13
                emaValue = emaValue * (n-1d) / (n+1d) + bar.getValue(typeValue) * m / (n+1d);
                if(bar.ema == null){
                    bar.ema = new EMA();
                }
                bar.ema.setEMA(n, typeValue.name(), emaValue);
            }
        }
        return this.ema.getEMA(n, typeValue.name());
    }

    //这个方法没有测试过。。。
	public final double getEMA(final int n, final int m, Function<Bar, Double> func) {
        if(ema != null){
            Double d = ema.getEMA(n, func.getClass().getName());
            if(d != null) {
                return d;
            }
        }

        Bar bar = this.before(n * 10);
        if(bar != null) {
            double emaValue = func.apply(bar);
            while (true) {
                bar = bar.after();
                if(bar == null){
                    break;
                }
                // EMA（12） = 前一日EMA（12） * 11/13 + 今日收盘价 * 2/13
                emaValue = emaValue * (n-1d) / (n+1d) + func.apply(bar) * m / (n+1d);
                if(bar.ema == null){
                    bar.ema = new EMA();
                }
                bar.ema.setEMA(n, func.getClass().getName(), emaValue);
            }
        }
        return this.ema.getEMA(n, func.getClass().getName());
	}

	public final double getEMA(final int n, Function<Bar, Double> func) {
		return this.getEMA(n, 2, func);
	}

    @ToString
    public static class MACD{
        public double dif;
        public double dea;
        public double macd;
    }

    /**
     * DIF:EMA(CLOSE,SHORT)-EMA(CLOSE,LONG);
     * DEA:EMA(DIF,MID);
     * MACD:(DIF-DEA)*2,COLORSTICK;
     * @return
     */
    public MACD getMACD(final int s,final int l, int m) {
        if(macd != null){
            return macd;
        }

        double emas = 0;
        double emal = 0;
        double dif = 0;
        double dea = 0;

        Bar bar = this.before(s * 10);
        if(bar != null) {
            emas = emal = bar.getValue(C);
            while (true) {
                bar = bar.after();
                if(bar == null){
                    break;
                }
                // EMA（12） = 前一日EMA（12） * 11/13 + 今日收盘价 * 2/13
                emas = emas * (s-1d) / (s+1d) + bar.getValue(C) * 2d / (s+1d);
                // EMA（26） = 前一日EMA（26） * 25/27 + 今日收盘价 * 2/27
                emal = emal * (l-1d) / (l+1d) + bar.getValue(C) * 2d / (l+1d);

                // DIF = EMA（12） - EMA（26） 。
                // 今日DEA = （前一日DEA * 8/10 + 今日DIF X 2/10）
                // 用（DIF-DEA）*2即为MACD柱状图。
                dif = emas - emal;
                dea = dea * (m-1d) / (m+1d) + dif * 2d / (m+1d);
                macd = new MACD();
                macd.dif = dif;
                macd.dea = dea;
                macd.macd = (dif - dea) * 2d;
                bar.macd = macd;
            }
        }
        return this.macd;
    }

    public MACD getMACD() {
        return getMACD(12, 26, 9);
    }

    /**
     * MACD金叉，得到day天bar之前第一个macd金叉bar
     * @return
     */
    public Bar getMACDUpperForkBar(int day){
        Bar bar = this.before(day);
        while (bar != null){
            MACD macd = bar.getMACD();
            //System.out.println(bar.date+", macd:"+macd);
            if(macd.dif > macd.dea){
                Bar before = bar.before();
                MACD macdBefore = before.getMACD();
                if(macdBefore.dif < macdBefore.dea){
                    return bar;
                }
            }
            bar = bar.before();
        }
        return null;
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

	/**
	 * 返回满足条件的Bar (包括today)，否则返回null
	 */
	public Bar getBar(int days, Predicate<Bar> predicate) {
		Bar k = this;
		while(k != null){
			if(predicate.test(k)){
				return k;
			}
			if(--days < 1){
				break;
			}
            k = k.before(1);
		}
		return null;
	}
    public Bar getBarExcludeToday(int days, Predicate<Bar> predicate) {
        return this.before().getBar(days, predicate);
    }

	/**
	 * 返回满足条件的Bar的个数 (包括today)
	 */
	public int getBarCount(int days, Predicate<Bar> predicate) {
		Bar k = this;
		int cnt = 0;
		while(k != null){
			if(predicate.test(k)){
				cnt ++;
			}
			if(--days < 1){
				break;
			}
			k = k.before(1);
		}
		return cnt;
	}

    public int getBarCountExcludeToday(int days, Predicate<Bar> predicate) {
        return this.before().getBarCount(days, predicate);
    }

	/**
	 * @param days
	 * @param indent 当满足条件时，k线往前进的天数
	 * @param predicate
	 */
	public int getBarCount(int days, int indent, Predicate<Bar> predicate) {
		Bar k = this;
		int cnt = 0;
		while(k != null){
			if(predicate.test(k)){
				cnt ++;
				k = k.before(indent);
			}
			if(--days < 1){
				break;
			}
			k = k.before(1);
		}
		return cnt;
	}


	//返回合并K线，n为合并几天的k线
	public Bar getBarMerge(int days, int n){
		Bar k = this;
		Bar after = null;
		Bar ret = null;
		while(k != null){
		    days = days - n;
			if(days < -n){
				break;
			}
			double high = k.getHighest(n, H);
			double low = k.getLowest(n, L);
			Bar current = new Bar();
			current.setCode(k.getCode());
			current.setDate(k.before(n-1).getDate());
			current.setHigh(high);
			current.setLow(low);
			current.setOpen(k.before(n-1).getOpen());
			current.setClose(k.getClose());
			current.setVolume(k.getSUM(n, V));
			current.setAmount(k.getSUM(n, A));
			if(after != null){
				current.setAfter(after);
				after.setBefore(current);
				after.setLastClose(current.getClose());
				after.setChange((after.getClose()-current.getClose())/current.getClose()*100);
			}else{
				ret = current;
			}
			after = current;
			k = k.before(n);
		}
		return ret;
	}


	//从当前Bar开始向前比较n个Bar，返回相似的个数
	public int similar(int n, Bar bar, BiPredicate<Bar, Bar>... biPredicates){
		int cnt = 0;
		int m = 0;
		while(n >= m){
			Bar a = this.before(m);
			Bar b = bar.before(m);

			if(a==null || b==null){
				break;
			}
			boolean similar = true;
			for(BiPredicate<Bar, Bar> biPredicate : biPredicates){
				if(!biPredicate.test(a, b)){
					similar = false;
					break;
				}
			}
			if(similar) {
				cnt++;
			}
			m++;
		}
		return cnt;
	}

	//定义相似Bar
	public static BiPredicate<Bar, Bar> getSimilarBar(double delta){
		return (a, b) -> {
			if(a.getChange() == b.getChange()){
				return true;
			}
			return Math.abs(a.getChange() - b.getChange()) <= delta;
		};
	}

	public int similarBar(int n, Bar bar, double d){
		return this.similar(n, bar, Bar.getSimilarBar(d));
	}

	//TODO 相似高低点
	public int similarHighLowPoint(int n, Bar bar) {
		return 0;
	}

	public double similarMass(int n, Bar bar, int length){
		List<Double> close1 = this.map(length+n, bar1 -> bar1.getMA(5, EnumValue.C));
		List<Double> close2 = bar.map(length, bar1 -> bar1.getMA(5, EnumValue.C));
		double[] tss = close1.stream().mapToDouble(Double::doubleValue).toArray();
		double[] query = close2.stream().mapToDouble(Double::doubleValue).toArray();

		if(tss.length != query.length){
		    return Double.MAX_VALUE;
        }

		double[] distances = KhivaUtils.mass(tss, query);

//		System.out.println(Arrays.toString(distances));
		int index = KhivaUtils.getIndexOfMin(distances);
//		System.out.println(index);
//		System.out.println(this.before(index).getDate());
//		System.out.println(Arrays.toString(KhivaUtils.getIndexesOfMin(distances, 3)));
		return distances[index];
	}

	//n=1 表示返回当前k线计算结果
	public <R> List<R> map(int n, Function<Bar, R> function){
		List<R> list = new ArrayList<>();
		Bar k = this;
		while(true) {
			if(n-- <= 0){
				return list;
			}
			list.add(function.apply(k));
			k = k.before();
			if(k == null)break;
		}
		return list;
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


	public List<Bar> getHistoryLowPoint(int days, int n) {
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
    public List<Bar> getHistoryHighPoint(int days, int n) {
        List<Bar> highPoints = new ArrayList<Bar>();
        Bar start = this.before(days);
        Bar end = this.before(n);
        Bar k = start;
        while((k = k.after(1)) != null){
            if(k.getDate().compareTo(end.getDate()) >= 0){
                break;
            }
            Bar high = k.after(n).getHighestBar(2*n, EnumValue.H);
            if(k.getDate().equals(high.getDate())){
                highPoints.add(k);
            }
        }
        return highPoints;
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
	public boolean getYpcd() {
		Bar yk = this.before(1);
		double a2 = (this.getHorse() - yk.getHorse())/yk.getHorse() * 100;
		double ya2 = this.getValue(1, k -> {
				Bar yk1 = k.before(1);
				return (k.getHorse() - yk1.getHorse())/yk1.getHorse() * 100;
			});
		if((a2 <= -10 || ya2 <= -10) && a2 > ya2)return true;
		return false;
	}

	public boolean getYpcd2() {
		double macd = this.getYpcdMACD();
		//System.out.println(k.getDate()+"="+macd);
		if(macd >= 4){
			Bar yk = this.before(1);
			double ymacd = yk.getYpcdMACD();
			if(ymacd < 4 && ymacd > -2){
				int cnt = this.getBarCount(10, k -> {
						double m = k.getYpcdMACD();
						if(m >= 5)return true;
						return false;
					});
				int cnt2 = this.getBarCount(10, k -> {
						double m = k.getYpcdMACD();
						if(m < 3 && m > -3)return true;
						return false;
					});

				if(cnt > 0 || cnt2 >= 6){
					cnt = this.getBarCount(8, k -> {
							double m = k.getYpcdMACD();
							if(m < -5)return true;
							return false;
						});
					cnt += this.getBarCount(20, k -> {
							double m = k.getYpcdMACD();
							if(m > 40)return true;
							return false;
						});
					//System.out.println(cnt);
					if(cnt == 0){
						cnt = this.getBarCount(10, k -> {
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
	public boolean hasHighVolumn(double d) {
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
			double kv = k.getMA(10, V);
			double ykv = yk.getMA(10, V);
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
	public double getVolumeMA(int n) {
		Bar kn = this.before(n);
		double vn = kn.getMA(n, V);
		double v = this.getMA(n, V);
		return v/vn;
	}
	
	public int getDayOnMaxOfVolumeMA(int n, int days) {
		//通常倍数(times)设为1，指只要大于前面的量就行
		return this.getDayOnMaxOfVolumeMA(n, days, 1);
	}
	/**
	 * 返回  days内  今天的n天均量  大于(或者大times倍于) 前面每天k线的n天均量  的连续天数
	 * 
	 * 即：n日平均量能连续最高(或者大于times倍)保持天数
	 */
	public int getDayOnMaxOfVolumeMA(int n, int days, double times) {
		Bar kvh = this.getHighestBar(5, V, 5, MA);
		double v = kvh.getMA(n, V);
		Bar k = kvh.before(n);
		int i = 0;
		do{
			k = k.before(1);
			double v1 = k.getMA(n, V);
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
	public double getSlopeOfMA(int n, int days) {
		double ma = this.getMA(n, C);
		Bar k = this.before(days);
		double ma1 = k.getMA(n, C);
		return (ma - ma1)/ma1;
	}
	
	public boolean dateBefore(Bar k) {
        Date d1 = CommonUtils.parseDate(this.getDate());
        Date d2 = CommonUtils.parseDate(k.getDate());
		return d1.before(d2);
	}
	public boolean dateBeforeOrEquals(Bar k) {
		return this.dateEquals(k) || this.dateBefore(k);
	}
	public boolean dateAfter(Bar k) {
		Date d1 = CommonUtils.parseDate(this.getDate());
		Date d2 = CommonUtils.parseDate(k.getDate());
		return d1.after(d2);
	}
	public boolean dateAfterOrEquals(Bar k) {
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

	public boolean isBreakTrendLine(int m, int n, double percent){
        List<Bar> ks = this.getHistoryHighPoint(m, n);
		for(Bar k : ks){
			print("=="+k.getDate());
		}
        Bar hk = null;
        Bar lk = null;
        if(ks != null && ks.size() >= 2){
            List list = zhuhe(ks);
            for(int i = list.size()-1;i>=0;i--){
                List<Bar> ksTmp = (List)list.get(i);
                hk = ksTmp.get(0);
                lk = ksTmp.get(1);
                print("i========================="+i+",hk="+hk.getDate()+",lk="+lk.getDate());
                if(hk.getHigh() > lk.getHigh() * (1+percent)){
                    int days = this.getDaysBetween(hk.getDate(), lk.getDate());
                    double d = hk.getHigh() - lk.getHigh();
                    print("d="+d+",days="+days);
                    if(d < 0) continue;
                    double decreasePerDay = d/hk.getHigh()/days;
                    print("decreasePerDay="+decreasePerDay);
                    int days2 = this.getDaysBetween(lk.getDate(), date);
                    print("day2=="+days2+",hk="+hk.getDate()+",lk="+lk.getDate()+",date="+date);
                    double trendLineValue = lk.getHigh()*Math.pow(1-decreasePerDay, days2)*0.97;
                    double ytrendLineValue = lk.getHigh()*Math.pow(1-decreasePerDay, days2-1)*0.97;
                    Bar curK = this.before(date);
                    print(curK.getDate()+","+curK.getHigh()+">="+trendLineValue+","+curK.before(1).getHigh()+"<"+ytrendLineValue);
                    if(curK.getClose() >= trendLineValue && curK.before(1).getHigh() < ytrendLineValue){
                        boolean flag = false;
                        final String lkDate = lk.getDate();
                        for(Bar bar : ks){
                            if(hk.dateBefore(bar) && !bar.getDate().equals(lkDate) ){
                                print("hk=="+hk.getDate()+",lk=="+lk.getDate()+",bar="+bar.getDate());
                                int ds = bar.getDaysBetween(hk.getDate(), bar.getDate());
                                double tv = hk.getHigh()*Math.pow(1-decreasePerDay, ds)*0.97;
                                if(bar.getHigh() > tv){
                                    print(hk.getDate()+","+bar.getDate()+",kkkkkkkkkk="+bar.getDate());
                                    flag = true;
                                    break;
                                }
                            }
                        }
                        if(flag)continue;
                        print("hk="+hk.getDate()+",lk="+lk.getDate());
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private String printDate = null;//"20210419";
    public void print(Object obj){
        if(this.getDate().equals(printDate)){
            System.out.println(obj);
        }
    }

    public int getDaysBetween(String startDate, String endDate) {
        int days = 0;
        if(startDate.compareTo(endDate) > 0){
            String tmp = startDate;
            startDate = endDate;
            endDate = tmp;
        }
        Bar tmpK = this.before(startDate);
        do{
            if(tmpK.getDate().compareTo(endDate) >= 0){
                break;
            }
            days ++;
            Bar tmpK2 = tmpK.after(1);
            if(tmpK2.getDate().equals(tmpK.getDate())){
                break;
            }
            tmpK = tmpK2;
        }while(true);
        return days;
    }
    //排列组合
    private List zhuhe(List<Bar> ks){
        int[] a = new int[ks.size()];
        for(int i=0;i<ks.size();i++){
            a[i] = i;
        }
        List result = new ArrayList();
        List list = AlgorithmUtils.zuhe(a, 2);
        for (int i = 0; i < list.size(); i++) {
            List tmp = new ArrayList();
            int[] temp = (int[]) list.get(i);
            //System.out.println();
            for (int j = 0; j < temp.length; j++) {
                tmp.add(ks.get(temp[j]));
                //System.out.print(temp[j]);
            }
            result.add(tmp);
        }
        return result;
    }

    //十字星
	public boolean isCrossstar(double d){
        double p = this.getChange();
        if(Math.abs(p) <= Math.abs(d)){
            if(this.getLow() < this.getOpen() && this.getLow() < this.getClose()
                    && this.getHigh() > this.getOpen() && this.getHigh() > this.getClose()){
                return true;
            }
            return false;
        }
        return false;
    }

	/**
	 * ----------------------override-----------------------------
	 */
	private String string(){
		return "code:"+code+", date:"+date+", o:"+open+", c:"+close+", h:"+high+", l:"+low+", p:"+change+", lc:"+lastClose+", v:"+volume+", a:"+amount;
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
