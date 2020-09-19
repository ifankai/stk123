package com.stk123.tool.util;

import org.apache.commons.lang3.StringUtils;

//https://vip.stock.finance.sina.com.cn/corp/view/vCB_AllBulletinDetail.php?stockid=600600&id=6578044
public class StrUtils {

    public static void main(String[] args) {
        String s = "第十节 财务报告 ...... 27\n" +
                "第十一节 备查文件目录 ...... 158\n" +
                "\n" +
                "青岛啤酒股份有限公司2020年半年度报告" +
                "目录\n" +
                "\n" +
                "第一节 释义 ...... 4\n" +
                "\n" +
                "第二节 公司简介和主要财务指标 ...... 4\n" +
                "\n" +
                "第三节 公司业务概要 ...... 6\n" +
                "\n" +
                "第四节 经营情况的讨论与分析 ...... 7\n" +
                "\n" +
                "第五节 重要事项 ...... 12";
        System.out.println(indexOfStringWithKeywordAfter(s,"目录", 10, "第一节"));
    }

    /**
     * 查找searchStr，并且找到的searchStr后面n个位置内必须包含任一containStrs
     */
    public static int indexOfStringWithKeywordAfter(String str, String searchStr, int n, String... containStrs) {
        int length = StringUtils.length(searchStr);
        int pos = 0;
        do {
            pos = StringUtils.indexOf(str, searchStr, pos);
            //System.out.println("pos:"+pos);
            if(pos == -1) return pos;

            String afterStr = StringUtils.substring(str, pos+length, pos+length+n);
            if(StringUtils.containsAny(afterStr, containStrs)) {
                return pos;
            }else{
                pos = pos + length;
            }
        }while(true);
    }

}
