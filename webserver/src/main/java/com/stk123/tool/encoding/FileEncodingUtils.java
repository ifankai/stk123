package com.stk123.tool.encoding;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;

import java.io.*;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileEncodingUtils {

    public static void main(String[] args) throws Exception {
        EncodingDetect ed = new EncodingDetect();
        String srcDir = "D:\\git\\stk123\\src\\com\\stk123";
        String tarDir = "D:\\git\\stk123\\src_utf8\\com\\stk123";
        Collection<File> files = FileUtils.listFiles(new File(srcDir), new String[]{"java"}, true);
        for (File srcfile : files) {
            displayFileEncoding(ed, srcfile);
            String srcFileEncoding = EncodingDetect.codings[ed.detectEncoding(srcfile)];
            File tarFile = new File(StringUtils.replace(srcfile.toString(), "src", "src_utf8"));
            javaToUTF8(srcfile, srcFileEncoding, tarFile, "UTF-8");
        }
        System.out.println("---------------------");
        files = FileUtils.listFiles(new File(tarDir), new String[]{"java"}, true);
        for (File file : files) {
            displayFileEncoding(ed, file);
        }
    }

    public static void transform(File source, String srcEncoding, File target, String tgtEncoding) throws IOException {
        BufferedReader br = null;
        BufferedWriter bw = null;
        try{
            br = new BufferedReader(new InputStreamReader(new FileInputStream(source),srcEncoding));
            bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(target), tgtEncoding));
            char[] buffer = new char[16384];
            int read;
            while ((read = br.read(buffer)) != -1)
                bw.write(buffer, 0, read);
        } finally {
            try {
                if (br != null)
                    br.close();
            } finally {
                if (bw != null)
                    bw.close();
            }
        }
    }

    public static void javaToUTF8(File srcfile, String srcFileEncoding, File tarFile, String tarFileEncoding) throws IOException {
        if("OTHER".equals(srcFileEncoding)){
            System.out.println("[OTHER]file:" + srcfile.toString() + ", encoding:"+srcFileEncoding);
        }
        String fileContent = FileUtils.readFileToString(srcfile, srcFileEncoding);
        FileUtils.writeStringToFile(tarFile, new String(fileContent.getBytes(tarFileEncoding), tarFileEncoding), tarFileEncoding);
    }

    public static void displayFileEncoding(EncodingDetect ed, File file) throws IOException {
        String fileEncode = EncodingDetect.codings[ed.detectEncoding(file)];
        if("OTHER".equals(fileEncode)){
            System.err.println("[OTHER]file:" + file.toString() + ", encoding:"+fileEncode);
        }
        String fileContent = FileUtils.readFileToString(file, fileEncode);
        if(hasMessyCode(fileContent)){
            System.err.println("[MessyCode]file:" + file.toString() + ", encoding:"+fileEncode);
        }else {
            System.out.println("file:" + file.toString() + ", encoding:"+fileEncode);
        }
    }

    //是否存在乱码
    public static boolean hasMessyCode(String str) {
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
