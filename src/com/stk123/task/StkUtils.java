package com.stk123.task;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.math.BigDecimal;
import java.sql.Connection;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.stk123.tool.db.sql.DBUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang.time.FastDateFormat;

import com.stk123.bo.StkEarningsNotice;
import com.stk123.bo.StkImportInfo;
import com.stk123.bo.StkRestricted;
import com.stk123.bo.cust.StkFnDataCust;
import com.stk123.model.Index;
import com.stk123.model.IndexUtils;
import com.stk123.tool.baidu.BaiduSearch;
import com.stk123.tool.db.util.CloseUtil;
import com.stk123.tool.db.util.DBUtil;
import com.stk123.tool.html.HtmlA;
import com.stk123.tool.html.HtmlTable;
import com.stk123.tool.html.HtmlTd;
import com.stk123.tool.html.HtmlTr;
import com.stk123.tool.util.collection.IntRange;
import com.stk123.web.StkConstant;


@SuppressWarnings({ "unchecked", "rawtypes" })
public class StkUtils {

    public final static SimpleDateFormat sf_ymd = new SimpleDateFormat("yyyy-MM-dd");
    public final static SimpleDateFormat sf_ymd2 = new SimpleDateFormat("yyyyMMdd");
    public final static FastDateFormat fd_ymd2 = FastDateFormat.getInstance("yyyyMMdd");
    public final static SimpleDateFormat sf_yyyyMMdd = new SimpleDateFormat("yyyyMMdd");
    public final static SimpleDateFormat sf_ymd3 = new SimpleDateFormat("_yyyy_MM_dd");
    public final static SimpleDateFormat sf_hh = new SimpleDateFormat("HH");
    public final static SimpleDateFormat sf_yyyy = new SimpleDateFormat("yyyy");
    public final static SimpleDateFormat sf_yyyyMM = new SimpleDateFormat("yyyyMM");
    public final static SimpleDateFormat sf_yyyy_MM = new SimpleDateFormat("yyyy-MM");
    public final static SimpleDateFormat sf_MMdd = new SimpleDateFormat("MMdd");
    public final static SimpleDateFormat sf_MM_dd = new SimpleDateFormat("MM-dd");
    public final static SimpleDateFormat sf_ymd4 = new SimpleDateFormat("yyyy-M-d");
    public final static SimpleDateFormat sf_ymd5 = new SimpleDateFormat("yyyy.MM.dd");
    public final static SimpleDateFormat sf_ymd6 = new SimpleDateFormat("MMM d, yyyy",Locale.US);
    public final static SimpleDateFormat sf_ymd7 = new SimpleDateFormat("MMM d yyyy",Locale.US);
    public final static SimpleDateFormat sf_ymd8 = new SimpleDateFormat("MMM d",Locale.US);
    public final static SimpleDateFormat sf_ymd9 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public final static SimpleDateFormat sf_ymd10 = new SimpleDateFormat("yyyy年MM月dd日");
    public final static SimpleDateFormat sf_ymd11 = new SimpleDateFormat("dd/MM/yyyy");
    public final static SimpleDateFormat sf_ymd12 = new SimpleDateFormat("yyyyMMddHHmmss");
    public final static SimpleDateFormat sf_ymd13 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    public final static SimpleDateFormat sf_ymd14 = new SimpleDateFormat("MM/dd/yyyy");
    public final static SimpleDateFormat sf_ymd15 = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
    public final static SimpleDateFormat sf_ymd16 = new SimpleDateFormat("yy-MM-dd");

    public final static String PATTERN_1 = "[0-9]*(\\.?)[0-9]+(亿|万)";
    public final static String PATTERN_YYYYMMDD_HHMM_CHINESE = "\\d{4}年\\d{2}月\\d{2}日\\s*\\d{2}:\\d{2}";
    public final static String PATTERN_YYYY_MM_DD = "\\d{4}-\\d{2}-\\d{2}";

    public final static Date now = new Date();

    public final static int YEAR = StkUtils.getCurYear();
    public final static String MMDD_Q1 = "0331";
    public final static String MMDD_Q2 = "0630";
    public final static String MMDD_Q3 = "0930";
    public final static String MMDD_Q4 = "1231";
    public final static String[] MMDD_Q321 = new String[]{MMDD_Q3,MMDD_Q2,MMDD_Q1};
    //public final static String[] MMDD_Q4321 = new String[]{MMDD_Q4,MMDD_Q3,MMDD_Q2,MMDD_Q1};

    public final static int[] MONTHS = {1,2,3,4,5,6,7,8,9,10,11,12};

    public static void main(String[] args) throws Exception {
        System.out.println(now);
        System.out.println(numberFormat(2.342,2));
        System.out.println(StringUtils.substring("20130201",4));
        System.out.println(sf_ymd6.parse("Sep 28, 2013"));
        Date date = StkUtils.addDay(StkUtils.now, -30);
        System.out.println(StkUtils.get(now, Calendar.MONTH));
		/*System.out.println(StkUtils.getLastQuarter(StkUtils.getToday()));

		Calendar cal_start = Calendar.getInstance();
		cal_start.setTime(now);
		System.out.println(cal_start.get(Calendar.DAY_OF_WEEK));

		System.out.println(StkUtils.calcCAGR(0.96, 1.73,2));

		System.out.println(Math.pow(1+StkUtils.calcCAGR(0.96,  1.73,2), 3));*/
        System.out.println(StkUtils.percentigeGreatThan("183.92～155.95%"));
        System.out.println(StkUtils.percentigeGreatThan("-183.92%～-155.95%"));


        System.out.println(NumberUtils.isNumber("-16"));

        System.out.println(StringUtils.substringBetween("# 交易体系#", "#", "#"));

        Pattern pattern=Pattern.compile("#[\u4e00-\u9fa5 /]+#");
        //通过match（）创建Matcher实例
        Matcher matcher=pattern.matcher("趋势#交易体系# #投研# 震荡做潜伏，趋势做突破 #行业# 香料上市公司");
        while (matcher.find())//查找符合pattern的字符串
        {
            System.out.println("The result is here :" +
                    matcher.group() + "\n" + "It starts from "
                    + matcher.start() + " to " + matcher.end() + ".\n");
        }
        System.out.println(getLabels("趋势#交易体系# #toread# 震荡做潜伏，趋势做突破 #行业# 香料上市公司"));

		/*StkLabel label = new StkLabel();
		label.setId(11);
		label.setName("test");
		PO po = new JdbcUtils.POBase();
		po.setUpdateColumn("name");
		po.putUpdateColumn("insert_time", "sysdate");
		po.setConditionColumn("name", "fankai");*/
        //JdbcUtils.insert(null, label, po);

        //JdbcUtils.update(null, label, po);


		/*StkKline kline = new StkKline();
		kline.setCode("USDHKD");
		kline.setKlineDate("20140920");
		kline.setOpen(100.0);
		kline.setClose(200.0);
		kline.setHigh(300.0);
		kline.setLow(50.0);
		kline.setCloseChange(0.3);
		po = new JdbcUtils.POBase();
		po.setPK("code");*/
        //JdbcUtils.update(null, kline, po);

        System.out.println(trim(" 北京市 "));

        NumberFormat nf = NumberFormat.getInstance();
        nf.setMinimumFractionDigits(2);
        String s = nf.format(2.0);
        System.out.println(s);

        System.out.println("=="+getMatchStrings("票交易总额÷定价基准日前 20 个交易日股票交易总量）的 90%，即 8880 元/股。",
                "\\d+(\\.?)(\\d+)?( ?)(元)"));

        System.out.println(getTodayUs());

        System.out.println("kkk="+StkUtils.getNumberFromString("\r募集资金总额 63,705.53 本季度投入募集资金总额 523.1 "));
        System.out.println("kkk="+StkUtils.getNumberFromString("亨通光电 [600487]"));

        System.out.println("vv="+StkUtils.getMatchString("中证网&nbsp;&nbsp;2015年12月25日  12:00&nbsp;&nbsp;4条相同新闻>>", "\\d{4}年\\d{2}月\\d{2}日\\s*\\d{2}:\\d{2}"));

        System.out.println(StkUtils.getMatchString("停牌终止收购4个多月！突然", "(中止|终止).*(重组|收购|并购)"));

        System.out.println(StkUtils.getMatchString("帝龙新材上修净业绩预告 净利大增150%—180%", "上修.{0,2}业绩预告"));

        System.out.println(StkUtils.getDayOfWeek("20161015"));

        System.out.println(StkUtils.addDayOfWorking(now, -6));

        System.out.println(StkUtils.getMatchString("非公开发行股票发行情况报告书", "非公开发行.{0,15}(报告|公告)"));
        System.out.println(StkUtils.getMatchString("关于公司发行股份及支付现金购买资产并募集配套资金获得中国证券监督管理委员会正式批复的公告", "获得中国(证监会|证券监督).{0,10}(核准|审核|批复)"));

        String str = StkUtils.getMatchString("利润亏损:433.71万元-533.79万元，","-?[0-9]*(\\.?)[0-9]*万元.{1}-?[0-9]*(\\.?)[0-9]*万元");
        System.out.println(StringUtils.substringBefore(str, "万元"));
        System.out.println(StkUtils.getNumberFromString(StringUtils.substringAfter(str, "万元")));
        System.out.println(StkUtils.getNextQuarter("20170115"));

        System.out.println(StkUtils.getMatchAllStrings("关于公司发行股份及支付现金购买资产并募集配套资金获得中国证券监督管理委员会正式批复的公告", "配套资金|中国证券"));
        System.out.println(StkUtils.getMatchStringAndWrapByString("关于公司发行股份及支付现金购买资产并募集配套资金获得中国证券监督管理委员会正式批复的公告", "配套资金|中国证券","<start>","<end>"));

        System.out.println(StkUtils.formatDate(StkUtils.addDay(now, -360),StkUtils.sf_yyyyMMdd));

        System.out.println(StkUtils.getMatchString("43{ps_ntile!='test'}test234</sql>", "\\{[^}]*\\}"));
        System.out.println(StkUtils.numberFormat2Digits(-5523649.375));
        printStackTrace();

        System.out.println(StringUtils.length("嗯，那是我以前写的。不过，对于吕建明这样的企业家，我认为钱对于他们早已要无有任何意义了，当把做事业放成第一位了。分红不分红倒在其次，上一次的高价增发失败，估计对公司形象确实也受到一定的影响。我感觉，吕倒是一个有情怀的人，不过，对自己的公司，或许也有时像对待自己的孩子一样，喜欢过了估值难免高些。但总体来讲，我认为并无大碍，一切还是按公司的公开信息和数据说话吧。"));

        //System.out.println(StkUtils.getSysClipboardText());

        InitialData.updateStkStaticInfo(DBUtil.getConnection(), "002038");
    }

    public static void printStackTrace(){
        StackTraceElement[] se = Thread.currentThread().getStackTrace();
        if(se != null){
            for(int i = se.length-1; i >= 0; i--){
                if("printStackTrace".equalsIgnoreCase(se[i].getMethodName()))break;
                if(!se[i].getClassName().contains("com.stk123"))continue;
                System.out.println("  "+se[i].getClassName()+"."+se[i].getMethodName()+"("+se[i].getLineNumber()+")");
                //System.out.println(stackElements[i].getFileName());
            }
        }
    }

    public static Object nvl(Object o, String replace, Object ret){
        return o==null?replace:ret;
    }
    public static Object nvl(Object o, String replace){
        return nvl(o, replace, o);
    }

    public static String trim(String s){
        return s.replaceAll("^\\s*|\\s*$", "");
    }

    private final static String TEXT_LABEL_PATTERN = "#[\u4e00-\u9fa5\\w]+#";

    public static Set<String> getLabels(String text){
        return StkUtils.getMatchStrings(text, TEXT_LABEL_PATTERN);
    }

    public static String getMatchString(String text, String pattern){
        if(text == null || text.trim().length()==0)return text;
        Pattern pat = Pattern.compile(pattern);
        Matcher matcher = pat.matcher(text);
        while (matcher.find())//查找符合pattern的字符串
        {
            return matcher.group();
        }
        return null;
    }

    public static String getMatchString(String text, String[] patterns){
        for(String pattern : patterns){
            String result = null;
            if((result = getMatchString(text, pattern)) != null){
                return result;
            }
        }
        return null;
    }

    public static Set<String> getMatchStrings(String text, String pattern){
        Set<String> labels = new HashSet<String>();
        if(text == null || text.trim().length()==0)return labels;
        Pattern pat = Pattern.compile(pattern);
        Matcher matcher = pat.matcher(text);
        while (matcher.find()){//查找符合pattern的字符串
            labels.add(matcher.group());
        }
        return labels;
    }

    public static List<String> getMatchAllStrings(String text, String pattern){
        List<String> labels = new ArrayList<String>();
        if(text == null || text.trim().length()==0)return labels;
        Pattern pat = Pattern.compile(pattern);
        Matcher matcher = pat.matcher(text);
        while (matcher.find()){//查找符合pattern的字符串
            labels.add(matcher.group());
            //System.out.println("The result:" + matcher.group() + ", starts from " + matcher.start() + " to " + matcher.end());
        }
        return labels;
    }

    public static String getMatchStringAndReplace(String text, String pattern,int offset, String insertString){
        if(text == null || text.trim().length()==0)return text;
        StringBuilder sb = new StringBuilder(text);
        Pattern pat = Pattern.compile(pattern);
        Matcher matcher = pat.matcher(text);
        List<Integer> pos = new ArrayList<Integer>();
        while (matcher.find())//查找符合pattern的字符串
        {
            pos.add(matcher.start());
        }
        if(pos.size() > 0){
            Collections.reverse(pos);
            for(Integer i : pos){
                sb.replace(i, offset+1, insertString);
            }
        }
        return sb.toString();
    }

    public static String getMatchStringAndReplace(String text, String pattern,String insertString){
        if(text == null || text.trim().length()==0)return text;
        StringBuilder sb = new StringBuilder(text);
        Pattern pat = Pattern.compile(pattern);
        Matcher matcher = pat.matcher(text);
        List<IntRange> pos = new ArrayList<IntRange>();
        while (matcher.find())//查找符合pattern的字符串
        {
            pos.add(new IntRange(matcher.start(), matcher.end()));
        }
        if(pos.size() > 0){
            Collections.reverse(pos);
            for(IntRange range : pos){
                sb.replace(range.getLower(), range.getUpper(), insertString);
            }
        }
        return sb.toString();
    }

    public static String getMatchStringAndWrapByString(String text, String pattern,String startString, String endString){
        if(text == null || text.trim().length()==0)return text;
        StringBuilder sb = new StringBuilder(text);
        Pattern pat = Pattern.compile(pattern);
        Matcher matcher = pat.matcher(text);
        List<IntRange> pos = new ArrayList<IntRange>();
        while (matcher.find())//查找符合pattern的字符串
        {
            pos.add(new IntRange(matcher.start(), matcher.end()));
        }
        if(pos.size() > 0){
            Collections.reverse(pos);
            for(IntRange range : pos){
                sb.insert(range.getUpper(), endString);
                sb.insert(range.getLower(), startString);
            }
        }
        return sb.toString();
    }

    public static Set<String> extractCodeFromText(String text){
        return IndexUtils.extractCodeFromText(text);
    }

    public static class Pair {
        public Pair(double f, double s) {
            min = f;
            max = s;
        }

        public double getMin() {
            return min;
        }

        public double getMax() {
            return max;
        }

        private double min;
        private double max;
    }

    public static Pair minmax(double[] values) {
        double min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;
        for (double v : values) {
            if (min > v)
                min = v;
            if (max < v)
                max = v;
        }
        return new Pair(min, max);
    }

    public static String getNumberFromString(String str){
        str = StringUtils.replace(str, ",", "");
        Pattern p = Pattern.compile("-?[0-9]*(\\.?)[0-9]*");
        Matcher m = p.matcher(str);
        while (m.find()) {
            String s = m.group();
            if(s != null && !"".equals(s)){
                return s;
            }
        }
        return null;

		/*str = StringUtils.replace(str, ",", "");
		str = str.replaceAll("[^0-9?!\\.-]","");
		return str;*/
    }

    public static String getDateFromString(String str){
        Pattern p = Pattern.compile("[0-9]{4}-[0-9]{2}-[0-9]{2}");
        Matcher m = p.matcher(str);
        while (m.find()) {
            String s = m.group();
            if(s != null && !"".equals(s)){
                return s;
            }
        }
        return null;
    }

    //default percentige = 15.0
    public static double percentigeGreatThan(String s){
        Pattern numbers = Pattern.compile("-?\\d+(\\.\\d+)?%?");
        Matcher matcher = numbers.matcher(s);
        int count = 0;
        double total = 0.0;
        boolean find = false;
        while(matcher.find()){
            String number = StringUtils.replace(matcher.group(), "%", "");
            count ++;
            total += Double.parseDouble(number);
            find = true;
        }
        if(find){
            return (total/count);
        }else{
            return 0;
        }
    }

    public static boolean percentigeContainAndGreatThan(String s,double percentige){
        Pattern numbers = Pattern.compile("-?\\d+(\\.\\d+)?%?");
        Matcher matcher = numbers.matcher(s);
        int count = 0;
        double total = 0.0;
        while(matcher.find()){
            String number = StringUtils.replace(matcher.group(), "%", "");
            count ++;
            total += Double.parseDouble(number);
        }
        return (total/count) >= percentige;
    }

    public static String getDate(int days, SimpleDateFormat sf){
        return StkUtils.formatDate(StkUtils.addDay(new Date(), days),sf);
    }

    public static String getToday(SimpleDateFormat sf){
        return StkUtils.formatDate(new Date(),sf);
    }
    public static String getToday(){
        return StkUtils.formatDate(new Date(),StkUtils.sf_ymd2);
    }
    public static String getTodayUs(){
        Calendar calendar = Calendar.getInstance();
        StkUtils.sf_ymd12.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));
        return StkUtils.sf_ymd12.format(calendar.getTime());
    }
    public static String getYesterday(){
        return StkUtils.formatDate(StkUtils.addDay(new Date(), -1),StkUtils.sf_ymd2);
    }
    public static String getYesterday(String yyyyMMdd) throws ParseException{
        return StkUtils.formatDate(StkUtils.addDay(StkUtils.sf_ymd2.parse(yyyyMMdd), -1),StkUtils.sf_ymd2);
    }

    public static String mmddToQuarter(String mmdd){
        if(MMDD_Q1.equals(mmdd)){
            return "Q1";
        }else if(MMDD_Q2.equals(mmdd)){
            return "Q2";
        }else if(MMDD_Q3.equals(mmdd)){
            return "Q3";
        }else{
            return "Q4";
        }
    }

    public static String getQuarter(String yyyyMMdd, int n) throws Exception {
        String result = yyyyMMdd;
        for(int i=0; i<n; i++){
            result = getPrevQuarter(result);
        }
        return result;
    }

    //TODO 需要重写
    //得到最近的一个季度
    public static String getPrevQuarter(String yyyyMMdd) throws Exception {
        String year = StkUtils.formatDate(StkUtils.sf_ymd2.parse(yyyyMMdd), StkUtils.sf_yyyy);
        String MMdd = StkUtils.formatDate(StkUtils.sf_ymd2.parse(yyyyMMdd), StkUtils.sf_MMdd);
        if(MMDD_Q1.compareTo(MMdd) < 0 && MMdd.compareTo(MMDD_Q2) <= 0){
            return year + MMDD_Q1;
        }else if(MMDD_Q2.compareTo(MMdd) < 0 && MMdd.compareTo(MMDD_Q3) <= 0){
            return year + MMDD_Q2;
        }else if(MMDD_Q3.compareTo(MMdd) < 0 && MMdd.compareTo(MMDD_Q4) <= 0){
            return year + MMDD_Q3;
        }else{
            return (Integer.parseInt(year)-1)+MMDD_Q4;
        }
    }

    public static String getNextQuarter(String yyyyMMdd) throws Exception {
        String year = StkUtils.formatDate(StkUtils.sf_ymd2.parse(yyyyMMdd), StkUtils.sf_yyyy);
        String MMdd = StkUtils.formatDate(StkUtils.sf_ymd2.parse(yyyyMMdd), StkUtils.sf_MMdd);
        if(MMDD_Q1.compareTo(MMdd) < 0 && MMdd.compareTo(MMDD_Q2) <= 0){
            return year + MMDD_Q2;
        }else if(MMDD_Q2.compareTo(MMdd) < 0 && MMdd.compareTo(MMDD_Q3) <= 0){
            return year + MMDD_Q3;
        }else if(MMDD_Q3.compareTo(MMdd) < 0 && MMdd.compareTo(MMDD_Q4) <= 0){
            return year + MMDD_Q4;
        }else{
            return (Integer.parseInt(year))+MMDD_Q1;
        }
    }


    /**
     * @param yearEnd yyyyMM
     * @return [201306,201303,201212,201209] ...
     * @throws ParseException
     */
    public static List<String> getQuarters(String yearEnd) throws ParseException{
        int year = StkUtils.get(StkUtils.sf_yyyyMM.parse(yearEnd), Calendar.YEAR);
        int MM = StkUtils.get(StkUtils.sf_yyyyMM.parse(yearEnd), Calendar.MONTH);
        MM += 1;
        int tmp = MM;
        List<String> ret = new ArrayList<String>();
        ret.add(yearEnd);
        while(true){
            MM -= 3;
            if(MM <= 0){
                MM += 12;
                year -= 1;
            }
            if(MM == tmp)break;
            ret.add(year+StringUtils.leftPad(String.valueOf(MM), 2, '0'));
        }
        return ret;
    }

    public static int getQuarterNumber(int MMend, int mm){
        int x = MMend;
        int n = 4;
        while(true){
            if(n == 0)return 0;
            if(x <= 0) x += 12;
            if(x == mm)return n;
            x -= 3;
            n --;
        }
    }

    public static String getQuartersAsSelect() throws Exception{
        StringBuffer sb = new StringBuffer();
        sb.append("<select id=\"quarter\" onclick=\"stopBubble(this);\">");
        sb.append("<option>-财务日期-</option>");
        String result = StkUtils.getToday();
        for(int i=0; i<4; i++){
            result = getPrevQuarter(result);
            sb.append("<option>").append(result).append("</option>");
        }
        sb.append("</select>");
        return sb.toString();
    }

    public static String number2String(double d,int i){
        BigDecimal value = new BigDecimal(d).setScale(i, BigDecimal.ROUND_HALF_UP);
        return value.toString();
    }

    public static double numberFormat(double d, int i){
        long lTemp = (long)Math.pow(10D, i);
        return (long)Math.round(d * (double)lTemp) / (double)lTemp;
    }

    public final static DecimalFormat df = new DecimalFormat("#####0.00") ;
    public final static DecimalFormat df2 = new DecimalFormat("#####0") ;
    //return df.format ( Double.parseDouble( str ) ) ;
    public final static NumberFormat nf = NumberFormat.getInstance();
    static {
        nf.setMinimumFractionDigits(2);
    }
    public static String numberFormat2Digits(double d){
        return df.format(d);
    }
    public static String numberFormat2Digits(Double d){
        if(d == null)return null;
        return df.format(d);
    }
    public static String numberFormat0Digits(Double d){
        if(d == null)return null;
        return df2.format(d);
    }

    //都是数字，不能包含.了
    public static boolean isAllNumeric(String str) {
        for (int i = str.length(); --i >= 0;) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static boolean isAllNumericOrDot(String str) {
        int cnt = StringUtils.countMatches(str, ".");
        String tmp = str;
        if(cnt == 1){
            tmp = StringUtils.replaceOnce(tmp, ".", "");
        }
        return isAllNumeric(tmp);
    }

    public static boolean dateBefore(String before, String after) {
        try {
            Date d1 = StkUtils.sf_ymd2.parse(before);
            Date d2 = StkUtils.sf_ymd2.parse(after);
            return d1.before(d2);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String formatDate(Date d){
        return StkUtils.sf_ymd.format(d);
    }

    public static String formatDate(Date d,DateFormat df){
        return df.format(d);
    }
    public static String formatDate(String d,DateFormat src,DateFormat des) throws ParseException{
        if(d == null) return null;
        return des.format(src.parse(d));
    }
    //yyyyMMdd to yyyy-MM-dd
    public static String formatDate(String d) throws ParseException{
        if(d == null) return null;
        return StkUtils.formatDate(d, sf_ymd2, sf_ymd);
    }

    private static int getCurYear(){
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        return cal.get(Calendar.YEAR);
    }
    public static int get(Date date, int type){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(type);
    }

    public static Date addDay(String yyyyMMdd, int i) throws ParseException{
        return addDay(StkUtils.sf_ymd2.parse(yyyyMMdd),i);
    }

    public static Date addDay(Date date, int i){
/*	    if (date == null)
	      throw new Exception("date can not be null");*/
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, i);
        return cal.getTime();
    }

    public static Date addDayOfWorking(Date date, int i){
        Date dt = date;
        while(i != 0){
            int abs = i/Math.abs(i);
            dt = StkUtils.addDay(dt, abs);
            int day = StkUtils.getDayOfWeek(dt);
            if(day != 6 && day != 0){
                i = i-abs;
            }
        }
        return dt;
    }

    public static Date addDayOfWorking(String yyyyMMdd, int i) throws ParseException{
        return addDayOfWorking(StkUtils.sf_ymd2.parse(yyyyMMdd),i);
    }

    public static Date addMinute(Date date, int i){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MINUTE, i);
        return cal.getTime();
    }

    public static String getStkLocation(String code) {
        if(code.startsWith(StkConstant.NUMBER_SIX)){
            return Index.SH_UPPER;
        }else{
            return Index.SZ_UPPER;
        }
    }

    public static String getMMDD(String MMMD) throws ParseException{
        Date date = StkUtils.sf_ymd8.parse(MMMD);
        return StkUtils.sf_MMdd.format(date);
    }

    public static Object getFirstNotNull(List list){
        for(Object obj : list){
            if(obj != null)return obj;
        }
        return null;
    }

    public static String createHtmlTableOneLine(List<String> titles, List<String> datas){
        List<List<String>> list = new ArrayList<List<String>>();
        list.add(datas);
        return createHtmlTable(titles, list);
    }

    public static String createTable(List<String> titles, List<List<HtmlTd>> datas){
        HtmlTable tab = new HtmlTable();
        tab.attributes.put("border", "1");
        tab.attributes.put("cellspacing", "0");
        tab.attributes.put("cellpadding", "1");
        tab.attributes.put("style", "font-size:11px");
        tab.attributes.put("bordercolor", "#add9c0");

        HtmlTr tr = new HtmlTr();
        if(titles != null){
            for(String title : titles){
                HtmlTd headTd = new HtmlTd();
                headTd.text = title;
                tr.columns.add(headTd);
            }
            tab.rows.add(tr);
        }
        for(List<HtmlTd> row : datas){
            tr = new HtmlTr();
            for(HtmlTd td : row){
                td.attributes.put("style", "word-break:keep-all;white-space:nowrap;");
                tr.columns.add(td);
            }
            tab.rows.add(tr);
        }
        return tab.toHtml();
    }

    public static String createHtmlTable(List<String> titles, List<List<String>> datas){
        HtmlTable tab = new HtmlTable();
        tab.attributes.put("border", "1");
        tab.attributes.put("cellspacing", "0");
        tab.attributes.put("cellpadding", "0");
        tab.attributes.put("style", "font:13px");

        HtmlTr tr = new HtmlTr();
        if(titles != null){
            for(String title : titles){
                HtmlTd headTd = new HtmlTd();
                headTd.text = title;
                tr.columns.add(headTd);
            }
            tab.rows.add(tr);
        }
        for(List<String> row : datas){
            tr = new HtmlTr();
            for(String cell : row){
                HtmlTd td = new HtmlTd();
                td.text = cell;
                td.attributes.put("style", "word-break:keep-all;white-space:nowrap;");
                tr.columns.add(td);
            }
            tab.rows.add(tr);
        }
        return tab.toHtml();
    }

    public static String createHtmlTable(String today,List<Index> indexs) throws Exception {
        if(indexs != null){
            Collections.sort(indexs, new Comparator<Index>(){
                @Override
                public int compare(Index arg0, Index arg1) {
                    try {
                        int market = arg0.getMarket();
                        if(market == 2){
                            int n1 = StringUtils.containsIgnoreCase(arg0.getName(), "etf")?1:0;
                            int n2 = StringUtils.containsIgnoreCase(arg1.getName(), "etf")?1:0;
                            return n1-n2;
                        }
                        return (int)(arg0.getMarketValue() - arg1.getMarketValue());
                    } catch (Exception e) {
                        return 0;
                    }
                }});
        }
        List<List> datas = new ArrayList<List>();
        for(Index index : indexs){
            List list = new ArrayList();
            list.add(index);
            datas.add(list);
        }
        return StkUtils.createHtmlTable(today, datas, null);
    }
    public static String createHtmlTable(String today,List<List> datas,List<String> addtitle) throws Exception {
        if(datas == null || datas.size() == 0){
            return "";
        }
        String date10 = StkUtils.formatDate(StkUtils.addDay(StkUtils.sf_ymd2.parse(today),-10), StkUtils.sf_ymd2);
        int market = 1;
        if(datas.size() > 0){
            Index index = (Index)datas.get(0).get(0);
            market = index.getMarket();
        }
        List<String> titles = new ArrayList<String>();
        titles.add("股票(红色为雪球自选股)");
        titles.add("热度");
        if(market == 1){
            titles.add("毛利率");
            titles.add("资金流向");
            titles.add("业绩预告");
            //titles.add("新闻");
            titles.add("限售解禁");
            titles.add("重组并购");
            titles.add("非公定增");
            titles.add("增持员持");
            titles.add("拐点");
        }else{
            titles.add("ETF");
        }
        titles.add("新增");
        titles.add("删除");
        if(addtitle != null){
            for(String title : addtitle){
                titles.add(title);
            }
        }
        //titles.add("日K线");
        //titles.add("月K线");
        List<Index> indexs = new ArrayList<Index>();
        List<Index> indexsNew = new ArrayList<Index>();
        List<Index> indexsNewEr = new ArrayList<Index>();
        List<Index> indexsGROSS_MARGIN = new ArrayList<Index>();//毛利大的
        List<Index> indexsJLRZZL = new ArrayList<Index>();//净利润增长大的
        List<Index> indexsRestriced = new ArrayList<Index>();
        List<Index> indexsBaiduHot = new ArrayList<Index>();

        List<List<HtmlTd>> data = new ArrayList<List<HtmlTd>>();

        Map alignR = new HashMap();
        alignR.put("align","right");

        for(List element : datas){
            List<HtmlTd> row = new ArrayList<HtmlTd>();
            for(int i=0;i<element.size();i++){
                if(i == 0){
                    Index index = (Index)element.get(i);
                    indexs.add(index);

                    if(market == 1){
                        boolean xqFollowStk = XueqiuUtils.existingXueqiuFollowStk("全部", index.getCode());
                        if(xqFollowStk && InitialKLine.addToCareStks){
                            InitialKLine.careStks.add(index.getCode());
                        }
                        String cd = xqFollowStk ? "<span style=\"color:red\">"+index.getCode()+"</span>" : index.getCode();
                        row.add(HtmlTd.getInstance(index.getName()+"[<a target='_blank' href='http://"+StkConstant.HOST_PORT+"/stk?s="+index.getCode()+"'>"+ cd +"</a>]"+"["+StkUtils.number2String(index.getTotalMarketValue(),2)+"亿]"+index.isGrowthOrPotentialOrReversionName().substring(0, 1)));
                    }else{
                        row.add(HtmlTd.getInstance("["+StkUtils.wrapCodeLink(index.getCode())+"]"+index.getName()));
                    }
                    row.add(HtmlTd.getInstance(index.getStk().getHot().toString(),alignR));
                    if(market == 1){
                        StkFnDataCust fn = index.getFnDataLastestByType(index.FN_GROSS_MARGIN);
                        Double gm = null;
                        if(fn != null){
                            gm = fn.getFnValue();
                        }
                        if(gm != null && gm.doubleValue() >= 50){
                            indexsGROSS_MARGIN.add(index);
                        }
                        //毛利率
                        row.add(HtmlTd.getInstance(StkUtils.numberFormat2Digits(gm)+"%", alignR));
                        //资金流向
                        //row.add(HtmlTd.getInstance(StkUtils.numberFormat2Digits(index.getCapitalFlowPercent(today))+"%", alignR));
                        row.add(HtmlTd.getInstance(index.getCapitalFlowImageOnMainAndSuper(10)));
                        if(index.NumberOfCapitalFlowPostive >= 6 && InitialKLine.addToFlowStks){
                            InitialKLine.flowStks.add(index);
                        }
                        List<String> nextEF = null;
                        try{
                            nextEF = index.getEarningsForecastAsList();
                        }catch(Exception e){}
                        if(nextEF != null && nextEF.size() > 3){
                            fn = index.getFnDataLastestByType(index.FN_JLRZZL);
                            if(fn!=null && fn.getFnDate().compareTo(StringUtils.replace(nextEF.get(0), "-", "")) > 0){
                                Double jlr = fn.getFnValue();
                                if(jlr != null && jlr.doubleValue() >= 40){
                                    indexsJLRZZL.add(index);
                                }
                                row.add(HtmlTd.getInstance(StkUtils.formatDate(fn.getFnDate())+"["+StkUtils.numberFormat2Digits(jlr) +"%]"));
                            }else{
                                double ef = StkUtils.percentigeGreatThan(nextEF.get(2));
                                if(ef >= 50){
                                    indexsJLRZZL.add(index);
                                }
                                row.add(HtmlTd.getInstance(nextEF.get(0)+"["+nextEF.get(2) +"]*"));
                            }
                        }else{
                            row.add(HtmlTd.getInstance(""));
                        }
                        //新闻热度
						/*int hot = 0;//baiduSearch(index.getName());
						row.add(HtmlTd.getInstance(String.valueOf(hot), alignR));
						if(hot >= 16){
							indexsBaiduHot.add(index);
						}*/
                        Date start = StkUtils.addDay(now, -500);
                        StkRestricted sr = index.isRestrictedDateBetween(start, StkUtils.addDay(now, -50));
                        boolean hadReduced = false;
                        String percent = null;
                        List<StkImportInfo> infos = null;
                        if(sr != null){
                            //查看有没有减持过
                            infos = index.getImportInfoAfterDate(190, start);
                            for(StkImportInfo info : infos){
                                if(info.getTitle().contains("减持") && !info.getTitle().contains("不减持")){
                                    hadReduced = true;
                                    break;
                                }
                            }
                            percent = StkUtils.number2String(sr.getBanMarketValue()/index.getMarketValue(),2);
                        }
                        row.add(HtmlTd.getInstance((sr != null && !hadReduced)?"未减持("+percent+"%)":(sr != null && hadReduced)?"限售有减("+percent+"%)":""));
                        if(sr != null && !hadReduced && percent != null && Double.parseDouble(percent) >= 0.10){
                            indexsRestriced.add(index);
                        }
                        //重组并购
                        start = StkUtils.addDay(now, -180);
                        infos = index.getImportInfoAfterDate(140, start);
                        row.add(HtmlTd.getInstance(infos.size() > 0?"重组并购":""));
                        //非公定增
                        infos = index.getImportInfoAfterDate(150, start);
                        row.add(HtmlTd.getInstance(infos.size() > 0?"非公定增":""));
                        //增持员工持股
                        infos = index.getImportInfoAfterDate(120, start);
                        row.add(HtmlTd.getInstance(infos.size() > 0?"增持员持":""));
                        //拐点
                        infos = index.getImportInfoAfterDate(210, start);
                        row.add(HtmlTd.getInstance(infos.size() > 0?"拐点":""));

                    }else{
                        row.add(HtmlTd.getInstance(StringUtils.containsIgnoreCase(index.getName(), "etf")?"ETF":""));
                    }
                    row.add(HtmlTd.getInstance(index.isNew?"新增":""));
                    row.add(HtmlTd.getInstance(index.isDeleted?"删除":""));

                    index.isDeleted = false;
                    if(index.isNew){
                        indexsNew.add(index);
                        StkEarningsNotice en = index.getPerformanceNoticeLatest(StkUtils.formatDate(StkUtils.addDay(now, -360),StkUtils.sf_yyyyMMdd));
                        if(en != null && en.getErLow()!=null && en.getErLow() >= 20){
                            indexsNewEr.add(index);
                        }
                    }
                    index.isNew = false;
                }else{
                    row.add(HtmlTd.getInstance(String.valueOf(element.get(i))));
                }
            }
			/*for(int i=0;i<element.size();i++){
				if(i == 0){
					Index index = (Index)element.get(i);
					row.add(StkUtils.createDailyKLine(index));
					row.add(StkUtils.createMonthlyKLine(index));
				}
			}*/
            data.add(row);
        }
        return StkUtils.createTable(titles, data)
                +(indexsNew.size()>0?"<br>(新加入标的):<br>"+StkUtils.join(indexsNew, ","):"")
                +(indexsNewEr.size()>0?"<br>(新加入且业绩预告大于20%标的):<br>"+StkUtils.join(indexsNewEr, ","):"")
                +(indexsGROSS_MARGIN.size()>0?"<br>(毛利>=50%标的):<br>"+StkUtils.join(indexsGROSS_MARGIN, ","):"")
                +(indexsJLRZZL.size()>0?"<br>(净利润增长>=40%标的):<br>"+StkUtils.join(indexsJLRZZL, ","):"")
                +(indexsRestriced.size()>0?"<br>(解禁未减持(数量>=0.1%)标的):<br>"+StkUtils.join(indexsRestriced, ","):"")
                +(indexsBaiduHot.size()>0?"<br>(百度新闻热点标的):<br>"+StkUtils.join(indexsBaiduHot, ","):"")

                +((indexsGROSS_MARGIN.size()+indexsJLRZZL.size()+indexsRestriced.size()+indexsBaiduHot.size())>0?
                "<br><br>(有亮点标的):<br>"+
                        StkUtils.join(combine(indexsGROSS_MARGIN,indexsJLRZZL,indexsRestriced,indexsBaiduHot), ","):"")
                +"<br><br>(全部标的)<br>"+ StkUtils.join(indexs, ",");
    }

    public static List<Index> combine(List<Index>... indexs){
        Set<Index> results = new HashSet<Index>();
        for(List<Index> is : indexs){
            results.addAll(is);
        }
        return new ArrayList(results);
    }

    private static Map<String, Integer> baiduSearchMaps = new HashMap<String, Integer>();
    private static int baiduSearch(String stkName) throws Exception{
        if(baiduSearchMaps.containsKey(stkName)){
            return baiduSearchMaps.get(stkName);
        }
        int n = BaiduSearch.getKeywordsTotalWeight(stkName);
        baiduSearchMaps.put(stkName, n);
        return n;
    }

    public static String getImgBase64(String img){
        return "<img src=\"data:image/png;base64," + img + "\"/>";
    }

    public static String getImgBase64(String img, int width, int height){
        return "<img height=\""+height+"\" width=\""+width+"\" src=\"data:image/png;base64," + img + "\"/>";
    }


    public static String wrapColorForKeyword(String str,String[] keywords) throws Exception{
        String ss = new String(str.getBytes("GBK"));
        for(String s:keywords){
            ss = StringUtils.replace(ss, s, "<font color='#FF0000'>"+s+"</font>");
        }
        return new String(ss.getBytes(),"GBK");
    }

    public static int getDayOfWeek(Date dt) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(dt);
        return cal.get(Calendar.DAY_OF_WEEK) - 1;
    }
    public static int getDayOfWeek(String dt) throws ParseException {
        return StkUtils.getDayOfWeek(sf_ymd2.parse(dt));
    }

    public static int getDaysBetween(java.util.Calendar d1, java.util.Calendar d2) {
        if (d1.after(d2)) { // swap dates so that d1 is start and d2 is end
            java.util.Calendar swap = d1;
            d1 = d2;
            d2 = swap;
        }
        int days = d2.get(java.util.Calendar.DAY_OF_YEAR)
                - d1.get(java.util.Calendar.DAY_OF_YEAR);
        int y2 = d2.get(java.util.Calendar.YEAR);
        if (d1.get(java.util.Calendar.YEAR) != y2) {
            d1 = (java.util.Calendar) d1.clone();
            do {
                days += d1.getActualMaximum(java.util.Calendar.DAY_OF_YEAR);
                d1.add(java.util.Calendar.YEAR, 1);
            } while (d1.get(java.util.Calendar.YEAR) != y2);
        }
        return days+1;
    }


    public static int getDaysBetween(java.util.Date d1, java.util.Date d2) {
        Calendar cal_start = Calendar.getInstance();
        Calendar cal_end = Calendar.getInstance();
        cal_start.setTime(d1);
        cal_end.setTime(d2);
        return getDaysBetween(cal_start,cal_end);
    }


    public static int getWorkingDays(java.util.Date d1, java.util.Date d2) {
        Calendar cal_start = Calendar.getInstance();
        Calendar cal_end = Calendar.getInstance();
        cal_start.setTime(d1);
        cal_end.setTime(d2);
        return getWorkingDays(cal_start,cal_end);
    }

    /**
     * 计算2个日期之间的相隔工作天数
     * @param d1
     * @param d2
     * @return
     */
    public static int getWorkingDays(java.util.Calendar d1, java.util.Calendar d2) {
        int result = -1;
        if (d1.after(d2)) { // swap dates so that d1 is start and d2 is end
            java.util.Calendar swap = d1;
            d1 = d2;
            d2 = swap;
        }

        int charge_start_date = 0;//开始日期的日期偏移量
        int charge_end_date = 0;//结束日期的日期偏移量
        // 日期不在同一个日期内
        int stmp;
        int etmp;
        stmp = 7 - d1.get(Calendar.DAY_OF_WEEK);
        etmp = 7 - d2.get(Calendar.DAY_OF_WEEK);
        if (stmp != 0 && stmp != 6) {// 开始日期为星期六和星期日时偏移量为0
            charge_start_date = stmp - 1;
        }
        if (etmp != 0 && etmp != 6) {// 结束日期为星期六和星期日时偏移量为0
            charge_end_date = etmp - 1;
        }
        result = (getDaysBetween(getNextMonday(d1), getNextMonday(d2)) / 7)
                * 5 + charge_start_date - charge_end_date;
        return result;
    }

    public static Calendar getNextMonday(Calendar date) {
        Calendar result = null;
        result = date;
        do {
            result = (Calendar) result.clone();
            result.add(Calendar.DATE, 1);
        } while (result.get(Calendar.DAY_OF_WEEK) != 2);
        return result;
    }

    public static int getHolidays(Calendar d1, java.util.Calendar d2){
        return getDaysBetween(d1,d2) - getWorkingDays(d1,d2);
    }



    public static String wrapColorForChange(double change){
        if(change > 0){
            return "<font color='#FF0000'>"+change+"</font>";
        }
        return String.valueOf(change);
    }
    public static String setHtmlFontColor(String word,String color){
        return "<font color='"+color+"'>"+word+"</font>";
    }

    public static String setHtmlFontSize(String str,String size){
        return "<font size='"+size+"'>"+str+"</font>";
    }


    public static String getValueFromMap(Map<String, String> map,String key){
        for(Map.Entry<String, String> kv : map.entrySet()){
            if(StringUtils.endsWith(kv.getKey(), key)){
                return kv.getValue();
            }
        }
        return null;
    }

    public static String join(List<Index> indexs, String separator){
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < indexs.size(); i++) {
            if (i > 0) {
                sb.append(separator);
            }
            Index index = indexs.get(i);
            sb.append(index.getCode());
        }
        return sb.toString();
    }



    public static String createDailyKLine(Index index){
        String code = index.getCode();
        int market = index.getMarket();
        if(market == 1){
            return "<img width='450' src='http://image.sinajs.cn/newchart/daily/n/"+StkUtils.getStkLocation(code).toLowerCase()+code+".gif' />";
        }else{
            return "<img width='450' src='http://image.sinajs.cn/newchart/usstock/daily/"+code+".gif' />";
        }
    }

    public static String createWeeklyKLine(Index index){
        String code = index.getCode();
        int market = index.getMarket();
        if(market == 1){
            return "<img width='450' src='http://image.sinajs.cn/newchart/weekly/n/"+StkUtils.getStkLocation(code).toLowerCase()+code+".gif' />";
        }else{
            return "<img width='450' src='http://image.sinajs.cn/newchart/usstock/weekly/"+code+".gif' />";
        }
    }

    public static String createMonthlyKLine(Index index){
        String code = index.getCode();
        int market = index.getMarket();
        if(market == 1){
            return "<img width='450' src='http://image.sinajs.cn/newchart/monthly/n/"+StkUtils.getStkLocation(code).toLowerCase()+code+".gif' />";
        }else{
            return "<img width='450' src='http://image.sinajs.cn/newchart/usstock/monthly/"+code+".gif' />";
        }
    }

    public static String wrapCode(String code){
        String _code = StkUtils.getStkLocation(code).toLowerCase()+code;
        return "<a href='javascript:goto();' onclick='javascript:show_kline(this,\""+_code+"\")'>"+code+"</a>";
    }
    public static String wrapCodeLink(String code){
        return "<a target='_blank' href='http://"+StkConstant.HOST_PORT+"/stk?s="+code+"'>"+code+"</a>";
    }

    private final static String WRAPCODEASHTML_1 = "<a target='_blank' href='/stk?s=";
    private final static String WRAPCODEASHTML_2 = "'>";
    private final static String WRAPCODEASHTML_3 = "</a>";

    public static String wrapCodeAsHtml(String code){
        return new StringBuffer(WRAPCODEASHTML_1).append(code).append(WRAPCODEASHTML_2).append(code).append(WRAPCODEASHTML_3).toString();
    }
    public static String wrapCodeAndNameAsHtml2(String code, String name){
        return new StringBuffer().append(name).append(StkConstant.MARK_BLANK_SPACE).append(WRAPCODEASHTML_1).append(code).append("' data-code='"+code+"'>").append(StkConstant.MARK_BRACKET_LEFT).append(code).append(StkConstant.MARK_BRACKET_RIGHT).append(WRAPCODEASHTML_3).toString();
    }
    public static String wrapCodeAndNameAsHtml(String code, String name){
        return new StringBuffer().append(WRAPCODEASHTML_1).append(code).append("' data-code='"+code+"'>").append(name).append(StkConstant.MARK_BLANK_SPACE).append(StkConstant.MARK_BRACKET_LEFT).append(code).append(StkConstant.MARK_BRACKET_RIGHT).append(WRAPCODEASHTML_3).toString();
    }

    public static String wrapCodeAndNameAsHtml(Index index) throws Exception {
        return "<a class=\"stk-code\" target=\"_blank\" href=\"/stk?s="+index.getCode()+"\">"+index.getName()+"["+index.getCode()+"]</a>["+StkUtils.number2String(index.getTotalMarketValue(),2)+"亿]";
    }

    public static String wrapIndustryAsHtml(Integer id,String name){
        return new StringBuffer("<a target='_blank' href='/industry?id=").append(id).append("'>").append(name).append("</a>").toString();
    }

    public static String wrapLink(String title, String url){
        return new StringBuffer("<a target='_blank' href='").append(url).append("'>").append(title).append("</a>").toString();
    }

    public static String wrapName(String name,String code){
        HtmlA link = new HtmlA();
        link.text = name;
        String loc = StkUtils.getStkLocation(code);
        link.attributes.put("href", "http://www.windin.com/home/stock/html/"+code+"."+loc+".shtml");
        return link.toHtml();
    }

    //计算复合增长率
    public static double calcCAGR(double startValue, double endValue, int years){
        return StkUtils.numberFormat(Math.pow(endValue/startValue,1.0/years) - 1D, 4) * 100;
    }

    private static Boolean isDev = null;

    public static boolean isDev() {
        if(isDev != null)return isDev;
        Connection conn = null;
        try{
            conn = DBUtil.getConnection();
            return isDev = true;
        }catch(Exception e){
            return isDev = false;
        }finally{
            CloseUtil.close(conn);
        }
    }

    public static double getAmount万(String amount){
    	int n = 1;
		String dd = StringUtils.replace(amount, "万", "");
		if(StringUtils.indexOf(dd, "亿") > 0){
			n = 10000;
			dd = StringUtils.replace(dd, "亿", "");
		}
		return Double.parseDouble(dd) * n;
    }

    //PEG＝市盈率／未来3至5年年度净利润复合增长率
    //净利润复合增长率 = [(现有价值/基础价值)^(1/年数) - 1] x 100%
    //Math.pow(a, 1/n) = b   =>   a = Math.pow(b, n)
    public static String createPEAndPEG(){
        HtmlTable tab = new HtmlTable();
        tab.attributes.put("border", "1");
        tab.attributes.put("cellspacing", "0");
        tab.attributes.put("cellpadding", "2");
        tab.attributes.put("style", "font: 13px");

        HtmlTr tr = new HtmlTr();
        HtmlTd headTd = new HtmlTd();
        headTd.text = "三年复合增长率";
        tr.columns.add(headTd);
        headTd = new HtmlTd();
        headTd.text = "当前可投资PE";
        tr.columns.add(headTd);
        headTd = new HtmlTd();
        headTd.text = "当前可投资PEG";
        tr.columns.add(headTd);
        tab.rows.add(tr);

        tr = new HtmlTr();
        headTd = new HtmlTd();
        headTd.text = "15%";
        tr.columns.add(headTd);
        headTd = new HtmlTd();
        headTd.text = "15.2";
        tr.columns.add(headTd);
        headTd = new HtmlTd();
        headTd.text = "1.01";
        tr.columns.add(headTd);
        tab.rows.add(tr);

        tr = new HtmlTr();
        headTd = new HtmlTd();
        headTd.text = "20%";
        tr.columns.add(headTd);
        headTd = new HtmlTd();
        headTd.text = "17.28";
        tr.columns.add(headTd);
        headTd = new HtmlTd();
        headTd.text = "0.86";
        tr.columns.add(headTd);
        tab.rows.add(tr);

        tr = new HtmlTr();
        headTd = new HtmlTd();
        headTd.text = "25%";
        tr.columns.add(headTd);
        headTd = new HtmlTd();
        headTd.text = "19.53";
        tr.columns.add(headTd);
        headTd = new HtmlTd();
        headTd.text = "0.78";
        tr.columns.add(headTd);
        tab.rows.add(tr);

        tr = new HtmlTr();
        tr.attributes.put("style", "color:red");
        headTd = new HtmlTd();
        headTd.text = "30%";
        tr.columns.add(headTd);
        headTd = new HtmlTd();
        headTd.text = "21.97";
        tr.columns.add(headTd);
        headTd = new HtmlTd();
        headTd.text = "0.73";
        tr.columns.add(headTd);
        tab.rows.add(tr);

        tr = new HtmlTr();
        tr.attributes.put("style", "color:red");
        headTd = new HtmlTd();
        headTd.text = "35%";
        tr.columns.add(headTd);
        headTd = new HtmlTd();
        headTd.text = "24.6";
        tr.columns.add(headTd);
        headTd = new HtmlTd();
        headTd.text = "0.70";
        tr.columns.add(headTd);
        tab.rows.add(tr);

        tr = new HtmlTr();
        tr.attributes.put("style", "color:red");
        headTd = new HtmlTd();
        headTd.text = "40%";
        tr.columns.add(headTd);
        headTd = new HtmlTd();
        headTd.text = "27.44";
        tr.columns.add(headTd);
        headTd = new HtmlTd();
        headTd.text = "0.69";
        tr.columns.add(headTd);
        tab.rows.add(tr);

        tr = new HtmlTr();
        tr.attributes.put("style", "color:red");
        headTd = new HtmlTd();
        headTd.text = "45%";
        tr.columns.add(headTd);
        headTd = new HtmlTd();
        headTd.text = "30.48";
        tr.columns.add(headTd);
        headTd = new HtmlTd();
        headTd.text = "0.68";
        tr.columns.add(headTd);
        tab.rows.add(tr);

        tr = new HtmlTr();
        tr.attributes.put("style", "color:red");
        headTd = new HtmlTd();
        headTd.text = "50%";
        tr.columns.add(headTd);
        headTd = new HtmlTd();
        headTd.text = "33.75";
        tr.columns.add(headTd);
        headTd = new HtmlTd();
        headTd.text = "0.68";
        tr.columns.add(headTd);
        tab.rows.add(tr);

        tr = new HtmlTr();
        tr.attributes.put("style", "color:red");
        headTd = new HtmlTd();
        headTd.text = "55%";
        tr.columns.add(headTd);
        headTd = new HtmlTd();
        headTd.text = "37.24";
        tr.columns.add(headTd);
        headTd = new HtmlTd();
        headTd.text = "0.68";
        tr.columns.add(headTd);
        tab.rows.add(tr);

        tr = new HtmlTr();
        tr.attributes.put("style", "color:red");
        headTd = new HtmlTd();
        headTd.text = "60%";
        tr.columns.add(headTd);
        headTd = new HtmlTd();
        headTd.text = "40.96";
        tr.columns.add(headTd);
        headTd = new HtmlTd();
        headTd.text = "0.68";
        tr.columns.add(headTd);
        tab.rows.add(tr);

        tr = new HtmlTr();
        tr.attributes.put("style", "color:red");
        headTd = new HtmlTd();
        headTd.text = "70%";
        tr.columns.add(headTd);
        headTd = new HtmlTd();
        headTd.text = "49.13";
        tr.columns.add(headTd);
        headTd = new HtmlTd();
        headTd.text = "0.70";
        tr.columns.add(headTd);
        tab.rows.add(tr);

        tr = new HtmlTr();
        headTd = new HtmlTd();
        headTd.text = "80%";
        tr.columns.add(headTd);
        headTd = new HtmlTd();
        headTd.text = "58.32";
        tr.columns.add(headTd);
        headTd = new HtmlTd();
        headTd.text = "0.73";
        tr.columns.add(headTd);
        tab.rows.add(tr);

        tr = new HtmlTr();
        headTd = new HtmlTd();
        headTd.text = "90%";
        tr.columns.add(headTd);
        headTd = new HtmlTd();
        headTd.text = "68.59";
        tr.columns.add(headTd);
        headTd = new HtmlTd();
        headTd.text = "0.76";
        tr.columns.add(headTd);
        tab.rows.add(tr);

        tr = new HtmlTr();
        headTd = new HtmlTd();
        headTd.text = "100%";
        tr.columns.add(headTd);
        headTd = new HtmlTd();
        headTd.text = "80";
        tr.columns.add(headTd);
        headTd = new HtmlTd();
        headTd.text = "0.80";
        tr.columns.add(headTd);
        tab.rows.add(tr);

        return tab.toHtml();
    }

	/*
	 分时K线查询：http://image.sinajs.cn/newchart/min/n/sh000001.gif
	 日K线查询：http://image.sinajs.cn/newchart/daily/n/sh000001.gif
	 周K线查询：http://image.sinajs.cn/newchart/weekly/n/sh000001.gif
	 月K线查询：http://image.sinajs.cn/newchart/monthly/n/sh000001.gif
	 */

    /**
     *1. 从剪切板获得文字。
     */
    public static String getSysClipboardText() {
        String ret = "";
        Clipboard sysClip = Toolkit.getDefaultToolkit().getSystemClipboard();
        // 获取剪切板中的内容
        Transferable clipTf = sysClip.getContents(null);

        if (clipTf != null) {
            // 检查内容是否是文本类型
            if (clipTf.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                try {
                    ret = (String) clipTf
                            .getTransferData(DataFlavor.stringFlavor);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return ret;
    }

}
