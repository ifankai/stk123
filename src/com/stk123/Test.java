package com.stk123;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Scanner;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.stk123.tool.util.ChineseUtils;


public class Test {
	
	private static Logger logger = Logger.getLogger(Test.class);

	private static String str = "ABCDE";// �ַ���
	   private static int n = 3;// ѡ��ĸ���
	   private static int count = 0;//��ϵĸ���

	   public static void main(String[] args) throws Exception {
	       //new Test();
		   //System.out.println(String.format("%s������Ҫѡ��ĸ���%s", str, str));
		   System.out.println(StringUtils.leftPad("�� ��", 4, "\t"));
		   System.out.println(ChineseUtils.length("�� ��"));
		   System.out.println(StringUtils.leftPad("�к��Ѱ�", 4, "\t"));
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
	       System.out.println("������Ҫѡ��ĸ�����Ҫ����" + str.length() + "����");
	       n = Integer.parseInt(input.nextLine());
	       find("", 0);
	       System.out.println("����"+count+"�����");
	       
	   }
	   /**
	    *��һ�������Ǵ����һ���ַ����ڶ�����������ʼѰ�ҵ��λ��
	    */
	   public static void find(String s, int i) {
	       // ������һ�ε��ַ���
	       String temp = s;
	       //�ж��Ƿ����Ҫ��
	       if (s.length() == n) {
	           count++;

	           System.out.print(s + " ");
	           if (count % 10 == 0)
	               System.out.println();
	           return;
	       }
	       //��Ѱ�ҵ㿪ʼѭ������֮����
	       for (int k =i; k < str.length(); k++) {
	           s = temp;
	           s += str.charAt(k);
	           find(s, k);
	       }     
	   }

}
