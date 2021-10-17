package com.stk123.service;

import java.util.HashMap;
import java.util.Map;

public class StkConstant {

    /** cookie code **/
    public static final String COOKIE_XUEQIU = "xueqiu";
    public static final String COOKIE_IWENCAI = "iwencai";

    /** dict **/
    public final static Integer DICT_MONITOR_K_PARAM1 = 1;
    public final static Integer DICT_MONITOR_K_PARAM2 = 2;

    public final static Integer DICT_XUEQIU_COMMENT = 3;
    public final static Integer DICT_XUEQIU_ARTICLE = 4;
    public final static Integer DICT_XUEQIU_ZHUTIE = 5;    //雪球主贴

    public final static Integer DICT_NIUSAN = 20;         //牛散
    public final static Integer DICT_FAMOUS_FUNDS = 21;   //著名基金  insert into stk_dictionary select 21,1,'华夏大盘精选',null,1,null,null,null,null from dual;

    public final static Integer DICT_INTERNET_SEARCH_TYPE = 10;//internet search type

    public final static Integer DICT_STKS_COLUMN_NAMES = 1000;  //多股同列下显示的列名
    public final static Integer DICT_INDUSTRY_SOURCE = 300;     //行业分类来源
    public final static Integer DICT_TEXT_SUB_TYPE = 400;       //文档子类型

    public final static Integer DICT_NEWS = 2000;           //stk_news.type
    public final static Integer DICT_COOKIE = 500;           //cookies

    /** stk_fn_type.type **/
    public final static Integer FN_TYPE_110 = 110; //主营业务收入增长率(%)
    public final static Integer FN_TYPE_111 = 111; //净利润增长率(%)
    public final static Integer FN_TYPE_106 = 106; //销售毛利率(%)
    public final static Integer FN_TYPE_123 = 123; //经营现金净流量与净利润的比率(%)


    /** stk_news.type **/
    public final static Integer NEWS_TYPE_100 = 100;  //	举牌
    public final static Integer NEWS_TYPE_120 = 120;  //	增持|员工持股
    public final static Integer NEWS_TYPE_121 = 121;  //	回购
    public final static Integer NEWS_TYPE_130 = 130;  //	股权激励
    public final static Integer NEWS_TYPE_140 = 140;  //	重组|并购|收购
    public final static Integer NEWS_TYPE_150 = 150;  //	增发|定增|非公
    public final static Integer NEWS_TYPE_180 = 180;  //	资产置换|协议转让
    public final static Integer NEWS_TYPE_190 = 190;  //	减持
    public final static Integer NEWS_TYPE_200 = 200;  //	订单|中标|合同
    public final static Integer NEWS_TYPE_210 = 210;  //	拐点|扭亏
    public final static Integer NEWS_TYPE_220 = 220;  //	高成长
    public final static Integer NEWS_TYPE_230 = 230;  //	业绩承诺
    public final static Integer NEWS_TYPE_240 = 240;  //	龙头
    public final static Integer NEWS_TYPE_250 = 250;  //	业绩大幅增长
    public final static Integer NEWS_TYPE_260 = 260;  //	业绩修正
    public final static Integer NEWS_TYPE_270 = 270;  //	问询函
    public final static Integer NEWS_TYPE_280 = 280;  //	调研
    public final static Integer NEWS_TYPE_290 = 290;  //	涨价


    /** stk_important_info.type **/
    public final static int IMPORT_INFO_TYPE_1 = 1;   //订单|中标|合同
    public final static int IMPORT_INFO_TYPE_3 = 3;   //牛散
    public final static int IMPORT_INFO_TYPE_4 = 4;   //增发|定增|非公
    public final static int IMPORT_INFO_TYPE_5 = 5;   //股权激励
    public final static int IMPORT_INFO_TYPE_20 = 20; //主营远大于利润
    public final static int IMPORT_INFO_TYPE_21 = 21; //募集资金使用大于80%


    /** stk_text.type **/
    public final static int TEXT_TYPE_ADD_BY_MYSELF = 0; //收藏文章:0;
    public final static int TEXT_TYPE_MANUAL = 1;//短文:1; => 手动添加
    public final static int TEXT_TYPE_AUTO = 2;//长文:2; => 系统自动  deleted
    public final static int TEXT_TYPE_XUEQIU = 3;  //雪球评论:3
    public final static int TEXT_TYPE_NOTICE = 4; //公告
    public final static int TEXT_TYPE_REPORT = 5; //研报
    public final static int TEXT_TYPE_HEART = 6; //加入自选备注

    public final static Map<Integer, String> TEXT_TYPE_MAP_ES_TYPE = new HashMap<Integer, String>(){{
        put(TEXT_TYPE_MANUAL, ES_TYPE_MANUAL);
        put(TEXT_TYPE_XUEQIU, ES_TYPE_POST);
        put(TEXT_TYPE_NOTICE, ES_TYPE_NOTICE);
        put(TEXT_TYPE_REPORT, ES_TYPE_REPORT);
        put(TEXT_TYPE_HEART, ES_TYPE_HEART);
    }};

    /** es type 和上面  stk_text.type 基本对应, 再加上其他一些type，比如：stock，industry等 **/
    public final static String ES_TYPE_MANUAL = "manual"; //我添加的文档
    public final static String ES_TYPE_STOCK = "stock";
    public final static String ES_TYPE_POST = "post";
    public final static String ES_TYPE_NOTICE = "notice";
    public final static String ES_TYPE_REPORT = "report";
    public final static String ES_TYPE_HEART = "heart";

    /** stk_text.code_type **/
    public final static int TEXT_CODE_TYPE_STOCK = 1; // 1:stock, 2:industry,

    /** stk_text.sub_type **/
    public final static int TEXT_SUB_TYPE_EARNING_FORECAST = 10;
    public final static int TEXT_SUB_TYPE_ORG_BUY_WITHIN_60 = 20;
    public final static int TEXT_SUB_TYPE_NIU_FUND_ONE_YEAR = 30;
    public final static int TEXT_SUB_TYPE_NIU_FUND_ALL_TIME = 31;
    public final static int TEXT_SUB_TYPE_FIND_REVERSION = 40;
    public final static int TEXT_SUB_TYPE_FIND_GROWTH = 45;
    public final static int TEXT_SUB_TYPE_STK_HOLDER_REDUCE = 50;
    public final static int TEXT_SUB_TYPE_COMPANY_RESEARCH = 100; //公司调研
    public final static int TEXT_SUB_TYPE_INDUSTRY_RESEARCH = 110; //行业分析
    public final static int TEXT_SUB_TYPE_STK_REPORT = 200; //年报季报
    public final static int TEXT_SUB_TYPE_XUEQIU_NOTICE = 300; //雪球公告

    public final static Map<String, String> TEXT_SUB_TYPE_MAP = new HashMap<String, String>(){{
        put(String.valueOf(TEXT_SUB_TYPE_COMPANY_RESEARCH), "公司研报");
        put(String.valueOf(TEXT_SUB_TYPE_INDUSTRY_RESEARCH), "行业分析");
        put(String.valueOf(TEXT_SUB_TYPE_STK_REPORT), "年报季报");
        put(String.valueOf(TEXT_SUB_TYPE_XUEQIU_NOTICE), "雪球公告");
    }};


    /** stk_report_header.type **/
    public final static String REPORT_HEADER_TYPE_ALLSTOCKS = "allstocks";
    public final static String REPORT_HEADER_TYPE_MYSTOCKS = "mystocks";
    public final static String REPORT_HEADER_TYPE_BKS = "bks";
    public final static String REPORT_HEADER_TYPE_RPSSTOCKS_STRATEGIES = "rpsstocks_strategies";
    public final static String REPORT_HEADER_TYPE_ALLSTOCKS_RPS = "allstocks_rps";
    public final static String REPORT_HEADER_TYPE_ALLSTOCKS_RPS_HK = "allstocks_rps_hk";
    public final static String REPORT_HEADER_TYPE_ALLSTOCKS_RPS_US = "allstocks_rps_us";

    /** stk_keyword_link.link_type **/
    public final static int KEYWORD_LINK_TYPE_DEFAULT = 0; //default(manually add):0
    public final static int KEYWORD_LINK_TYPE_MAIN_BUSINESS = 1;  //主营业务:1
    public final static int KEYWORD_LINK_TYPE_MAIN_PRODUCT = 2;  //主营产品:2
    /** stk_keyword_link.code_type **/
    public final static int KEYWORD_CODE_TYPE_STOCK = 1; //stock
    public final static int KEYWORD_CODE_TYPE_INDUSTRY = 2; //industry

    /** stk_keyword.status **/
    public final static int KEYWORD_STATUS__1 = -1;  //-1:deleted
    public final static int KEYWORD_STATUS_0 = 0;  // 0:normal(manual-add)
    public final static int KEYWORD_STATUS_1 = 1;  // 1:normal(auto-add)


    /** stk_status.type **/
    public final static Integer STATUS_TYPE_1 = 1;  //	阶段排除股票 => stk_dict=5000
    public final static Integer STATUS_TYPE_2 = 2;  //	自选股标签  => stk_dict=5010
}
