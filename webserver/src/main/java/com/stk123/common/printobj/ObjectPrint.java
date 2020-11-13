package com.stk123.common.printobj;

import java.beans.PropertyDescriptor;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;
import java.util.Vector;

public class ObjectPrint {
	
	public static String outputFile = "d:/log/bean_" + System.currentTimeMillis() + ".txt";
	public static PrintWriter pw = null;
	
	static {
		try {
			// 准备输出文件
			// pw = new PrintWriter(outputFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void println(String s) {
		System.out.println(s);
		// pw.println(s);
	}
	
	public static void main(String[] args) {
//		TreeNode node = new TreeNode();
//		node.setName("dddd");
//		TreeNode node1 = new TreeNode();
//		node1.setName("xxxxxxx");
//		node1.setParent(node1);
//		node.getChildren().add(node1);
//		ObjectPrint.dump(node,null);
	}

	/**
	 * 调试, 打印出给定 Bean 的所有属性的取值.
	 * 
	 * @param bean
	 * @param proArray
	 *            需要打印的对象
	 */
	public static void dump(Object bean, String[] proArray) {
		if (bean instanceof Collection) {
			@SuppressWarnings("unchecked")
			Collection<Object> collection = (Collection<Object>) bean;
			println("print Collection begein!");
			for (Object o : collection) {
				dump(o, proArray);
			}
			println("print Collection end!");
		} else if (bean instanceof Map) {
			println("print Map begein!");
			@SuppressWarnings("unchecked")
			Map<Object, Object> map = (Map<Object, Object>) bean;
			for (Object o : map.keySet()) {
				Object value = map.get(o);
				if (isImmutableObjects(value)) {
					println("[" + o.toString() + "]=" + value.toString());
				} else {
					println("[" + o.toString() + "] begin");
					dump(value, proArray);
					println("[" + o.toString() + "] end!");
				}
			}
			println("print Map end!");
		} else {
			if (isImmutableObjects(bean)) {
				// 不可变类
				println("[" + bean.getClass().getName() + "]="
						+ bean.toString());
			} else {
				printObject(bean, proArray);
			}
		}
	}

	/**
	 * 判断是否是不可变类
	 * 
	 * @param bean
	 * @return
	 */
	private static boolean isImmutableObjects(Object bean) {
		if (bean instanceof Integer || bean instanceof Long
				|| bean instanceof String || bean instanceof Short
				|| bean instanceof Boolean || bean instanceof Byte
				|| bean instanceof Character || bean instanceof Double
				|| bean instanceof Float || bean instanceof Number) {
			return true;
		}
		return false;
	}

	/**
	 * 从 bean 中读取有效的属性描述符. NOTE: 名称为 class 的 PropertyDescriptor 被排除在外.
	 * 
	 * @param bean Object - 需要读取的 Bean
	 * @return PropertyDescriptor[] - 属性列表
	 */
	public static java.beans.PropertyDescriptor[] getAvailablePropertyDescriptors(
			Object bean) {
		try {
			// 从 Bean 中解析属性信息并查找相关的 write 方法
			java.beans.BeanInfo info = java.beans.Introspector.getBeanInfo(bean.getClass());
			if (info != null) {
				java.beans.PropertyDescriptor pd[] = info
						.getPropertyDescriptors();
				Vector<PropertyDescriptor> columns = new Vector<PropertyDescriptor>();
				for (int i = 0; i < pd.length; i++) {
					String fieldName = pd[i].getName();
					if (fieldName != null && !fieldName.equals("class")) {
						columns.add(pd[i]);
					}
				}
				PropertyDescriptor[] arrays = new PropertyDescriptor[columns
						.size()];
				for (int j = 0; j < columns.size(); j++) {
					arrays[j] = (PropertyDescriptor) columns.get(j);
				}
				return arrays;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
		return null;

	}

	public static void dump(Object bean) {
		dump(bean, null);
	}

	private static void printObject(Object bean, String[] proArray) {
		PropertyDescriptor[] descriptors = getAvailablePropertyDescriptors(bean);
		for (int i = 0; descriptors != null && i < descriptors.length; i++) {
			Method readMethod = descriptors[i].getReadMethod();
			try {
				String proName = descriptors[i].getName();
				if (proArray == null
						|| (proArray != null && inArray(proName, proArray))) {
					Object value = readMethod.invoke(bean, new Object[] {});
					println("[" + bean.getClass().getName() + "]." + proName
							+ "(" + descriptors[i].getPropertyType().getName()
							+ ") = " + value);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * 判断proName是否在proArray里面
	 * 
	 * @param proName
	 * @param proArray
	 * @return
	 */
	private static boolean inArray(String proName, String[] proArray) {
		if (proArray == null)
			return false;
		boolean in = false;
		for (String s : proArray) {
			if (proName.equals(s)) {
				in = true;
				break;
			}
		}
		return in;
	}
}
