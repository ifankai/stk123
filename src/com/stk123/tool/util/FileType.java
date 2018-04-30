package com.stk123.tool.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import org.apache.commons.lang.StringUtils;

public class FileType {
	// �����ļ�ͷ��Ϣ-�ļ�ͷ��Ϣ
	public static final HashMap<String, String> mFileTypes = new MatchHashMap<String, String>();

	static {
		// images
		mFileTypes.put("FFD8FF", "jpg");
		mFileTypes.put("89504E47", "png");
		mFileTypes.put("47494638", "gif");
		mFileTypes.put("49492A00", "tif");
		mFileTypes.put("424D", "bmp");
		//
		mFileTypes.put("41433130", "dwg"); // CAD
		mFileTypes.put("38425053", "psd");
		mFileTypes.put("7B5C7274", "rtf"); // �ռǱ�
		mFileTypes.put("3C3F786D6C", "xml");
		mFileTypes.put("68746D6C3E", "html");
		mFileTypes.put("44656C69766572792D646174653A", "eml"); // �ʼ�
		mFileTypes.put("D0CF11E0", "doc");
		mFileTypes.put("5374616E64617264204A", "mdb");
		mFileTypes.put("252150532D41646F6265", "ps");
		mFileTypes.put("255044462D312E", "pdf");
		mFileTypes.put("504B0304", "docx");
		mFileTypes.put("52617221", "rar");
		mFileTypes.put("57415645", "wav");
		mFileTypes.put("41564920", "avi");
		mFileTypes.put("2E524D46", "rm");
		mFileTypes.put("000001BA", "mpg");
		mFileTypes.put("000001B3", "mpg");
		mFileTypes.put("6D6F6F76", "mov");
		mFileTypes.put("3026B2758E66CF11", "asf");
		mFileTypes.put("4D546864", "mid");
		mFileTypes.put("1F8B08", "gz");
		mFileTypes.put("504B030414", "pptx");
	}

	/**
	 * �����ļ�·����ȡ�ļ�ͷ��Ϣ
	 * 
	 * @param filePath
	 *            �ļ�·��
	 * @return �ļ�ͷ��Ϣ
	 */
	public static String getFileType(String filePath) {
		return mFileTypes.get(getFileHeader(filePath));
	}

	/**
	 * �����ļ�·����ȡ�ļ�ͷ��Ϣ
	 * 
	 * @param filePath
	 *            �ļ�·��
	 * @return �ļ�ͷ��Ϣ
	 */
	public static String getFileHeader(String filePath) {
		FileInputStream is = null;
		String value = null;
		try {
			is = new FileInputStream(filePath);
			byte[] b = new byte[4];
			is.read(b, 0, b.length);
			value = bytesToHexString(b);
			//System.out.println(value);
		} catch (Exception e) {
		} finally {
			if (null != is) {
				try {
					is.close();
				} catch (IOException e) {
				}
			}
		}
		return value;
	}

	/**
	 * �����Ķ����ȡ�ļ�ͷ��Ϣ
	 * 
	 * @param filePath
	 *            �ļ�·��
	 * @return �ļ�ͷ��Ϣ
	 */
	public static String getFileHeader(File filePath) {
		InputStream is = null;
		String value = null;
		try {
			is = new FileInputStream(filePath);
			byte[] b = new byte[4];
			is.read(b, 0, b.length);
			value = bytesToHexString(b);
		} catch (Exception e) {
		} finally {
			if (null != is) {
				try {
					is.close();
				} catch (IOException e) {
				}
			}
		}
		return value;
	}

	/**
	 * ��Ҫ��ȡ�ļ�ͷ��Ϣ���ļ���byte����ת����string���ͱ�ʾ
	 * 
	 * @param src
	 *            Ҫ��ȡ�ļ�ͷ��Ϣ���ļ���byte����
	 * @return �ļ�ͷ��Ϣ ������δ�������������ļ���������֤�ķ����� ��һ���������ļ����ֽ����飬�ڶ������Ƕ���Ŀ�ͨ�����͡�����ܼ򵥣�
	 *         ��Ҫ��ע���м��һ��
	 *         �����ֽ������ǰ��λת����16�����ַ���������ת����ʱ��Ҫ�Ⱥ�0xFF��һ�������㡣������Ϊ�������ļ������ֽ�������
	 *         ���кܶ��Ǹ���
	 *         ��������������󣬿��Խ�ǰ��ķ���λ��ȥ��������ת���ɵ�16�����ַ�����ౣ����λ�������������С��10����ôת����ֻ��һλ
	 *         ����Ҫ��ǰ�油0����������Ŀ���Ƿ���Ƚϣ�ȡ��ǰ��λ���ѭ���Ϳ�����ֹ�ˡ�
	 */
	private static String bytesToHexString(byte[] src) {
		StringBuilder builder = new StringBuilder();
		if (src == null || src.length <= 0) {
			return null;
		}
		String hv;
		for (int i = 0; i < src.length; i++) {
			// ��ʮ�����ƣ����� 16���޷���������ʽ����һ�������������ַ�����ʾ��ʽ����ת��Ϊ��д
			hv = Integer.toHexString(src[i] & 0xFF).toUpperCase();
			if (hv.length() < 2) {
				builder.append(0);
			}
			builder.append(hv);
		}
		return builder.toString();
	}

	public static void main(String[] args) throws Exception {
		final String fileType = getFileType("E:\\stock\\000625_��������\\invest\\64016299.DOC");
		System.out.println(fileType);
	}
}
class MatchHashMap<K,V> extends HashMap{
	public Object get(Object key){
		for(Object k : this.keySet()){
			if(StringUtils.startsWithIgnoreCase(String.valueOf(k), String.valueOf(key))){
				return super.get(k);
			}
		}
		return super.get(key);
	}
}
