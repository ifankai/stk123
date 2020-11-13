package com.stk123.common.util;

import java.io.UnsupportedEncodingException;

import com.stk123.common.CommonConstant;

public class ChineseUtils {

    private final static String BLANK = "";
    /**
     * 判断一个字符是Ascill字符还是其它字符（如汉，日，韩文字符）
     *
     * @param c 需要判断的字符
     * @return 返回true,Ascill字符
     */
    public static boolean isLetter(char c) {
        int k = 0x80;
        return c / k == 0 ? true : false;
    }

    /**
     * 得到一个字符串的长度,显示的长度,一个汉字或日韩文长度为2,英文字符长度为1
     *
     * @param s 需要得到长度的字符串
     * @return i得到的字符串长度
     */
    public static int length(String s) {
        if (s == null)
            return 0;
        char[] c = s.toCharArray();
        int len = 0;
        for (int i = 0; i < c.length; i++) {
            len++;
            if (!isLetter(c[i])) {
                len++;
            }
        }
        return len;
    }

    public static int lengthForOracle(String s) {
        if (s == null)
            return 0;
        char[] c = s.toCharArray();
        int len = 0;
        for (int i = 0; i < c.length; i++) {
            len++;
            if (!isLetter(c[i])) {
                len++;
                len++;//for oracle 发现一个汉字占3个字节
                /*
                SQL> select parameter,value from nls_database_parameters where parameter like 'NLS_CHARACTERSET';

                PARAMETER
                ------------------------------------------------------------
                VALUE
                --------------------------------------------------------------------------------
                NLS_CHARACTERSET
                AL32UTF8
                */
            }
        }
        return len;
    }

    /**
     * 截取一段字符的长度,不区分中英文,如果数字不正好，则少取一个字符位
     *
     * @param  origin 原始字符串
     * @param len 截取长度(一个汉字长度按2算的)
     * @param c 后缀
     * @return 返回的字符串
     */
    public static String substring(String origin, int len, String c) {
        if (origin == null || origin.equals(BLANK) || len < 1)
            return BLANK;
        byte[] strByte = new byte[len];
        if (len >= length(origin)) {
            return origin+c;
        }
        try {
            System.arraycopy(origin.getBytes(CommonConstant.ENCODING_GBK), 0, strByte, 0, len);
            int count = 0;
            for (int i = 0; i < len; i++) {
                int value = (int) strByte[i];
                if (value < 0) {
                    count++;
                }
            }
            if (count % 2 != 0) {
                len = (len == 1) ? ++len : --len;
            }
            len = len - 2;
            return new String(strByte, 0, len, CommonConstant.ENCODING_GBK)+c;
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }


    // 简体中文的编码范围从B0A1（45217）一直到F7FE（63486）
    private static int BEGIN = 45217;
    private static int END = 63486;

    // 按照声母表示，这个表是在GB2312中的出现的第一个汉字，也就是说“啊”是代表首字母a的第一个汉字。
    // i, u, v都不做声母, 自定规则跟随前面的字母
    private static char[] chartable = { '啊', '芭', '擦', '搭', '蛾', '发', '噶', '哈',
            '哈', '击', '喀', '垃', '妈', '拿', '哦', '啪', '期', '然', '撒', '塌', '塌',
            '塌', '挖', '昔', '压', '匝', };

    // 二十六个字母区间对应二十七个端点
    // GB2312码汉字区间十进制表示
    private static int[] table = new int[27];

    // 对应首字母区间表
    private static char[] initialtable = { 'a', 'b', 'c', 'd', 'e', 'f', 'g',
            'h', 'h', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't',
            't', 't', 'w', 'x', 'y', 'z', };

    // 初始化
    static {
        for (int i = 0; i < 26; i++) {
            table[i] = gbValue(chartable[i]);// 得到GB2312码的首字母区间端点表，十进制。
        }
        table[26] = END;// 区间表结尾
    }

    // ------------------------public方法区------------------------
    /**
     * 根据一个包含汉字的字符串返回一个汉字拼音首字母的字符串 最重要的一个方法，思路如下：一个个字符读入、判断、输出
     */
    public static String cn2py(String SourceStr) {
        String Result = "";
        int StrLength = SourceStr.length();
        int i;
        try {
            for (i = 0; i < StrLength; i++) {
                Result += Char2Initial(SourceStr.charAt(i));
            }
        } catch (Exception e) {
            Result = "";
        }
        return Result;
    }

    // ------------------------private方法区------------------------
    /**
     * 输入字符,得到他的声母,英文字母返回对应的大写字母,其他非简体汉字返回 '0'
     *
     */
    private static char Char2Initial(char ch) {
        // 对英文字母的处理：小写字母转换为大写，大写的直接返回
        if (ch >= 'a' && ch <= 'z')
            return (char) (ch - 'a' + 'A');
        if (ch >= 'A' && ch <= 'Z')
            return ch;

        // 对非英文字母的处理：转化为首字母，然后判断是否在码表范围内，
        // 若不是，则直接返回。
        // 若是，则在码表内的进行判断。
        int gb = gbValue(ch);// 汉字转换首字母

        if ((gb < BEGIN) || (gb > END))// 在码表区间之前，直接返回
            return ch;

        int i;
        for (i = 0; i < 26; i++) {// 判断匹配码表区间，匹配到就break,判断区间形如“[,)”
            if ((gb >= table[i]) && (gb < table[i+1]))
                break;
        }

        if (gb==END) {//补上GB2312区间最右端
            i=25;
        }
        return initialtable[i]; // 在码表区间中，返回首字母
    }

    /**
     * 取出汉字的编码 cn 汉字
     */
    private static int gbValue(char ch) {// 将一个汉字（GB2312）转换为十进制表示。
        String str = new String();
        str += ch;
        try {
            byte[] bytes = str.getBytes("GB2312");
            if (bytes.length < 2)
                return 0;
            return (bytes[0] << 8 & 0xff00) + (bytes[1] & 0xff);
        } catch (Exception e) {
            return 0;
        }
    }

    public static void main(String[] args) throws Exception {
        System.out.println(length("上周"));
    }
}
