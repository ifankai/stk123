package com.stk123.service;

public class StkConstant {

    /** dict **/
    public final static Integer DICT_MONITOR_K_PARAM1 = new Integer(1);
    public final static Integer DICT_MONITOR_K_PARAM2 = new Integer(2);

    public final static Integer DICT_XUEQIU_COMMENT = new Integer(3);
    public final static Integer DICT_XUEQIU_ARTICLE = new Integer(4);
    public final static Integer DICT_XUEQIU_ZHUTIE = new Integer(5);//雪球主贴

    public final static Integer DICT_NIUSAN = new Integer(20);//牛散
    public final static Integer DICT_FAMOUS_FUNDS = new Integer(21);//著名基金  insert into stk_dictionary select 21,1,'华夏大盘精选',null,1,null,null,null,null from dual;

    public final static Integer DICT_INTERNET_SEARCH_TYPE = new Integer(10);//internet search type

    public final static Integer DICT_STKS_COLUMN_NAMES = new Integer(1000);//多股同列下显示的列名
    public final static Integer DICT_INDUSTRY_SOURCE = new Integer(300);//行业分类来源
    public final static Integer DICT_TEXT_SUB_TYPE = new Integer(400);//文档子类型

    public final static Integer DICT_NEWS = 2000;

    //news type
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


    //important info
    public final static int IMPORT_TYPE_1 = 1; //订单|中标|合同
    public final static int IMPORT_TYPE_3 = 3; //牛散
    public final static int IMPORT_TYPE_4 = 4; //增发|定增|非公
    public final static int IMPORT_TYPE_5 = 5; //股权激励
    public final static int IMPORT_TYPE_20 = 20; //主营远大于利润
    public final static int IMPORT_TYPE_21 = 21; //募集资金使用大于80%
}
