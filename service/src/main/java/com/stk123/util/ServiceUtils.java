package com.stk123.util;

import com.stk123.common.CommonConstant;
import com.stk123.util.baidu.BaiduSearch;
import com.stk123.model.Index;
import com.stk123.model.IndexUtils;
import com.stk123.common.CommonUtils;
import com.stk123.common.html.HtmlA;
import lombok.SneakyThrows;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.type.filter.AssignableTypeFilter;

import java.io.InputStream;
import java.lang.management.ManagementFactory;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.NumberFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ServiceUtils extends CommonUtils {

    public static <T extends Enum<?>> T searchEnum(Class<T> enumClass, String search, T defaultIfNull) {
        /*for (T each : enumeration.getEnumConstants()) {
            if (each.name().compareToIgnoreCase(search) == 0) {
                return each;
            }
        }
        return null;*/
        return Arrays.stream(enumClass.getEnumConstants())
                .filter(e -> e.name().equalsIgnoreCase(search)).findAny().orElse(defaultIfNull==null?null:defaultIfNull);
    }

    public static <T extends Enum<?>> T searchEnum(Class<T> enumClass, String search) {
        return searchEnum(enumClass, search, null);
    }


    public static String getProcessId(final String fallback) {
        // Note: may fail in some JVM implementations
        // therefore fallback has to be provided

        // something like '<pid>@<hostname>', at least in SUN / Oracle JVMs
        final String jvmName = ManagementFactory.getRuntimeMXBean().getName();
        final int index = jvmName.indexOf('@');

        if (index < 1) {
            // part before '@' empty (index = 0) / '@' not found (index = -1)
            return fallback;
        }

        try {
            return Long.toString(Long.parseLong(jvmName.substring(0, index)));
        } catch (NumberFormatException e) {
            // ignore
        }
        return fallback;
    }

    /**
     * 得到packagePath下面所有clazz类的子类，未经测试
     * @param clazz
     * @param packagePath "org/example/package"
     * @return
     * @throws ClassNotFoundException
     */
    public static List<Class> getBeans(Class clazz, String packagePath) throws
            ClassNotFoundException {
        ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(false);
        provider.addIncludeFilter(new AssignableTypeFilter(clazz));

        List<Class> results = new ArrayList<>();
        // scan in org.example.package
        Set<BeanDefinition> components = provider.findCandidateComponents(packagePath);
        for (BeanDefinition component : components) {
            Class cls = Class.forName(component.getBeanClassName());
            // use class cls found
            results.add(cls);
        }
        return results;
    }


    public static void main(String[] args) throws Exception {
        System.out.println(now);
        System.out.println(numberFormat(2.342,2));
        System.out.println(StringUtils.substring("20130201",4));
        System.out.println(sf_ymd6.parse("Sep 28, 2013"));
        Date date = ServiceUtils.addDay(ServiceUtils.now, -30);
        System.out.println(ServiceUtils.get(now, Calendar.MONTH));
		/*System.out.println(StkUtils.getLastQuarter(StkUtils.getToday()));

		Calendar cal_start = Calendar.getInstance();
		cal_start.setTime(now);
		System.out.println(cal_start.get(Calendar.DAY_OF_WEEK));

		System.out.println(StkUtils.calcCAGR(0.96, 1.73,2));

		System.out.println(Math.pow(1+StkUtils.calcCAGR(0.96,  1.73,2), 3));*/
        System.out.println(ServiceUtils.percentigeGreatThan("183.92～155.95%"));
        System.out.println(ServiceUtils.percentigeGreatThan("-183.92%～-155.95%"));


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

        System.out.println("kkk="+ServiceUtils.getNumberFromString("\r募集资金总额 63,705.53 本季度投入募集资金总额 523.1 "));
        System.out.println("kkk="+ServiceUtils.getNumberFromString("亨通光电 [600487]"));

        System.out.println("vv="+ServiceUtils.getMatchString("中证网&nbsp;&nbsp;2015年12月25日  12:00&nbsp;&nbsp;4条相同新闻>>", "\\d{4}年\\d{2}月\\d{2}日\\s*\\d{2}:\\d{2}"));

        System.out.println(ServiceUtils.getMatchString("停牌终止收购4个多月！突然", "(中止|终止).*(重组|收购|并购)"));

        System.out.println(ServiceUtils.getMatchString("帝龙新材上修净业绩预告 净利大增150%—180%", "上修.{0,2}业绩预告"));

        System.out.println(ServiceUtils.getDayOfWeek("20161015"));

        System.out.println(ServiceUtils.addDayOfWorking(now, -6));

        System.out.println(ServiceUtils.getMatchString("非公开发行股票发行情况报告书", "非公开发行.{0,15}(报告|公告)"));
        System.out.println(ServiceUtils.getMatchString("关于公司发行股份及支付现金购买资产并募集配套资金获得中国证券监督管理委员会正式批复的公告", "获得中国(证监会|证券监督).{0,10}(核准|审核|批复)"));

        String str = ServiceUtils.getMatchString("利润亏损:433.71万元-533.79万元，","-?[0-9]*(\\.?)[0-9]*万元.{1}-?[0-9]*(\\.?)[0-9]*万元");
        System.out.println(StringUtils.substringBefore(str, "万元"));
        System.out.println(ServiceUtils.getNumberFromString(StringUtils.substringAfter(str, "万元")));
        System.out.println(ServiceUtils.getNextQuarter("20170115"));

        System.out.println(ServiceUtils.getMatchAllStrings("关于公司发行股份及支付现金购买资产并募集配套资金获得中国证券监督管理委员会正式批复的公告", "配套资金|中国证券"));
        System.out.println(ServiceUtils.getMatchStringAndWrapByString("关于公司发行股份及支付现金购买资产并募集配套资金获得中国证券监督管理委员会正式批复的公告", "配套资金|中国证券","<start>","<end>"));

        System.out.println(ServiceUtils.formatDate(ServiceUtils.addDay(now, -360),ServiceUtils.sf_yyyyMMdd));

        System.out.println(ServiceUtils.getMatchString("43{ps_ntile!='test'}test234</sql>", "\\{[^}]*\\}"));
        System.out.println(ServiceUtils.numberFormat2Digits(-5523649.375));
        printStackTrace();

        System.out.println(StringUtils.length("嗯，那是我以前写的。不过，对于吕建明这样的企业家，我认为钱对于他们早已要无有任何意义了，当把做事业放成第一位了。分红不分红倒在其次，上一次的高价增发失败，估计对公司形象确实也受到一定的影响。我感觉，吕倒是一个有情怀的人，不过，对自己的公司，或许也有时像对待自己的孩子一样，喜欢过了估值难免高些。但总体来讲，我认为并无大碍，一切还是按公司的公开信息和数据说话吧。"));

        //System.out.println(StkUtils.getSysClipboardText());

        //InitialData.updateStkStaticInfo(DBUtil.getConnection(), "002038");
    }

    public static Set<String> extractCodeFromText(String text){
        return IndexUtils.extractCodeFromText(text);
    }

    public static String getStkLocation(String code) {
        if(code.startsWith(CommonConstant.NUMBER_SIX)){
            return Index.SH_UPPER;
        }else{
            return Index.SZ_UPPER;
        }
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
            return "<img width='450' src='http://image.sinajs.cn/newchart/daily/n/"+ServiceUtils.getStkLocation(code).toLowerCase()+code+".gif' />";
        }else{
            return "<img width='450' src='http://image.sinajs.cn/newchart/usstock/daily/"+code+".gif' />";
        }
    }

    public static String createWeeklyKLine(Index index){
        String code = index.getCode();
        int market = index.getMarket();
        if(market == 1){
            return "<img width='450' src='http://image.sinajs.cn/newchart/weekly/n/"+ServiceUtils.getStkLocation(code).toLowerCase()+code+".gif' />";
        }else{
            return "<img width='450' src='http://image.sinajs.cn/newchart/usstock/weekly/"+code+".gif' />";
        }
    }

    public static String createMonthlyKLine(Index index){
        String code = index.getCode();
        int market = index.getMarket();
        if(market == 1){
            return "<img width='450' src='http://image.sinajs.cn/newchart/monthly/n/"+ServiceUtils.getStkLocation(code).toLowerCase()+code+".gif' />";
        }else{
            return "<img width='450' src='http://image.sinajs.cn/newchart/usstock/monthly/"+code+".gif' />";
        }
    }

    public static String wrapCode(String code){
        String _code = ServiceUtils.getStkLocation(code).toLowerCase()+code;
        return "<a href='javascript:goto();' onclick='javascript:show_kline(this,\""+_code+"\")'>"+code+"</a>";
    }

    public static String wrapCodeAndName(String code, String name) {
        String scode = code;
        if(StringUtils.length(code) > 5){
            scode = ServiceUtils.getStkLocation(code)+code;
        }
        return ServiceUtils.wrapLink(name, "http://xueqiu.com/S/"+scode) + "[" + ServiceUtils.wrapLink(code, "/stk?s="+code) + "]";
    }

    public static String wrapCodeAndNameAsHtml(Index index) throws Exception {
        return "<a class=\"stk-code\" target=\"_blank\" href=\"/stk?s="+index.getCode()+"\">"+index.getName()+"["+index.getCode()+"]</a>["+CommonUtils.number2String(index.getTotalMarketValue(),2)+"亿]";
    }

    public static String wrapName(String name,String code){
        HtmlA link = new HtmlA();
        link.text = name;
        String loc = ServiceUtils.getStkLocation(code);
        link.attributes.put("href", "http://www.windin.com/home/stock/html/"+code+"."+loc+".shtml");
        return link.toHtml();
    }

    @SneakyThrows
    public static byte[] getResourceFileAsBytes(String fileName){
        Resource resource = new ClassPathResource(fileName);
        //InputStream is = resource.getInputStream();
        return Files.readAllBytes(Paths.get(resource.getURI()));
    }
}
