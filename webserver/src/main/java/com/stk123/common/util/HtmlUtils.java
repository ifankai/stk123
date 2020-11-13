package com.stk123.common.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
//import org.htmlcleaner.HtmlCleaner;
//import org.htmlcleaner.XPatherException;
import org.htmlparser.*;
import org.htmlparser.filters.*;
import org.htmlparser.nodes.TagNode;
import org.htmlparser.tags.TableColumn;
import org.htmlparser.tags.TableHeader;
import org.htmlparser.tags.TableRow;
import org.htmlparser.tags.TableTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.visitors.NodeVisitor;


public class HtmlUtils {

	public final static String RegxpForImgTag = "<\\s*(img|IMG)\\s+([^>]*)\\s*>"; // 找出IMG标签

    public final static String RegxpForImaTagSrcAttr = "src\\s*=\\s*\"([^\"]+)\""; // 找出IMG标签的SRC属性

	public final static byte[] val = { 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F,
        0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F,
        0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F,
        0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F,
        0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x00, 0x01,
        0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x3F, 0x3F, 0x3F,
        0x3F, 0x3F, 0x3F, 0x3F, 0x0A, 0x0B, 0x0C, 0x0D, 0x0E, 0x0F, 0x3F,
        0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F,
        0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F,
        0x3F, 0x3F, 0x3F, 0x0A, 0x0B, 0x0C, 0x0D, 0x0E, 0x0F, 0x3F, 0x3F,
        0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F,
        0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F,
        0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F,
        0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F,
        0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F,
        0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F,
        0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F,
        0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F,
        0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F,
        0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F,
        0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F,
        0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F,
        0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F,
        0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F };


	public static String unescape(String s) {
        StringBuffer sbuf = new StringBuffer();
        int i = 0;
        int len = s.length();
        while (i < len) {
        int ch = s.charAt(i);
        if ('A' <= ch && ch <= 'Z') {
        sbuf.append((char) ch);
        } else if ('a' <= ch && ch <= 'z') {
        sbuf.append((char) ch);
        } else if ('0' <= ch && ch <= '9') {
            sbuf.append((char) ch);
        } else if (ch == '-' || ch == '_'|| ch == '.' || ch == '!' || ch == '~' || ch == '*'|| ch == '\'' || ch == '(' || ch == ')') {
        sbuf.append((char) ch);
        } else if (ch == '%') {
            int cint = 0;
            if ('u' != s.charAt(i + 1)) {
            cint = (cint << 4) | val[s.charAt(i + 1)];
            cint = (cint << 4) | val[s.charAt(i + 2)];
            i += 2;
            } else {
                cint = (cint << 4) | val[s.charAt(i + 2)];
                cint = (cint << 4) | val[s.charAt(i + 3)];
                cint = (cint << 4) | val[s.charAt(i + 4)];
                cint = (cint << 4) | val[s.charAt(i + 5)];
                i += 5;
            }
            sbuf.append((char) cint);
        } else {
            sbuf.append((char) ch);
        }
        i++;
        }
        return sbuf.toString();
    }

	public static String removeHTML(String str) {
		if(str == null)return null;
        str = stringReplace(str, "\\s", "");// 去掉页面上看不到的字符
        str = stringReplace(str, "<br ?/?>", "\n");// 去<br><br />
        str = stringReplace(str, "<([^<>]+)>", "");// 去掉<>内的字符
        str = stringReplace(str, "&nbsp;", " ");// 替换空格
        str = stringReplace(str, "&(\\S)(\\S?)(\\S?)(\\S?);", "");// 去<br><br />
        return str;
    }

	public static String stringReplace(String str, String sr, String sd) {
        String regEx = sr;
        Pattern p = Pattern.compile(regEx, Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(str);
        str = m.replaceAll(sd);
        return str;
    }

	/**
	 * @param tab
	 * @param thead 第几行作为result里的Map里的key，-1为没有key，默认0
	 * @keyColumn 第几列作为result里子Map里的key,默认0
	 * @return
	 * @throws Exception
	 */
	public static Map<String,Map<String, String>> getListFromTable(TableTag tab,int thead,int keyColumn) throws Exception {
		Map<String,Map<String, String>> result = new HashMap<String,Map<String, String>>();
		TableRow[] rows = tab.getRows();
		for (int j = thead+1; j < rows.length; j++) {
			TableRow tr = (TableRow) rows[j];
			TableColumn[] td = tr.getColumns();
			TableRow theadRow = rows[thead];
			Map<String, String> map = null;
			for (int i = 0; i < td.length; i++) {
				if(i == keyColumn){
					continue;
				}
				String k = null;

				if(theadRow.getColumnCount() > 0){
					k = theadRow.getColumns()[i].toPlainTextString();
				}else{
					k = theadRow.getHeaders()[i].toPlainTextString();
				}
				if(k != null)k = StringUtils.trim(k);
				if(result.get(k)==null){
					map = new HashMap<String, String>();
				}else{
					map = result.get(k);
				}
				String key = "";
				if(thead == -1){
					key = String.valueOf(k+1);
				}else{
					key = td[keyColumn].toPlainTextString();
				}
				map.put(StringUtils.trim(key), StringUtils.trim(td[i].toPlainTextString()));
				if(result.get(k) == null){
					result.put(k,map);
				}
			}

		}
		return result;
	}

	/**
	 * @param thead 指示head是第几行，这行会忽略，从0开始
	 */
	public static List<List<String>> getListFromTable(TableTag tab,int thead){
		List<List<String>> result = new ArrayList<List<String>>();
		TableRow[] rows = tab.getRows();
		for (int j = thead+1; j < rows.length; j++) {
			TableRow tr = (TableRow) rows[j];
			TableColumn[] td = tr.getColumns();
			List<String> row = new ArrayList<String>();
			for (int i = 0; i < td.length; i++) {
				String text = td[i].toPlainTextString();
				row.add(StringUtils.trim(text));
			}
			result.add(row);
		}
		return result;
	}

	public static List<List<String>> getListFromTable2(TableTag tab,int thead){
		List<List<String>> result = new ArrayList<List<String>>();
		TableRow[] rows = tab.getRows();
		for (int j = thead+1; j < rows.length; j++) {
			TableRow tr = (TableRow) rows[j];
			TableColumn[] td = tr.getColumns();
			TableHeader[] th = tr.getHeaders();
			List<String> row = new ArrayList<String>();
			for (int i = 0; i < th.length; i++) {
				String text = th[i].toPlainTextString();
				row.add(StringUtils.trim(text));
			}
			for (int i = 0; i < td.length; i++) {
				String text = td[i].toPlainTextString();
				row.add(StringUtils.trim(text));
			}
			result.add(row);
		}
		return result;
	}

	public static List<List<String>> getListFromTable(TableTag tab){
		return getListFromTable(tab,-1);//-1为没有标题行
	}

	public static Map<String,Map<String, String>> getListFromTable(String page,String charset,String tableId,int thead,int keyColumn) throws Exception {
		return getListFromTable(getTable(page,charset,tableId),thead,keyColumn);
	}

	public static TableTag getTable(String page,String charset,String tableId) throws Exception {
		NodeFilter tableFilter = new NodeClassFilter(TableTag.class);
		NodeList nodeList = null;
		nodeList = getParser(page,charset).parse(tableFilter);
		for (int i = 0; i <= nodeList.size(); i++) {
			if (nodeList.elementAt(i) instanceof TableTag) {
				TableTag tag = (TableTag) nodeList.elementAt(i);
				if(tableId.equalsIgnoreCase(tag.getAttribute("id"))){
					return tag;
				}
			}
		}
		return null;
	}

	private static Parser getParser(String page,String charset) throws Exception {
		return Parser.createParser(page,charset);
	}

	public static List<String> getText(Node node,final String tagName) {
		final List<String> result = new ArrayList<String>();
		node.accept(new NodeVisitor(){
			public void visitTag(Tag tag) {
		        if(tagName.equalsIgnoreCase(tag.getTagName())){
		        	result.add(tag.toPlainTextString());
		        }
		    }
		});
		return result;
	}

	public static Node getNodeByText(String page,String charset,String text) throws Exception {
		NodeFilter tableFilter = new StringFilter(text);
		NodeList nodeList = null;
		nodeList = getParser(page,charset).extractAllNodesThatMatch(tableFilter);
		for (int i = 0; i < nodeList.size(); i++) {
			Node node = nodeList.elementAt(i);
			if(node != null){
				return node.getParent();
			}
		}
		return null;
	}

	public static List<Node> getNodesByText(String page,String charset,String text) throws Exception {
		NodeFilter tableFilter = new StringFilter(text);
		NodeList nodeList = null;
		nodeList = getParser(page,charset).extractAllNodesThatMatch(tableFilter);
		List<Node> nodes = new ArrayList<Node>();
		for (int i = 0; i < nodeList.size(); i++) {
			Node node = nodeList.elementAt(i);
			if(node != null){
				nodes.add( node.getParent());
			}
		}
		return nodes;
	}

	public static TableTag getTableNodeByText(String page, String charset, String text) throws Exception {
		NodeFilter tableFilter = new StringFilter(text);
		NodeList nodeList = null;
		nodeList = getParser(page,charset).extractAllNodesThatMatch(tableFilter);
		for (int i = 0; i <= nodeList.size(); i++) {
			Node node = nodeList.elementAt(i);
			if(node != null){
				while(true){
					Node tag = node.getParent();
					if(tag == null)return null;
					if(tag instanceof TableTag){
						return (TableTag)tag;
					}
					node = node.getParent();
				}
			}
		}
		return null;
	}

	public static List<TableTag> getTableNodesByText(String page, String charset, String text) throws Exception {
		NodeFilter tableFilter = new StringFilter(text);
		NodeList nodeList = null;
		nodeList = getParser(page,charset).extractAllNodesThatMatch(tableFilter);
		List<TableTag> tabs = new ArrayList<TableTag>();
		for (int i = 0; i <= nodeList.size(); i++) {
			Node node = nodeList.elementAt(i);
			if(node != null){
				while(true){
					Node tag = node.getParent();
					if(tag == null)continue;
					if(tag instanceof TableTag){
						tabs.add((TableTag)tag);
						break;
					}
					node = node.getParent();
				}
			}
		}
		return tabs;
	}

	public static Node getNodeByAttribute(Node node,String charset,String attribute,String value) throws Exception {
		NodeList nodeList = new NodeList();
		NodeFilter tableFilter = new HasAttributeFilter(attribute,value);
		node.collectInto(nodeList, tableFilter);
		for (int i = 0; i < nodeList.size(); i++) {
			Node n = nodeList.elementAt(i);
			return n;
		}
		return null;
	}

	public static Node getNodeByAttribute(String page,String charset,String attribute,String value) throws Exception {
		NodeFilter tableFilter = new HasAttributeFilter(attribute,value);
		NodeList nodeList = null;
		nodeList = getParser(page,charset).parse(tableFilter);
		for (int i = 0; i < nodeList.size(); i++) {
			Node node = nodeList.elementAt(i);
			if(node != null){
				return node;
			}
		}
		return null;
	}

	/**
	 *  <table class="table table-bg p_table table-border">
	 *  @example getNodeByAttributeContain(page, null, "class", "p_table") or getNodeByAttributeContain(page, null, "class", "table-bg")
	 */
	public static Node getNodeByAttributeContain(String page, String charset, final String attribute, final String value) throws Exception {
		NodeFilter tableFilter = new RegexFilter("");
		NodeList nodeList = null;
		nodeList = getParser(page,charset).parse(new NodeFilter() {
			@Override
			public boolean accept(Node node) {
				if (node instanceof Tag) {
					Tag tag = (Tag)node;
					Attribute attr = tag.getAttributeEx(StringUtils.upperCase(attribute));
					if (attr != null && StringUtils.containsIgnoreCase(attr.getValue(), value)) {
						return true;
					}
				}
				return false;
			}
		});
		for (int i = 0; i < nodeList.size(); i++) {
			Node node = nodeList.elementAt(i);
			if(node != null){
				return node;
			}
		}
		return null;
	}

	public static List<Node> getNodesByAttribute(String page,String charset,String attribute,String value) throws Exception {
		NodeFilter tableFilter = new HasAttributeFilter(attribute,value);
		NodeList nodeList = null;
		nodeList = getParser(page,charset).parse(tableFilter);
		List<Node> result = new ArrayList<Node>();
		for (int i = 0; i < nodeList.size(); i++) {
			Node node = nodeList.elementAt(i);
			if(node != null){
				result.add(node);
			}
		}
		return result;
	}

	public static Node getNodeByAttribute(String page,String charset,String attribute,String value,int k) throws Exception {
		NodeFilter tableFilter = new HasAttributeFilter(attribute,value);
		NodeList nodeList = null;
		nodeList = getParser(page,charset).parse(tableFilter);
		int j = 0;
		for (int i = 0; i < nodeList.size(); i++) {
			Node node = nodeList.elementAt(i);
			if(node != null){
				if(j==k){
					return node;
				}else{
					j ++;
				}

			}
		}
		return null;
	}

	public static Node getNodeByAttributes(String page,String charset,Map<String,String> attributes) throws Exception {
		List<NodeFilter> filters = new ArrayList<NodeFilter>(attributes.size());
		Set<Map.Entry<String, String>> set = attributes.entrySet();
		for(Map.Entry<String, String> kv:set){
			filters.add(new HasAttributeFilter(kv.getKey(),kv.getValue()));
		}
		NodeFilter[] arrays = new HasAttributeFilter[attributes.size()];
		filters.toArray(arrays);
		AndFilter andFilter = new AndFilter(arrays);

		NodeList nodeList = null;
		nodeList = getParser(page,charset).parse(andFilter);
		for (int i = 0; i < nodeList.size(); i++) {
			Node node = nodeList.elementAt(i);
			if(node != null){
				return node;
			}
		}
		return null;
	}

	public static Node getNodeByTagName(Node node,String tagName) throws Exception {
		NodeList nodeList = new NodeList();
		NodeFilter nodefilter = new org.htmlparser.filters.TagNameFilter(tagName);
		node.collectInto(nodeList, nodefilter);
		for (int i = 0; i < nodeList.size(); i++) {
			Node n = nodeList.elementAt(i);
			return n;
		}
		return null;
	}
	public static Node getNodeByTagName(Node node,String tagName,int k) throws Exception {
		NodeList nodeList = new NodeList();
		NodeFilter nodefilter = new org.htmlparser.filters.TagNameFilter(tagName);
		node.collectInto(nodeList, nodefilter);
		for (int i = 0; i < nodeList.size(); i++) {
			Node n = nodeList.elementAt(k);
			return n;
		}
		return null;
	}

	public static List<Node> getNodeListByTagName(Node node,String tagName) throws Exception {
		List<Node> result = new ArrayList<Node>();
		NodeList nodeList = new NodeList();
		NodeFilter nodefilter = new org.htmlparser.filters.TagNameFilter(tagName);
		node.collectInto(nodeList, nodefilter);
		for (int i = 0; i < nodeList.size(); i++) {
			Node n = nodeList.elementAt(i);
			if(n != null)
				result.add(n);
		}
		return result;
	}

	public static List<Node> getNodeListByTagName(String page,String charset,String tagName) throws Exception {
		List<Node> result = new ArrayList<Node>();
		NodeList nodeList = null;
		NodeFilter nodefilter = new org.htmlparser.filters.TagNameFilter(tagName);
		nodeList = getParser(page,charset).parse(nodefilter);
		for (int i = 0; i < nodeList.size(); i++) {
			Node n = nodeList.elementAt(i);
			if(n != null)
				result.add(n);
		}
		return result;
	}

	public static List<Node> getNodeListByTagNameAndAttribute(Node node,String tagName,String attribute,String value) throws Exception {
		List<Node> result = new ArrayList<Node>();
		NodeList nodeList = new NodeList();
		NodeFilter nodefilter = new org.htmlparser.filters.TagNameFilter(tagName);
		NodeFilter attrFilter = new HasAttributeFilter(attribute,value);
		AndFilter andFilter = new AndFilter(nodefilter,attrFilter);
		node.collectInto(nodeList, andFilter);
		for (int i = 0; i < nodeList.size(); i++) {
			Node n = nodeList.elementAt(i);
			if(n != null)
				result.add(n);
		}
		return result;
	}

	public static List<Node> getNodeListByTagNameAndAttribute(String page,String charset,String tagName,String attribute,String value) throws Exception {
		List<Node> result = new ArrayList<Node>();
		NodeList nodeList = null;
		NodeFilter nodefilter = new TagNameFilter(tagName);
		NodeFilter attrFilter = new HasAttributeFilter(attribute,value);
		AndFilter andFilter = new AndFilter(nodefilter,attrFilter);
		nodeList = getParser(page,charset).parse(andFilter);
		for (int i = 0; i < nodeList.size(); i++) {
			Node n = nodeList.elementAt(i);
			if(n != null)
				result.add(n);
		}
		return result;
	}

	public static String getTextFromTable(TableTag tab,int x,int y) throws Exception {
		TableRow row = tab.getRow(y);
		TableColumn[] td = row.getColumns();
		return td[x].toPlainTextString();
	}

	/**
	 * 通过xpath获取node (TagNode)
	 */
//	public static Object[] getNodesByXpath(String page, String xpath) throws XPatherException{
//		HtmlCleaner hc = new HtmlCleaner();
//        org.htmlcleaner.TagNode tn = hc.clean(page);
//        return tn.evaluateXPath(xpath);
//	}

	public static String unicodeToCn(String unicode) {
	    /* 以 \ u 分割，因为java注释也能识别unicode，因此中间加了一个空格*/
	    String[] strs = unicode.split("\\\\u");
	    String returnStr = "";
	    // 由于unicode字符串以 \ u 开头，因此分割出的第一个字符是""。
	    for (int i = 1; i < strs.length; i++) {
	      returnStr += (char) Integer.valueOf(strs[i], 16).intValue();
	    }
	    return returnStr;
	}

	public static String getAttribute(Node node, String attribute){
		return ((TagNode)node).getAttribute(attribute);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		/*String page = HttpUtils.get("http://money.finance.sina.com.cn/corp/go.php/vFD_FinancialGuideLine/stockid/002275/ctrl/2012/displaytype/4.phtml", null, "gb2312");
		Node n = getNode(page,"","历年数据");
		System.out.println(getText(n,"a"));*/


		/*String page = HttpUtils.get("http://stockdata.stock.hexun.com/2009_gsgk_002275.shtml", null, "GBK");
		Node n = getNodeByAttribute(page, "","id", "zaiyaocontent");
		System.out.println(n.getChildren().elementAt(1).toHtml());
		System.out.println(getTextFromTable((TableTag)n.getChildren().elementAt(1), 5, 2));*/

		/*String page = HttpUtils.get("http://hq.sinajs.cn/list=sh600960,sh600952", null, "");
		System.out.println(page);
		page = HttpUtils.get("http://hq.sinajs.cn/list=sz000802", null, "");
		System.out.println(page);*/

		/*String page = HttpUtils.get("http://ggzx.stock.hexun.com/more.jsp?t=0&k=600588&s=0",null,"GBK");
		Node node = getNodeByAttribute(page,"","class","temp01");
		List<Node> nl = getNodeListByTagName(node,"a");
		//System.out.println(nl);
		Node n = nl.get(0);
		System.out.println(n.toPlainTextString());
		System.out.println(((LinkTag)n).getAttribute("href"));*/

		/*String page = HttpUtils.get("http://wwtnews.windin.com/home/FinNews/newslist.aspx?columnid=30802000&windCode=002450.SZ&t=1",null,"GBK");
		Node node = HtmlUtils.getNodeByAttribute(page,"","id","tbNewsList",1);
		List<Node> nodes =HtmlUtils.getNodeListByTagNameAndAttribute(node, "td", "class", "NewsList");
		for(Node n:nodes){
			TableColumn td = (TableColumn)n;
			System.out.println(td.getStringText().replaceAll("NewsDetail", "http://wwtnews.windin.com/home/FinNews/NewsDetail"));
		}*/

		/*String page = HttpUtils.get("http://www.windin.com/Tools/NewsDetail.aspx?windcode=600577.SH", null, "gbk");
		//System.out.println(unescape(page));
		Node node = HtmlUtils.getNodeByAttribute(unescape(page),"","id","lblData");
		Span span = (Span)node;
		String infos = StringUtils.substringBetween(span.getStringText(), ":[", "],");
		String[] ss = infos.split("\\u007B");
		for(String s:ss){
			System.out.println(s);
			String target = StringUtils.substringBetween(s, "\"target\":\"", "\"");
			System.out.println(target);
		}*/

		/*String url = "http://114.80.159.18/CorpEventsWeb/NewsEventAlert.aspx?windcode=002275.SZ&t=1";
		String page = HttpUtils.get(url,null,"GBK");
		Node node = HtmlUtils.getNodeByAttribute(page,"","id","TABLE2");
		//System.out.println(node.toHtml());
		//String s = HtmlUtils.getTextFromTable((TableTag)node, 0, 2);
		List<Node> m = HtmlUtils.getNodeListByTagNameAndAttribute(node, "td", "class", "NewsDetail");
		//System.out.println(m);
		for(Node n:m){
			System.out.println(n.toPlainTextString());
		}*/

		/*String page = HttpUtils.get("http://www.windin.com/home/stock/html_wind/002275.SZ.shtml", null, "utf8");
		//Node industryCompare = HtmlUtils.getNodeByAttribute(page, "", "id", "industryCompare");
		//System.out.println(industryCompare.getChildren().elementAt(2).toPlainTextString());
		Node redstar = HtmlUtils.getNodeByAttribute(page, "", "src", "http://i1.windin.com/imgserver/common/redstar.gif");
		//System.out.println(redstar.toHtml());
		//System.out.println(((TagNode)redstar.getParent().getNextSibling()).getAttribute("onmouseover"));

		List<Node> redstars = HtmlUtils.getNodesByAttribute(page, "", "src", "http://i1.windin.com/imgserver/common/redstar.gif");
		for(Node star : redstars){
			System.out.println(StringUtils.trim(star.getParent().getNextSibling().toPlainTextString())+":"+StringUtils.substringBetween(((TagNode)star.getParent().getNextSibling()).getAttribute("onmouseover"), "event,'", "')"));
			System.out.println(((TagNode)star.getParent().getNextSibling()).getAttribute("onmouseover"));
		}*/

		/*Node n = HtmlUtils.getNodeByText(page, "", "限售股份解禁时间表");
		System.out.println(getListFromTable((TableTag)n.getParent().getParent(),1));
		System.out.println(JsonUtils.getJsonString4JavaPOJO(getListFromTable((TableTag)n.getParent().getParent(),1)));


		/*Node fn = HtmlUtils.getNodeByAttribute(page, null, "id", "browseByReport");
		System.out.println(fn.toHtml());
		Map<String,Map<String, String>> map = HtmlUtils.getListFromTable((TableTag)fn.getFirstChild(), 0, 0);
		System.out.println(map);*/

		/*String page = HttpUtils.get("http://money.finance.sina.com.cn/corp/view/vFD_FinancialGuideLineHistory.php?stockid=600315&typecode=financialratios42", null, "GBK");
		Map<String,Map<String, String>> tab = HtmlUtils.getListFromTable(page, "", "Table1", 0, 0);
		System.out.println(tab.get(""));*/

		/*Node n = HtmlUtils.getNodeByText(page, "", "公司简介");
		System.out.println(n.getParent().getNextSibling().getFirstChild().toHtml());
		*/

		/*Node holder = HtmlUtils.getNodeByText(page, "", "A股股东户数");
		System.out.println(holder.getParent().getParent().toHtml());
		Map<String,Map<String, String>> map = HtmlUtils.getListFromTable((TableTag)holder.getParent().getParent(), 0, 0);
		System.out.println(map);*/

		/*String page = HttpUtils.get("http://vip.stock.finance.sina.com.cn/quotes_service/api/json_v2.php/Market_Center.getHQNodes", null, "gb2312");
		System.out.println(page);
		System.out.println(new String(page.getBytes(),"GBK"));*/

		/*String page = HttpUtils.get("http://flashquote.stock.hexun.com/Quotejs/DA/2_399005_DA.html?", null, "gbk");
		List<List> datas = JsonUtils.getList4Json("[["+StringUtils.substringBetween(page, "[[", "]]")+"]]", ArrayList.class );
		for(List data:datas){
			System.out.println(data);
		}

		page = HttpUtils.get("http://finance.sina.com.cn/realstock/company/sz002571/qianfuquan.js", null, "gbk");
		System.out.println(page);
		SinaQianFuQuan sina = (SinaQianFuQuan)JsonUtils.getObject4JsonString(StringUtils.substringBetween(page, "[", "]"), SinaQianFuQuan.class, null);
		System.out.println(sina.getTotal());
		System.out.println(sina.getData());*/

		/*String page = HttpUtils.get("http://data.cfi.cn/cfidata.aspx?sortfd=%e9%a2%84%e5%91%8a%e7%b1%bb%e5%9e%8b&sortway=desc&fr=content&ndk=A0A1934A1939A1940A4431A4432&xztj=&mystock=&curpage=1", null, "utf-8");
		//System.out.println(page);
		Node table = HtmlUtils.getNodeByAttribute(page, "", "class", "table_data");
		//System.out.println(table.toHtml());
		List<List<String>> datas = HtmlUtils.getListFromTable((TableTag)table, 0);
		for(List data:datas){
			System.out.println(data);
		}*/

		/*String page = HttpUtils.get("http://quote.hexun.com/js/conception.ashx", null, "GBK");
		//System.out.println(page);
		List<HexunIndustryConception> inds = JsonUtils.getList4Json(StringUtils.substringBetween(page, "conceptionData=", ";"), HexunIndustryConception.class);
		for(HexunIndustryConception ind:inds){
			System.out.println(ind.getType_code());
		}*/

		/*page = HttpUtils.get("http://quote.tool.hexun.com/hqzx/stocktype.aspx?columnid=5522&type_code=gn_rzrq&sorttype=3&updown=up&page=1&count=5000", null, "GBK");
		System.out.println(page);
		List<List> stks = JsonUtils.getList4Json(StringUtils.substringBetween(page, "dataArr = ", ";"), ArrayList.class);
		for(List stk:stks){
			System.out.println(stk);
		}*/
		/*String page = HttpUtils.get("http://www.chinaclear.cn/main/03/0303/030305/030305_15.html", null, "GBK");
		System.out.println(page);*/

		/*String page = HttpUtils.get("http://xueqiu.com/S/SZ300124", null, "utf-8");
		Node node = HtmlUtils.getNodeByAttribute(page, "", "class", "stockQuote");
		String str = node.toHtml();
		System.out.println("close="+StringUtils.substringBetween(str, "data-current=\"", "\""));
		node = HtmlUtils.getNodeByText(str, "", "今开");
		System.out.println("open="+node.getLastChild().toPlainTextString());
		node = HtmlUtils.getNodeByText(str, "", "昨收");
		System.out.println("last close="+node.getLastChild().toPlainTextString());
		node = HtmlUtils.getNodeByText(str, "", "最高");
		System.out.println("high="+node.getLastChild().toPlainTextString());
		node = HtmlUtils.getNodeByText(str, "", "最低");
		System.out.println("low="+node.getLastChild().toPlainTextString());
		node = HtmlUtils.getNodeByText(str, "", "成交量");
		System.out.println("volumn="+node.getChildren().elementAt(1).toPlainTextString());
		node = HtmlUtils.getNodeByText(str, "", "成交额");
		System.out.println("amount="+node.getLastChild().toPlainTextString());
		node = HtmlUtils.getNodeByAttribute(str, "", "id", "timeInfo");
		System.out.println(StkUtils.formatDate(StkUtils.sf_ymd4.parse(StringUtils.substring(node.toPlainTextString(), 0, 10)),StkUtils.sf_ymd2));
	*/
		/*String page = HttpUtils.get("http://wwtnews.windin.com/wuds/web/f9/stock/west/eps/ComprehensiveProfitForecastGeneralView.aspx?Version=1.1&WindCode=002275.sz&Style=3", null, "utf-8");
		System.out.println(page);
		Node table = HtmlUtils.getNodeByAttribute(page, "", "id", "GeneralDataTable");
		Map<String,Map<String, String>> datas = HtmlUtils.getListFromTable((TableTag)table, 0, 0);
		System.out.println(datas);*/


		/*String page = HttpUtils.get("http://q.stock.sohu.com/app2/history.up?method=history&code=cn_600278&sd=2012-08-29&ed=2013-09-06&t=d&res=js&r=", null, "utf-8");
		page = StringUtils.replace(page, "\n", "");
		System.out.println(page);

		System.out.println(StringUtils.substringBetween(page, "[[", "]]"));
		List<List> list = JsonUtils.getList4Json("[["+StringUtils.substringBetween(page, "[[", "]]")+"]]", ArrayList.class);
		System.out.println(list.size());
		for(List stk:list){
			System.out.println(stk);
		}*/

		/*String page = HttpUtils.get("http://xueqiu.com/stock/forchartk/stocklist.json?period=1day&symbol=SH600140&type=before&access_token=a17OeMQ9Fk11u7Dnp1y3em&_="+new Date().getTime(), null, "GBK");
		System.out.println(page);
		Map<String, Class> m = new HashMap<String, Class>();
        m.put("chartlist", Map.class);
		XueQiuQianFuQuan xq = (XueQiuQianFuQuan) JsonUtils.getObject4JsonString(page, XueQiuQianFuQuan.class, m);
		for(Map map : xq.getChartlist()){
			String time = (String)map.get("time");
			time = StkUtils.sf_ymd2.format(new Date(time));
			map.put("time", time);
		}
		System.out.println(xq.getChartlist());*/

		/*String page = HttpUtils.get("http://data.eastmoney.com/report/447yb.html", null, "GBK");
		Node node = HtmlUtils.getNodeByAttribute(page, null, "id", "s1-cont1");
		//System.out.println(node.toHtml());
		List<Node> nodes = HtmlUtils.getNodeListByTagName(node, "ul");
		int i = 0;
		for(Node n : nodes){
			if(i++==0)continue;
			//System.out.println(n.toHtml());
			Node n2 = HtmlUtils.getNodeByAttribute(n.toHtml(), null, "class", "report");
			if(n2 != null){
				String title = StringUtils.substringBetween(n2.getLastChild().toHtml(), "title=\"", "\"");
				String msg = n2.getLastChild().toPlainTextString();
				Node n3 = HtmlUtils.getNodeByAttribute(n.toHtml(), null, "class", "reportObj");
				Node n4 = HtmlUtils.getNodeByAttribute(n.toHtml(), null, "class", "date");
				String s = n3.getLastChild().toHtml()+n2.getLastChild().toHtml()+" ("+n4.toPlainTextString()+")";
				s = StringUtils.replace(s, "href=\"", "href=\"http://data.eastmoney.com");
				s = StringUtils.replace(s, msg, title);
				System.out.println(s);
			}
		}*/
		/*String page = HttpUtils.get("http://stock.finance.sina.com.cn/usstock/api/jsonp.php/IO.XSRV2.CallbackList"+URLEncoder.encode("['f0j3ltzVzdo2Fo4p']","utf-8")+"/US_CategoryService.getList?page=2&num=60&sort=&asc=0&market=&id=", null, "GBK");
		System.out.println(page);
		Map<String, Class> m = new HashMap<String, Class>();
        m.put("data", Map.class);
		MeiGuSina meiGu = (MeiGuSina)JsonUtils.getObject4JsonString(StringUtils.substringBetween(page, "((", "));"), MeiGuSina.class, m);
		int i = 0;
		for(Map map : meiGu.getData()){
			System.out.println(map);
			i++;
		}
		System.out.println(i);*/

		/*String page = HttpUtils.get("http://stock.finance.sina.com.cn/usstock/api/json.php/US_MinKService.getDailyK?symbol=carh&___qn=3", null, "GBK");
		System.out.println(page);
		List<Map> ks = JsonUtils.getList4Json(page, Map.class);
		for(Map k : ks){
			System.out.println(k);
		}*/

		/*String page = HttpUtils.get("http://www.chinaclear.cn/cms-webapp/wcm/getManuscriptByTitle_mzkb.action?weekflag=meiyou&dateStr=2013.09.09", null, "GBK");
		//System.out.println(page);
		List<Node> nodeDate = HtmlUtils.getNodeListByTagName(page, null, "h2");
		System.out.println(StringUtils.substringBetween(nodeDate.get(0).toPlainTextString(), "（", "）").split("-")[0]);
		Node node = HtmlUtils.getNodeByAttribute(page, null, "border", "1");
		//System.out.println(node.toHtml());
		if(node == null)return;
		Map<String,Map<String, String>> datas = HtmlUtils.getListFromTable((TableTag)node, 0, 0);
		//System.out.println(datas);
		for(Map.Entry<String, Map<String, String>> data : datas.entrySet()){
			if(StringUtils.contains(data.getKey(), "合计")){
				for(Map.Entry<String, String> value : data.getValue().entrySet()){
					//System.out.println(value.getKey()+"-"+value.getValue());
					if(StringUtils.contains(value.getKey(), "一、期末有效账户数（万户）")){
						System.out.println("一、期末有效账户数（万户）="+StringUtils.trim(value.getValue()));
					}else if(StringUtils.contains(value.getKey(), "二、新增股票账户数（户）")){
						System.out.println("二、新增股票账户数（户）="+StringUtils.trim(value.getValue()));
					}else if(StringUtils.contains(value.getKey(), "（1）期末持仓A股账户数（万户）")){
						System.out.println("（1）期末持仓A股账户数（万户）="+StringUtils.trim(value.getValue()));
					}else if(StringUtils.contains(value.getKey(), "（2）本周参与交易的A股账户数（万户）")){
						System.out.println("（2）本周参与交易的A股账户数（万户）="+StringUtils.trim(value.getValue()));
					}
				}
			}
		}*/

		/*String page = HttpUtils.get("http://finance.yahoo.com/q/hp?s=HIMX&a=02&b=31&c=2013&d=8&e=28&f=2013&g=d", "utf8");
		//System.out.println(page);
		Node node = HtmlUtils.getNodeByAttribute(page, null, "class", "yfnc_datamodoutline1");
		System.out.println(node.toHtml());
		List<Node> list = HtmlUtils.getNodeListByTagName(node, "table");
		System.out.println(list.size());
		List<List<String>> datas = HtmlUtils.getListFromTable((TableTag)list.get(1), 0);
		for(List data:datas){
			System.out.println(data);
		}*/

		//http://data.gtimg.cn/flashdata/hushen/latest/daily/sh600837.js?maxage=43201
		//http://data.gtimg.cn/flashdata/us/latest/daily/usA.N.js?maxage=43201
		/*String page = HttpUtils.get("http://data.gtimg.cn/flashdata/us/latest/daily/usHIMX.OQ.js?maxage=43201", "GBK");
		System.out.println(page);*/

		/*String page = HttpUtils.get("http://xueqiu.com/hq/US", "GBK");
		String text = "{"+StringUtils.substringBetween(page, "stockList.searchResult={", "};")+"}";
		System.out.println(text);
		Map<String, Class> m = new HashMap<String, Class>();
        m.put("industries", Map.class);
		Map map = (Map)JsonUtils.getObject4JsonString(text, Map.class, m);
		List<Map> list = (List)map.get("industries");
		for(Map industry : list){
			System.out.println(industry);
		}*/

		/*String page = HttpUtils.get("http://xueqiu.com/hq/US/"+URLEncoder.encode("明星股","utf-8"), "GBK");
		//System.out.println(page);
		String text = "{"+StringUtils.substringBetween(page, "stockList.searchResult={", "};")+"}";
		Map<String, Class> m = new HashMap<String, Class>();
        m.put("stocks", Map.class);
		Map map = (Map)JsonUtils.getObject4JsonString(text, Map.class, m);
		Map stks = (Map)map.get("stocks");
		for(Map<String, String> stk :(List<Map>)stks.get("stocks")){
			//System.out.println(stk);
			String code = stk.get("code");
			String name = stk.get("name");
			page = HttpUtils.get("http://xueqiu.com/S/"+code, null, "utf-8");
			Node profileNode = HtmlUtils.getNodeByAttribute(page, null, "class", "companyInfo detailContent");
			String profile = "";
			if(profileNode != null){
				profile = StringUtils.replace(profileNode.toPlainTextString(), "收起", "");
			}
			//System.out.println(profile.toHtml());
			Node node = HtmlUtils.getNodeByAttribute(page, "", "class", "stockQuote");
			String str = node.toHtml();
			//System.out.println(str);
			Node nodeCap = HtmlUtils.getNodeByText(str, "", "总股本：");
			String totalCap = nodeCap!=null?nodeCap.getLastChild().toPlainTextString():"0";
			totalCap = StringUtils.replace(totalCap, "万", "").replaceAll(",", "");
			int n = 1;
			if(StringUtils.indexOf(totalCap, "亿") > 0){
				n = 10000;
				totalCap = StringUtils.replace(totalCap, "亿", "");
			}
			double totalCapital = Double.parseDouble(totalCap)*n;
			System.out.println(code+","+name+","+totalCapital+","+profile);
			//break;
		}*/

		//http://vip.stock.finance.sina.com.cn/usstock/balance.php?s=IBM&t=quarter
		//http://vip.stock.finance.sina.com.cn/usstock/cash.php?s=IBM&t=quarter
		/*String page = HttpUtils.get("http://vip.stock.finance.sina.com.cn/usstock/income.php?s=IBM&t=quarter", "GBK");
		page = StringUtils.replace(page, "th", "td");
		Node node = HtmlUtils.getNodeByAttribute(page, "", "class", "data_tbl os_tbl", 1);
		Map<String,Map<String, String>> datas = HtmlUtils.getListFromTable((TableTag)node, 0, 0);
		System.out.println(datas);*/

		/*String page = HttpUtils.get("http://hq.sinajs.cn/rn="+Math.random()+"&list=gb_dji", null, "GBK");
		System.out.println(page.split(",")[25]);
		String dateTmp = page.split(",")[25];
		String[] tmp = dateTmp.split(" ");
		System.out.println(StkUtils.sf_ymd2.format(StkUtils.sf_ymd7.parse(tmp[0]+" "+tmp[1]+" "+StkUtils.YEAR)));
*/
		/*String page = HttpUtils.get("http://finance.yahoo.com/q/ks?s=NTES+Key+Statistics", "utf-8");
		//System.out.println(page);
		TableTag table = HtmlUtils.getTableNodeByText(page, "", "Fiscal Year Ends:");
		System.out.println(table.toHtml());
		List<List<String>> datas = HtmlUtils.getListFromTable(table);
		System.out.println(datas);
		table = HtmlUtils.getTableNodeByText(page, "", "Profitability");
		datas = HtmlUtils.getListFromTable(table);
		System.out.println(datas);
		table = HtmlUtils.getTableNodeByText(page, "", "Management Effectiveness");
		datas = HtmlUtils.getListFromTable(table);
		System.out.println(datas);*/

		/*String page = HttpUtils.get("http://www.windin.com/home/stock/stock-ho/600315.SH.shtml", "UTF-8");
		Node report = HtmlUtils.getNodeByAttribute(page, null, "id", "report1");
		System.out.println(report.toPlainTextString());
		report = HtmlUtils.getNodeByAttribute(page, null, "id", "report5");
		System.out.println(report.toPlainTextString());
		//System.out.println(page);
		List<TableTag> tables = HtmlUtils.getTableNodesByText(page, null, "持股变动数(万)");
		for(TableTag table : tables){
			List<List<String>> list = HtmlUtils.getListFromTable(table, 0);
			System.out.println(list);
		}*/

		/*String page = HttpUtils.get("http://money.finance.sina.com.cn/corp/go.php/vFD_CashFlow/stockid/000001/ctrl/part/displaytype/4.phtml", "GBK");
		System.out.println(page);
		Node table = HtmlUtils.getNodeByAttribute(page, null, "id", "BalanceSheetNewTable0");
		System.out.println(table.toHtml());
		Map<String,Map<String, String>> datas = HtmlUtils.getListFromTable((TableTag)table, 1, 0);
		System.out.println(datas);*/

		/*String page = HttpUtils.get("http://www.cnindex.com.cn/syl/2013-10-25/cninfo_hsls.html", "utf-8");
		List<Node> nodes = HtmlUtils.getNodesByText(page, null, "查看");
		for(Node node : nodes){
			String str = StringUtils.substringBetween(((LinkTag)node).getAttribute("onclick"), "showStockPE(", ")");
			String[] ss = str.split(",");
			String url = "http://www.cnindex.com.cn/stockPEs.do?query.category="+StringUtils.substringBetween(ss[1], "'", "'")+"&query.industry="+StringUtils.substringBetween(ss[2], "'", "'")+"&query.date="+StringUtils.substringBetween(ss[3], "'", "'")+"&pageNo=1&pageSize=1000";
			String tmpPage = HttpUtils.get(url, "utf-8");
			//System.out.println(tmpPage);
			Node tab = HtmlUtils.getTableNodeByText(tmpPage, null, "A股代码");
			List<List<String>> list = HtmlUtils.getListFromTable((TableTag)tab, 0);
			//System.out.println(list);
			for(List<String> stk : list){
				//System.out.println(StringUtils.replace(stk.get(10), "&nbsp;", "")+","+StringUtils.replace(stk.get(1), "&nbsp;", ""));

			}
			break;
		}
		//System.out.println(page);
		TableTag tab = (TableTag)HtmlUtils.getNodeByAttribute(page, null, "class", "table_01_box window0");
		//System.out.println(tab.toHtml());
		List<List<String>> datas = HtmlUtils.getListFromTable(tab);
		//System.out.println(datas);
		for(List<String> data : datas){
			if(data.size() >= 9){
				System.out.println(data);
			}
		}*/

		//盈利预测排行
		/*String page = HttpUtils.get("http://nufm.dfcfw.com/EM_Finance2014NumericApplication/JS.aspx?type=CT&cmd=C._A&sty=GEMCPF&st=(AllNum)&sr=-1&p=2&ps=5000&cb=&js=&token=3a965a43f705cf1d9ad7e1a3e429d622&rt=47445483", null, "gb2312");
		System.out.println(page);
		List<String[]> sd = new ArrayList<String[]>();
		Map map = (Map)JsonUtils.getObject4Json("{\"data\":"+StringUtils.substringBetween(page, "(", ")")+"}", Map.class,null);
		System.out.println(map);
		List<String> data = (List)map.get("data");
		for(String str : data){
			//System.out.println(str);
			String[] ss = StringUtils.split(str,",");
			//System.out.println(ss[10]);//2012
			System.out.println(ss[12]);//2013
			if("-".equals(ss[12]) || Double.parseDouble(ss[12]) == 0)continue;
			System.out.println(ss[14]);//2014
			if("-".equals(ss[14]))continue;
			System.out.println(ss[16]);//2015
			if(ss.length < 15)continue;
			sd.add(ss);
		}
		System.out.println(sd.size());
		Collections.sort(sd, new Comparator(){
			public int compare(Object arg0, Object arg1) {
				String[] s1 = (String[])arg0;
				String[] s2 = (String[])arg1;
				double d1 = (Double.parseDouble(s1[14])-Double.parseDouble(s1[12]))/Double.parseDouble(s1[12]);
				double d2 = (Double.parseDouble(s2[14])-Double.parseDouble(s2[12]))/Double.parseDouble(s2[12]);
				return (int)((d2-d1)*100);
			}

		});
		for(String[] s : sd){
			System.out.println(s[3]+","+s[1]+","+s[12]+","+s[14]+",,"+(Double.parseDouble(s[14])-Double.parseDouble(s[12]))/Double.parseDouble(s[12])*100);
		}*/

		/*String page = HttpUtils.get("http://xueqiu.com/hq#exchange=US&firstName=美国股市&secondName=美股一览", "GBK");
		String text = "{"+StringUtils.substringBetween(page, "stockList.searchResult={", "};")+"}";
		System.out.println(text);*/

		/*String page = HttpUtils.get("http://vip.stock.finance.sina.com.cn/q/go.php/vFinanceAnalyze/kind/performance/index.phtml?num=600&order=xiaxian%7C2", "GB2312");
		Node table = HtmlUtils.getNodeByAttribute(page, null, "id", "dataTable");
		List<List<String>> datas = HtmlUtils.getListFromTable((TableTag)table,0);
		for(List<String> data : datas){
			System.out.println(data+","+StkUtils.percentigeGreatThan(data.get(7)));
		}*/


		//getMethod.setRequestHeader("Cookie", "Hm_lpvt_1db88642e346389874251b5a1eded6e3=1401870866; Hm_lvt_1db88642e346389874251b5a1eded6e3=1399279853,1399456116,1399600423,1401761324; xq_a_token=bBfpd2WIHkEiOXxCZuvJKz; xq_r_token=HoJplnghTo9TdCtmaYhQ9C; bid=26948a7b701285b58366203fbc172ea6_hvykico0; xq_im_active=false; __utma=1.1861748176.1401870866.1401870866.1401870866.1; __utmb=1.2.9.1401870869035; __utmc=1; __utmz=1.1401870866.1.1.utmcsr=(direct)|utmccn=(direct)|utmcmd=(none)");
        /*String page = HttpUtils.get("http://xueqiu.com/stock/portfolio/stocks.json?size=1000&pid=7&tuid=6237744859&showAll=false", "GBK");
        //System.out.println(page);
        Map<String, Class> m = new HashMap<String, Class>();
        m.put("portfolios", Map.class);
        Map<String, List> map = (Map)JsonUtils.getObject4JsonString(page, Map.class, m);
        //System.out.println(map.get("portfolios"));
        for(Object obj : map.get("portfolios")){
            Map care = (Map)obj;
            if("关注A".equals(care.get("name"))){
                System.out.println(care.get("stocks"));
            }
        }*/

		//String page = HttpUtils.get("http://news.baidu.com/ns?ct=0&rn=20&ie=utf-8&bs=%E6%96%B0%E5%A4%A7%E9%99%86+%E5%A2%9E%E6%8C%81&rsv_bp=1&sr=0&cl=2&f=8&prevct=0&word=%E6%96%B0%E5%A4%A7%E9%99%86+%E5%A2%9E%E6%8C%81&tn=newstitle&inputT=0","utf-8");
		/*String page = HttpUtils.get("http://news.baidu.com/ns?ct=0&rn=20&ie=utf-8&bs=%E5%8F%8C%E9%B9%AD%E8%8D%AF%E4%B8%9A%20%E5%87%BA%E8%B4%A7%E9%87%8F%20|%20%E5%8F%8C%E9%B9%AD%E8%8D%AF%E4%B8%9A%20%E5%BC%80%E5%B7%A5%E7%8E%87&rsv_bp=1&sr=0&cl=2&f=8&prevct=0&word=%E5%8F%8C%E9%B9%AD%E8%8D%AF%E4%B8%9A%20%E5%87%BA%E8%B4%A7%E9%87%8F%20|%20%E5%8F%8C%E9%B9%AD%E8%8D%AF%E4%B8%9A%20%E5%BC%80%E5%B7%A5%E7%8E%87&tn=news&inputT=0","utf-8");

        System.out.println(page);
        Node node = HtmlUtils.getNodeByAttribute(page, null, "id", "header_top_bar");
        String div = node.toPlainTextString();
        System.out.println(div+"="+StkUtils.getNumberFromString(div));
        if(div.indexOf("找到相关新闻0篇") >= 0){
            System.out.println("true");
        }else{
            System.out.println("false");
            Node left = HtmlUtils.getNodeByAttribute(page, null, "id", "content_left");
            List<Node> list = HtmlUtils.getNodeListByTagName(left, "li");
            for(Node li : list){
            	Node span = HtmlUtils.getNodeByAttribute(li.toHtml(), null, "class", "c-author");
            	if(span != null){
            		String text = span.toPlainTextString();
            		String time = StringUtils.substringAfterLast(text, ";");
            		Date date = StkUtils.sf_ymd9.parse(time);
            		if(date.after(new Date())){

            		}
            	}
            }
        }*/


		/*String page = HttpUtils.get("http://stock.jrj.com.cn/share,000997,zyyw.shtml","gbk");
        List<Node> nodes = HtmlUtils.getNodeListByTagNameAndAttribute(page, null, "table", "class", "tab1");
        Node table = nodes.get(1);
        TableTag tab = (TableTag)table;
        TableRow[] rows = tab.getRows();
        List<TableColumn> columns = new ArrayList<TableColumn>();
        for(TableRow row : rows){
        	TableHeader[] headers = row.getHeaders();
        	if(headers.length > 0){
        		TableHeader header = headers[0];
        		if(header.toPlainTextString().contains("地域"))break;
        	}
        	TableColumn[] cols = row.getColumns();
        	if(cols.length > 0){
        		columns.add(cols[0]);
        	}
        }
        for(TableColumn column : columns){
        	System.out.println(column.toHtml());
        }*/

		/*String page = HttpUtils.get("http://vip.stock.finance.sina.com.cn/fund_center/api/jsonp.php/IO.XSRV2.CallbackList"+URLEncoder.encode("['DcBGhAVnTMWhR1GU']","utf-8")+"/NetValueReturn_Service.NetValueReturnOpen?page=1&num=40&sort=one_year&asc=0&ccode=&type2=2&type3=&%5Bobject%20HTMLDivElement%5D=nkyl2","gb2312");
		System.out.println(page);
		String json = StringUtils.substringBetween(page, "((", "))");
		Map<String, Class> m = new HashMap<String, Class>();
        m.put("data", Map.class);
		Object obj = JsonUtils.getObject4JsonString(json, Map.class, m);
		Map datas = (Map)obj;
		for(Map data : (List<Map>)datas.get("data")){
			System.out.println(data.get("sname"));
		}*/

		/*String page = HttpUtils.get("http://www.ndrc.gov.cn/xwzx/xwfb/", "utf-8");
		System.out.println(page);
		Node node = HtmlUtils.getNodeByAttribute(page, null, "class", "list_02 clearfix");
		List<Node> lis = HtmlUtils.getNodeListByTagName(node, "li");
		for(Node li : lis){
			Node a = HtmlUtils.getNodeByTagName(li, "a");
			if(a != null){
				String date = StringUtils.substringBetween(li.toHtml(), "<font class=\"date\">", "</font>");
				System.out.println(StringUtils.replace(a.toHtml(), "./", "http://www.ndrc.gov.cn/xwzx/xwfb/")+"["+date+"]");
			}
		}*/

		/*String page = HttpUtils.get("http://www.cninfo.com.cn//disclosure/tzzgxxx/stocks/zxxx1y/cninfo/002666.js?ver=", "gb2312");
		System.out.println(page);
		String json = StringUtils.substringBetween(page, "=", ";");
		List<List> list = JsonUtils.getList4Json(json, List.class);
		for(List item : list){
			System.out.println(item);
			String filePath = String.valueOf(item.get(1));
			String downloadUrl = "http://www.cninfo.com.cn/"+filePath;
			String fileName = StringUtils.substringAfterLast(filePath, "/");
			HttpUtils.download(downloadUrl,null, "d:\\", fileName);
		}*/

		String page = CommonHttpUtils.get("http://hq.sinajs.cn/list=sz000710", null, "");
		System.out.println(page);
		//page = HttpUtils.get("http://hq.sinajs.cn/list=s_sz399617", null, "");
		//System.out.println(page);
		/*Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("channelId","6ac54ce22db4474abc234d6edbe53ae7");
		parameters.put("dateStr", "2015.05.29");
		parameters.put("mondayStr", "2015.05.29");
		String page = HttpUtils.post("http://www.chinaclear.cn/cms-webapp/wcm/getManuscriptByTitle_mzkb.action?weekflag=meiyou", parameters, "");
		Node node = HtmlUtils.getNodeByAttribute(page, null, "style", "WIDTH: 100%; BORDER-COLLAPSE: collapse");
		Map<String,Map<String, String>> datas = HtmlUtils.getListFromTable((TableTag)node, 0, 0);

		System.out.println(datas);*/

		/*String page = HttpUtils.get("http://www.hibor.com.cn/elitelist_1_0.html", "GBK");
		System.out.println(page);
		List<Node> nodes = HtmlUtils.getNodeListByTagNameAndAttribute(page, null, "div", "class", "classbaogao_sousuo_list");
		for(Node node : nodes){
			System.out.println(HtmlUtils.getNodeByTagName(node, "img").toHtml());
		}*/



		//String page = HttpUtils.post("http://xueqiu.com/service/comments?filtered=true&id=58844969","", "GBK");

		/*List params = new ArrayList();
		String page = HttpUtils.get("http://q.10jqka.com.cn/stock/gn/", null, "");
		List<Node> cates = HtmlUtils.getNodeListByTagNameAndAttribute(page, null, "div", "class", "cate_items");
		for(Node cate : cates){
			List<Node> items = HtmlUtils.getNodeListByTagName(cate, "a");
			for(Node item : items){
				System.out.println(((LinkTag)item).getAttribute("href"));
			}
		}

		page = HttpUtils.get("http://q.10jqka.com.cn/stock/gn/tbf/", null, "");
		Node table = HtmlUtils.getNodeByAttribute(page, null, "class", "m_table");
		List<List<String>> lists = HtmlUtils.getListFromTable((TableTag)table,0);
		for(List<String> list : lists){
			System.out.println(list);
		}*/

		/*String page = HttpUtils.get("http://d.10jqka.com.cn/v2/line/bk_885611/01/last.js", null, "gbk");
		String data = StringUtils.substringBetween(page, "data\":\"", "\"})");
		String[] ks = StringUtils.split(data, ";");
		for(String k : ks){
			System.out.println(k);
		}*/
		/*String page = HttpUtils.get("http://stockpage.10jqka.com.cn/600088/holder/", null, "gbk");
		Node div = HtmlUtils.getNodeByAttribute(page, null, "id", "bd_list1");
		//System.out.println(div.toHtml());

		for(int i=1;i<=5;i++){
			Node n1 = HtmlUtils.getNodeByAttribute(div, null, "targ", "fher_"+i);
			System.out.println(n1.toHtml());
			Node n2 = HtmlUtils.getNodeByAttribute(div, null, "id", "fher_"+i);
			Node tab = HtmlUtils.getNodeByTagName(n2, "table");
			//System.out.println(tab.toHtml());
			List<List<String>> list = HtmlUtils.getListFromTable2((TableTag)tab, 0);
			System.out.println(list);
		}*/

		/*Map<String, String> requestHeaders = new HashMap<String, String>();
    	requestHeaders.put("Cookie", "cna=/+KKDJfijAECAWUsw7c3ZJ4U; x=__ll%3D-1%26_ato%3D0; otherx=e%3D1%26p%3D*%26s%3D0%26c%3D0%26f%3D0%26g%3D0%26t%3D0; _med=dw:1364&dh:768&pw:1364&ph:768&ist:0; tracknick=kevincomcn; t=19935f2ccae6f9ec5eb97e82ec378fc9; _tb_token_=yEDgBWpaVJpA; cookie2=489cc1e1519d4225bb844b5f13978265; pnm_cku822=061UW5TcyMNYQwiAiwQRHhBfEF8QXtHcklnMWc%3D%7CUm5Ockt%2BRH9He0J6RH9BeS8%3D%7CU2xMHDJ7G2AHYg8hAS8XKgQkClY3UT1aJF5wJnA%3D%7CVGhXd1llXGlTaFBsVW1TaFZuWWRGe0Z%2FRXBNeEd9QHlBdEx5RHtBbzk%3D%7CVWldfS0TMwgwDi4SLg4gW3cXZlZyTz4AbVh2IHY%3D%7CVmhIGCUFOBgkGyAePgYzCzMTLxAlGDgEOQY%2FHyMcKRQ0ADUMWgw%3D%7CV25Tbk5zU2xMcEl1VWtTaUlwJg%3D%3D; res=scroll%3A1263*5855-client%3A1263*898-offset%3A1263*5855-screen%3A1280*1024; cq=ccp%3D1; l=As3NECFj8rLUE5QN1cChP4CrXeNNGAFp; isg=AtPTAQCmj1HSUUy65bRon8GUYlfRSeWG4JEBvIX6nfI8BPemDFg0m5QSSPvU");
		String page = HttpUtils.get("https://list.tmall.com/search_product.htm?spm=a220m.1000858.1000724.4.btun1J&q=%C7%E9%C8%A4%D6%C6%B7%FE&sort=d&style=g&from=rs_1_key-top-s", null, requestHeaders, "GBK");

		Node list = HtmlUtils.getNodeByAttribute(page, null, "id", "J_ItemList");
		List<Node> items = HtmlUtils.getNodeListByTagNameAndAttribute(list, "div", "class", "product-iWrap");

		for(Node item : items){
			System.out.println(item.toHtml());
			Node imgNode = HtmlUtils.getNodeByAttribute(item, null, "class", "productImg-wrap");
			Node img = HtmlUtils.getNodeByTagName(imgNode, "img");
			System.out.println("img="+ HtmlUtils.getAttribute(img, "src"));
			Node price = HtmlUtils.getNodeByTagName(item, "em");
			System.out.println("price="+ HtmlUtils.getAttribute(price, "title"));
			Node productNode = HtmlUtils.getNodeByAttribute(item, null, "class", "productTitle");
			Node product = HtmlUtils.getNodeByTagName(productNode, "a");
			System.out.println("product="+HtmlUtils.getAttribute(product,"title"));
			Node shopNode = HtmlUtils.getNodeByAttribute(item, null, "class", "productShop-name");
			System.out.println("shop="+shopNode.toPlainTextString());
			break;
		}*/



	}


}
