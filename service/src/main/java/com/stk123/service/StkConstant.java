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


}
