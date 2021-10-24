package com.stk123.common.util;

import cn.hutool.core.codec.Base64;

import java.io.*;

public class ImageUtils {
	
	public static void main(String[] args){
		System.out.println(getImageStr("D:\\K线图\\000990.PNG"));
	}

	public static String getImageStr(String imgFile) {// 将图片文件转化为字节数组字符串，并对其进行Base64编码处理
		//String imgFile = "d:\\111.jpg";// 待处理的图片
		InputStream in = null;
		byte[] data = null;
		// 读取图片字节数组
		try {
			in = new FileInputStream(imgFile);
			data = new byte[in.available()];
			in.read(data);
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// 对字节数组Base64编码
		return Base64.encode(data);// 返回Base64编码过的字节数组字符串
	}
	
	public static String getImageStr(byte[] data) {
		if(data == null)return null;
		// 对字节数组Base64编码
		//BASE64Encoder encoder = new BASE64Encoder();
		return Base64.encode(data);// 返回Base64编码过的字节数组字符串
	}
	
	/**
	 * 将Base64位编码的图片进行解码，并保存到指定目录
	 * 
	 * @param base64
	 *            base64编码的图片信息
	 * @return
	 */
	public static void decodeBase64ToImage(String base64, String path,
			String imgName) {
		try {
			FileOutputStream write = new FileOutputStream(path + imgName);
			byte[] decoderBytes = Base64.decode(base64);
			write.write(decoderBytes);
			write.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static boolean GenerateImage(String imgStr) {// 对字节数组字符串进行Base64解码并生成图片
		if (imgStr == null) // 图像数据为空
			return false;
		try {
			// Base64解码
			byte[] b = Base64.decode(imgStr);
			for (int i = 0; i < b.length; ++i) {
				if (b[i] < 0) {// 调整异常数据
					b[i] += 256;
				}
			}
			// 生成jpeg图片
			String imgFilePath = "d:\\222.jpg";// 新生成的图片
			OutputStream out = new FileOutputStream(imgFilePath);
			out.write(b);
			out.flush();
			out.close();
			return true;
		} catch (Exception e) {
			return false;
		}
	}

}
