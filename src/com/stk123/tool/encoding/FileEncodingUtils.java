package com.stk123.tool.encoding;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileEncodingUtils {

    public static void main(String[] args) throws Exception {
        EncodingDetect ed = new EncodingDetect();
        Collection<File> files = FileUtils.listFiles(new File("D:\\git\\stk123\\src\\com\\stk123"), new String[]{"java"}, true);
        for (File file : files) {
            String fileEncode = EncodingDetect.codings[ed.detectEncoding(file)];
            if("OTHER".equals(fileEncode)){
                System.out.println("[OTHER]file:" + file.toString() + ", encoding:"+fileEncode);
                continue;
            }
            String fileContent = FileUtils.readFileToString(file, fileEncode);
            if(isMessyCode(fileContent)){
                System.out.println("file:" + file.toString() + ", encoding:"+fileEncode);
            }
        }

    }

    //是否存在乱码
    public static boolean isMessyCode(String str) {
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            // 当从Unicode编码向某个字符集转换时，如果在该字符集中没有对应的编码，则得到0x3f（即问号字符?）
            //从其他字符集向Unicode编码转换时，如果这个二进制数在该字符集中没有标识任何的字符，则得到的结果是0xfffd
            if ((int) c == 0xfffd) {
                // 存在乱码
                return true;
            }
        }
        return false;
    }

}
