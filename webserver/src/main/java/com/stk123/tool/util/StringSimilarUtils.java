package com.stk123.tool.util;

import com.stk123.tool.ik.IKUtils;

import java.util.List;

public class StringSimilarUtils {

    private final static String[] COLOR = {"red", "green", "blue"};

    public static void main(String[] args) throws Exception {
        String s1 = "http://www.cninfo.com.cn//disclosure/fulltext/stocks/fulltext1y/cninfo/603322.js?ver=20200821224002";
        String s2 = "http://blog.csdn.net/weixin_33966095/article/details/91771649";
        System.out.println(StringSimilarUtils.getSimilarRatio(s1, s2));
        System.out.println(StringSimilarUtils.getSimilarRateByIKAnalyzer(s1, s2));
    }

    //通过分词器得到相似度
    public static double getSimilarRateByIKAnalyzer(String o1, String o2) throws Exception{
        List<String> chs1 = IKUtils.split(o1.toString());
        List<String> chs2 = IKUtils.split(o2.toString());
        List<String> min = chs1;
        List<String> max = chs2;
        if(chs1.size() > chs2.size()){
            min = chs2;
            max = chs1;
        }
        double match = 0;
        for(String str : min){
            if(max.contains(str)){
                match ++;
            }
        }
        //System.out.println("similar rate="+(match/min.size()));
        return match/min.size();
    }

    /**
     * 获取两字符串的相似度
     */
    public static float getSimilarRatio(String str, String target) {
        return 1 - (float) compare(str, target)
                / Math.max(str.length(), target.length());
    }

    private static int compare(String str, String target) {
        int d[][]; // 矩阵
        int n = str.length();
        int m = target.length();
        int i; // 遍历str 的
        int j; // 遍历target的
        char ch1; // str的
        char ch2; // target的
        int temp; // 记录相同字符,在某个矩阵位置值的增量,不是0就是1
        if (n == 0) {
            return m;
        }
        if (m == 0) {
            return n;
        }
        d = new int[n + 1][m + 1];
        for (i = 0; i <= n; i++) { // 初始化第一列
            d[i][0] = i;
        }

        for (j = 0; j <= m; j++) { // 初始化第一行
            d[0][j] = j;
        }

        for (i = 1; i <= n; i++) { // 遍历str
            ch1 = str.charAt(i - 1);
            // 去匹配target
            for (j = 1; j <= m; j++) {
                ch2 = target.charAt(j - 1);
                if (ch1 == ch2) {
                    temp = 0;
                } else {
                    temp = 1;
                }

                // 左边+1,上边+1, 左上角+ temp取最小
                d[i][j] = min(d[i - 1][j] + 1, d[i][j - 1] + 1, d[i - 1][j - 1]
                        + temp);
            }
        }
        return d[n][m];
    }

    private static int min(int one, int two, int three) {
        return (one = one < two ? one : two) < three ? one : three;
    }


}
