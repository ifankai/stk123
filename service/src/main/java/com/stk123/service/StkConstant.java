package com.stk123.service;

public class StkConstant {

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
    public final static int TEXT_TYPE_SHORT_TEXT = 1;
    public final static int TEXT_TYPE_LONG_TEXT = 2;
    public final static int TEXT_TYPE_XUEQIU = 3;  //--0:收藏文章; 短文:1; 长文:2; 雪球评论:3
    public final static int TEXT_TYPE_NOTICE = 4; //公告

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


    /** stk_report_header.type **/
    public final static String REPORT_HEADER_TYPE_ALLSTOCKS = "allstocks";
    public final static String REPORT_HEADER_TYPE_MYSTOCKS = "mystocks";
    public final static String REPORT_HEADER_TYPE_BKS = "bks";
    public final static String REPORT_HEADER_TYPE_RPSSTOCKS_STRATEGIES = "rpsstocks_strategies";
    public final static String REPORT_HEADER_TYPE_ALLSTOCKS_RPS = "allstocks_rps";

    /** stk_keyword_link.link_type **/
    public final static int KEYWORD_LINK_TYPE_0 = 0; //default(manually add):0
    public final static int KEYWORD_LINK_TYPE_1 = 1;  //主营业务:1

    public final static int KEYWORD_STATUS__1 = 1;  //-1:deleted

}
