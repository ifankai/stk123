package com.stk123;

import java.util.Scanner;

import org.apache.commons.lang.StringUtils;

import com.stk123.common.util.ChineseUtils;

/*****
 * idea shortcut: https://blog.csdn.net/fmwind/article/details/80930840
 * F11:bookmark
 * shift+F11:view bookmark
 * ctrl+F12:file structure/find method
 * ctrl+shift+c: copy path
 * ctrl + q: view javadoc(查看说明)
 * Ctrl + E: 显示最近打开的文件记录列表
 * ctrl + alt + l:格式化
 */

public class Test {

	private static String str = "ABCDE";// 字符串
	   private static int n = 3;// 选择的个数
	   private static int count = 0;//组合的个数

	   public static void main(String[] args) throws Exception {
	       //new Test();
		   //System.out.println(String.format("%s请输入要选择的个数%s", str, str));
		   System.out.println(StringUtils.leftPad("柳 工", 4, "\t"));
		   System.out.println(ChineseUtils.length("柳 工"));
		   System.out.println(StringUtils.leftPad("1", 4, "0"));
		  //logger.error("ddddddddddddddd");

		   StackTraceElement[] temp=Thread.currentThread().getStackTrace();
		   StackTraceElement a=(StackTraceElement)temp[1];
		   System.out.println("----from--"+a.getMethodName()+"--");


	   }

	   public static String formatLeftS(String str, int min_length) {
	        String format = "%-" + (min_length < 1 ? 1 : min_length) + "s";
	        return String.format(format, str);
	    }

	   public Test() {
	       Scanner input = new Scanner(System.in);
	       System.out.println("请输入要选择的个数（要少于" + str.length() + "个）");
	       n = Integer.parseInt(input.nextLine());
	       find("", 0);
	       System.out.println("共有"+count+"种组合");

	   }
	   /**
	    *第一个参数是代表第一个字符，第二个参数代表开始寻找点的位置
	    */
	   public static void find(String s, int i) {
	       // 保存上一次的字符串
	       String temp = s;
	       //判断是否符合要求
	       if (s.length() == n) {
	           count++;

	           System.out.print(s + " ");
	           if (count % 10 == 0)
	               System.out.println();
	           return;
	       }
	       //从寻找点开始循环，风之境地
	       for (int k =i; k < str.length(); k++) {
	           s = temp;
	           s += str.charAt(k);
	           find(s, k);
	       }
	   }

}
