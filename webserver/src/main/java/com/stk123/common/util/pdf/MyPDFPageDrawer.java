package com.stk123.common.util.pdf;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.pdfbox.pdfviewer.PageDrawer;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.fdf.FDFDocument;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDFontDescriptorDictionary;
import org.apache.pdfbox.pdmodel.graphics.xobject.PDXObject;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import org.apache.pdfbox.util.PDFTextStripper;

public class MyPDFPageDrawer extends PageDrawer {

	public MyPDFPageDrawer() throws IOException {
		super();
	}
	
	public void strokePath() {
		
	}

	public static void main(String[] args) throws Exception {
		String s = null;
		String pdffile = "d:/1203407521.PDF";
		PDDocument pdfdoc = null;
		
		PrintImageLocations.print(pdffile);
		
		if(true) return;
		
		try {
			pdfdoc = PDDocument.load(pdffile);
		
			List pages = pdfdoc.getDocumentCatalog().getAllPages();
            Iterator iter = pages.iterator();
            while( iter.hasNext() )
            {
                PDPage page = (PDPage)iter.next();
                PDResources resources = page.getResources();
                // extract all fonts which are part of the page resources
                Map<String, PDXObject> xo = resources.getXObjects();
                Iterator<String> xoIter = xo.keySet().iterator();
                while( xoIter.hasNext() )
                {
                	String key = xoIter.next();
                	PDXObject x = xo.get( key );
                	if(x != null){
                		System.out.println("xx="+x.getClass());
                	}
                }
                
                
                Map<String, PDFont> fonts = resources.getFonts();
                Iterator<String> fontIter = fonts.keySet().iterator();
                while( fontIter.hasNext() )
                {
                    String key = fontIter.next();
                    PDFont font = fonts.get( key );
                    if(font != null){
                    	PDFontDescriptorDictionary fd = (PDFontDescriptorDictionary)font.getFontDescriptor();
                    	if(fd != null)
                    	System.out.println(fd.getFontName());
                    }
                }
                
            }
			
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

	}
	
	

}
