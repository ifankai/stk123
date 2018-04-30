package com.stk123.tool.util;

import java.io.UnsupportedEncodingException;

import com.stk123.web.StkConstant;

public class ChineseUtils {
	
	private final static String BLANK = "";
	/**  
     * �ж�һ���ַ���Ascill�ַ����������ַ����纺���գ������ַ���  
     *   
     * @param c ��Ҫ�жϵ��ַ�  
     * @return ����true,Ascill�ַ�  
     */   
    public static boolean isLetter(char c) {   
        int k = 0x80;   
        return c / k == 0 ? true : false;   
    }   
   
    /**  
     * �õ�һ���ַ����ĳ���,��ʾ�ĳ���,һ�����ֻ��պ��ĳ���Ϊ2,Ӣ���ַ�����Ϊ1  
     *   
     * @param s ��Ҫ�õ����ȵ��ַ���  
     * @return i�õ����ַ�������  
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
                len++;//for oracle ����һ������ռ3���ֽ�
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
     * ��ȡһ���ַ��ĳ���,��������Ӣ��,������ֲ����ã�����ȡһ���ַ�λ  
     *   
     * @param  origin ԭʼ�ַ���  
     * @param len ��ȡ����(һ�����ֳ��Ȱ�2���)  
     * @param c ��׺             
     * @return ���ص��ַ���  
     */   
    public static String substring(String origin, int len, String c) {   
        if (origin == null || origin.equals(BLANK) || len < 1)   
            return BLANK;   
        byte[] strByte = new byte[len]; 
        if (len >= length(origin)) {   
            return origin+c;   
        }   
        try {   
            System.arraycopy(origin.getBytes(StkConstant.ENCODING_GBK), 0, strByte, 0, len);   
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
            return new String(strByte, 0, len, StkConstant.ENCODING_GBK)+c;   
        } catch (UnsupportedEncodingException e) {   
            throw new RuntimeException(e);   
        }   
    } 
    
    
 // �������ĵı��뷶Χ��B0A1��45217��һֱ��F7FE��63486��
    private static int BEGIN = 45217;
    private static int END = 63486;

    // ������ĸ��ʾ�����������GB2312�еĳ��ֵĵ�һ�����֣�Ҳ����˵�������Ǵ�������ĸa�ĵ�һ�����֡�
    // i, u, v��������ĸ, �Զ��������ǰ�����ĸ
    private static char[] chartable = { '��', '��', '��', '��', '��', '��', '��', '��',
            '��', '��', '��', '��', '��', '��', 'Ŷ', 'ž', '��', 'Ȼ', '��', '��', '��',
            '��', '��', '��', 'ѹ', '��', };

    // ��ʮ������ĸ�����Ӧ��ʮ�߸��˵�
    // GB2312�뺺������ʮ���Ʊ�ʾ
    private static int[] table = new int[27];

    // ��Ӧ����ĸ�����
    private static char[] initialtable = { 'a', 'b', 'c', 'd', 'e', 'f', 'g',
            'h', 'h', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't',
            't', 't', 'w', 'x', 'y', 'z', };

    // ��ʼ��
    static {
        for (int i = 0; i < 26; i++) {
            table[i] = gbValue(chartable[i]);// �õ�GB2312�������ĸ����˵��ʮ���ơ�
        }
        table[26] = END;// ������β
    }

    // ------------------------public������------------------------
    /**
     * ����һ���������ֵ��ַ�������һ������ƴ������ĸ���ַ��� ����Ҫ��һ��������˼·���£�һ�����ַ����롢�жϡ����
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

    // ------------------------private������------------------------
    /**
     * �����ַ�,�õ�������ĸ,Ӣ����ĸ���ض�Ӧ�Ĵ�д��ĸ,�����Ǽ��庺�ַ��� '0'
     * 
     */
    private static char Char2Initial(char ch) {
        // ��Ӣ����ĸ�Ĵ���Сд��ĸת��Ϊ��д����д��ֱ�ӷ���
        if (ch >= 'a' && ch <= 'z')
            return (char) (ch - 'a' + 'A');
        if (ch >= 'A' && ch <= 'Z')
            return ch;

        // �Է�Ӣ����ĸ�Ĵ���ת��Ϊ����ĸ��Ȼ���ж��Ƿ������Χ�ڣ�
        // �����ǣ���ֱ�ӷ��ء�
        // ���ǣ���������ڵĽ����жϡ�
        int gb = gbValue(ch);// ����ת������ĸ

        if ((gb < BEGIN) || (gb > END))// ���������֮ǰ��ֱ�ӷ���
            return ch;

        int i;
        for (i = 0; i < 26; i++) {// �ж�ƥ��������䣬ƥ�䵽��break,�ж��������硰[,)��
                if ((gb >= table[i]) && (gb < table[i+1]))
                    break;
        }
        
        if (gb==END) {//����GB2312�������Ҷ�
            i=25;
        }
        return initialtable[i]; // ����������У���������ĸ
    }

    /**
     * ȡ�����ֵı��� cn ����
     */
    private static int gbValue(char ch) {// ��һ�����֣�GB2312��ת��Ϊʮ���Ʊ�ʾ��
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
    	System.out.println(cn2py("�������ӷ�չIT��ҵ������������磬IBM�Ƚ�פɽ��"));
    }
}
