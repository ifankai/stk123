package com.stk123.tool.util;

import java.io.FileInputStream;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.usermodel.Paragraph;
import org.apache.poi.hwpf.usermodel.Range;
import org.apache.poi.hwpf.usermodel.Table;
import org.apache.poi.hwpf.usermodel.TableCell;
import org.apache.poi.hwpf.usermodel.TableIterator;
import org.apache.poi.hwpf.usermodel.TableRow;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;

public class WordUtils {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
		    /*InputStream is = new FileInputStream(new File("d:\\63671749.doc"));
		    WordExtractor ex = new WordExtractor(is);
		    String text2003 = ex.getText();
		    System.out.println(text2003);*/
		    
		    /*HWPFDocument doc = new HWPFDocument(new FileInputStream("d:\\63671749.doc"));
		    String text = doc.getRange().text();
		    System.out.println(text);*/
			
			getTables("E:\\stock\\000050_深天马Ａ\\invest\\1200431862.DOC");
			
			//getTables("d:\\63671749.doc");
			//String text = getCellAsText("d:\\63671749.doc",0,5,1);
			Table tb = getTable("E:\\stock\\000050_深天马Ａ\\invest\\1200431862.DOC",0);
			//System.out.println(tb.text());
			String text = getCellWhenColumnContain(tb, 0, "主要内容", 1);
			System.out.println(text);
			
			/*System.setProperty("javax.xml.parsers.DocumentBuilderFactory","com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl"); 
			XWPFTable tbx = getTableX("d:\\1200372807.DOCX",0);
			text = getCellXWhenColumnContain(tbx, 0, "主要内容", 1);
			System.out.println(text);*/
		} catch (Exception e) {
		    e.printStackTrace();
		}

	}
	
	public static XWPFTable getTableX(String sourceFile, int i) throws Exception {
		FileInputStream in = new FileInputStream(sourceFile);  
        XWPFDocument xwpf = new XWPFDocument(in);  
        List<XWPFTable> tbs = xwpf.getTables();
        if(tbs.size() <= i)return null;
        return tbs.get(i);
	}
	
	//取column列包含content的行的第column2列内容
	public static String getCellXWhenColumnContain(XWPFTable tb, int column, String content, int column2) throws Exception {
		for (int i = 0; i < tb.getNumberOfRows(); i++) {
			XWPFTableRow tr = tb.getRow(i);
	        // 迭代列，默认从0开始  
	        if(tr != null){
	        	XWPFTableCell td = tr.getCell(column);// 取得单元格  
		        // 取得单元格的内容
		        String text = td.getText();
		        
		        if(StringUtils.containsIgnoreCase(text.toString(), content)){
		        	td = tr.getCell(column2);
		        	if(td != null){
			        	StringBuffer sb = new StringBuffer();
			        	List<XWPFParagraph> ps = td.getParagraphs();
				        for (int k = 0; k < ps.size(); k++) {  
				        	XWPFParagraph para = ps.get(k);
				            String s = para.getText();
				            sb.append(s).append("\n");  
				        }
				        return sb.toString();
		        	}
		        }
	        }
		}
        return null;
	}
	
	//-----------------------------------------------------------------------------
	
	public static Table getTable(String sourceFile, int i) throws Exception {
		FileInputStream in = new FileInputStream(sourceFile);  
        HWPFDocument hwpf = new HWPFDocument(in);  
        Range range = hwpf.getRange();// 得到文档的读取范围  
        TableIterator it = new TableIterator(range);
        Table tb = null;
        for(int j=0;it.hasNext() && j<=i;j++){
        	tb = it.next();
        }
        return tb;
	}
	
	public static String getCellAsText(Table tb, int row, int column){
        TableRow tr = tb.getRow(row);  
        // 迭代列，默认从0开始  
        if(tr != null){
	        TableCell td = tr.getCell(column);// 取得单元格  
	        // 取得单元格的内容
	        StringBuffer text = new StringBuffer();
	        for (int k = 0; k < td.numParagraphs(); k++) {  
	            Paragraph para = td.getParagraph(k);  
	            String s = para.text();  
	            text.append(s);  
	        }
	        return text.toString();
        }
        return null;
	}
	
	public static String getCellAsText(String sourceFile, int tableNumber, int row, int column) throws Exception {
		Table tb = getTable(sourceFile,tableNumber);
		return getCellAsText(tb, row, column);
	}
	
	//取column列包含content的行的第column2列内容
	public static String getCellWhenColumnContain(Table tb, int column, String content, int column2) throws Exception {
		for (int i = 0; i < tb.numRows(); i++) {
			TableRow tr = tb.getRow(i);  
	        // 迭代列，默认从0开始  
	        if(tr != null){
	        	if(tr.numCells() <= column)
	        		continue;
		        TableCell td = tr.getCell(column);// 取得单元格  
		        // 取得单元格的内容
		        StringBuffer text = new StringBuffer();
		        for (int k = 0; k < td.numParagraphs(); k++) {  
		            Paragraph para = td.getParagraph(k);  
		            String s = para.text();  
		            text.append(s);  
		        }
		        //System.out.println(text);
		        if(StringUtils.containsIgnoreCase(text.toString(), content)){
		        	if(tr.numCells()<=column2)
		        		return null;
		        	td = tr.getCell(column2);
		        	text = new StringBuffer();
			        for (int k = 0; k < td.numParagraphs(); k++) {  
			            Paragraph para = td.getParagraph(k);  
			            String s = para.text();  
			            text.append(s);  
			        }
			        return text.toString();
		        }
	        }
		}
        return null;
	}
	
	public static String getCellWhenColumnContain(String sourceFile, int tableNumber, int column, String content, int column2) throws Exception {
		Table tb = getTable(sourceFile,tableNumber);
		return getCellWhenColumnContain(tb, column, content, column2);
	}
	
	public static String getCellWhenColumnContain(String sourceFile, int column, String content, int column2) throws Exception {
		return getCellWhenColumnContain(sourceFile,0,column,content,column2);
	}
	
	public static void getTables(String sourceFile) throws Exception {  
        FileInputStream in = new FileInputStream(sourceFile);  
        HWPFDocument hwpf = new HWPFDocument(in);  
        Range range = hwpf.getRange();// 得到文档的读取范围  
        TableIterator it = new TableIterator(range);  
        // 迭代文档中的表格  
        while (it.hasNext()) {  
            Table tb = (Table) it.next();  
            // 迭代行，默认从0开始  
            for (int i = 0; i < tb.numRows(); i++) {  
                TableRow tr = tb.getRow(i);  
                // 迭代列，默认从0开始  
                for (int j = 0; j < tr.numCells(); j++) {  
                    TableCell td = tr.getCell(j);// 取得单元格  
                    // 取得单元格的内容  
                    for (int k = 0; k < td.numParagraphs(); k++) {  
                        Paragraph para = td.getParagraph(k);  
                        String s = para.text();  
                        System.out.print(s);  
                    }
                    System.out.print("[cell];");
                }
                System.out.println();
                System.out.println("[line]----------------------");
            }
            
        }
    } 

}
