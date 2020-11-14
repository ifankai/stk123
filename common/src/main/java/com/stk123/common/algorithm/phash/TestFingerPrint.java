//package com.stk123.tool.algorithm.phash;
//
//import java.io.File;
//import java.io.IOException;
//
//import javax.imageio.ImageIO;
//
//import org.junit.Test;
//
//public class TestFingerPrint {
//
//    @Test
//    public void testCompare() throws IOException{
//        FingerPrint fp1 = new FingerPrint(ImageIO.read(new File("d:\\GetPic1.png")));
//        FingerPrint fp2 =new FingerPrint(ImageIO.read(new File("d:\\GetPic3.png")));
//        System.out.println(fp1.toString(true));
//        System.out.printf("similar rate=%f",fp1.compare(fp2));
//    }
//}