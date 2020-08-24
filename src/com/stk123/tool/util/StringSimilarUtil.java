package com.stk123.tool.util;

public class StringSimilarUtil {

    private final static String[] COLOR = {"red", "green", "blue"};

    public static void main(String[] args) {
        String s1 = "http://www.cninfo.com.cn//disclosure/fulltext/stocks/fulltext1y/cninfo/603322.js?ver=20200821224002";
        String s2 = "https://blog.csdn.net/weixin_33966095/article/details/91771649";
        System.out.println(StringSimilarUtil.getStringSimilarityRatio(s1, s2));
    }
    private static StringSimilarUtil instance = new StringSimilarUtil();


    private static String getColorName(String color) {
        if (color == null || color.trim().length() == 0)
            return null;
        char[] cha = color.toCharArray();
        String str = "";
        for (int i = 0; i < cha.length; i++) {
            String str1 = matchStr(cha[i] + "", COLOR);
            if (str1 != null) {
                str += str1;
            }
        }
        return str;
    }

    /**
     * 匹配字符串
     *
     * @param param
     *            匹配字符
     * @param arr
     *            匹配字库
     * @return 匹配到的字符
     */
    public static String matchStr(String param, String[] arr) {
        return instance.getDate(param, arr);
    }

    public String getDate(String param, String[] arr) {
        if (param == null || "".equals(param.trim())) {
            return null;
        }
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0, count = 0; i < arr.length && count < 10; i++) {
            if (arr[i].matches(this.changeStyle(param))) {
                // if(arr[i].indexOf(param)==0)
                // {
                sb.append("\"" + arr[i] + "\",");
                count++;
            }
        }

        sb.deleteCharAt(sb.length() - 1); // 删除最后一个，
        sb.append("]");
        if (sb.length() <= 2) { // 没有找到数据
            return null;
        }
        return sb.toString();
    }

    public String changeStyle(String old) {
        StringBuilder stringBuilder = new StringBuilder();

        char[] chararray = old.toCharArray();

        stringBuilder.append(".*");

        for (int i = 0; i < chararray.length; i++) {
            stringBuilder.append(chararray[i] + ".*");
        }
        return stringBuilder.toString();
    }

    private int compare(String str, String target) {
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

    private int min(int one, int two, int three) {
        return (one = one < two ? one : two) < three ? one : three;
    }

    /**
     *
     * 获取两字符串的相似度
     *
     *
     *
     * @param str
     *
     * @param target
     *
     * @return
     */
    public float getSimilarityRatio(String str, String target) {
        return 1 - (float) compare(str, target)
                / Math.max(str.length(), target.length());
    }

    public static float getStringSimilarityRatio(String str, String target) {
        return instance.getSimilarityRatio(str, target);
    }
}
