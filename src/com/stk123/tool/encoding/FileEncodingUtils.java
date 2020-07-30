package com.stk123.tool.encoding;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

public class FileEncodingUtils {

    public static void main(String[] args) throws Exception {
        EncodingDetect ed = new EncodingDetect();
        Collection<File> files = FileUtils.listFiles(new File("D:\\git\\stk123\\src\\com\\stk123"), new String[]{"java"}, true);
        for (File file : files) {
            String fileContent = FileUtils.readFileToString(file);
            if(test(fileContent)){
                String fileEncode = EncodingDetect.codings[ed.detectEncoding(file)];
                System.out.println("file:" + file.toString() + ", encoding:"+fileEncode);
            }
        }

    }

    public static boolean test(String str) {
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
