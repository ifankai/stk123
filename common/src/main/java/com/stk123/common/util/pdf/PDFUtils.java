package com.stk123.common.util.pdf;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.stk123.common.CommonUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.lang.StringUtils;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSObject;
import org.apache.pdfbox.cos.COSString;
import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.pdfparser.PDFStreamParser;
import org.apache.pdfbox.pdfwriter.ContentStreamWriter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDStream;
import org.apache.pdfbox.pdmodel.fdf.FDFDocument;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import org.apache.pdfbox.util.PDFOperator;
import org.apache.pdfbox.util.PDFText2HTML;
import org.apache.pdfbox.util.PDFTextStripper;

public class PDFUtils {
	
	public static String getText(String file) throws Exception{
		String s = null;
		String pdffile = file;
		PDDocument pdfdoc = null;
		try {
			pdfdoc = PDDocument.load(pdffile);
			PDFTextStripper stripper = new PDFTextStripper();
			s = stripper.getText(pdfdoc);
		} finally {
			if(pdfdoc != null)pdfdoc.close();
		}
		return s;
	}

	public static String getText2(String file) {
		String s = null;
		String pdffile = file;
		PDDocument pdfdoc = null;
		try {
			pdfdoc = PDDocument.load(pdffile);
			List<COSObject> list = pdfdoc.getDocument().getObjects();
			PDFTextStripper stripper = new PDFTextStripper();
			s = stripper.getText(pdfdoc);

			PDDocumentCatalog docCatalog = pdfdoc.getDocumentCatalog();
			PDAcroForm acroForm = docCatalog.getAcroForm();
			List fields = acroForm.getFields();
			//System.out.println(fields.size());
			for(int i=0;i<fields.size();i++){
				PDField field = (PDField)fields.get(i);
				//System.out.println(field.getValue());
			}
			FDFDocument fdf = acroForm.exportFDF();
			// Document luceneDocument = LucenePDFDocument.getDocument( ... );
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (pdfdoc != null) {
					pdfdoc.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return s;
	}

	public static void toTextFile(String doc, String filename) throws Exception {
		PDDocument pdfdoc = null;
		try {
			pdfdoc = PDDocument.load(doc);
			PDFTextStripper stripper = new PDFTextStripper();
			stripper.setWordSeparator("\\w");
			stripper.setLineSeparator("\r\\r");
			stripper.setArticleStart("@@");
			stripper.setArticleEnd("##");
			/*System.out.println("start="+stripper.getParagraphStart());
			System.out.println("end="+stripper.getParagraphEnd());
			System.out.println("bead="+stripper.getSeparateByBeads());*/
			/*stripper.setParagraphStart("<");
			stripper.setParagraphEnd(">");*/
			PrintWriter pw = new PrintWriter(new FileWriter(filename));
			stripper.writeText(pdfdoc, pw);
			pw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (pdfdoc != null) {
					pdfdoc.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public static void toHtmlFile(String doc, String filename) throws Exception {
		PDDocument pdfdoc = null;
		try {
			pdfdoc = PDDocument.load(doc);
			PDFText2HTML stripper = new PDFText2HTML("UTF-8");
			PrintWriter pw = new PrintWriter(new FileWriter(filename));
			stripper.writeText(pdfdoc, pw);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (pdfdoc != null) {
					pdfdoc.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			/*String sc = getText("D:/share/投资/上市公司报表/000997/63861351.PDF");
			// System.out.print(sc);*/
			/*toTextFile("D:/1200682312.PDF",
					"D:/solution.txt");
			
			toHtmlFile("D:/1200682312.PDF",
					"D:/solution.html");*/
			
			/*String spdf = PDFUtils.getText("D:/share/投资/研报/1200987692.PDF");
			Set<String> sets = CommonUtils.getMatchStrings(spdf, "(为公司带来)( ?)[0-9]*(,)?[0-9]*( ?)(万元的净利润)");
			System.out.print(sets);*/

			String pdfContent = PDFUtils.getText("D:\\其他\\量子生物：2019年半年度报告.PDF");
			String[] lines = StringUtils.split(pdfContent, "\n");
			int l = 1;
			for(String line : lines){
				System.out.println("["+(l++)+"]"+line);
			}
			List<String> pdf = Arrays.stream(lines).map(row -> StringUtils.trim(row)).collect(Collectors.toList());
			System.out.println(sublines(pdf, new Line("公司业务概要", "第(.)节", null, "第三节公司业务概要".length()), "第四节 经营情况讨论与分析"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Data
	@AllArgsConstructor
	static class Line{
		private String mustContain;
		private String mustRegex;
		private String notContain;
		private int lengthExcludeEmpty;

		public boolean equals(String line){
			if(mustContain != null && !StringUtils.contains(line, mustContain)) {
				return false;
			}
			if(mustRegex != null && CommonUtils.getMatchString(line, mustRegex) == null){
				return false;
			}
			if(notContain != null && StringUtils.contains(line, notContain)){
				return false;
			}
			if(lengthExcludeEmpty != 0 && StringUtils.replace(line, " ", "").length() != lengthExcludeEmpty){
				return false;
			}
			return true;
		}
	}

	public static List<String> sublines(List<String> lines, Line startLine, String endLineExclude){
		List<String> results = null;
		for(String line : lines){
			if(results == null && startLine.equals(line)){
				results = new ArrayList<>();
			}
			if(StringUtils.equals(line, endLineExclude)){
				break;
			}else if(results != null){
				results.add(line);
			}
		}
		return results;
	}

	public void editPDF() {

		try {
			// pdfwithText
			PDDocument helloDocument = PDDocument.load(new File(
					"D:\\gloomyfish\\pdfwithText.pdf"));
			// PDDocument helloDocument = PDDocument.load(new
			// File("D:\\gloomyfish\\hello.pdf"));
			// int pageCount = helloDocument.getNumberOfPages();
			PDPage firstPage = (PDPage) helloDocument.getDocumentCatalog()
					.getAllPages().get(0);
			// PDPageContentStream content = new
			// PDPageContentStream(helloDocument, firstPage);
			PDStream contents = firstPage.getContents();

			PDFStreamParser parser = new PDFStreamParser(contents.getStream());
			parser.parse();
			List tokens = parser.getTokens();
			for (int j = 0; j < tokens.size(); j++) {
				Object next = tokens.get(j);
				if (next instanceof PDFOperator) {
					PDFOperator op = (PDFOperator) next;
					// Tj and TJ are the two operators that display strings in a
					// PDF
					if (op.getOperation().equals("Tj")) {
						// Tj takes one operator and that is the string
						// to display so lets update that operator
						COSString previous = (COSString) tokens.get(j - 1);
						String string = previous.getString();
						string = string.replaceFirst("Hello",
								"Hello World, fish");
						// Word you want to change. Currently this code changes
						// word "Solr" to "Solr123"
						previous.reset();
						previous.append(string.getBytes("ISO-8859-1"));
					} else if (op.getOperation().equals("TJ")) {
						COSArray previous = (COSArray) tokens.get(j - 1);
						for (int k = 0; k < previous.size(); k++) {
							Object arrElement = previous.getObject(k);
							if (arrElement instanceof COSString) {
								COSString cosString = (COSString) arrElement;
								String string = cosString.getString();
								string = string.replaceFirst("Hello",
										"Hello World, fish");

								// Currently this code changes word "Solr" to
								// "Solr123"
								cosString.reset();
								cosString.append(string.getBytes("ISO-8859-1"));
							}
						}
					}
				}
			}
			// now that the tokens are updated we will replace the page content
			// stream.
			PDStream updatedStream = new PDStream(helloDocument);
			OutputStream out = updatedStream.createOutputStream();
			ContentStreamWriter tokenWriter = new ContentStreamWriter(out);
			tokenWriter.writeTokens(tokens);
			firstPage.setContents(updatedStream);
			helloDocument.save("D:\\gloomyfish\\helloworld.pdf"); // Output file
																	// name
			helloDocument.close();
			// PDFTextStripper textStripper = new PDFTextStripper();
			// System.out.println(textStripper.getText(helloDocument));
			// helloDocument.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (COSVisitorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
